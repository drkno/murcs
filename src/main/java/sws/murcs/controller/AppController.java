package sws.murcs.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import sws.murcs.controller.editor.BacklogEditor;
import sws.murcs.controller.windowManagement.ShortcutManager;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.listeners.ViewUpdate;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Backlog;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.Release;
import sws.murcs.model.Skill;
import sws.murcs.model.Story;
import sws.murcs.model.Team;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.observable.ModelObservableArrayList;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.reporting.ui.ReportGeneratorView;
import sws.murcs.view.App;
import sws.murcs.view.CreatorWindowView;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main app class controller. This controls all the main window functionality, so anything that isn't in a seperate
 * window is controlled here.
 */
public class AppController implements ViewUpdate<Model>, UndoRedoChangeListener {

    /**
     * The Menu bar for the application.
     */
    @FXML
    private MenuBar menuBar;

    /**
     * The Menu items for the main window.
     */
    @FXML
    private MenuItem fileQuit, undoMenuItem, redoMenuItem, open, save, saveAs, generateReport, addProject, newModel,
            addTeam, addPerson, addSkill, addRelease, addStory, addBacklog, showHide, revert, highlightToggle;

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
     * The back and forward buttons.
     */
    @FXML
    private Button backButton, forwardButton;

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
     * Stores and instance of the main app window.
     */
    private Window window;

    /**
     * Initialises the GUI, setting up the the options in the choice box and populates the display list if necessary.
     * Put all initialisation of GUI in this function.
     */
    @FXML
    public final void initialize() {
        NavigationManager.setAppController(this);
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }

        for (ModelType type : ModelType.values()) {
            displayChoiceBox.getItems().add(type);
        }
        displayChoiceBox.getStyleClass().add("no-shadow");
        displayChoiceBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observer, oldValue, newValue) -> updateList());

        displayChoiceBox.getSelectionModel().select(0);
        displayList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                if (editorPane != null && newValue != null) {
                    editorPane.getController().saveChanges();
                }
                updateDisplayListSelection(newValue, oldValue);
            }
        });

        undoRedoNotification(ChangeState.Commit);
        UndoRedoManager.addChangeListener(this);
        updateList();
    }

    /**
     * Updates the currently selected item in the display list.
     * @param newValue The new value
     * @param oldValue The old value
     */
    private void updateDisplayListSelection(final Object newValue, final Object oldValue) {
        if (newValue == null && editorPane != null) {
            editorPane.dispose();
            editorPane = null;
            contentPane.getChildren().clear();

            return;
        }

        NavigationManager.navigateTo((Model) newValue);
        updateBackForwardButtons();

        if (oldValue == null) {
            displayList.scrollTo(newValue);
        }
    }

    /**
     * Sets up the keyboard shortcuts for the application.
     */
    private void setUpShortCuts() {
        //Menu item shortcuts
        // You should use SHORTCUT_DOWN as it uses the COMMAND key for Mac and the CTRL key for Windows
        ShortcutManager shortcutManager = App.getShortcutManager();

        //Global shortcuts
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN,
                KeyCombination.SHIFT_DOWN), () -> revert(null));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN),
                () -> newModel(null));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                this::save);
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN,
                        KeyCombination.SHIFT_DOWN), () -> saveAs(null));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN),
                () -> open(null));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.G, KeyCombination.SHORTCUT_DOWN),
                () -> generateReport(null));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN),
                () -> ErrorReporter.get().reportManually());
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.SHORTCUT_DOWN),
                () -> addNewItem(ModelType.Project));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHORTCUT_DOWN),
                () -> addNewItem(ModelType.Team));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.SHORTCUT_DOWN),
                () -> addNewItem(ModelType.Person));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.SHORTCUT_DOWN),
                () -> addNewItem(ModelType.Skill));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT5, KeyCombination.SHORTCUT_DOWN),
                () -> addNewItem(ModelType.Release));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT6, KeyCombination.SHORTCUT_DOWN),
                () -> addNewItem(ModelType.Backlog));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT7, KeyCombination.SHORTCUT_DOWN),
                () -> addNewItem(ModelType.Story));

        //Local shortcuts.
        Map<KeyCombination, Runnable> accelerators = new HashMap<>();
        accelerators.put(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN),
                () -> undoMenuItemClicked(null));
        accelerators.put(new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
                () -> redoMenuItemClicked(null));
        accelerators.put(new KeyCodeCombination(KeyCode.H, KeyCombination.SHORTCUT_DOWN),
                () -> toggleItemListView(null));
        accelerators.put(new KeyCodeCombination(KeyCode.EQUALS),
                () -> addClicked(null));
        accelerators.put(new KeyCodeCombination(KeyCode.DELETE),
                () -> removeClicked(null));
        accelerators.put(new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN),
                () -> backClicked(null));
        accelerators.put(new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN),
                () -> forwardClicked(null));

        App.getStage().getScene().getAccelerators().putAll(accelerators);
    }

    /**
     * Updates the display list on the left hand side of the screen.
     */
    private void updateList() {
        ModelType type = ModelType.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex());
        displayList.getSelectionModel().clearSelection();
        Organisation model = PersistenceManager.getCurrent().getCurrentModel();

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
            case Backlog: arrayList = model.getBacklogs(); break;
            default: throw new UnsupportedOperationException();
        }

        if (arrayList.getClass() == ModelObservableArrayList.class) {
            ModelObservableArrayList<? extends Model> arrList = (ModelObservableArrayList) arrayList;
            arrayList = new SortedList<>(arrList, (Comparator<? super Model>) arrList);
        }
        else {
            System.err.println("This list type does not yet have an ordering specified, "
                    + "please correct this so that the display list is shown correctly.");
        }

        displayList.setItems((ObservableList) arrayList);
        displayList.getSelectionModel().select(0);
    }

    /**
     * Called when the Quit button is pressed in the
     * file menu and quit the current application.
     * @param event The even that triggers the function
     */
    @FXML
    private void fileQuitPress(final ActionEvent event) {
        borderPaneMain.requestFocus();
        if (UndoRedoManager.canRevert() || App.getWindowManager().getAllWindows().size() > 1) {
            GenericPopup popup = new GenericPopup(window);
            popup.setWindowTitle("Still working on something?");
            popup.setTitleText("Looks like you are still working on something.\nOr have unsaved changes.");
            popup.setMessageText("Do you want to,");
            popup.addButton("Discard and Exit", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, () -> {
                popup.close();
                App.getWindowManager().cleanUp();
                Platform.exit();
            });
            if (UndoRedoManager.canRevert()) {
                popup.addButton("Save and Exit", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -> {
                    // Let the user save the project
                    if (save()) {
                        popup.close();
                        App.getWindowManager().cleanUp();
                        Platform.exit();
                    }
                });
            }
            popup.addButton("Back to Safety", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, () -> {
                popup.close();
                App.getWindowManager().getAllWindows()
                        .stream()
                        .filter(openWindow -> openWindow.getController().getClass() != this.getClass())
                        .forEach(openWindow -> {
                            App.getWindowManager().bringToTop(openWindow, true);
                        });
            });
            popup.show();
        }
        else {
            App.getWindowManager().cleanUp();
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
     * Switches the state of the story highlighting.
     * @param event The event that is fired when the Highlight Stories menu item is clicked
     */
    @FXML
    private void toggleBacklogStories(final ActionEvent event) {
        BacklogEditor.toggleHighlightState();
    }

    /**
     * Saves the current project.
     * @return If the project successfully saved.
     */
    private boolean save() {
        return save(null);
    }

    /**
     * Save the current model.
     * @param event The event that causes this function to be called, namely clicking save.
     * @return If the project successfully saved.
     */
    @FXML
    private boolean save(final ActionEvent event) {
        try {
            if (PersistenceManager.getCurrent().getLastFile() != null) {
                PersistenceManager.getCurrent().save();
                UndoRedoManager.forget();
                return true;
            }
            else {
                return saveAs(null);
            }
        }
        catch (Exception e) {
            ErrorReporter.get().reportError(e, "Something went wrong saving :(");
        }
        return false;
    }

    /**
     * Saves the model as a new file.
     * @param event The event that causes this function to be called, namely clicking save.
     * @return If the project successfully saved.
     */
    @FXML
    private boolean saveAs(final ActionEvent event) {
        return saveAs(event, true);
    }

    /**
     * Saves the model as a new file.
     * @param event The event that causes this function to be called, namely clicking save.
     * @param forgetHistory If true all history about the project is forgotten
     * @return If the project successfully saved.
     */
    private boolean saveAs(final ActionEvent event, final boolean forgetHistory) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save As");
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("Project File (*.project)", "*.project"));
            fileChooser.setInitialDirectory(new File(PersistenceManager.getCurrent().getCurrentWorkingDirectory()));
            File file = fileChooser.showSaveDialog(App.getStage());
            if (file != null) {
                String fileName = file.getName();
                if (!fileName.endsWith(".project")) {
                    fileName += ".project";
                }
                PersistenceManager.getCurrent().setCurrentWorkingDirectory(file.getParentFile().getAbsolutePath());
                PersistenceManager.getCurrent().saveModel(fileName);
                if (forgetHistory) {
                    UndoRedoManager.forget();
                }
                return true;
            }
        }
        catch (Exception e) {
            ErrorReporter.get().reportError(e, "Something went wrong saving :(");
        }
        return false;
    }

    /**
     * Handles the creation of a new Model.
     * @param event The event that causes this function to be called, namely clicking save.
     */
    @FXML
    private void newModel(final ActionEvent event) {
        if (UndoRedoManager.canRevert() || App.getWindowManager().getAllWindows().size() > 1) {
            GenericPopup popup = new GenericPopup(window);
            popup.setWindowTitle("Still working on something?");
            popup.setTitleText("Looks like you are still working on something.\nOr have unsaved changes.");
            popup.setMessageText("Do you want to,");
            popup.addButton("Discard Them", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, () -> {
                popup.close();
                try {
                    // Close all windows which are not the main app.
                    App.getWindowManager().cleanUp();
                    createNewModel();
                } catch (Exception e) {
                    ErrorReporter.get().reportError(e, "Something went wrong creating a new organisation :(");
                }
            });
            if (UndoRedoManager.canRevert()) {
                popup.addButton("Save Them", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -> {
                    // Let the user save the project
                    if (save()) {
                        popup.close();
                        try {
                            // Close all windows which are not the main app.
                            App.getWindowManager().cleanUp();
                            createNewModel();
                        } catch (Exception e) {
                            ErrorReporter.get().reportError(e, "Something went wrong creating a new organisation :(");
                        }
                    }
                });
            }
            popup.addButton("Back to Safety", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, () -> {
                popup.close();
                App.getWindowManager().getAllWindows()
                        .stream()
                        .filter(openWindow -> openWindow.getController().getClass() != this.getClass())
                        .forEach(openWindow -> {
                            App.getWindowManager().bringToTop(openWindow, true);
                        });
            });
            popup.show();
        }
        else {
            try {
                createNewModel();
            } catch (Exception e) {
                ErrorReporter.get().reportError(e, "Something went wrong creating a new organisation :(");
            }
        }
    }

    /**
     * Creates a new model and adds it to the program.
     * @exception Exception thrown if the undo redo manager fails to import the new model.
     */
    private void createNewModel() throws Exception {
        PersistenceManager.getCurrent().setCurrentModel(null);
        Organisation model = new Organisation();
        PersistenceManager.getCurrent().setCurrentModel(model);
        UndoRedoManager.importModel(model);
        NavigationManager.clearHistory();
        updateList();
        editorPane = null;
    }

    /**
     * Opens a file.
     * @param event The event that caused the function to be called.
     */
    @FXML
    private void open(final ActionEvent event) {
        if (UndoRedoManager.canRevert() || App.getWindowManager().getAllWindows().size() > 1) {
            GenericPopup popup = new GenericPopup(window);
            popup.setWindowTitle("Still working on something?");
            popup.setTitleText("Looks like you are still working on something.\nOr have unsaved changes.");
            popup.setMessageText("Do you want to,");
            popup.addButton("Discard Them", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, () -> {
                if (openFile()) {
                    popup.close();
                    // Close all windows which are not the main app.
                    App.getWindowManager().cleanUp();
                }
            });
            if (UndoRedoManager.canRevert()) {
                popup.addButton("Save Them", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -> {
                    // Let the user save the project
                    if (save()) {
                        if (openFile()) {
                            popup.close();
                            // Close all windows which are not the main app.
                            App.getWindowManager().cleanUp();
                        }
                    }
                });
            }
            popup.addButton("Back to Safety", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, () -> {
                popup.close();
                App.getWindowManager().getAllWindows()
                        .stream()
                        .filter(openWindow -> openWindow.getController().getClass() != this.getClass())
                        .forEach(openWindow -> {
                            App.getWindowManager().bringToTop(openWindow, true);
                        });
            });
            popup.show();
        }
        else {
            openFile();
        }
    }

    /**
     * Prompts the user to open an organisation.
     * @return If opening the file was successful.
     */
    private boolean openFile() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("Project File (*.project)", "*.project"));
            fileChooser.setInitialDirectory(new File(PersistenceManager.getCurrent().getCurrentWorkingDirectory()));
            fileChooser.setTitle("Select Project");
            File file = fileChooser.showOpenDialog(App.getStage());
            if (file != null) {
                PersistenceManager.getCurrent().setCurrentWorkingDirectory(file.getParentFile().getAbsolutePath());
                Organisation model = PersistenceManager.getCurrent().loadModel(file.getName());
                if (model == null) {
                    throw new Exception("Project was not opened.");
                }
                PersistenceManager.getCurrent().setCurrentModel(model);
                updateList();
                UndoRedoManager.forget(true);
                UndoRedoManager.importModel(model);

                //This is a workaround for making sure you can go back when you open a new project
                //This happens because forget clears the navigation history
                displayList.getSelectionModel().clearSelection();
                displayList.getSelectionModel().select(0);
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            GenericPopup popup = new GenericPopup(window);
            popup.setTitleText("Old or corrupted project!");
            popup.setMessageText("The project you attempted to open is for an older version or is corrupted. "
                    + "Please use the version it was created with to open the file.");
            popup.show();
            return false;
        }
    }

    /**
     * Generates a report to a specified location.
     * @param event The event that caused the report to be generated
     */
    @FXML
    private void generateReport(final ActionEvent event) {
        ReportGeneratorView reportGenerator = new ReportGeneratorView();
        reportGenerator.show();
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
            ErrorReporter.get().reportError(e, "Undo-redo failed to revert");
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
            ErrorReporter.get().reportError(e, "Undo-redo failed to remake");
        }
    }

    /**
     * Reverts the the model to its original save state.
     * @param event event arguments.
     */
    @FXML
    private void revert(final ActionEvent event) {
        if (UndoRedoManager.canRevert() || App.getWindowManager().getAllWindows().size() > 1) {
            GenericPopup popup = new GenericPopup(window);
            popup.setTitleText("Revert changes?");
            popup.setMessageText("Look like you are still working on something"
                    + "\nChanges will be lost if you continue.");
            popup.addButton("Revert Changes", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, () -> {
                try {
                    UndoRedoManager.revert(0);
                    popup.close();
                    // Close all windows which are not the main app.
                    App.getWindowManager().cleanUp();
                    selectItem(null);
                } catch (Exception e) {
                    ErrorReporter.get().reportError(e, "Something went wrong reverting the state of the organisation.");
                }
            });
            if (UndoRedoManager.canRevert()) {
                popup.addButton("Save Changes", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -> {
                    // Let the user save the project
                    if (saveAs(null, false)) {
                        try {
                            UndoRedoManager.revert(0);
                            popup.close();
                            // Close all windows which are not the main app.
                            App.getWindowManager().cleanUp();
                            selectItem(null);
                        } catch (Exception e) {
                            ErrorReporter.get().reportError(e, "Something went wrong saving the organisation");
                        }
                    }
                });
            }
            popup.addButton("Back to Safety", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, () -> {
                popup.close();
                App.getWindowManager().getAllWindows()
                        .stream()
                        .filter(openWindow -> openWindow.getController().getClass() != this.getClass())
                        .forEach(openWindow -> {
                            App.getWindowManager().bringToTop(openWindow, true);
                        });
            });
            popup.show();
        }
    }

    /**
     * Updates the undo/redo menu to reflect the current undo/redo state.
     * @param change type of change that has been made
     */
    @Override
    public final void undoRedoNotification(final ChangeState change) {
        if (!UndoRedoManager.canRevert()) {
            revert.setDisable(true);
            undoMenuItem.setDisable(true);
            undoMenuItem.setText("Undo...");
            App.removeTitleStar();
        }
        else {
            revert.setDisable(false);
            undoMenuItem.setDisable(false);
            undoMenuItem.setText("Undo " + UndoRedoManager.getRevertMessage());
            App.addTitleStar();
        }

        if (!UndoRedoManager.canRemake()) {
            redoMenuItem.setDisable(true);
            redoMenuItem.setText("Redo...");
        }
        else {
            redoMenuItem.setDisable(false);
            redoMenuItem.setText("Redo " + UndoRedoManager.getRemakeMessage());
        }

        switch (change) {
            case Forget:
            case Remake:
            case Revert:
                NavigationManager.clearHistory();
                updateBackForwardButtons();
                break;
            default: break;
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
                case "addBacklog":
                    clazz = Backlog.class;
                    break;
                case "addStory":
                    clazz = Story.class;
                    break;
                default:
                    throw new UnsupportedOperationException("Adding has not been implemented.");
            }
        }
        else {
            //If pressing the add button at the bottom of the display list
            ModelType type = ModelType.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex());
            clazz = ModelType.getTypeFromModel(type);
        }
        createNewItem(clazz);
    }

    /**
     * Adds a new item from a given model type.
     * @param modelType The type to get the class from.
     */
    private void addNewItem(final ModelType modelType) {
        Class<? extends Model> clazz = ModelType.getTypeFromModel(modelType);
        createNewItem(clazz);
    }

    /**
     * Creates a new model from a class instance.
     * @param clazz the class to create the model instance from.
     */
    private void createNewItem(final Class<? extends Model> clazz) {
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
                            if (creatorWindow != null) {
                                creatorWindow.dispose();
                                creatorWindow = null;
                            }
                        });
                creatorWindow.show();
            }
            catch (InstantiationException | IllegalAccessException e) {
                ErrorReporter.get().reportError(e, "Initialising a creation window failed");
            }
        }
    }

    /**
     * Function that is called when you want to delete a model.
     * @param event Event that sends you to the remove clicked function
     */
    @FXML
    private void removeClicked(final ActionEvent event) {
        Organisation model = PersistenceManager.getCurrent().getCurrentModel();
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

        Collection<Model> usages = UsageHelper.findUsages(selectedItem);
        GenericPopup popup = new GenericPopup(window);
        String message = "Are you sure you want to delete this?";
        if (usages.size() != 0) {
            message += "\nThis ";
            ModelType type = ModelType.getModelType(selectedItem);
            message += type.toString().toLowerCase() + " is used in " + usages.size() + " place(s):";
            for (Model usage : usages) {
                message += "\n" + usage.getShortName();
            }
        }
        popup.setTitleText("Really delete?");
        popup.setMessageText(message);

        popup.addButton("Yes", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -> {
            popup.close();
            NavigationManager.clearHistory();
            Model item = (Model) displayList.getSelectionModel().getSelectedItem();
            model.remove(item);
            updateBackForwardButtons();
        });
        popup.addButton("No", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, popup::close);
        popup.show();
    }

    @Override
    public final void selectItem(Model parameter) {
        ModelType type;
        ModelType selectedType = ModelType.getModelType(displayChoiceBox.getSelectionModel().getSelectedIndex());

        if (parameter == null) {
            displayList.getSelectionModel().select(0);
            displayList.scrollTo(0);
            parameter = (Model) displayList.getSelectionModel().getSelectedItem();
        }
        else {
            type = ModelType.getModelType(parameter);
            if (type != selectedType) {
                NavigationManager.setIgnore(true);
                displayChoiceBox.getSelectionModel().select(ModelType.getSelectionType(type));
                NavigationManager.setIgnore(false);
            }
            if (parameter != displayList.getSelectionModel().getSelectedItem()) {
                displayList.getSelectionModel().select(parameter);
                displayList.scrollTo(parameter);
            }

            if (displayList.getSelectionModel().getSelectedIndex() < 0) {
                displayList.scrollTo(parameter);
            }
        }

        // The remove button should be greyed out
        // if no item is selected or built in skills (PO or SM) are selected
        removeButton.setDisable(parameter == null
                || parameter instanceof Skill
                && (((Skill) parameter).getShortName().equals("PO")
                || ((Skill) parameter).getShortName().equals("SM")));

        if (editorPane == null) {
            editorPane = new EditorPane(parameter);
            contentPane.getChildren().clear();
            contentPane.getChildren().add(editorPane.getView());
        }
        else {
            if (editorPane.getModel().getClass() == parameter.getClass()) {
                editorPane.setModel(parameter);
            }
            else {
                editorPane.dispose();
                contentPane.getChildren().clear();
                editorPane = new EditorPane(parameter);
                contentPane.getChildren().add(editorPane.getView());
            }
        }
    }

    /**
     * Toggles the state of the back and forward buttons if they disabled or enabled.
     */
    private void updateBackForwardButtons() {
        backButton.setDisable(!NavigationManager.canGoBack());
        forwardButton.setDisable(!NavigationManager.canGoForward());
    }

    /**
     * Navigates back.
     * @param event The event that caused the function to be called.
     */
    @FXML
    private void backClicked(final ActionEvent event) {
        if (!NavigationManager.canGoBack()) {
            return;
        }
        displayList.getSelectionModel().clearSelection();
        NavigationManager.goBackward();
    }

    /**
     * Navigates forward.
     * @param event The event that caused the function to be called.
     */
    @FXML
    private void forwardClicked(final ActionEvent event) {
        if (!NavigationManager.canGoForward()) {
            return;
        }
        displayList.getSelectionModel().clearSelection();
        NavigationManager.goForward();
    }

    /**
     * Reports a bug to the developers.
     */
    @FXML
    private void reportBug() {
        ErrorReporter.get().reportManually();
    }

    /**
     * Shows the main app.
     */
    public final void show() {
        window = new Window(App.getStage(), this);
        window.register();
        window.show();
        window.getStage().setOnCloseRequest(App::notifyListeners);
        App.addListener(e -> {
            e.consume();
            fileQuitPress(null);
        });
        setUpShortCuts();
    }

    /**
     * Gets the window of the AppController.
     * @return The window for AppController.
     */
    public final Window getWindow() {
        return window;
    }
}
