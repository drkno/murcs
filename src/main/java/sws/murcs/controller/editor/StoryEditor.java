package sws.murcs.controller.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sws.murcs.controller.GenericPopup;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.AcceptanceCondition;
import sws.murcs.model.Person;
import sws.murcs.model.Story;
import sws.murcs.model.persistence.PersistenceManager;

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
     * A choice box for the creator.
     */
    @FXML
    private ChoiceBox creatorChoiceBox;

    /**
     * A label that indicates any errors.
     */
    @FXML
    private Label labelErrorMessage;

    /**
     * A table for displaying and updating acceptance conditions
     */
    @FXML
    private TableView<AcceptanceCondition> acceptanceCriteriaTable;

    /**
     * The columns on the AC table
     */
    @FXML
    private TableColumn conditionColumn, removeColumn;

    /**
     * Buttons for increasing and decreasing the priority of an AC
     */
    @FXML
    private Button increasePriorityButton, decreasePriorityButton;

    /**
     * The TextField containing the text for the new condition
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
        String viewShortName = shortNameTextField.getText();
        isCreationMode = modelShortName == null;
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextArea.setText(modelDescription);
        }

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

        updateAcceptanceCriteria();
    }

    /**
     * Updates the list of acceptance criteria in the Table
     */
    private void updateAcceptanceCriteria(){
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
     * Refreshes the priority buttons so they have the correct enable state
     */
    private void refreshPriorityButtons(){
        //Enable both buttons, we'll turn them off if we have to
        increasePriorityButton.setDisable(false);
        decreasePriorityButton.setDisable(false);

        AcceptanceCondition selected = acceptanceCriteriaTable.getSelectionModel().getSelectedItem();

        //If nothing is selected then both buttons should be disabled
        if (selected == null){
            increasePriorityButton.setDisable(true);
            decreasePriorityButton.setDisable(true);
            return;
        }

        //If something is selected we don't have to worry about having no items in the list

        //If this is the first item, we can't go up
        if (selected == getModel().getAcceptanceCriteria().get(0)){
            increasePriorityButton.setDisable(true);
        }

        //If this is the last item, we can't go down
        if (selected == getModel().getAcceptanceCriteria().get(getModel().getAcceptanceCriteria().size() - 1)){
            decreasePriorityButton.setDisable(true);
        }
    }

    @Override
    public final void initialize() {
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue) {
                saveChanges();
            }
        });

        shortNameTextField.focusedProperty().addListener(getChangeListener());
        descriptionTextArea.focusedProperty().addListener(getChangeListener());
        creatorChoiceBox.focusedProperty().addListener(getChangeListener());

        acceptanceCriteriaTable.getSelectionModel().selectedItemProperty().addListener(c -> refreshPriorityButtons());
        conditionColumn.setCellFactory(param -> new AcceptanceConditionCell());
        removeColumn.setCellFactory(param -> new RemoveButtonCell());

        setErrorCallback(message -> {
            if (message.getClass() == String.class) {
                labelErrorMessage.setText(message);
            }
        });
    }

    @Override
    public final void dispose() {
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        descriptionTextArea.focusedProperty().removeListener(getChangeListener());
        creatorChoiceBox.focusedProperty().removeListener(getChangeListener());
        setChangeListener(null);
        UndoRedoManager.removeChangeListener(this);
        setModel(null);
        setErrorCallback(null);
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNullOrNotEqual(modelShortName, viewShortName)) {
            getModel().setShortName(viewShortName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNullOrNotEqual(modelDescription, viewDescription)) {
            getModel().setDescription(viewDescription);
        }

        if (isCreationMode) {
            Person modelCreator = getModel().getCreator();
            Person viewCreator = (Person) creatorChoiceBox.getValue();
            if (viewCreator != null) {
                getModel().setCreator(viewCreator);
            } else {
                throw new InvalidParameterException("Creator cannot be empty");
            }
        }
    }

    /**
     * Called when the "Add Condition" button is clicked
     * @param event The event information
     */
    @FXML
    protected final void addConditionButtonClicked(final ActionEvent event){
        String conditionText = addConditionTextField.getText();
        //TODO perform some checks and do error handling on the condition text

        //Create a new condition
        AcceptanceCondition newCondition = new AcceptanceCondition();
        newCondition.setCondition(conditionText);

        //Add the new condition to the model
        getModel().addAcceptanceCondition(newCondition);

        //Clear the acceptance condition box
        addConditionTextField.setText("");

        //Make sure that the table gets updated
        updateAcceptanceCriteria();
    }

    /**
     * Decreases the priority of a selected row in the table
     * @param event the event information
     */
    @FXML
    protected final void increasePriorityClicked(final ActionEvent event){
        AcceptanceCondition condition = acceptanceCriteriaTable.getSelectionModel().getSelectedItem();
        moveCondition(condition, -1);
        updateAcceptanceCriteria();
    }

    /**
     * Increases the priority of a selected row in the table
     * @param event the event information
     */
    @FXML
    protected final void decreasePriorityClicked(final ActionEvent event){
        AcceptanceCondition condition = acceptanceCriteriaTable.getSelectionModel().getSelectedItem();
        moveCondition(condition, 1);
        updateAcceptanceCriteria();
    }

    /**
     * Moves a condition down the list of Acceptance Criteria by a specified number of places (the number of
     * places wraps)
     * @param condition The condition to move
     * @param places The number of places to move it.
     */
    public final void moveCondition(final AcceptanceCondition condition, final int places){
        int index = getModel().getAcceptanceCriteria().indexOf(condition);

        //If the item is not in the list, throw an exception
        if (index == -1) return;

        index += places;

        //Wrap the index.
        while (index < 0) {
            index += getModel().getAcceptanceCriteria().size();
        }
        while (index >= getModel().getAcceptanceCriteria().size()) {
            index -= getModel().getAcceptanceCriteria().size();
        }

        getModel().repositionCondition(condition, index);
        acceptanceCriteriaTable.getSelectionModel().select(condition);
    }

    /**
     * A cell representing an acceptance condition in the table of conditions
     */
    private class AcceptanceConditionCell extends TableCell<AcceptanceCondition, Object>{
        @Override
        protected void updateItem(final Object unused, final boolean empty){
            super.updateItem(unused, empty);

            //Store the acceptance condition for this row
            final AcceptanceCondition condition = (AcceptanceCondition) getTableRow().getItem();

            if (condition == null || empty){
                setText(null);
                setGraphic(null);
                return;
            }

            TextField conditionTextField = new TextField();
            //Add a change listener
            conditionTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                //If nothing has changed or we received focus we don't have to do anything
                if (oldValue == newValue || newValue) return;

                //Update the text of the condition
                condition.setCondition(conditionTextField.getText());
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
            button.setOnAction(event -> {
                GenericPopup popup = new GenericPopup();
                popup.setTitleText("Are you sure?");
                popup.setMessageText("Are you sure you wish to remove this acceptance condition?");
                popup.addYesNoButtons(p -> {
                    getModel().removeAcceptanceCriteria(condition);
                    updateAcceptanceCriteria();
                    popup.close();
                });
                popup.show();
            });
            setGraphic(button);
        }
    }
}
