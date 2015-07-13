package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sws.murcs.exceptions.CustomException;
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
        super.setupSaveChangesButton();
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

        String viewDescription = descriptionTextArea.getText();
        String modelDescription = getModel().getDescription();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextArea.setText(modelDescription);
        }

        // if 'model' is ScrumMaster or PO
        // then disable the short name
        // as this should be unique
        // but allow the editing of the long name and description
        shortNameTextField.setDisable(false);
        if (modelShortName != null
                && (modelShortName.equals(Skill.ROLES.PO.toString())
                || modelShortName.equals(Skill.ROLES.SM.toString()))) {
            shortNameTextField.setDisable(true);
        }
    }

    @Override
    public final void dispose() {
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        longNameTextField.focusedProperty().removeListener(getChangeListener());
        descriptionTextArea.focusedProperty().removeListener(getChangeListener());
        super.dispose();
    }

    @Override
    protected final void saveChangesAndErrors() {
        String modelShortName =  getModel().getShortName();
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
            getModel().setLongName(viewLongName); //This is always valid
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextArea.getText();
        if (isNullOrNotEqual(modelDescription, viewDescription)) {
            getModel().setDescription(viewDescription); //This is always valid
        }
    }
}
