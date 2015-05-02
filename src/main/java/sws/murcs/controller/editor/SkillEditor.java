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
    /**
     * The skill to edit.
     */
    private Skill model;

    @FXML
    @Override
    public final void initialize() {
        shortNameTextField.focusedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (oldValue && !newValue) {
                        saveChanges();
                    }
                });

        longNameTextField.focusedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (oldValue && !newValue) {
                        saveChanges();
                    }
                });

        descriptionTextArea.focusedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (oldValue && !newValue) {
                        saveChanges();
                    }
                });

        setErrorCallback(message -> {
            if (message.getClass() == String.class) {
                labelErrorMessage.setText(message);
            }
        });
        this.model = super.getModel();
    }

    @Override
    public final void loadObject() {
        String modelShortName = model.getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        String modelLongName = model.getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqual(modelLongName, viewLongName)) {
            longNameTextField.setText(modelLongName);
        }

        String viewDescription = descriptionTextArea.getText();
        String modelDescription = model.getDescription();
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
    }

    @Override
    public final void dispose() {
        model = null;
        UndoRedoManager.removeChangeListener(this);
        super.setModel(null);
        this.setErrorCallback(null);
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        String modelShortName =  model.getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelShortName, viewShortName)) {
            model.setShortName(viewShortName);
        }

        String modelLongName = model.getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelLongName, viewLongName)) {
            model.setLongName(viewLongName);
        }

        String modelDescription = model.getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNotEqualOrIsEmpty(modelDescription, viewDescription)) {
            model.setDescription(viewDescription);
        }
    }
}
