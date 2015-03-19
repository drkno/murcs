package sws.project.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sws.project.model.Project;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the edit creator popup window.
 * Since there should only be one instance of this PopUp
 */
public class ProjectEditor extends GenericEditor<Project> implements Initializable {

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
            onSaved.call();
    }

    /**
     * Updates the object in memory and handles any exception that might be thrown
     */
    private void updateAndHandle(){
        try {
            update();
        }catch (Exception e){
            this.labelErrorMessage.setText(e.getMessage());
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectTextFieldShortName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        textFieldLongName.focusedProperty().addListener((p, o, n) -> {
            if (o && !n)  updateAndHandle();
        });

        descriptionTextField.focusedProperty().addListener((p, o, n) -> {
            if (o && !n)  updateAndHandle();
        });
    }
}
