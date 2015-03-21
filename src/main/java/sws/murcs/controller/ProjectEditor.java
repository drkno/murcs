package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    public void update() {
        try {
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
                onSaved.call();

        }
        catch (Exception e) {
            labelErrorMessage.setText(e.getMessage());
        }
    }

    /**
     * Loads the edit into the form
     */
    public void load(){
        projectTextFieldShortName.setText(edit.getShortName());
        textFieldLongName.setText(edit.getLongName());
        descriptionTextField.setText(edit.getDescription());
        update();
    }

    /**
     * Initializes the editor for use, sets up listeners etc.
     */
    @FXML
    public void initialize() {
        projectTextFieldShortName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) update();
        });

        textFieldLongName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) update();
        });

        descriptionTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) update();
        });
    }
}
