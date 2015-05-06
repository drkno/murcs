package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Skill;

/**
 * A controller to model skills.
 */
public class SkillEditor extends GenericEditor<Skill> {

    /**
     * The shortName and longName fields of a skill.
     */
    @FXML
    private TextField shortNameTextField, longNameTextField;
    /**
     * The description of a skill.
     */
    @FXML
    private TextArea descriptionTextArea;
    /**
     * The label for showing error messages.
     */
    @FXML
    private Label labelErrorMessage;

    @FXML
    @Override
    public final void initialize() {
        this.setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        });

        shortNameTextField.focusedProperty().addListener(this.getChangeListener());
        longNameTextField.focusedProperty().addListener(this.getChangeListener());
        descriptionTextArea.focusedProperty().addListener(this.getChangeListener());

        setErrorCallback(message -> {
            if (message.getClass() == String.class) {
                labelErrorMessage.setText(message);
            }
        });
    }

    @Override
    public final void loadObject() {
        String modelShortName = this.getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        String modelLongName = this.getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqual(modelLongName, viewLongName)) {
            longNameTextField.setText(modelLongName);
        }

        String viewDescription = descriptionTextArea.getText();
        String modelDescription = this.getModel().getDescription();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextArea.setText(modelDescription);
        }

        // if 'model' is ScrumMaster or PO
        // then disable the short name
        // as this should be unique
        // but allow the editing of the long name and description
        if (modelShortName != null
                && (modelShortName.equals(Skill.ROLES.PO.toString())
                || modelShortName.equals(Skill.ROLES.SM.toString()))) {
            shortNameTextField.setDisable(true);
        }

        //hack set the error text to nothing when first loading the object
        labelErrorMessage.setText(" ");
    }

    @Override
    public final void dispose() {
        shortNameTextField.focusedProperty().removeListener(this.getChangeListener());
        longNameTextField.focusedProperty().removeListener(this.getChangeListener());
        descriptionTextArea.focusedProperty().removeListener(this.getChangeListener());
        this.setChangeListener(null);
        UndoRedoManager.removeChangeListener(this);
        this.setModel(null);
        this.setErrorCallback(null);
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        String modelShortName =  this.getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelShortName, viewShortName)) {
            this.getModel().setShortName(viewShortName);
        }

        String modelLongName = this.getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelLongName, viewLongName)) {
            this.getModel().setLongName(viewLongName);
        }

        String modelDescription = this.getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNotEqualOrIsEmpty(modelDescription, viewDescription)) {
            this.getModel().setDescription(viewDescription);
        }
    }
}
