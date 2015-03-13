package sws.project.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import sws.project.magic.easyedit.EditFormGenerator;
import sws.project.model.Model;
import sws.project.model.Project;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;
import sws.project.view.App;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main app class controller
 * 11/03/2015
 */
public class AppController implements Initializable {

    @FXML Parent root;
    @FXML MenuItem fileQuit, newProjectMenuItem;
    @FXML VBox vBoxSideDisplay;
    @FXML HBox hBoxMainDisplay;
    @FXML BorderPane borderPaneMain;
    @FXML ChoiceBox displayChoiceBox;
    @FXML ListView displayList;

    @FXML
    GridPane contentPane;

    private ObservableList displayListItems;

    /***
     * Initialises the GUI, setting up the the options in the choice box and populates the display list if necessary.
     * Put all initialisation of GUI in this function.
     * @param location Location of the fxml that is related to the controller
     * @param resources Pretty sure it's probably something, don't know what though
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayListItems = FXCollections.observableArrayList();
        displayList.setItems(displayListItems);

        for (ModelTypes type : ModelTypes.values()) {
            displayChoiceBox.getItems().add(type);
        }
        displayChoiceBox.getSelectionModel().selectedIndexProperty().addListener((ov, v, nv) -> {
            updateDisplayList(ModelTypes.getModelType(nv.intValue()));
        });

        displayChoiceBox.getSelectionModel().select(0);
        displayList.getSelectionModel().selectedItemProperty().addListener((p, o, n)->{
            contentPane.getChildren().clear();
            if (n == null) return;

            Parent pane = null;
            try {
                pane = EditFormGenerator.generatePane(n);
            } catch (Exception e) {
                //This isn't really something the user should have to deal with
                e.printStackTrace();
            }
            contentPane.getChildren().add(pane);
        });
    }

    /***
     * Updates the display list on the left hand side of the screen to the type selected in the choice box.
     * @param type The type selected in the choice box.
     */
    private void updateDisplayList(ModelTypes type) {
        displayListItems.clear();

        RelationalModel model = PersistenceManager.Current.getCurrentModel();
        if (model == null) return;
        switch (type) {
            case Project:
                Project project = model.getProject();
                if (project != null) {
                    displayListItems.add(project);
                }
                break;
            case People:
                break;
            case Team:
                break;
            case Skills:
                break;
        }
    }

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

    /***
     * Create a new project, opens a dialog to fill out for the new project.
     * @param event The event that causes the function to be called, namely clicking new project.
     */
    @FXML
    private void createNewProject(ActionEvent event) {
        CreateProjectPopUpController.displayPopUp(() -> {
            updateDisplayList(ModelTypes.Project);
            return null;
        });
    }

    /***
     * Save the current project. Currently you choose where to save the project every time, however it does remember the
     * last location saved or loaded from.
     * @param event The event that causes this function to be called, namely clicking save.
     */
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
            GenericPopup popup = new GenericPopup(e);
            popup.show();
        }
    }

    /***
     * Opens a specified project file, from a specified location.
     * @param event The event that causes the function to be called, clicking open.
     */
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
                RelationalModel model = PersistenceManager.Current.loadModel(file.getName());
                PersistenceManager.Current.setCurrentModel(model);
            }
            updateDisplayList(ModelTypes.Project);
        }
        catch (Exception e) {
            GenericPopup popup = new GenericPopup(e);
            popup.show();
        }
    }
}
