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
        //Load the acceptance conditions
        acceptanceCriteriaTable.getItems().clear();
        acceptanceCriteriaTable.getItems().addAll(getModel().getAcceptanceCriteria());
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
     * A cell representing an acceptance condition in the table of conditions
     */
    private class AcceptanceConditionCell extends TableCell<AcceptanceCondition, Object>{
        @Override
        protected void updateItem(final Object unused, final boolean empty){
            super.updateItem(unused, empty);

            AcceptanceCondition condition = (AcceptanceCondition) getTableRow().getItem();
            if (condition == null || empty){
                setText(null);
                setGraphic(null);
                return;
            }

            TextField conditionTextField = new TextField();
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
