package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.persistence.PersistenceManager;

/**
 * Controller for the edit creator popup window.
 * Since there should only be one instance of this PopUp
 */
public class ProjectEditor extends GenericEditor<Project> {

    @FXML
    private TextField projectTextFieldShortName, textFieldLongName, descriptionTextField;

    @FXML
    private Label labelErrorMessage;

    /**
     * Creates a new or updates the current edit being edited.
     */
    public void update() throws Exception {
        if (edit.getShortName() == null || !projectTextFieldShortName.getText().equals(edit.getShortName())) {
            edit.setShortName(projectTextFieldShortName.getText());
        }
        if (edit.getLongName() == null || !textFieldLongName.getText().equals(edit.getLongName())) {
            edit.setLongName(textFieldLongName.getText());
        }
        if (edit.getDescription() == null || !descriptionTextField.getText().equals(edit.getDescription())) {
            edit.setDescription(descriptionTextField.getText());
        }

        // Save the project if it hasn't been yet
        RelationalModel model = PersistenceManager.Current.getCurrentModel();

        if (!model.getProjects().contains(edit))
            model.addProject(edit);

        //If we have a saved callBack, call it
//        if (onSaved != null)
//            onSaved.updateListView(edit);
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
    public void load(){
        updateFields();
    }

    /**
     * Sets the fields in the editing pane if and only if they are different to the current values.
     * Done so that Undo/Redo can update the editing pane without losing current selection.
     */
    public void updateFields() {
        String currentShortName = projectTextFieldShortName.getText();
        String currentLongName = textFieldLongName.getText();
        String currentDescription = descriptionTextField.getText();
        if (edit.getShortName() != null && !currentShortName.equals(edit.getShortName())) {
            projectTextFieldShortName.setText(edit.getShortName());
        }
        if (edit.getLongName() != null && !currentLongName.equals(edit.getLongName())) {
            textFieldLongName.setText(edit.getLongName());
        }
        if (edit.getDescription() != null && !currentDescription.equals(edit.getShortName())) {
            descriptionTextField.setText(edit.getDescription());
        }
    }

    /**
     * Initializes the editor for use, sets up listeners etc.
     */
    @FXML
    public void initialize() {
        projectTextFieldShortName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        textFieldLongName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        descriptionTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });
    }
}
