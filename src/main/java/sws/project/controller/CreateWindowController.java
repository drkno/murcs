package sws.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.concurrent.Callable;

/**
 *
 */
public class CreateWindowController {
    private Callable<Void> okayClicked;
    private Callable<Void> cancelClicked;

    @FXML
    GridPane contentPane;

    @FXML
    private void cancelButtonClicked(ActionEvent event) {
        if (cancelClicked != null) try {
            cancelClicked.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stage stage = (Stage)contentPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void okayButtonClicked(ActionEvent event) {
        if (okayClicked != null) try {
            okayClicked.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stage stage = (Stage)contentPane.getScene().getWindow();
        stage.close();
    }

    public void setCancelClicked(Callable<Void> cancelClicked) {
        this.cancelClicked = cancelClicked;
    }

    public void setOkayClicked(Callable<Void> okayClicked) {
        this.okayClicked = okayClicked;
    }

    public void setContent(Node content){
        contentPane.add(content, 0, 0);
    }

    public static Parent newCreateNode(Node content, Callable<Void> okayClicked, Callable<Void> cancelClicked){
        try {
            FXMLLoader loader = new FXMLLoader(CreateWindowController.class.getResource("/sws/project/CreatorWindow.fxml"));
            Parent root = loader.load();

            CreateWindowController controller = loader.getController();
            controller.setOkayClicked(okayClicked);
            controller.setCancelClicked(cancelClicked);
            controller.setContent(content);

            return root;
        }catch (Exception e){
            System.err.println("Unable to create a project editor!(this is seriously bad)");
            e.printStackTrace();
        }
        return null;
    }
}
