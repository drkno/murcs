package sws.project.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.project.view.App;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * Created by James on 12/03/2015.
 */
public class PopupController extends AnchorPane {

    public enum Controls {
        OK,
        OKCANCEL,
        CANCEL
    }

    private @FXML Button okButton;
    private @FXML Button cancelButton;
    private @FXML Text message;

    private Stage popupStage;

    public PopupController(String title) {
        popupStage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/project/Popup.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO catch this nicely
        }

        Scene popupScene = new Scene(this, 400, 200);
        popupStage.setTitle(title);
        popupStage.initOwner(App.stage);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(popupScene);
        popupStage.hide();
    }

    public void show() {
        popupStage.show();
    }

    public void close() {
        popupStage.close();
    }

    public void setMessage(String text) {
        message.setText(text);
    }

    public Button getOkButton() {
        return okButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public void setOkAction(Callable<Void> func) {
        okButton.setOnAction((a) -> {
            try {
                func.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
