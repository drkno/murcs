package sws.project.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.project.model.Project;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;
import sws.project.view.App;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
 * Controller for the project creator popup window.
 * Since there should only be one instance of this PopUp
 */
public class ProjectEditor implements Initializable {
    private Project project;

    @FXML
    TextField textFieldShortName, textFieldLongName, descriptionTextField;

    @FXML
    Label labelErrorMessage;

    private Callable<Void> onSaved;

    /***
     * Clears all of the fields (short name, long name, description)
     */
    private void clearFields() {
        textFieldShortName.setText("");
        textFieldLongName.setText("");
        descriptionTextField.setText("");
    }

    public static Parent createNew(){
        return createFor(null);
    }

    public static Parent createNew(Callable<Void> onSaved){
        return createFor(new Project(), null);
    }

    public static Parent createFor(Project project){
        return createFor(project, null);
    }

    public static Parent createFor(Project project, Callable<Void> onSaved){
        try {
            FXMLLoader loader = new FXMLLoader(ProjectEditor.class.getResource("/sws/project/ProjectEdit.fxml"));
            AnchorPane anchorPane = loader.load();

            ProjectEditor controller = loader.getController();
            controller.project = project;
            controller.onSaved = onSaved;
            return anchorPane;
        }catch (Exception e){
            System.err.println("Unable to create a project editor!(this is seriously bad)");
            e.printStackTrace();
        }

        return null;
    }

    public static void displayWindow(Callable<Void> savedCallback){
        try {
            Parent root = createNew(savedCallback);
            Scene scene = new Scene(root);

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Create Project");

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(App.stage);

            newStage.show();
        }catch (Exception e){

        }
    }

    private void saveProject() {
        try {
            project.setShortName(textFieldShortName.getText());
            project.setLongName(textFieldLongName.getText());
            project.setDescription(descriptionTextField.getText());


            //This line will need to be changed if we support multiple projects
            //What we're trying to do here is check if the current project already exist
            //or if we're creating a new one.
            RelationalModel model= PersistenceManager.Current.getCurrentModel();
            if (model == null || model.getProject() != project) {
                model = new RelationalModel();
                model.setProject(project);

                PersistenceManager.Current.setCurrentModel(model);
            }

            //If we have a saved callBack, call it
            if (onSaved != null)
                onSaved.call();

        }catch (Exception e){
            labelErrorMessage.setText(e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldShortName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) saveProject();
        });

        textFieldLongName.focusedProperty().addListener((p, o, n) -> {
            if (o && !n)  saveProject();
        });

        descriptionTextField.focusedProperty().addListener((p, o, n) -> {
            if (o && !n)  saveProject();
        });
    }
}
