package sws.murcs.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.*;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main app class controller
 */
public class AppController implements Initializable, ViewUpdate{

    @FXML
    private Parent root;
    @FXML
    private MenuItem fileQuit, newProjectMenuItem, undoMenuItem, redoMenuItem;
    @FXML
    private VBox vBoxSideDisplay;
    @FXML
    private HBox hBoxMainDisplay;
    @FXML
    private BorderPane borderPaneMain;
    @FXML
    private ChoiceBox displayChoiceBox;
    @FXML
    private ListView displayList;
    @FXML
    private Button removeButton;

    @FXML
    private GridPane contentPane;

    private ObservableList displayListItems;
    private boolean consumeChoiceBoxEvent = false;

    /**
     * Initialises the GUI, setting up the the options in the choice box and populates the display list if necessary.
     * Put all initialisation of GUI in this function.
     * @param location The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        App.addListener(e -> {
            e.consume();
            fileQuitPress(null);
        });
        displayListItems = FXCollections.observableArrayList();
        displayList.setItems(displayListItems);

        for (ModelTypes type : ModelTypes.values()) {
            displayChoiceBox.getItems().add(type);
        }
        displayChoiceBox.getSelectionModel().selectedItemProperty().addListener((observer, oldValue, newValue) -> {
            if (!consumeChoiceBoxEvent) {
                updateListView(null, true);
            } else {
                // Consume the event, don't update the display
                consumeChoiceBoxEvent = false;
            }
        });

        displayChoiceBox.getSelectionModel().select(0);
        displayList.getSelectionModel().selectedIndexProperty().addListener((observable, oldIndex, newIndex) -> {
            //The remove button should be greyed out if no item is selected
            Object newValue = null;
            if (newIndex.intValue() >= 0) displayList.getItems().get(c.intValue());

            removeButton.setDisable(newValue == null);

            contentPane.getChildren().clear();
            if (newValue == null) return;

            Parent pane = null;
            try {
                ViewUpdate update = this;
                pane = EditorHelper.getEditForm((Model) newValue, update);
            } catch (Exception e) {
                //This isn't really something the user should have to deal with
                e.printStackTrace();
            }
            contentPane.getChildren().add(pane);
        });

        updateUndoRedoMenuItems(0);
        UndoRedoManager.addChangeListener(changeType -> Platform.runLater(() -> updateUndoRedoMenuItems(changeType)));
        updateListView(null, true);
    }

    /**
     * Updates the display list on the left hand side of the screen to the type selected in the choice box.
     * @param newModelObject The type selected in the choice box.
     */
    public void updateListView(Model newModelObject, boolean changeSelection) {
        ModelTypes type;
        ModelTypes selectedType = ModelTypes.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex());

        if (newModelObject == null) {
            updateList(null, selectedType, changeSelection);
        }
        else {
            type = ModelTypes.getModelType(newModelObject);
            if (selectedType == type) {
                updateList(newModelObject, type, changeSelection);
            }
            else {
                // Set listener event to be consumed, because when the displayChoiceBox is changed it fire an event.
                consumeChoiceBoxEvent = true;
                displayChoiceBox.getSelectionModel().select(ModelTypes.getSelectionType(type));
                updateList(newModelObject, type, changeSelection);
            }
        }
    }

    /**
     * Updates the display list on the left hand side of the screen.
     * @param newModelObject new model object, that may have been created.
     * @param type type of model object to refresh
     */
    private void updateList(Model newModelObject, ModelTypes type, boolean ChangeSelection) {
        displayList.getSelectionModel().clearSelection();
        displayListItems.clear();
        RelationalModel model = PersistenceManager.Current.getCurrentModel();

        if (model == null) return;

        switch (type) {
            case Project:
                displayListItems.addAll(model.getProjects());
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
        if (ChangeSelection) {
            if (newModelObject != null) {
                displayList.getSelectionModel().select(newModelObject);
            } else {
                displayList.getSelectionModel().select(0);
            }
        }
    }

    /**
     * Called when the Quit button is pressed in the file menu and quit the current application.
     * @param event The even that triggers the function
     */
    @FXML
    private void fileQuitPress(ActionEvent event) {
        if (UndoRedoManager.canRevert()) {
            GenericPopup popup = new GenericPopup();
            popup.setWindowTitle("Unsaved Changes");
            popup.setMessageText("You have unsaved changes to your project.");
            popup.addButton("Discard", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, m -> {
                popup.close();
                Platform.exit();
            });
            popup.addButton("Save", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, m -> {
                popup.close();
                // Let the user save the project
                saveProject();
                Platform.exit();
            });
            popup.addButton("Cancel", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, m -> popup.close());
            popup.show();
        }
        else {
            Platform.exit();
        }
    }

    /**
     * Toggles the view of the display list box at the side.
     * @param event The event that triggers the function
     */
    @FXML
    private void toggleItemListView(ActionEvent event) {
        if (!vBoxSideDisplay.managedProperty().isBound()) {
            vBoxSideDisplay.managedProperty().bind(vBoxSideDisplay.visibleProperty());
        }
        vBoxSideDisplay.setVisible(!vBoxSideDisplay.isVisible());
    }

    /**
     * Create a new project, opens a dialog to fill out for the new project.
     * @param event The event that causes the function to be called, namely clicking new project.
     */
    @FXML
    private void createNewProject(ActionEvent event) {
        ViewUpdate update = this;
        EditorHelper.createNew(Project.class, update);
    }

    /**
     * Saves the current project.
     */
    private void saveProject() {
        saveProject(null);
    }

    /**
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
        } catch (Exception e) {
            GenericPopup popup = new GenericPopup(e);
            popup.show();
        }
    }

    /**
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
            updateListView(null, true);
        } catch (Exception e) {
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
            UndoRedoManager.revert();
        }
        catch (Exception e) {
            // Something went very wrong
            UndoRedoManager.forget();
            e.printStackTrace();
        }
    }

    /**
     * Redo menu item has been clicked.
     * @param event event arguments.
     */
    @FXML
    private void redoMenuItemClicked(ActionEvent event) {
        try {
            UndoRedoManager.remake();
        }
        catch (Exception e) {
            // something went terribly wrong....
            UndoRedoManager.forget();
            e.printStackTrace();
        }
    }

    /**
     * Updates the undo/redo menu to reflect the current undo/redo state.
     * @param change type of change that has been made
     */
    private void updateUndoRedoMenuItems(int change) {
        if (!UndoRedoManager.canRevert()) {
            undoMenuItem.setDisable(true);
            undoMenuItem.setText("Undo...");
        }
        else {
            undoMenuItem.setDisable(false);
            undoMenuItem.setText("Undo \"" + UndoRedoManager.getRevertMessage() +  "\"");
        }

        if (!UndoRedoManager.canRemake()) {
            redoMenuItem.setDisable(true);
            redoMenuItem.setText("Redo...");
        }
        else {
            redoMenuItem.setDisable(false);
            redoMenuItem.setText("Redo \"" + UndoRedoManager.getRemakeMessage() +  "\"");
        }

        // TODO: List refresh code (story: 119, task: 46)
    }

    @FXML
    private void addClicked(ActionEvent event) {
        Class<? extends Model> clazz = null;
        if (event.getSource() instanceof MenuItem) {
            //If pressing a menu item to add a person, team or skill
            String id = ((MenuItem) event.getSource()).getId();
            switch (id) {
                case "addPerson":
                    clazz = Person.class;
                    break;
                case "addTeam":
                    clazz = Team.class;
                    break;
                case "addSkill":
                    clazz = Skill.class;
                    break;
            }
        }
        else {
            //If pressing the add button at the bottom of the display list
            ModelTypes type = ModelTypes.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex());
            clazz = ModelTypes.getTypeFromModel(type);
        }

        if (clazz != null) {
            ViewUpdate viewUpdate = this;
            EditorHelper.createNew(clazz, viewUpdate);
        }
    }

    @FXML
    private void removeClicked(ActionEvent event) {
        RelationalModel model = PersistenceManager.Current.getCurrentModel();
        if (model == null) return;

        final int selectedIndex = displayList.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) return;

        // Ensures you can't delete Product Owner or Scrum Master
        Model selectedItem = (Model) displayList.getSelectionModel().getSelectedItem();
        if (ModelTypes.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex()) == ModelTypes.Skills)
            if (selectedItem.getShortName().equals("PO") || selectedItem.getShortName().equals("SM"))
                return;

        model.remove((Model) displayList.getSelectionModel().getSelectedItem());
        updateListView(null, true);
    }
}

