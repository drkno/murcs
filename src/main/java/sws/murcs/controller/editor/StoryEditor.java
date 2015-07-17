package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.NavigationManager;
import sws.murcs.controller.controls.SearchableComboBox;
import sws.murcs.controller.controls.md.MaterialDesignButton;
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.AcceptanceCondition;
import sws.murcs.model.Backlog;
import sws.murcs.model.EstimateType;
import sws.murcs.model.Person;
import sws.murcs.model.Story;
import sws.murcs.model.helpers.DependenciesHelper;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * An editor for the story model.
 */
public class StoryEditor extends GenericEditor<Story> {

    /**
     * The short name of the story.
     */
    @FXML
    private TextField shortNameTextField;

    /**
     * The description of the story.
     */
    @FXML
    private TextArea descriptionTextArea;

    /**
     * A choice box for the creator and the estimate choice box and a choice box for changing the story state.
     */
    @FXML
    private ChoiceBox creatorChoiceBox, estimateChoiceBox, storyStateChoiceBox;

    /**
     * Drop down with dependencies that can be added to this story.
     */
    @FXML
    private ComboBox<Story> dependenciesDropDown;

    /**
     * Container that dependencies are added to when they are added.
     */
    @FXML
    private VBox dependenciesContainer;

    /**
     * A map of dependencies and their respective nodes.
     */
    private Map<Story, Node> dependenciesMap;

    /**
     * A decorator to make the ComboBox searchable.
     * Done a little weirdly to ensure SceneBuilder still works.
     */
    private SearchableComboBox<Story> searchableComboBoxDecorator;

    /**
     * A table for displaying and updating acceptance conditions.
     */
    @FXML
    private TableView<AcceptanceCondition> acceptanceCriteriaTable;

    /**
     * The columns on the AC table.
     */
    @FXML
    private TableColumn conditionColumn, removeColumn;

    /**
     * Buttons for increasing and decreasing the priority of an AC. Also the button for adding a new AC.
     */
    @FXML
    private Button increasePriorityButton, decreasePriorityButton, addACButton;

    /**
     * The TextField containing the text for the new condition.
     */
    @FXML
    private TextField addConditionTextField;

    /**
     * Indicates whether or not the form is in creation mode.
     */
    private boolean isCreationMode;

    @Override
    public final void loadObject() {
        String modelShortName = getModel().getShortName();
        setIsCreationWindow(modelShortName == null);
        String viewShortName = shortNameTextField.getText();
        isCreationMode = modelShortName == null;
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        //Add all the story states to the choice box
        storyStateChoiceBox.getItems().clear();
        storyStateChoiceBox.getItems().addAll(Story.StoryState.values());
        storyStateChoiceBox.getSelectionModel().select(getModel().getStoryState());

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextArea.setText(modelDescription);
        }

        dependenciesDropDown.getItems().clear();
        dependenciesDropDown.getItems().addAll(PersistenceManager.getCurrent().getCurrentModel().getStories());
        dependenciesDropDown.getItems().remove(getModel());
        dependenciesDropDown.getItems().removeAll(getModel().getDependencies());

        dependenciesMap.clear();
        dependenciesContainer.getChildren().clear();
        getModel().getDependencies().forEach(dependency -> {
            Node dependencyNode = generateStoryNode(dependency);
            dependenciesContainer.getChildren().add(dependencyNode);
            dependenciesMap.put(dependency, dependencyNode);
        });

        //Enable or disable whether you can change the creator
        if (isCreationMode) {
            Person modelCreator = getModel().getCreator();
            creatorChoiceBox.getItems().clear();
            creatorChoiceBox.getItems().addAll(PersistenceManager.getCurrent().getCurrentModel().getPeople());
            if (modelCreator != null) {
                creatorChoiceBox.getSelectionModel().select(modelCreator);
            }
        }
        else {
            creatorChoiceBox.getItems().clear();
            creatorChoiceBox.getItems().add(getModel().getCreator());
            creatorChoiceBox.getSelectionModel().select(getModel().getCreator());
            creatorChoiceBox.setDisable(true);
        }

        String currentEstimation = getModel().getEstimate();
        Backlog backlog = (Backlog) UsageHelper.findUsages(getModel())
                .stream()
                .filter(model -> model instanceof Backlog)
                .findFirst()
                .orElse(null);

        estimateChoiceBox.getItems().clear();
        estimateChoiceBox.getItems().add(EstimateType.NOT_ESTIMATED);
        if (backlog == null  || getModel().getAcceptanceCriteria().size() == 0) {
            estimateChoiceBox.getSelectionModel().select(0);
            estimateChoiceBox.setDisable(true);
        }
        else {
            estimateChoiceBox.setDisable(false);
            estimateChoiceBox.getItems().addAll(backlog.getEstimateType().getEstimates());
            estimateChoiceBox.getSelectionModel().select(currentEstimation);
        }

        updateAcceptanceCriteria();
        if (!getIsCreationWindow()) {
            super.setupSaveChangesButton();
        }
    }

    /**
     * Updates the list of acceptance criteria in the Table.
     */
    private void updateAcceptanceCriteria() {
        //store selection
        AcceptanceCondition selected = acceptanceCriteriaTable.getSelectionModel().getSelectedItem();

        //Load the acceptance conditions
        acceptanceCriteriaTable.getItems().clear();
        acceptanceCriteriaTable.getItems().addAll(getModel().getAcceptanceCriteria());

        //restore selection
        acceptanceCriteriaTable.getSelectionModel().select(selected);
        refreshPriorityButtons();
    }

    /**
     * Refreshes the priority buttons so they have the correct enable state.
     */
    private void refreshPriorityButtons() {
        //Enable both buttons, we'll turn them off if we have to
        increasePriorityButton.setDisable(false);
        decreasePriorityButton.setDisable(false);

        AcceptanceCondition selected = acceptanceCriteriaTable.getSelectionModel().getSelectedItem();

        //If nothing is selected then both buttons should be disabled
        if (selected == null || getModel().getAcceptanceCriteria().size() == 0) {
            increasePriorityButton.setDisable(true);
            decreasePriorityButton.setDisable(true);
            return;
        }


        // and this is the first item priority wise, we can't increase its priority
        if (selected == getModel().getAcceptanceCriteria().get(0)) {
            increasePriorityButton.setDisable(true);
        }

        //If this is the last item, we can't go down
        if (selected == getModel().getAcceptanceCriteria().get(getModel().getAcceptanceCriteria().size() - 1)) {
            decreasePriorityButton.setDisable(true);
        }
    }

    @FXML
    @Override
    public final void initialize() {
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue) {
                saveChanges();
            }
        });
        searchableComboBoxDecorator = new SearchableComboBox(dependenciesDropDown);
        dependenciesMap = new HashMap<>();

        shortNameTextField.focusedProperty().addListener(getChangeListener());
        descriptionTextArea.focusedProperty().addListener(getChangeListener());
        creatorChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        estimateChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        storyStateChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
        dependenciesDropDown.valueProperty().addListener(getChangeListener());

        acceptanceCriteriaTable.getSelectionModel().selectedItemProperty().addListener(c -> refreshPriorityButtons());
        conditionColumn.setCellFactory(param -> new AcceptanceConditionCell());
        removeColumn.setCellFactory(param -> new RemoveButtonCell());
    }

    @Override
    public final void dispose() {
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        descriptionTextArea.focusedProperty().removeListener(getChangeListener());
        creatorChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        estimateChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        storyStateChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        dependenciesDropDown.valueProperty().removeListener(getChangeListener());
        searchableComboBoxDecorator.dispose();
        searchableComboBoxDecorator = null;
        dependenciesMap = null;
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

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNullOrNotEqual(modelDescription, viewDescription)) {
            getModel().setDescription(viewDescription);
        }

        updateStoryState();

        if (isCreationMode) {
            Person modelCreator = getModel().getCreator();
            Person viewCreator = (Person) creatorChoiceBox.getValue();
            if (viewCreator != null) {
                getModel().setCreator(viewCreator);
            } else {
                addFormError(creatorChoiceBox, "Creator cannot be empty");
            }
        }

        if (estimateChoiceBox.getValue() != null && getModel().getEstimate() != estimateChoiceBox.getValue()) {
            getModel().setEstimate((String) estimateChoiceBox.getValue());
            // Updates the story state as this gets changed if you set the estimate to Not Estimated
            storyStateChoiceBox.setValue(getModel().getStoryState());
        }

        Story selectedStory = dependenciesDropDown.getValue();
        if (selectedStory != null) {
            try {
                getModel().addDependency(selectedStory);
                Node dependencyNode = generateStoryNode(selectedStory);
                dependenciesContainer.getChildren().add(dependencyNode);
                dependenciesMap.put(selectedStory, dependencyNode);
                Platform.runLater(() -> {
                    searchableComboBoxDecorator.remove(selectedStory);
                    dependenciesDropDown.getSelectionModel().clearSelection();
                });
            } catch (CustomException e) {
                addFormError(dependenciesDropDown, e.getMessage());
            }
        }
    }

    /**
     * Checks to see if the current story state is valid and
     * displays an error if it isn't.
     */
    private void updateStoryState() {
        Story.StoryState state = (Story.StoryState) storyStateChoiceBox.getSelectionModel().getSelectedItem();
        Story model = getModel();
        boolean hasErrors = false;

        if (state == Story.StoryState.Ready) {
            if (getModel().getAcceptanceCriteria().size() == 0) {
                addFormError(storyStateChoiceBox, "The story must have at least one AC to set the state to Ready");
                hasErrors = true;
            }
            if (UsageHelper.findUsages(model).stream().noneMatch(m -> m instanceof Backlog)) {
                addFormError(storyStateChoiceBox, "The story must be part of a backlog to set the state to Ready");
                hasErrors = true;
            }
            if (model.getEstimate().equals(EstimateType.NOT_ESTIMATED)) {
                addFormError(storyStateChoiceBox, "The story must be estimated to set the state to Ready");
                hasErrors = true;
            }
        }

        if (!hasErrors) {
            getModel().setStoryState((Story.StoryState) storyStateChoiceBox.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Generate a new node for a story dependency.
     * @param newDependency story to generate a node for.
     * @return a JavaFX node representing the dependency.
     */
    private Node generateStoryNode(final Story newDependency) {
        MaterialDesignButton removeButton = new MaterialDesignButton("X");
        removeButton.getStyleClass().add("mdr-button");
        removeButton.getStyleClass().add("mdrd-button");
        removeButton.setOnAction(event -> {
            GenericPopup popup = new GenericPopup();
            popup.setMessageText("Are you sure you want to remove the dependency "
                    + newDependency.getShortName() + " from "
                    + getModel().getShortName() + "?");
            popup.setTitleText("Remove Dependency");
            popup.addYesNoButtons(func -> {
                searchableComboBoxDecorator.add(newDependency);
                Node dependencyNode = dependenciesMap.get(newDependency);
                dependenciesContainer.getChildren().remove(dependencyNode);
                dependenciesMap.remove(newDependency);
                getModel().removeDependency(newDependency);
                popup.close();
            });
            popup.show();
        });

        GridPane pane = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        column1.fillWidthProperty().setValue(true);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.SOMETIMES);

        ColumnConstraints column3 = new ColumnConstraints();
        column3.setHgrow(Priority.SOMETIMES);

        pane.getColumnConstraints().add(column1);
        pane.getColumnConstraints().add(column2);
        pane.getColumnConstraints().add(column3);

        if (getIsCreationWindow()) {
            Text nameText = new Text(newDependency.toString());
            pane.add(nameText, 0, 0);
        }
        else {
            Hyperlink nameLink = new Hyperlink(newDependency.toString());
            nameLink.setOnAction(a -> NavigationManager.navigateTo(newDependency));
            pane.add(nameLink, 0, 0);
        }
        String depth = "(" + Integer.toString(DependenciesHelper.dependenciesDepth(newDependency)) + " deep) ";
        Text depthText = new Text(depth);
        pane.add(depthText, 1, 0);
        pane.add(removeButton, 2, 0);
        GridPane.setMargin(removeButton, new Insets(1, 1, 1, 0));

        return pane;
    }

    /**
     * Called when the "Add Condition" button is clicked. Adds the Acceptance Condition
     * created by the user
     * @param event The event information
     */
    @FXML
    protected final void addConditionButtonClicked(final ActionEvent event) {
        String conditionText = addConditionTextField.getText();

        //Create a new condition
        AcceptanceCondition newCondition = new AcceptanceCondition();
        try {
            newCondition.setCondition(conditionText);
        } catch (CustomException e) {
            addFormError(addACButton, e.getMessage());
            return;
        }

        //Add the new condition to the model
        getModel().addAcceptanceCondition(newCondition);

        //Clear the acceptance condition box
        addConditionTextField.setText("");

        //Make sure that the table gets updated
        loadObject();

        //Select the item we just created
        acceptanceCriteriaTable.getSelectionModel().select(newCondition);
    }

    /**
     * Decreases the priority of a selected row in the table.
     * @param event the event information
     */
    @FXML
    protected final void increasePriorityClicked(final ActionEvent event) {
        //Get the selected item and move it up one place
        AcceptanceCondition condition = acceptanceCriteriaTable.getSelectionModel().getSelectedItem();
        moveCondition(condition, -1);
    }

    /**
     * Increases the priority of a selected row in the table.
     * @param event the event information
     */
    @FXML
    protected final void decreasePriorityClicked(final ActionEvent event) {
        //Get the selected item and move it down one place
        AcceptanceCondition condition = acceptanceCriteriaTable.getSelectionModel().getSelectedItem();
        moveCondition(condition, 1);
    }

    /**
     * Moves a condition down the list of Acceptance Criteria by a specified number of places (the number of
     * places wraps).
     * @param condition The condition to move
     * @param places The number of places to move it.
     */
    public final void moveCondition(final AcceptanceCondition condition, final int places) {
        //Get the current index of the AC
        int index = getModel().getAcceptanceCriteria().indexOf(condition);

        //If the item is not in the list, return
        if (index == -1) {
            return;
        }

        index += places;

        //Wrap the index.
        while (index < 0) {
            index += getModel().getAcceptanceCriteria().size();
        }
        while (index >= getModel().getAcceptanceCriteria().size()) {
            index -= getModel().getAcceptanceCriteria().size();
        }

        //Reposition the item to our calculated index in the model
        getModel().repositionCondition(condition, index);

        //Update the ACs in the table
        updateAcceptanceCriteria();
    }

    /**
     * A cell representing an acceptance condition in the table of conditions.
     */
    private class AcceptanceConditionCell extends TableCell<AcceptanceCondition, Object> {
        @Override
        protected void updateItem(final Object unused, final boolean empty) {
            super.updateItem(unused, empty);

            //Store the acceptance condition for this row
            final AcceptanceCondition condition = (AcceptanceCondition) getTableRow().getItem();

            if (condition == null || empty) {
                setText(null);
                setGraphic(null);
                return;
            }

            TextField conditionTextField = new TextField();
            //Add a change listener
            conditionTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                //If nothing has changed or we received focus we don't have to do anything
                if (oldValue == newValue || newValue == null) {
                    return;
                }

                //Update the text of the condition
                try {
                    condition.setCondition(conditionTextField.getText());
                } catch (CustomException e) {
                    addFormError(conditionTextField, e.getMessage());
                }
                conditionTextField.setText(condition.getCondition());
            });
            conditionTextField.setText(condition.getCondition());
            setGraphic(conditionTextField);
        }
    }

    /**
     * A RemoveButtonCell that contains the button used to remove an AcceptanceCondition from a story.
     */
    private class RemoveButtonCell extends TableCell<AcceptanceCondition, Object> {
        @Override
        protected void updateItem(final Object unused, final boolean empty) {
            super.updateItem(unused, empty);
            AcceptanceCondition condition = (AcceptanceCondition) getTableRow().getItem();

            if (condition == null || empty) {
                setText(null);
                setGraphic(null);
                return;
            }

            Button button = new Button("X");
            button.getStyleClass().add("mdr-button");
            button.getStyleClass().add("mdrd-button");
            button.setOnAction(event -> {
                GenericPopup popup = new GenericPopup();
                popup.setTitleText("Are you sure?");
                popup.setMessageText("Are you sure you wish to remove this acceptance condition?");
                popup.addYesNoButtons(p -> {
                    getModel().removeAcceptanceCondition(condition);
                    loadObject();
                    popup.close();
                });
                popup.show();
            });
            setGraphic(button);
        }
    }
}
