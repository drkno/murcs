package sws.project.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

/**
* 11/03/2015
* @author Dion
*/
public class AppController {

    @FXML
    MenuItem fileQuit;

    @FXML
    VBox sideDisplay;

    @FXML
    private void fileQuitPress(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void toggleItemListView(ActionEvent event) {
        if (sideDisplay.isVisible()) {
            sideDisplay.setVisible(false);
        }
        else {
            sideDisplay.setVisible(true);
        }
    }
}
