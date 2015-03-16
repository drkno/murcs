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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sws.project.magic.easyedit.EditFormGenerator;
import sws.project.magic.tracking.TrackableObject;
import sws.project.magic.tracking.ValueChange;
import sws.project.model.*;
import sws.project.model.persistence.PersistenceManager;
import sws.project.view.App;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import static sws.project.magic.tracking.TrackableObject.*;

/**
 * Main app class controller
 * 11/03/2015
 */
public class AppController implements Initializable {

    @FXML Parent root;
    @FXML MenuItem fileQuit, newProjectMenuItem, undoMenuItem, redoMenuItem;
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
        displayChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            updateDisplayList(ModelTypes.getModelType(newValue.intValue()));
        });

        displayChoiceBox.getSelectionModel().select(0);
        displayList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            contentPane.getChildren().clear();
            if (newValue == null) return;

            Parent pane = null;
            try {
                if (newValue instanceof Project)
                    pane = ProjectEditor.createFor((Project)newValue);
                else if (newValue instanceof Person)
                    pane = PersonEditor.createFor((Person)newValue);
            } catch (Exception e) {
                //This isn't really something the user should have to deal with
                e.printStackTrace();
            }
            contentPane.getChildren().add(pane);
        });

        TrackableObject.addSavedListener(change -> {
            Platform.runLater(() -> {updateUndoRedoMenuItems(change);});
        });
    }

    /***
     * Updates the display list on the left hand side of the screen to the type selected in the choice box.
     * @param type The type selected in the choice box.
     */
    private void updateDisplayList(ModelTypes type) {
        displayListItems.clear();
        //displayList.getSelectionModel().clearSelection();

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
                displayListItems.addAll(model.getPeople());
                break;
            case Team:
                displayListItems.addAll(model.getTeams());
                break;
            case Skills:
                displayListItems.addAll(model.getSkills());
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
        if (!canUndo()) {
            ProjectEditor.displayWindow(() -> {
                updateDisplayList(ModelTypes.Project);
                return null;
            }, null);
        }
        else {
            GenericPopup popup = new GenericPopup();
            popup.setWindowTitle("Unsaved Changes");
            popup.setMessageText("You have unsaved changes to your project.");
            popup.addButton("Discard", GenericPopup.Position.LEFT, () -> {
                popup.close();
                // Reset Tracked history
                reset();
                // Create a new project
                ProjectEditor.displayWindow(() -> {
                    updateDisplayList(ModelTypes.Project);
                    return null;
                }, null);
                return null;
            });
            popup.addButton("Save", GenericPopup.Position.RIGHT, () -> {
                popup.close();
                // Let the user save the project
                saveProject(null);
                // Reset Tracked History
                reset();
                // Create a new project
                ProjectEditor.displayWindow(() -> {
                    updateDisplayList(ModelTypes.Project);
                    return null;
                }, null);
                return null;
            });
            popup.addButton("Cancel", GenericPopup.Position.RIGHT, () -> {popup.close(); return null;});
            popup.show();

        }
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

    /**
     * Called when the undo menu item has been clicked.
     * @param event event arguments.
     */
    @FXML
    private void undoMenuItemClicked(ActionEvent event) {
        try {
            undo();
        }
        catch (Exception e) {
            // something went terribly wrong....
            reset();
        }
        updateUndoRedoMenuItems(null);
    }

    /**
     * Redo menu item has been clicked.
     * @param event event arguments.
     */
    @FXML
    private void redoMenuItemClicked(ActionEvent event) {
        try {
            redo();
        }
        catch (Exception e) {
            // something went terribly wrong....
            reset();
            e.printStackTrace();
        }
        updateUndoRedoMenuItems(null);
    }

    /**
     * Updates the undo/redo menu to reflect the current undo/redo state.
     * @param change change that has been made
     */
    private void updateUndoRedoMenuItems(ValueChange change) {
        if (!canUndo()) {
            undoMenuItem.setDisable(true);
            undoMenuItem.setText("Undo...");
        }
        else {
            undoMenuItem.setDisable(false);
            undoMenuItem.setText("Undo \"" + getUndoDescription() +  "\"");
        }

        if (!canRedo()) {
            redoMenuItem.setDisable(true);
            redoMenuItem.setText("Redo...");
        }
        else {
            redoMenuItem.setDisable(false);
            redoMenuItem.setText("Redo \"" + getRedoDescription() +  "\"");
        }

        if (change == null) return;
        Class affectedType = change.getAffectedObject().getClass();
        if (affectedType.isArray()) {
            affectedType = affectedType.getComponentType();
        }
        ModelTypes type = ModelTypes.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex());
        Class expectedType = null;
        switch (type) {
            case Project:
                expectedType = RelationalModel.class;
                break;
            case People:
                expectedType = Person.class;
                break;
            case Team:
                expectedType = Team.class;
                break;
            case Skills:
                expectedType = Skill.class;
                break;
        }

        if (affectedType.equals(expectedType) &&
                Arrays.stream(change.getChangedFields())
                        .filter(f -> f.getField().getName().equals("shortName")
                                || f.getField().getName().equals("project"))
                        .findAny().isPresent()) {
            updateDisplayList(type);
        }
    }
}
