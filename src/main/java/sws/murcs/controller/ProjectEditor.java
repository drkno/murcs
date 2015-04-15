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
        edit.setShortName(projectTextFieldShortName.getText());
        edit.setLongName(textFieldLongName.getText());
        edit.setDescription(descriptionTextField.getText());

        // Save the project if it hasn't been yet
        RelationalModel model = PersistenceManager.Current.getCurrentModel();

        }

        if (!model.getProjects().contains(edit))
            model.addProject(edit);

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
        }
    }

    /**
     * Loads the edit into the form
     */
    public void load(){
        projectTextFieldShortName.setText(edit.getShortName());
        textFieldLongName.setText(edit.getLongName());
        descriptionTextField.setText(edit.getDescription());
        updateAndHandle();
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
