package sws.project.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sws.project.magic.easyedit.EditFormGenerator;
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
    AnchorPane contentPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Object> list = displayChoiceBox.getItems();

        for (ModelTypes name : ModelTypes.values()) {
            list.add(name.toString());
        }
        displayChoiceBox.getSelectionModel().selectedIndexProperty().addListener((ov, v, nv) -> {
            updateDisplayList(ModelTypes.getModelType(nv.intValue()));
        });
        displayChoiceBox.getSelectionModel().select(0);
        displayList.getSelectionModel().selectedItemProperty().addListener((p, o, n)->{
            contentPane.getChildren().clear();
            if (n == null) return;

            Parent pane = EditFormGenerator.generatePane(n);
            contentPane.getChildren().add(pane);
        });
    }

    private void updateDisplayList(ModelTypes type) {
        RelationalModel model = PersistenceManager.Current.getCurrentModel();
        if (model == null) return;
        switch (type) {
            case Project:
                Project project = model.getProject();
                if (project != null) {
                    displayList.getItems().add(project);
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

    @FXML
    private void createNewProject(ActionEvent event) {
        CreateProjectPopUpController.displayPopUp();
        updateDisplayList(ModelTypes.Project);
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
            GenericPopup popup = new GenericPopup(e);
            popup.show();
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
            GenericPopup popup = new GenericPopup(e);
            popup.show();
        }
    }
}
