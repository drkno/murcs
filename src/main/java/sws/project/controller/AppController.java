package sws.project.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
* 11/03/2015
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

    /***
     * Called when the Quit button is pressed in the file menu and quit the current application.
     * @param event The even that triggers the function
     */
    @FXML
    private void fileQuitPress(ActionEvent event) {
        Platform.exit();
    }

    /***
     * Toggles the view of the display list box at the side.
     * @param event The event that triggers the function
     */
    @FXML
    private void toggleItemListView(ActionEvent event) {
        if (vBoxSideDisplay.isVisible()) {
            vBoxSideDisplay.managedProperty().bind(vBoxSideDisplay.visibleProperty());
            vBoxSideDisplay.setVisible(false);
        }
        else {
            vBoxSideDisplay.managedProperty().bind(vBoxSideDisplay.visibleProperty());
            vBoxSideDisplay.setVisible(true);
        }

    }
}
