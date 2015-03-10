package sws.project.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

/**
* 11/03/2015
* @author Dion
*/
public class AppController {


    @FXML
    MenuItem fileQuit;

    @FXML
private void fileQuitPress(ActionEvent event) {
        Platform.exit();
    }
}
