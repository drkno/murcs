package sws.project.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sws.project.model.persistence.loaders.FilePersistenceLoader;
import sws.project.view.App;

import java.io.File;

/**
 * Main app class controller
 * 11/03/2015
 */
public class AppController {

    @FXML Parent root;
    @FXML MenuItem fileQuit, newProjectMenuItem;
    @FXML VBox vBoxSideDisplay;
    @FXML HBox hBoxMainDisplay;
    @FXML BorderPane borderPaneMain;

    private Node removedDisplay;
    private boolean showHide = true;

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

    @FXML
    private void createNewProject(ActionEvent event) {

    }

    @FXML
    private void saveProject(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Project");
        File file = fileChooser.showSaveDialog(App.stage);
        if (file != null) {
            try {
                App.persistenceManager.savePersistence(App.model);
            }catch (Exception e) {
                //May it burn in hell
            }
        }
    }

    @FXML
    private void openProject(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Project");
        File file = fileChooser.showOpenDialog(App.stage);
        if (file != null) {
            App.persistenceManager.setPersistenceLoader(new FilePersistenceLoader(file.getAbsolutePath()));
            App.persistenceManager.loadModel("Model");
        }
    }
}
