package sws.project.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.project.view.App;

import java.util.concurrent.Callable;

/**
 * Created by James on 12/03/2015.
 */
public class OkCancelPopup extends AnchorPane {

    public enum Controls {
        OK,
        OKCANCEL,
        CANCEL
    }

    public enum Type {
        Message,
        Exception
    }

    private @FXML Button okButton;
    private @FXML Button cancelButton;
    private @FXML Text messageText;
    private @FXML Text messageTitle;
    private @FXML ImageView messageImage;

    private Stage popupStage;

    public OkCancelPopup(Exception e) {
        this(e.getClass().getName(), e.getStackTrace().toString(), e.getMessage());
    }

    public OkCancelPopup() {
        this("Ok Cancel Popup");
    }

    public OkCancelPopup(String windowTitle) {
        this(windowTitle, "There is a message here");
    }

    public OkCancelPopup(String windowTitle, String message) {
        this(windowTitle, message, null);
    }

    public OkCancelPopup(String windowTitle, String message, String messageTitle) {
        popupStage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/project/OkCancelPopupFXML.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO catch this nicely
        }

        Scene popupScene = new Scene(this, 400, 200);
        popupStage.setTitle(windowTitle);
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

    public void setMessageText(String message) {
        messageText.setText(message);
    }

    public void setWindowTitle(String title) {
        popupStage.setTitle(title);
    }

    public void setTitleText(String titleText) {
        messageTitle.setText(titleText);
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
                e.printStackTrace(); //Todo catch this as well
            }
        });
    }

    public void setCancelAction(Callable<Void> func) {
        cancelButton.setOnAction((a) -> {
            try {
                func.call();
            }catch (Exception e) {
                e.printStackTrace(); //Todo catch this as well
            }
        });
    }
}
