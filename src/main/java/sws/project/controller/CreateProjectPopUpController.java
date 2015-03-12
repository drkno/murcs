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

/**
 * Controller for the project creator popup window.
 * Since there should only be one instance of this PopUp
 *
 * Created by Haydon on 11/03/2015.
 */
public class CreateProjectPopUpController {

    @FXML
    TextField textFieldShortName, textFieldLongName;

    @FXML
    TextArea textAreaDescription;

    @FXML
    Label labelErrorMessage;

    private static Stage stage;

    public static void displayPopUp() {
        try {
            AnchorPane anchorPane = FXMLLoader.load(CreateProjectPopUpController.class.getResource("/sws/project/CreateProjectPopUp.fxml"));
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

    @FXML
    private void buttonActionOK(ActionEvent event) {

        if (textFieldShortName.getText().length() == 0) {
            labelErrorMessage.setText("Short Name cannot be null");
        } else {
            Project project = new Project();
            project.setShortName(textFieldShortName.getText());
            project.setLongName(textFieldLongName.getText());
            project.setDescription(textAreaDescription.getText());

            RelationalModel model = new RelationalModel();
            model.setProject(project);
            PersistenceManager.Current.setCurrentModel(model);

            clearFields();
            stage.close();
        }
    }

    @FXML
    private void buttonActionCancel(ActionEvent event) {
        stage.close();
    }

    private void clearFields() {
        textFieldShortName.setText("");
        textFieldLongName.setText("");
        textAreaDescription.setText("");
    }

}
