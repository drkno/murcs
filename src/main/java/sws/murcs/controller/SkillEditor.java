package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Skill;
import sws.murcs.model.persistence.PersistenceManager;

/**
 * A controller to edit skills
 */
public class SkillEditor extends GenericEditor<Skill> {

    @FXML
    TextField shortNameTextField, longNameTextField;
    @FXML
    TextArea descriptionTextArea;
    @FXML
    Label labelErrorMessage;
    @FXML
    AnchorPane editor;

    /**
     * Saves the edit being edited
     */
    public void update()  throws Exception{
        String shortName = shortNameTextField.getText();
        if (shortName == null || edit.getShortName() == null || !shortName.equals(edit.getShortName())) {
            edit.setShortName(shortName);
        }

        String longName = longNameTextField.getText();
        if (longName == null || edit.getLongName() == null || !longName.equals(edit.getLongName())) {
            edit.setLongName(longName);
        }

        String description = descriptionTextArea.getText();
        if (description == null || edit.getDescription() == null || !description.equals(edit.getDescription())) {
            edit.setDescription(descriptionTextArea.getText());
        }
    }

    /**
     * Updates the object in memory and handles any exception
     */
    public void updateAndHandle(){
        try {
            labelErrorMessage.setText("");
            update();
        }
        catch (CustomException e) {
            labelErrorMessage.setText(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            //Output any other exception to the console
        }
    }

    /**
     * Loads the edit into the form
     */
    public void load() {
        updateFields();

        //if 'edit' is ScrumMaster or PO
        //  then disable the short name as this should be unique but allow the editing of the long name and description
        if (edit.getShortName() != null && (edit.getShortName().equals(Skill.ROLES.PO.toString()) || edit.getShortName().equals(Skill.ROLES.SM.toString())))
            shortNameTextField.setDisable(true);
    }

    /**
     * Sets the fields in the editing pane if and only if they are different to the current values.
     * Done so that Undo/Redo can update the editing pane without losing current selection.
     */
    public void updateFields() {
        String currentShortName = shortNameTextField.getText();
        String currentLongName = longNameTextField.getText();
        String currentDescription = descriptionTextArea.getText();

        if (edit.getShortName() != null && !currentShortName.equals(edit.getShortName())) {
            shortNameTextField.setText(edit.getShortName());
        }
        if (edit.getLongName() != null && !currentLongName.equals(edit.getLongName())) {
            longNameTextField.setText(edit.getLongName());
        }
        if (edit.getDescription() != null && !currentDescription.equals(edit.getShortName())) {
            descriptionTextArea.setText(edit.getDescription());
        }
    }

    @FXML
    public void initialize() {
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                updateAndHandle();
        });

        longNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                updateAndHandle();
        });

        descriptionTextArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)
                updateAndHandle();
        });
    }
}
