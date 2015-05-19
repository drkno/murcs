package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Backlog;
import sws.murcs.model.Person;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Skill;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for the model creator popup window.
 * Since there should only be one instance of this PopUp
 */
public class BacklogEditor extends GenericEditor<Backlog> {

    /**
     *
     */
    @FXML
    private TextField shortNameTextField, longNameTextField;

    /**
     *
     */
    @FXML
    private TextArea descriptionTextArea;

    /**
     *
     */
    @FXML
    private ChoiceBox<Person> poChoiceBox;

    /**
     *
     */
    @FXML
    private Label labelErrorMessage;

    @FXML
    @Override
    public final void initialize() {
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        });

        shortNameTextField.focusedProperty().addListener(getChangeListener());
        longNameTextField.focusedProperty().addListener(getChangeListener());
        descriptionTextArea.focusedProperty().addListener(getChangeListener());
        poChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());

        setErrorCallback(message -> {
            if (message.getClass() == String.class) {
                labelErrorMessage.setText(message);
            }
        });
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

        updateAssignedPO();

        //fixme set the error text to nothing when first loading the object
        labelErrorMessage.setText(" ");
    }

    /**
     * Update the assigned PO.
     */
    private void updateAssignedPO() {
        RelationalModel relationalModel = PersistenceManager.Current.getCurrentModel();

        Person productOwner = getModel().getAssignedPO();

        // Add all the people with the PO skill to the list of POs
        List<Person> productOwners = relationalModel.getPeople()
                .stream()
                .filter(p -> p.canBeRole(Skill.PO_NAME))
                .collect(Collectors.toList());

        // Remove listener while editing the product owner picker
        poChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        poChoiceBox.getItems().clear();
        poChoiceBox.getItems().addAll(productOwners);
        if (poChoiceBox != null) {
            poChoiceBox.getSelectionModel().select(productOwner);
        }
        poChoiceBox.getSelectionModel().selectedItemProperty().addListener(getChangeListener());
    }

    @Override
    public final void dispose() {
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        longNameTextField.focusedProperty().removeListener(getChangeListener());
        poChoiceBox.getSelectionModel().selectedItemProperty().removeListener(getChangeListener());
        descriptionTextArea.focusedProperty().removeListener(getChangeListener());
        setChangeListener(null);
        UndoRedoManager.removeChangeListener(this);
        setModel(null);
        setErrorCallback(null);
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        Person modelProductOwner = getModel().getAssignedPO();
        Person viewProductOwner = poChoiceBox.getValue();
        if (isNullOrNotEqual(modelProductOwner, viewProductOwner)) {
            getModel().setAssignedPO(viewProductOwner);
            updateAssignedPO();
        }

        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNullOrNotEqual(modelShortName, viewShortName)) {
            getModel().setShortName(viewShortName);
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
    }
}
