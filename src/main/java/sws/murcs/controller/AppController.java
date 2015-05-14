package sws.murcs.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sws.murcs.listeners.ViewUpdate;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Release;
import sws.murcs.model.Skill;
import sws.murcs.model.Team;
import sws.murcs.model.observable.ModelObservableArrayList;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.reporting.ReportGenerator;
import sws.murcs.view.App;
import sws.murcs.view.CreatorWindowView;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Main app class controller.
 */
public class AppController implements ViewUpdate<Model>, UndoRedoChangeListener {

    /**
     * The Menu items for the main window.
     */
    @FXML
    private MenuItem fileQuit, undoMenuItem, redoMenuItem, openProject, saveProject, generateReport, addProject,
            addTeam, addPerson, addSkill, addRelease, addStory, showHide;
    /**
     * The side display which contains the display list.
     */
    @FXML
    private VBox vBoxSideDisplay;
    /**
     * The main display of the window which contains the display
     * list and the content pane.
     */
    @FXML
    private HBox hBoxMainDisplay;
    /**
     * The root of the fxml file.
     */
    @FXML
    private BorderPane borderPaneMain;
    /**
     * The choice box for selecting the model type to be
     * displayed in the list.
     */
    @FXML
    private ChoiceBox<ModelType> displayChoiceBox;
    /**
     * The list which contains the models of the type selected
     * in the display list choice box.
     */
    @FXML
    private ListView displayList;
    /**
     * The button used to remove models from the display list.
     */
    @FXML
    private Button removeButton;
    /**
     * The content pane contains the information about the
     * currently selected model item.
     */
    @FXML
    private GridPane contentPane;

    /**
     * The content pane to show the view for a model.
     */
    private EditorPane editorPane;
    /**
     * A creation window.
     */
    private CreatorWindowView creatorWindow;

    /**
     * Initialises the GUI, setting up the the options in the choice box and populates the display list if necessary.
     * Put all initialisation of GUI in this function.
     */
    @FXML
    public final void initialize() {
        App.addListener(e -> {
            e.consume();
            fileQuitPress(null);
        });

        for (ModelType type : ModelType.values()) {
            displayChoiceBox.getItems().add(type);
        }
        displayChoiceBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observer, oldValue, newValue) -> updateList());

        displayChoiceBox.getSelectionModel().select(0);
        displayList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //The remove button should be greyed out
            // if no item is selected or built in skills (PO or SM) are selected
            removeButton.setDisable(newValue == null
                    || newValue instanceof Skill
                    && (((Skill) newValue).getShortName().equals("PO")
                    || ((Skill) newValue).getShortName().equals("SM")));

            if (newValue == null) {
                if (editorPane != null) {
                    editorPane.dispose();
                    editorPane = null;
                    contentPane.getChildren().clear();
                }
                return;
            }
            if (editorPane == null) {
                editorPane = new EditorPane((Model) newValue);
                contentPane.getChildren().clear();
                contentPane.getChildren().add(editorPane.getView());
            }
            else {
                if (editorPane.getModel().getClass() == newValue.getClass()) {
                    editorPane.setModel((Model) newValue);
                }
                else {
                    editorPane.dispose();
                    contentPane.getChildren().clear();
                    editorPane = new EditorPane((Model) newValue);
                    contentPane.getChildren().add(editorPane.getView());
                }
            }
        });

        setUpShortCuts();

        undoRedoNotification(ChangeState.Commit);
        UndoRedoManager.addChangeListener(this);
        updateList();
    }

    /**
     * Sets up the keyboard shortcuts for the application.
     */
    private void setUpShortCuts() {
        //Menu item short cuts
        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        saveProject.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        openProject.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        generateReport.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));
        addProject.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
        addPerson.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.SHIFT_DOWN,
                KeyCombination.CONTROL_DOWN));
        addRelease.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        addSkill.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN,
                KeyCombination.CONTROL_DOWN));
        addTeam.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
        addStory.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN,
                KeyCombination.CONTROL_DOWN));
        showHide.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));

        //Key combinations for things other than menu items
        borderPaneMain.addEventHandler(KeyEvent.KEY_PRESSED, event -> handleKey(event));
    }

    /**
     * Handles keys being pressed.
     * @param event Key event
     */
    private void handleKey(final KeyEvent event) {
        if (new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHIFT_DOWN,
                KeyCombination.CONTROL_DOWN).match(event)) {
            addClicked(null);
        }
        if (new KeyCodeCombination(KeyCode.DELETE, KeyCombination.CONTROL_DOWN).match(event)) {
            removeClicked(null);
        }
    }

    /**
     * Updates the display list on the left hand side of the screen.
     */
    private void updateList() {
        if (creatorWindow != null) {
            creatorWindow.dispose();
            creatorWindow = null;
        }
        ModelType type = ModelType.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex());
        displayList.getSelectionModel().clearSelection();
        RelationalModel model = PersistenceManager.Current.getCurrentModel();

        if (model == null) {
            return;
        }

        List<? extends Model> arrayList;
        switch (type) {
            case Project: arrayList = model.getProjects(); break;
            case Person: arrayList = model.getPeople(); break;
            case Team: arrayList = model.getTeams(); break;
            case Skill: arrayList = model.getSkills(); break;
            case Release: arrayList = model.getReleases(); break;
            case Story: arrayList = model.getStories(); break;
            default: throw new UnsupportedOperationException();
        }
        displayList.setItems((ModelObservableArrayList) arrayList);
        displayList.getSelectionModel().select(0);
    }

    /**
     * Called when the Quit button is pressed in the
     * file menu and quit the current application.
     * @param event The even that triggers the function
     */
    @FXML
    private void fileQuitPress(final ActionEvent event) {
        if (UndoRedoManager.canRevert()) {
            GenericPopup popup = new GenericPopup();
            popup.setWindowTitle("Unsaved Changes");
            popup.setTitleText("Do you wish to save changes?");
            popup.setMessageText("You have unsaved changes to your project.");
            popup.addButton("Discard", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, m -> {
                popup.close();
                Platform.exit();
            });
            popup.addButton("Save", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, m -> {
                // Let the user save the project
                if (saveProject()) {
                    popup.close();
                    Platform.exit();
                }
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
    private void toggleItemListView(final ActionEvent event) {
        if (!vBoxSideDisplay.managedProperty().isBound()) {
            vBoxSideDisplay.managedProperty().bind(vBoxSideDisplay.visibleProperty());
        }
        vBoxSideDisplay.setVisible(!vBoxSideDisplay.isVisible());
    }

    /**
     * Saves the current project.
     * @return If the project successfully saved.
     */
    private boolean saveProject() {
        return saveProject(null);
    }

    /**
     * Save the current project. Currently you choose where
     * to save the project every time, however it does remember the
     * last location saved or loaded from.
     * @param event The event that causes this function to be called,namely clicking save.
     * @return If the project successfully saved.
     */
    @FXML
    private boolean saveProject(final ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Project");
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("Project File (*.project)", "*.project"));
            fileChooser.setInitialDirectory(new File(PersistenceManager.Current.getCurrentWorkingDirectory()));
            File file = fileChooser.showSaveDialog(App.getStage());
            if (file != null) {
                String fileName = file.getName();
                if (!fileName.endsWith(".project")) {
                    fileName += ".project";
                }
                PersistenceManager.Current.setCurrentWorkingDirectory(file.getParentFile().getAbsolutePath());
                PersistenceManager.Current.saveModel(fileName);
                return true;
            }
        }
        catch (Exception e) {
            GenericPopup popup = new GenericPopup(e);
            popup.show();
        }
        return false;
    }

    /**
     * Opens a specified project file, from a specified location.
     * @param event The event that caused the function to be called.
     */
    @FXML
    private void openProject(final ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("Project File (*.project)", "*.project"));
            fileChooser.setInitialDirectory(new File(PersistenceManager.Current.getCurrentWorkingDirectory()));
            fileChooser.setTitle("Select Project");
            File file = fileChooser.showOpenDialog(App.getStage());
            if (file != null) {
                PersistenceManager.Current.setCurrentWorkingDirectory(file.getParentFile().getAbsolutePath());
                RelationalModel model = PersistenceManager.Current.loadModel(file.getName());
                if (model == null) {
                    throw new Exception("Project was not opened.");
                }
                PersistenceManager.Current.setCurrentModel(model);
                updateList();
                UndoRedoManager.importModel(model);
            }
        }
        catch (Exception e) {
            GenericPopup popup = new GenericPopup();
            popup.setTitleText("Old or corrupted project!");
            popup.setMessageText("The project you attempted to open is for an older version or is corrupted. "
                    + "Please use the version it was created with to open the file.");
            popup.show();
        }
    }

    /**
     * Generates a report to a specified location.
     * @param event The event that caused the report to be generated
     */
    @FXML
    private void generateReport(final ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("XML File (*.xml)", "*.xml"));
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("Report File (*.report)", "*.report"));
            fileChooser.setInitialDirectory(new File(PersistenceManager.Current.getCurrentWorkingDirectory()));
            fileChooser.setTitle("Report Save Location");
            File file = fileChooser.showSaveDialog(App.getStage());
            if (file != null) {
                ReportGenerator.generate(PersistenceManager.Current.getCurrentModel(), file);
                PersistenceManager.Current.setCurrentWorkingDirectory(file.getParentFile().getAbsolutePath());
            }

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
    private void undoMenuItemClicked(final ActionEvent event) {
        try {
            UndoRedoManager.revert();
        }
        catch (Exception e) {
            // Something went very wrong
            UndoRedoManager.forget();
        }
    }

    /**
     * Redo menu item has been clicked.
     * @param event event arguments.
     */
    @FXML
    private void redoMenuItemClicked(final ActionEvent event) {
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
    @Override
    public final void undoRedoNotification(final ChangeState change) {
        if (!UndoRedoManager.canRevert()) {
            undoMenuItem.setDisable(true);
            undoMenuItem.setText("Undo...");
        }
        else {
            undoMenuItem.setDisable(false);
            undoMenuItem.setText("Undo " + UndoRedoManager.getRevertMessage());
        }

        if (!UndoRedoManager.canRemake()) {
            redoMenuItem.setDisable(true);
            redoMenuItem.setText("Redo...");
        }
        else {
            redoMenuItem.setDisable(false);
            redoMenuItem.setText("Redo " + UndoRedoManager.getRemakeMessage());
        }
    }

    /**
     * Function that is called when a new model is created from the plus button.
     * @param event The event of the add button being called
     */
    @FXML
    private void addClicked(final ActionEvent event) {
        Class<? extends Model> clazz = null;
        if (event != null && event.getSource() instanceof MenuItem) {
            //If pressing a menu item to add a person, team or skill
            String id = ((MenuItem) event.getSource()).getId();
            switch (id) {
                case "addProject":
                    clazz = Project.class;
                    break;
                case "addPerson":
                    clazz = Person.class;
                    break;
                case "addTeam":
                    clazz = Team.class;
                    break;
                case "addSkill":
                    clazz = Skill.class;
                    break;
                case "addRelease":
                    clazz = Release.class;
                    break;
                default:
                    break;
            }
        }
        else {
            //If pressing the add button at the bottom of the display list
            ModelType type = ModelType.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex());
            clazz = ModelType.getTypeFromModel(type);
        }

        if (clazz != null) {
            try {
                creatorWindow = new CreatorWindowView(clazz.newInstance(),
                        model -> {
                            selectItem(model);
                            if (creatorWindow != null) {
                                creatorWindow.dispose();
                                creatorWindow = null;
                            }
                        },
                        func -> {
                            creatorWindow.dispose();
                            creatorWindow = null;
                        });
                creatorWindow.show();
            }
            catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Function that is called when you want to delete a model.
     * @param event Event that sends you to the remove clicked function
     */
    @FXML
    private void removeClicked(final ActionEvent event) {
        RelationalModel model = PersistenceManager.Current.getCurrentModel();
        if (model == null) {
            return;
        }

        final int selectedIndex = displayList.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) {
            return;
        }

        Model selectedItem = (Model) displayList.getSelectionModel().getSelectedItem();

        // Ensures you can't delete Product Owner or Scrum Master
        if (ModelType.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex()) == ModelType.Skill) {
            if (selectedItem.getShortName().equals("PO") || selectedItem.getShortName().equals("SM")) {
                return;
            }
        }

        Collection<Model> usages = model.findUsages(selectedItem);
        GenericPopup popup = new GenericPopup();
        String message = "Are you sure you want to delete this?";
        if (usages.size() != 0) {
            message += "\nThis ";
            ModelType type = ModelType.getModelType(selectedItem);
            if (type == ModelType.Person) {
                message += "person";
            }
            else {
                message += type.toString().toLowerCase();
            }
             message += " is used in " + usages.size() + " place(s):";
            for (Model usage : usages) {
                message += "\n" + usage.getShortName();
            }
        }
        popup.setTitleText("Really delete?");
        popup.setMessageText(message);

        popup.addButton("Yes", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, v -> {
            popup.close();
            Model item = (Model) displayList.getSelectionModel().getSelectedItem();
            model.remove(item);
        });
        popup.addButton("No", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, v -> popup.close());
        popup.show();
    }

    @Override
    public final void selectItem(final Model parameter) {
        ModelType type;
        ModelType selectedType = ModelType.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex());

        if (parameter == null) {
            displayList.getSelectionModel().select(0);
        }
        else {
            type = ModelType.getModelType(parameter);
            if (selectedType == type) {
                displayList.getSelectionModel().select(parameter);
            }
            else {
                displayChoiceBox.getSelectionModel().select(ModelType.getSelectionType(type));
                displayList.getSelectionModel().select(parameter);
            }
        }
    }
}
