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
        labelErrorMessage.setText("");
        edit.setShortName(projectTextFieldShortName.getText());
        edit.setLongName(textFieldLongName.getText());
        edit.setDescription(descriptionTextField.getText());

        //This line will need to be changed if we support multiple projects
        //What we're trying to do here is check if the current edit already exist
        //or if we're creating a new one.
        RelationalModel model = PersistenceManager.Current.getCurrentModel();
        if (model == null || model.getProject() != edit) {
            if (PersistenceManager.Current.getCurrentModel() == null) {
                model = new RelationalModel();
            }
            model.setProject(edit);

            PersistenceManager.Current.setCurrentModel(model);
        }

        //If we have a saved callBack, call it
        if (onSaved != null)
            onSaved.eventNotification(edit);
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
            //Don't show the user this.
            e.printStackTrace();
        }
    }

    /**
     * Loads the edit into the form
     */
    public void load(){
        updateFields();
        updateAndHandle();
    }

    /**
     * Sets the fields in the editing pane if and only if they are different to the current values.
     * Done so that Undo/Redo can update the editing pane without losing current selection.
     */
    public void updateFields() {
        String currentShortName = projectTextFieldShortName.getText();
        String currentLongName = textFieldLongName.getText();
        String currentDescription = descriptionTextField.getText();

        if (!currentShortName.equals(edit.getShortName())) {
            projectTextFieldShortName.setText(edit.getShortName());
        }
        if (!currentLongName.equals(edit.getLongName())) {
            textFieldLongName.setText(edit.getLongName());
        }
        if (!currentDescription.equals(edit.getShortName())) {
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
