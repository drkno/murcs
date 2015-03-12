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
import sws.project.model.Person;
import sws.project.model.Project;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;
import sws.project.view.App;

import java.io.File;
import java.util.concurrent.Callable;

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
            //removedDisplay = hBoxMainDisplay.getChildren().get(0);
            //hBoxMainDisplay.getChildren().remove(0);
            hBoxMainDisplay.setVisible(false);
            showHide = false;
        }
        else {
            hBoxMainDisplay.setVisible(true);
            showHide = true;
        }
        PopupController controller = new PopupController("Test");
        controller.setOkAction(() -> {
            System.out.println("work already");
            controller.close();
            return null;
        });
        controller.setMessage("Hi there");
        controller.show();
    }

    @FXML
    private void createNewProject(ActionEvent event) {
        try {
            RelationalModel model = new RelationalModel();
            Project project = new Project();
            //TODO: GUI instantiate project here
            model.setProject(project);
            PersistenceManager.Current.setCurrentModel(model);
        }
        catch (Exception e) {
            // hell is coming
        }
    }

    @FXML
    private void saveProject(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Project");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Project File (*.project)", "*.project"));
            fileChooser.setInitialDirectory(new File(PersistenceManager.Current.getCurrentWorkingDirectory()));
            File file = fileChooser.showSaveDialog(App.stage);
            if (file != null) {
                PersistenceManager.Current.setCurrentWorkingDirectory(file.getParentFile().getAbsolutePath());
                PersistenceManager.Current.saveModel(file.getName());
            }
        }catch (Exception e) {
            //May it burn in hell
        }
    }

    @FXML
    private void openProject(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Project File (*.project)", "*.project"));
            fileChooser.setInitialDirectory(new File(PersistenceManager.Current.getCurrentWorkingDirectory()));
            fileChooser.setTitle("Select Project");
            File file = fileChooser.showOpenDialog(App.stage);
            if (file != null) {
                PersistenceManager.Current.setCurrentWorkingDirectory(file.getParentFile().getAbsolutePath());
                PersistenceManager.Current.loadModel(file.getName());
            }
        }
        catch (Exception e) {
            //May it burn in hell again
        }
    }
}
