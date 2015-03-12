package sws.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.project.view.App;

import java.io.IOException;

/**
 * Controller for the project creator popup window.
 * Since there should only be one instance of this PopUp
 *
 * Created by Haydon on 11/03/2015.
 */
public class CreateProjectPopUpController {

    private static Stage stage;

    @FXML
    static TextField textFieldShortName, textFieldLongName;

    @FXML
    static TextArea textAreaDescription;

    @FXML
    private void buttonActionCancel(ActionEvent event) {
        stage.close();

    }

    @FXML
    private void buttonActionOK(ActionEvent event) {
    }

    private void clearFields() {

    }

    public static void displayPopUp() {
        try {
            AnchorPane anchorPane = FXMLLoader.load(CreateProjectPopUpController.class.getResource("/sws/project/CreateProjectPopUp.fxml"));
            stage = new Stage();
            stage.setScene(new Scene(anchorPane));
            stage.setTitle("Create New Project");
            //stage.initModality(Modality.APPLICATION_MODAL);
            //stage.initOwner(App.stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
