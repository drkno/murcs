package sws.project.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
* 11/03/2015
* @author Dion
*/
public class AppController {

    @FXML
    MenuItem fileQuit;

    @FXML
    VBox vBoxSideDisplay;

    @FXML
    HBox hBoxMainDisplay;

    @FXML
    BorderPane borderPaneMain;

    Node removedDisplay;
    boolean showHide = true;

    @FXML
    private void fileQuitPress(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void toggleItemListView(ActionEvent event) {
        if (showHide) {
            removedDisplay = hBoxMainDisplay.getChildren().get(0);
            hBoxMainDisplay.getChildren().remove(0);
            showHide = false;
        }
        else {
            hBoxMainDisplay.getChildren().add(0, removedDisplay);
            showHide = true;
        }

    }
}
