package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.controls.RemovableHyperlinkCell;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.internationalization.InternationalizationHelper;
import sws.murcs.listeners.ChangeCallback;
import sws.murcs.listeners.GenericCallback;
import sws.murcs.model.Backlog;
import sws.murcs.model.EstimateType;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller for the model creator popup window.
 * Since there should only be one instance of this PopUp
 */
public class BacklogEditor extends GenericEditor<Backlog> {

    /**
     * The fixed height of rows in the table view for stories.
     */
    private static final Double FIXED_ROW_HEIGHT_STORY_TABLE = 30.0;

    /**
     * The Button for navigating to the PO.
     */
    @FXML
    private Button navigateToPOButton;

    /**
     * Text fields for displaying short name, long name and priority.
     */
    @FXML
    private TextField shortNameTextField, longNameTextField, priorityTextField;

    /**
     * A text area for the description of a back log.
     */
    @FXML
    private TextArea descriptionTextArea;

    /**
     * A choice box for chooses the PO for a backlog.
     */
    @FXML
    private ComboBox<Person> poComboBox;

    /**
     * A choice box for choosing the estimation method for a backlog.
     */
    @FXML
    private ChoiceBox<EstimateType> estimationMethodChoiceBox;

    /**
     * A ChoiceBox for adding a story to the backlog.
     */
    @FXML
    private ComboBox<Story> storyPicker;

    /**
     * A table containing all the stories in a backlog.
     */
    @FXML
    private TableView<Story> storyTable;

    /**
     * A column containing stories.
     */
    @FXML
    private TableColumn<Story, String> storyColumn;

    /**
     * A column containing story priorities.
     */
    @FXML
    private TableColumn<Story, Integer> priorityColumn;

    /**
     * Increase and decrease priority buttons.
     */
    @FXML
    private Button increasePriorityButton, decreasePriorityButton, jumpPriorityButton, dropPriorityButton;

    /**
     * An observable list of backlog stories.
     */
    private ObservableList<Story> observableStories;

    /**
     * The state of the story highlighting.
     */
    private static SimpleBooleanProperty highlighted = new SimpleBooleanProperty(true);

    /**
     * A flag if a popup is already active to prevent duplicates.
     */
    private boolean popUpIsActive = false;

    /**
     * Sets the state of the story highlighting.
     */
    public static void toggleHighlightState() {
        toggleHighlightState(!highlighted.getValue());
    }

    /**
     * Sets the state of story highlighting.
     * @param highlights The highlight state.
     */
    public static void toggleHighlightState(final boolean highlights) {
        highlighted.setValue(highlights);
    }

    /**
     * An observable object representing the currently selected story.
     */
    private ObservableObjectValue<Story> selectedStory;

    @Override
    protected final void initialize() {
        // set up change listener
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        });

        // assign change listeners to fields
        highlighted.addListener((observable, oldValue, newValue) -> {
            updateStoryTable();
        });

        shortNameTextField.focusedProperty().addListener(getChangeListener());
        longNameTextField.focusedProperty().addListener(getChangeListener());
        descriptionTextArea.focusedProperty().addListener(getChangeListener());
        poComboBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        estimationMethodChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        storyTable.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        storyTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int selectedIndex = storyTable.getSelectionModel().getSelectedIndex();

            //Configures the change-priority buttons
            Integer priority = getModel().getStoryPriority(selectedStory.get());
            boolean isMaxPriority = priority == 1;
            boolean isMinPriority = priority == -1;
            increasePriorityButton.setDisable(isMaxPriority);
            decreasePriorityButton.setDisable(isMinPriority);
            jumpPriorityButton.setDisable(isMaxPriority);
            dropPriorityButton.setDisable(isMinPriority);
        });
        storyTable.setFixedCellSize(FIXED_ROW_HEIGHT_STORY_TABLE);

        // setup the observable stories
        observableStories = FXCollections.observableArrayList();
        storyTable.setItems(observableStories);
        selectedStory = storyTable.getSelectionModel().selectedItemProperty();
        storyColumn.setCellValueFactory(param -> {
            SimpleStringProperty property = new SimpleStringProperty();
            property.set(param.getValue().getShortName());
            return property;
        });
        List<ChangeCallback<Story>> callbacks = new ArrayList<>();
        callbacks.add(this::removeStory);
        callbacks.add(this::workspaceStory);
        storyColumn.setCellFactory(param -> new RemovableHyperlinkCell(this, callbacks));
        priorityColumn.setCellValueFactory(param -> {
            SimpleObjectProperty<Integer> property = new SimpleObjectProperty<>();
            Integer priority = getModel().getStoryPriority(param.getValue());
            if (priority != -1) {
                property.set(priority);
            }
            return property;
        });
        priorityColumn.setCellFactory(param -> new EditablePriorityCell());
        storyTable.setEditable(true);
        priorityColumn.setEditable(true);
        priorityColumn.setComparator((storyPriority1, storyPriority2) -> {
            if (storyPriority1 == null && storyPriority2 == null) {
                return 0;
            }
            if (storyPriority1 == null) {
                return 1;
            }
            if (storyPriority2 == null) {
                return -1;
            }
            return storyPriority1.compareTo(storyPriority2);
        });
        storyColumn.setComparator(String::compareTo);

        navigateToPOButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (poComboBox.getSelectionModel().getSelectedItem() != null) {
                Person person = poComboBox.getSelectionModel().getSelectedItem();
                if (e.isControlDown()) {
                    getNavigationManager().navigateToNewTab(person);
                } else {
                    getNavigationManager().navigateTo(person);
                }
            }
        });
        navigateToPOButton.setDisable(true);

        jumpPriorityButton.setDisable(true);
        dropPriorityButton.setDisable(true);
        decreasePriorityButton.setDisable(true);
        increasePriorityButton.setDisable(true);
    }

    /**
     * Adds or removes a story from the workspace depending on if the story is in the workspace.
     * @param story The story to add or remove from the workspace.
     */
    private void workspaceStory(final Story story) {
        if (getModel().getWorkspaceStories().contains(story)) {
            getModel().removeStoryFromWorkspace(story);
        }
        else {
            getModel().addToWorkspaceStories(story);
        }
        updateStoryTable();
    }

    /**
     * Method for managing the removal of stories from the backlog.
     * @param story story to remove.
     */
    private void removeStory(final Story story) {
        if (!isCreationWindow) {
            GenericPopup popup = new GenericPopup(getWindowFromNode(shortNameTextField));
            popup.setTitleText("{AreYouSure}");
            popup.setWindowTitle("{AreYouSure}");
            String extraWarning = "";
            Sprint storyUsage = UsageHelper.findBy(ModelType.Sprint, s -> s.getStories().contains(story));
            if (storyUsage != null) {
                extraWarning = "{StoryWillBeRemovedFromSprint} \"" + storyUsage.getShortName() + "\"";
            }
            popup.setMessageText("{AreYouSureRemoveStory} \""
                    + story.getShortName() + "\" {FromBacklog}"
                    + (extraWarning.length() > 0 ? "\n\n" + extraWarning : ""));
            popup.addYesNoButtons(() -> {
                if (storyUsage != null) {
                    storyUsage.removeStory(story);
                }
                if (story.getStoryState() != Story.StoryState.None) {
                    story.setStoryState(Story.StoryState.None);
                }
                getModel().removeStory(story);
                updateStoryTable();
                updateAvailableStories();
                popup.close();
            }, "danger-will-robinson", "everything-is-fine");
            popup.show();
        }
        else {
            getModel().removeStory(story);
            updateStoryTable();
            updateAvailableStories();
        }
    }

    /**
     * Increases the priority of a story.
     * @param event the button clicked event
     */
    @FXML
    private void increasePriority(final ActionEvent event) {
        Story story = storyTable.getSelectionModel().getSelectedItem();
        if (story != null) {
            Integer storyPriority = getModel().getStoryPriority(story);
            if (storyPriority == -1) {
                storyPriority = getModel().getLowestPriorityStory();
            }
            else if (storyPriority == 1) {
                return;
            }
            else {
                storyPriority--;
            }
            try {
                getModel().modifyStory(story, storyPriority);
            }
            catch (CustomException e) {
                //Should not ever happen, this should be handled by the GUI,
                //e.g Disabling buttons.
                ErrorReporter.get().reportError(e, "Failed to modify the story priority");
            }
            updateStoryTable();
        }
    }

    /**
     * Called when the user tries to decrease the priority of a story.
     * @param event the button clicked event
     */
    @FXML
    private void decreasePriority(final ActionEvent event) {
        Story story = storyTable.getSelectionModel().getSelectedItem();
        if (story != null) {
            Integer storyPriority = getModel().getStoryPriority(story);
            if (storyPriority == -1) {
                return;
            }
            else if (storyPriority + 1 >= getModel().getLowestPriorityStory()) {
                storyPriority = null;
            }
            else {
                storyPriority++;
            }
            try {
                if (storyPriority != null) {
                    getModel().modifyStory(story, storyPriority);
                }
                else {
                    changeStoryStateToNone(story, () -> {
                        try {
                            getModel().modifyStory(story, null);
                        } catch (CustomException e) {
                            //Should not ever happen, this should be handled by the GUI
                            ErrorReporter.get().reportError(e, "Failed to modify the priority of the story");
                        }
                    });
                }
            }
            catch (CustomException e) {
                //Should not ever happen, this should be handled by the GUI
                ErrorReporter.get().reportError(e, "Failed to modify the priority of the story");
            }
            updateStoryTable();
        }
    }

    /**
     * Increases the selected stories priority up to the maximum value of 0.
     * @param event Button clicked event
     */
    @FXML
    private void jumpPriority(final ActionEvent event) {
        Story story = storyTable.getSelectionModel().getSelectedItem();
        if (story != null) {
            Integer storyPriority = getModel().getStoryPriority(story);
            if (storyPriority == -1 || storyPriority != 0) {
                try {
                    getModel().modifyStory(story, 1);
                }
                catch (CustomException e) {
                    //Should not ever happen, this should be handled by the GUI
                    ErrorReporter.get().reportError(e, "Cannot increase priority");
                }
                updateStoryTable();
            }
        }
    }

    /**
     * Unprioritises the selected story.
     * @param event Button clicked event
     */
    @FXML
    private void dropPriority(final ActionEvent event) {
        Story story = storyTable.getSelectionModel().getSelectedItem();
        if (story != null) {
            Integer storyPriority = getModel().getStoryPriority(story);
            if (storyPriority != -1) {
                changeStoryStateToNone(story, () -> {
                    try {
                        getModel().modifyStory(story, null);
                    } catch (CustomException e) {
                        //Should not ever happen, this should be handled by the GUI
                        ErrorReporter.get().reportError(e, "Cannot decrease priority");
                    }
                });
                updateStoryTable();
            }
        }
    }

    /**
     * Change the story state from none to ready and informs the user if this will effect any sprints.
     * @param story The story to change the state of.
     */
    private void changeStoryStateToNone(final Story story) {
        changeStoryStateToNone(story, null);
    }
    /**
     * Change the story state from none to ready and informs the user if this will effect any sprints.
     * @param story The story to change the state of.
     * @param callback A callback to call once the state change has happened.
     */
    private void changeStoryStateToNone(final Story story, final GenericCallback callback) {
        if (story.getStoryState() == Story.StoryState.Ready) {
            if (UsageHelper.findUsages(story).stream().anyMatch(m -> m instanceof Sprint)) {
                popUpIsActive = true;
                List<Sprint> sprintsWithStory = UsageHelper.findUsages(story).stream()
                        .filter(m -> ModelType.getModelType(m).equals(ModelType.Sprint))
                        .map(m -> (Sprint) m)
                        .collect(Collectors.toList());
                List<String> collect = sprintsWithStory.stream()
                        .map(Model::toString)
                        .collect(Collectors.toList());
                String[] sprintNames = collect.toArray(new String[collect.size()]);
                GenericPopup popup = new GenericPopup();
                popup.setMessageText("{SureMakeStory} "
                        + story.toString()
                        + " {UnprioritiseWarning}\n"
                        + String.join("\n\t", sprintNames));
                popup.setTitleText("{UnprioritiseTitle}");
                popup.setWindowTitle("{Areyousure}");
                popup.addYesNoButtons(() -> {
                    popUpIsActive = false;
                    if (callback != null) {
                        callback.call();
                    }
                    sprintsWithStory.forEach(sprint -> sprint.removeStory(story));
                    story.setStoryState(Story.StoryState.None);
                    popup.close();
                }, () -> {
                    popUpIsActive = false;
                    popup.close();
                }, "danger-will-robinson", "everything-is-fine");
                popup.show();
            }
            else {
                story.setStoryState(Story.StoryState.None);
                if (callback != null) {
                    callback.call();
                }
            }
        }
        else {
            if (callback != null) {
                callback.call();
            }
        }
    }

    /**
     * Adds a story to the backlog.
     * @param event the button clicked event
     */
    @FXML
    private void addStory(final ActionEvent event) {
        Story currentStory = storyPicker.getValue();
        Integer priority = null;
        String priorityString = priorityTextField.getText().trim();
        clearErrors();
        boolean hasErrors = false;

        if (currentStory == null) {
            addFormError(storyPicker, "{NoStorySelectedError}");
            hasErrors = true;
        }
        if (!priorityString.isEmpty()) {
            try {
                priority = Integer.parseInt(priorityString);
                if (priority < 1) {
                    addFormError(priorityTextField, "{NegativePriorityError}");
                    hasErrors = true;
                }
            } catch (Exception e) {
                addFormError(priorityTextField, "{NanError}");
                hasErrors = true;
            }
        }

        if (hasErrors) {
            return;
        }
        try {
            getModel().addStory(currentStory, priority);
            updateAvailableStories();
            updateStoryTable();
        } catch (CustomException e) {
            addFormError(storyPicker, e.getMessage());
        }
        priorityTextField.clear();
    }


    @Override
    public final void loadObject() {
        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        String modelLongName = getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqual(modelLongName, viewLongName)) {
            longNameTextField.setText(modelLongName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextArea.setText(modelDescription);
        }

        EstimateType current = getModel().getEstimateType();
        estimationMethodChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        estimationMethodChoiceBox.getItems().clear();
        estimationMethodChoiceBox.getItems().addAll(EstimateType.values());
        if (current != null) {
            estimationMethodChoiceBox.getSelectionModel().select(current);
        }
        estimationMethodChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());

        updateAssignedPO();
        updateAvailableStories();
        updateStoryTable();
        super.clearErrors();
        if (!getIsCreationWindow()) {
            super.setupSaveChangesButton();
        }
        else {
            shortNameTextField.requestFocus();
        }
        isLoaded = true;
    }

    /**
     * Updates the PO assigned to the currently selected backlog.
     */
    private void updateAssignedPO() {
        Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();

        Person productOwner = getModel().getAssignedPO();

        // Add all the people with the PO skill to the list of POs
        List<Person> productOwners = organisation.getPeople()
                .stream()
                .filter(p -> p.canBeRole(Skill.PO_NAME))
                .collect(Collectors.toList());

        // Remove listener while editing the product owner picker
        poComboBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        poComboBox.getItems().clear();
        poComboBox.getItems().addAll(productOwners);
        if (poComboBox != null) {
            poComboBox.getSelectionModel().select(productOwner);
            if (!isCreationWindow) {
                navigateToPOButton.setDisable(false);
            }
        }
        poComboBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
    }

    /**
     * Updates the list of available stories based on what is available
     * in the backlog.
     */
    private void updateAvailableStories() {
        Platform.runLater(() -> {
            Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();
            int selectedIndex = storyPicker.getSelectionModel().getSelectedIndex();

            Collection<Story> stories = organisation.getUnassignedStories();
            getModel().getAllStories().forEach(stories::remove);
            storyPicker.getItems().clear();
            storyPicker.getItems().addAll(stories);
            storyPicker.getSelectionModel().select(Math.min(selectedIndex, stories.size() - 1));
        });
    }

    /**
     * Updates the table containing the list of stories.
     */
    private void updateStoryTable() {
        List<Story> stories = getModel().getAllStories();
        observableStories.setAll(stories);

        if (selectedStory.get() != null) {
            storyTable.getSelectionModel().select(selectedStory.get());
        }

    }

    @Override
    public final void dispose() {
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        longNameTextField.focusedProperty().removeListener(getChangeListener());
        poComboBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        descriptionTextArea.focusedProperty().removeListener(getChangeListener());
        super.dispose();
    }

    @Override
    protected final void saveChangesAndErrors() {
        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNullOrNotEqual(modelShortName, viewShortName)) {
            try {
                getModel().setShortName(viewShortName);
            } catch (DuplicateObjectException e) {
                addFormError(shortNameTextField, "{NameExistsError1} {Backlog} {NameExistsError2}");
            }
            catch (InvalidParameterException e) {
                addFormError(shortNameTextField, "{ShortNameEmptyError}");
            }
        }

        String modelLongName = getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNullOrNotEqual(modelLongName, viewLongName)) {
            getModel().setLongName(viewLongName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNullOrNotEqual(modelDescription, viewDescription)) {
            getModel().setDescription(viewDescription);
        }

        Person modelProductOwner = getModel().getAssignedPO();
        Person viewProductOwner = poComboBox.getValue();
        if (isNullOrNotEqual(modelProductOwner, viewProductOwner) && viewProductOwner != null) {
            try {
                getModel().setAssignedPO(viewProductOwner);
                updateAssignedPO();
            } catch (InvalidParameterException e) {
                addFormError(poComboBox, "{InvalidPOError}");
            }
        }
        if (getModel().getAssignedPO() == null) {
            addFormError(poComboBox, "{NoPOError}");
        }

        EstimateType newEstimateType = estimationMethodChoiceBox.getValue();
        if (isNotEqual(getModel().getEstimateType(), newEstimateType)) {
            getModel().setEstimateType(newEstimateType);
        }
    }

    /**
     * An editable cell for priorities of stories. Also contains the color tab for highlighting stories.
     */
    private class EditablePriorityCell extends TableCell<Story, Integer> {

        /**
         * The text field for editing the priority.
         */
        private TextField textField;

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.requestFocus();
            }
        }

        @Override
        public void commitEdit(final Integer priority) {
            if (!isEmpty()) {
                if (priority == null) {
                    super.commitEdit(null);
                    setPriority(null);
                    textField.setTooltip(new Tooltip(InternationalizationHelper.tryGet("ThisIsNonPrioritorised")));
                    updateStoryTable();
                }
                else if (priority < 1) {
                    textField.setTooltip(null);
                    addFormError(textField, "{NegativePriorityError}");
                }
                else {
                    super.commitEdit(priority);
                    setPriority(priority);
                    textField.setTooltip(null);
                    updateStoryTable();
                }
            }
        }

        @Override
        protected void updateItem(final Integer priority, final boolean empty) {
            super.updateItem(priority, empty);
            setTooltip(new Tooltip());
            this.setAlignment(Pos.CENTER);
            setColorTab(highlighted.getValue());
            getStyleClass().add("default-tablecell");

            if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                setText(null);
                setGraphic(null);
            } else if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
            }
        }

        /**
         * Highlight the story with a color tab. For example, if the story is not ready the color tab will be red.
         * @param set Whether the tab is being set or unset
         */
        private void setColorTab(final boolean set) {
            getStyleClass().removeAll("red-tab-tablecell", "green-tab-tablecell", "orange-tab-tablecell");
            if (!(getTableRow() == null || getTableRow().getItem() == null || isEmpty() || !set)) {
                Story story = (Story) getTableRow().getItem();
                Story.StoryState storyState = story.getStoryState();

                final int storyPriority = getModel().getStoryPriority(story);
                long lowerPriorityCount = story.getDependencies()
                        .stream()
                        .filter(param -> {
                            Integer priority = getModel().getStoryPriority(param);
                            return priority < storyPriority;
                        })
                        .count();
                boolean badDependency = lowerPriorityCount > 0;

                if (badDependency) {
                    getStyleClass().add("red-tab-tablecell");
                    getTooltip().setText(InternationalizationHelper.tryGet("BadDependencyTooltip"));
                }
                else if (storyState == Story.StoryState.Ready) {
                    getStyleClass().add("green-tab-tablecell");
                    getTooltip().setText(InternationalizationHelper.tryGet("ReadyStoryTooltip"));
                }
                else if (story.getAcceptanceCriteria().size() > 0) {
                    getStyleClass().add("orange-tab-tablecell");
                    getTooltip().setText(InternationalizationHelper.tryGet("UnestimatedTooltip"));
                }
            }
        }

        /**
         * Create the text field for editing a priority.
         */
        private void createTextField() {
            textField = new TextField(getString());
            textField.setAlignment(Pos.CENTER);
            textField.focusedProperty().addListener(
                        (observable, oldValue, newValue) -> {
                            if (!newValue) {
                                Integer priority = null;
                                if (!(textField.getText() == null || textField.getText().trim().isEmpty())) {
                                    try {
                                        priority = Integer.parseInt(textField.getText());
                                        commitEdit(priority);
                                    }
                                    catch (NumberFormatException e) {
                                        if (Objects.equals(textField.getText(), "-") && !popUpIsActive) {
                                            commitEdit(null);
                                        }
                                        else {
                                            addFormError(textField, "{NanError}");
                                        }
                                    }
                                }
                            }
                        });

            textField.setOnKeyReleased(t -> {
                if (t.getCode() == KeyCode.ENTER) {
                    Integer priority = null;
                    if (!(textField.getText() == null || textField.getText().trim().isEmpty())) {
                        try {
                            priority = Integer.parseInt(textField.getText());
                            commitEdit(priority);
                        }
                        catch (NumberFormatException e) {
                            if (Objects.equals(textField.getText(), "-")) {
                                commitEdit(null);
                            }
                            else {
                                addFormError(textField, "{NanError}");
                            }
                        }
                    }
                    else {
                        commitEdit(null);
                    }
                }
                if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }

        /**
         * Get the string representation of a priority for a table row.
         * @return The string representation
         */
        private String getString() {
            TableRow<Story> row = getTableRow();
            Story story = null;
            Integer priority;
            String priorityString = null;
            if (row != null) {
                story = row.getItem();
            }
            if (story != null) {
                priority = getModel().getStoryPriority(story);
                if (priority != -1) {
                    priorityString = priority.toString();
                }
                else {
                    priorityString = "-";
                }
            }

            return priorityString;
        }

        /**
         * Changes the priority of the selected story to the given value.
         * @param priority The new priority of this story
         */
        private void setPriority(final Integer priority) {
            Story story = (Story) getTableRow().getItem();
            if (priority != null) {
                try {
                    getModel().changeStoryPriority(story, priority);
                }
                catch (CustomException e) {
                    addFormError(textField, "{NanError}");
                }
            }
            else {
                changeStoryStateToNone(story, () -> {
                    try {
                        getModel().changeStoryPriority(story, null);
                    } catch (CustomException e) {
                        ErrorReporter.get().reportError(e, "Failed to set priority");
                    }
                });
            }
        }
    }
}
