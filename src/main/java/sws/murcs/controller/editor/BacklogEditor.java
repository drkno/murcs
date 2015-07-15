package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.apache.commons.lang.NotImplementedException;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.NavigationManager;
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Backlog;
import sws.murcs.model.EstimateType;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.model.Story;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the model creator popup window.
 * Since there should only be one instance of this PopUp
 */
public class BacklogEditor extends GenericEditor<Backlog> {

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
    private ComboBox<EstimateType> estimationMethodComboBox;

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
    private TableColumn<Story, Object> storyColumn;

    /**
     * A column containing story priorities.
     */
    @FXML
    private TableColumn<Story, String> priorityColumn;

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
     * The sort order
     */
    private enum StorySortOrder {
        /**
         * Sorted by priority.
         */
        PRIORITY,
        /**
         * Sorted by story name.
         */
        NAME
    }

    /**
     * The current story sort order of the the stories in the backlog. Defaults to story priority ordering.
     */
    private static StorySortOrder sortOrder = StorySortOrder.PRIORITY;

    /**
     * The combo box for choosing the sort order of stories in the backlog.
     */
    @FXML
    private ComboBox<StorySortOrder> sortOrderComboBox;

    /**
     * An observable object representing the currently selected story.
     */
    private ObservableObjectValue<Story> selectedStory;

    @FXML
    @Override
    public final void initialize() {
        // set up change listener
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        });

        // assign change listeners to fields
        shortNameTextField.focusedProperty().addListener(getChangeListener());
        longNameTextField.focusedProperty().addListener(getChangeListener());
        descriptionTextArea.focusedProperty().addListener(getChangeListener());
        poComboBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        estimationMethodComboBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        storyTable.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        storyTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int selectedIndex = storyTable.getSelectionModel().getSelectedIndex();

            //Configures the change-priority buttons
            Integer priority = getModel().getStoryPriority(selectedStory.get());
            boolean isMaxPriority = selectedIndex == 0 && priority != null || selectedIndex == -1;
            boolean isMinPriority = priority == null;
            increasePriorityButton.setDisable(isMaxPriority);
            decreasePriorityButton.setDisable(isMinPriority);
            jumpPriorityButton.setDisable(isMaxPriority);
            dropPriorityButton.setDisable(isMinPriority);
        });


        // setup the observable stories
        observableStories = FXCollections.observableArrayList();
        storyTable.setItems(observableStories);
        selectedStory = storyTable.getSelectionModel().selectedItemProperty();
        storyColumn.setCellFactory(param -> new RemovableHyperlinkCell());
        priorityColumn.setCellValueFactory(param -> {
            SimpleStringProperty property = new SimpleStringProperty();
            Integer priority = getModel().getStoryPriority(param.getValue());
            if (priority != null) {
                property.set(priority.toString());
            }
            return property;
        });
        priorityColumn.setCellFactory(param -> new EditablePriorityCell());
        storyTable.setEditable(true);
        priorityColumn.setEditable(true);
        sortOrderComboBox.setItems(FXCollections.observableArrayList(StorySortOrder.values()));
        sortOrderComboBox.getSelectionModel().selectFirst();
        sortOrderComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            sortOrder = newValue;
            updateStoryTable();
        });
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
            if (storyPriority == null) {
                storyPriority = getModel().getLowestPriorityStory() + 1;
            }
            if (storyPriority == 0) {
                return;
            }
            try {
                getModel().modifyStory(story, storyPriority - 1);
            }
            catch (CustomException e) {
                //Should not ever happen, this should be handled by the GUI,
                //e.g Disabling buttons.
                e.printStackTrace();
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
            if (storyPriority == null) {
                return;
            }
            else if (storyPriority + 1 >= getModel().getLowestPriorityStory()) {
                storyPriority = null;
            }
            else {
                storyPriority++;
            }
            try {
                getModel().modifyStory(story, storyPriority);
            }
            catch (CustomException e) {
                //Should not ever happen, this should be handled by the GUI
                e.printStackTrace();
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
            if (storyPriority == null || storyPriority != 0) {
                try {
                    getModel().modifyStory(story, 0);
                }
                catch (CustomException e) {
                    //Should not ever happen, this should be handled by the GUI
                    e.printStackTrace();
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
            if (storyPriority != null) {
                try {
                    getModel().modifyStory(story, null);
                }
                catch (CustomException e) {
                    //Should not ever happen, this should be handled by the GUI
                    e.printStackTrace();
                }
                updateStoryTable();
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
        boolean hasErrors = false;

        if (currentStory == null) {
            addFormError(storyPicker, "No story selected");
            hasErrors = true;
        }
        if (!priorityString.isEmpty()) {
            try {
                priority = Integer.parseInt(priorityString) - 1;
                if (priority < 0) {
                    addFormError(priorityTextField, "Priority cannot be less than 0");
                    hasErrors = true;
                }
            } catch (Exception e) {
                addFormError(priorityTextField, "Position is not a number");
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
        estimationMethodComboBox.getItems().clear();
        estimationMethodComboBox.getItems().addAll(EstimateType.values());
        estimationMethodComboBox.getSelectionModel().select(current);

        updateAssignedPO();
        updateAvailableStories();
        updateStoryTable();
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
            for (Story story : getModel().getAllStories()) {
                stories.remove(story);
            }
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
        switch (sortOrder) {
            case NAME:
                stories.sort((o1, o2) -> o1.getShortName().compareTo(o2.getShortName()));
                break;
            case PRIORITY:
                stories.clear();
                stories.addAll(getModel().getPrioritisedStories()
                        .stream()
                        .sorted((o1, o2) -> getModel().getStoryPriority(o1).compareTo(getModel().getStoryPriority(o2)))
                        .collect(Collectors.toList()));
                stories.addAll(getModel().getUnprioritisedStories()
                        .stream()
                        .sorted((o1, o2) -> o1.getShortName().compareTo(o2.getShortName()))
                        .collect(Collectors.toList()));
                break;
            default:
                throw new NotImplementedException("You should add this sort type to the this method");
        }
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
        observableStories = null;
        selectedStory = null;
        super.dispose();
    }

    @Override
    protected final void saveChangesAndErrors() {
        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNullOrNotEqual(modelShortName, viewShortName)) {
            try {
                getModel().setShortName(viewShortName);
            } catch (CustomException e) {
                addFormError(shortNameTextField, e.getMessage());
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
            } catch (CustomException e) {
                addFormError(poComboBox, e.getMessage());
            }
        }
        if (getModel().getAssignedPO() == null) {
            addFormError(poComboBox, "There must be a PO");
        }

        EstimateType newEstimateType = estimationMethodComboBox.getSelectionModel().getSelectedItem();
        if (isNotEqual(getModel().getEstimateType(), newEstimateType)) {
            getModel().setEstimateType(newEstimateType);
        }
    }

    /**
     *
     */
    private class EditablePriorityCell extends TableCell<Story, String> {

        /**
         * The text field for.
         */
        private TextField textField;

        @Override
        public void startEdit() {
            super.startEdit();
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.requestFocus();
            }
        }

        @Override
        public void commitEdit(final String priority) {
            super.commitEdit(priority);
            if (priority != null && !isEmpty()) {
                setPriority(priority);
                updateStoryTable();
            }
        }

        @Override
        protected void updateItem(final String priority, final boolean empty) {
            super.updateItem(priority, empty);

            if (empty) {
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
         * Create the text field for editing a priority.
         */
        private void createTextField() {
            textField = new TextField(getString());

            //doesn't work if clicking a different cell, only focusing out of table
            textField.focusedProperty().addListener(
                        (ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) -> {
                            if (!arg2) {
                                commitEdit(textField.getText());
                            }
                        });

            textField.setOnKeyReleased((KeyEvent t) -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                }
                if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }

        /**
         *
         * @return Get the string representation of a priority for a table row.
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
                if (priority != null) {
                    priority += 1;
                    priorityString = priority.toString();
                }
            }

            return priorityString;
        }

        /**
         * Changes the priority of the selected story to the given value.
         * @param priorityString The new priority of this story
         */
        private void setPriority(final String priorityString) {
            Story story = (Story) getTableRow().getItem();
            if (!priorityString.trim().isEmpty()) {
                try {
                    int priority = Integer.parseInt(priorityString) - 1;
                    if (priority < 0) {
                        addFormError("Priority cannot be less than 1");
                    }
                    else {
                        getModel().changeStoryPriority(story, priority);
                    }
                } catch (Exception e) {
                    addFormError("Priority must be an int");
                }
            }
            else {
                try {
                    getModel().changeStoryPriority(story, null);
                }
                catch (CustomException e) {
                }
            }
        }
    }

    /**
     * A TableView cell that contains a link to the story it represents and a button to remove it.
     */
    private class RemovableHyperlinkCell extends TableCell<Story, Object> {
        @Override
        protected void updateItem(final Object unused, final boolean empty) {
            super.updateItem(unused, empty);
            TableRow<Story> row = getTableRow();
            if (row == null || empty || row.getItem() == null) {
                setText(null);
                setGraphic(null);
            } else {
                Story story = row.getItem();
                AnchorPane container = new AnchorPane();
                container.setPrefWidth(461);
                if (getIsCreationWindow()) {
                    Label name = new Label(story.getShortName());
                    container.getChildren().add(name);
                } else {
                    Hyperlink nameLink = new Hyperlink(story.getShortName());
                    nameLink.setOnAction(a -> NavigationManager.navigateTo(story));
                    container.getChildren().add(nameLink);
                }

                //Delete button
                Button button = new Button("X");
                button.setOpacity(0.3);
                button.setOnAction(e -> {
                    GenericPopup popup = new GenericPopup();
                    popup.setTitleText("Are you sure?");
                    popup.setMessageText("Are you sure you wish to remove the story \""
                            + story.getShortName() + "\" from this backlog?");
                    popup.addYesNoButtons(p -> {
                        getModel().removeStory(story);
                        updateStoryTable();
                        updateAvailableStories();
                        popup.close();
                    });
                    popup.show();
                });
                this.setOnMouseEntered(event -> button.setOpacity(1.0));
                this.setOnMouseExited(event -> button.setOpacity(0.3));
                AnchorPane.setRightAnchor(button, 0.0);
                container.getChildren().add(button);

                setGraphic(container);
            }
        }
    }
}
