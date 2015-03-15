package sws.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.project.model.Project;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;
import sws.project.view.App;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Controller for the project creator popup window.
 * Since there should only be one instance of this PopUp
 */
public class CreateProjectPopUpController {

    @FXML
    TextField textFieldShortName, textFieldLongName;

    @FXML
    TextArea textAreaDescription;

    @FXML
    Label labelErrorMessage;

    public Callable completedCallback;

    private static Stage stage;

    /**
     * Creates a dialog for entering information about a new project. Takes one parameter which is the function to be
     * called after the new project has been made (usually to update the display list)
     * @param func The function that you wish to be called after the new project has been made.
     */
    public static void displayProjectPopUp(Callable<Void> func) {
        try {
            FXMLLoader loader = new FXMLLoader(CreateProjectPopUpController.class.getResource("/sws/project/CreateProjectPopUp.fxml"));
            AnchorPane anchorPane = loader.load();

            CreateProjectPopUpController controller = loader.getController();
            controller.completedCallback = func;
            stage = new Stage();
            stage.setScene(new Scene(anchorPane));
            stage.setTitle("Create New Project");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(App.stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Sets up a new project when the ok button is clicked with the information entered. If the short name is nothing
     * then it will give an error in the create project dialog and the project will not be saved.
     * @param event The event sent by clicking the ok button in the dialog.
     */
    @FXML
    private void buttonActionOK(ActionEvent event) {
        try {
            Project project = new Project();
            project.setShortName(textFieldShortName.getText());
            project.setLongName(textFieldLongName.getText());
            project.setDescription(textAreaDescription.getText());

            RelationalModel model = new RelationalModel();
            model.setProject(project);
            PersistenceManager.Current.setCurrentModel(model);

            clearFields();
            stage.close();
            completedCallback.call();
        } catch (Exception e) {
            labelErrorMessage.setText(e.getMessage());
        }
    }

    /***
     * Sets the dialog to close if the user clicks cancel.
     * @param event The event sent by clicking the cancel button.
     */
    @FXML
    private void buttonActionCancel(ActionEvent event) {
        stage.close();
    }

    /***
     * Clears all of the fields (short name, long name, description)
     */
    private void clearFields() {
        textFieldShortName.setText("");
        textFieldLongName.setText("");
        textAreaDescription.setText("");
    }

}
