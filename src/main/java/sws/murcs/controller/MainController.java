package sws.murcs.controller;

import com.sun.javaws.exceptions.InvalidArgumentException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sws.murcs.controller.tabs.Navigable;
import sws.murcs.controller.tabs.Tabbable;
import sws.murcs.controller.tabs.ToolBarCommands;
import sws.murcs.controller.windowManagement.ShortcutManager;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.*;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.reporting.ui.ReportGeneratorView;
import sws.murcs.view.AboutView;
import sws.murcs.view.App;
import sws.murcs.view.CreatorWindowView;
import sws.murcs.view.SearchView;

/**
 * A controller for the base pane.
 */
public class MainController implements UndoRedoChangeListener, ToolBarCommands{
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
     * The menu that contains the check menu items for toggling sections of the toolbar.
     */
    @FXML
    private Menu toolBarMenu;

    /**
     * The top toolbar and menu container.
     */
    @FXML
    private VBox vBoxSideDisplay, titleVBox;

    /**
     * The main tab pane (where everything goes)
     */
    @FXML
    private TabPane mainTabPane;

    /**
     * A map of all the tabbable objects in tabs.
     */
    private Collection<Tabbable> tabs = new ArrayList<>();

    /**
     * The current controller for the toolbar.
     */
    private ToolBarController toolBarController;

    /**
     * The current tab controller
     */
    private Tabbable currentTabbable;

    /**
     * Stores and instance of the main app window.
     */
    private Window window;

    @FXML
    public final void initialize() {
        App.addListener(e -> {
            e.consume();
            fileQuitPress(null);
        });
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }

        mainTabPane.getSelectionModel().selectedItemProperty().addListener(observable -> {
            Tab selected = mainTabPane.getSelectionModel().getSelectedItem();
            //If all the tabs have been closed, close the window
            if (selected == null) {
                //TODO ((Stage)mainTabPane.getScene().getWindow()).close();
                return;
            }

            Tabbable tabbable = tabs
                    .stream()
                    .filter(t -> selected.getContent().equals(t.getRoot())).findFirst()
                    .orElse(null);

            if (tabbable == null) {
                return;
            }

            toolBarController.setNavigable(tabbable);
            toolBarController.setModelManagable(tabbable);

            currentTabbable = tabbable;
        });

        loadToolbar();
        toolBarController.setLinkedController(this);

        undoRedoNotification(ChangeState.Commit);
        setUpShortCuts();

        addModelViewTab();
        addModelViewTab();
    }

    /**
     * Adds the toolbar to the application window.
     */
    private void loadToolbar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/ToolBar.fxml"));
            Parent view = loader.load();

            ToolBarController controller = loader.getController();
            toolBarController = controller;

            titleVBox.getChildren().add(view);
        }
        catch (Exception e) {
            ErrorReporter.get().reportErrorSecretly(e, "Unable to create editor");
        }
    }

    /**
     * Gets the toolbar controller for this pane.
     * @return The ToolBarController.
     */
    public ToolBarController getToolBarController() {
        return toolBarController;
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
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN),
                () -> search(null));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.SPACE, KeyCombination.SHORTCUT_DOWN),
                () -> search(null));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Project));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Team));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Person));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Skill));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT5, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Release));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT6, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Backlog));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT7, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Story));

        //Local shortcuts.
        /*Map<KeyCombination, Runnable> accelerators = new HashMap<>();
        accelerators.put(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN),
                () -> undo(null));
        accelerators.put(new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
                () -> redo(null));

        //TODO Work out routing for menu items
        accelerators.put(new KeyCodeCombination(KeyCode.H, KeyCombination.SHORTCUT_DOWN),
                () -> toggleItemListView(null));
        accelerators.put(new KeyCodeCombination(KeyCode.EQUALS),
                () -> currentTabbable.create());
        accelerators.put(new KeyCodeCombination(KeyCode.DELETE),
                () -> remove(null));
        accelerators.put(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.SHORTCUT_DOWN),
                () -> back(null));
        accelerators.put(new KeyCodeCombination(KeyCode.PERIOD, KeyCombination.SHORTCUT_DOWN),
                () -> forward(null));

        App.getStage().getScene().getAccelerators().putAll(accelerators);*/
    }

    /**
     * Adds a model view tab to the main pane
     */
    public ModelViewController addModelViewTab() {
        return (ModelViewController)addTab("/sws/murcs/ModelView.fxml");
    }

    /**
     * Adds a tab to the pane.
     * @param fxmlPath
     */
    public Tabbable addTab(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Tabbable controller = loader.getController();
            controller.registerMainController(this);
            controller.setToolBarController(toolBarController);
            tabs.add(controller);

            Tab tabNode = new Tab(controller.getTitle());
            tabNode.setClosable(true);
            tabNode.setContent(controller.getRoot());

            tabNode.setOnClosed(e -> tabs.remove(controller));

            mainTabPane.getTabs().add(tabNode);
            mainTabPane.getSelectionModel().select(tabNode);
            
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Removes all tabs.
     */
    public void clearTabs() {
        mainTabPane.getTabs().clear();
        tabs.clear();
    }

    /**
     * Resets the tabs, adding a new model view tab.
     */
    public void reset() {
        clearTabs();
        addModelViewTab();
    }

    /**
     * Reports a bug to the developers.
     */
    @FXML
    public final void reportBug() {
        ErrorReporter.get().reportManually();
    }

    /**
     * The function that is called to bring up the search window.
     * @param event Clicking the search button on the toolbar.
     */
    public final void search(final ActionEvent event) {
        SearchView.get().setNavigationManager(currentTabbable);
        SearchView.get().show(mainTabPane.getScene().getWindow());
    }


    /**
     * Updates the undo/redo menu to reflect the current undo/redo state.
     * @param change type of change that has been made
     */
    @Override
    public final void undoRedoNotification(final ChangeState change) {
        if (!UndoRedoManager.canRevert()) {
            revert.setDisable(true);
            toolBarController.updateRevertButton(true);
            String undoPrompt = "Undo...";
            undoMenuItem.setDisable(true);
            undoMenuItem.setText(undoPrompt);
            toolBarController.updateUndoButton(true, undoPrompt);
            App.removeTitleStar();
        }
        else {
            revert.setDisable(false);
            toolBarController.updateRevertButton(false);
            String undoPrompt = "Undo " + UndoRedoManager.getRevertMessage();
            undoMenuItem.setDisable(false);
            undoMenuItem.setText(undoPrompt);
            toolBarController.updateUndoButton(false, undoPrompt);
            App.addTitleStar();
        }

        if (!UndoRedoManager.canRemake()) {
            redoMenuItem.setDisable(true);
            String redoPrompt = "Redo...";
            redoMenuItem.setText(redoPrompt);
            toolBarController.updateRedoButton(true, redoPrompt);
        }
        else {
            redoMenuItem.setDisable(false);
            String redoPrompt = "Redo " + UndoRedoManager.getRemakeMessage();
            redoMenuItem.setText(redoPrompt);
            toolBarController.updateRedoButton(false, redoPrompt);
        }
    }

    /**
     * Gets the window of the AppController.
     * @return The window for AppController.
     */
    public final Window getWindow() {
        return window;
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

    @FXML
    private void create(final ActionEvent event) throws InvalidArgumentException {
        if (!(event.getSource() instanceof MenuItem)){
            throw new InvalidArgumentException(new String[]{"event"});
        }

        MenuItem source = (MenuItem)event.getSource();
        ModelType type = ModelType.parseString(source.getId());

        //If we couldn't parse from the id, try the menutext
        if (type == null){
            type = ModelType.parseString(source.getText());
        }

        showCreateWindow(type);
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
            }, "danger-will-robinson");
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
            }, "dont-panic");
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

        //We need to reset.
        reset();
    }

    /**
     * Opens a file.
     * @param event The event that caused the function to be called.
     */
    @FXML
    public final void open(final ActionEvent event) {
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
            }, "danger-will-robinson");
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
            }, "dont-panic");
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
                UndoRedoManager.forget(true);
                UndoRedoManager.importModel(model);

                reset();
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
     * Called when the Quit button is pressed in the
     * file menu and quit the current application.
     * @param event The even that triggers the function
     */
    @FXML
    private void fileQuitPress(final ActionEvent event) {
        mainTabPane.requestFocus();
        if (UndoRedoManager.canRevert() || App.getWindowManager().getAllWindows().size() > 1) {
            GenericPopup popup = new GenericPopup(window);
            popup.setWindowTitle("Still working on something?");
            popup.setTitleText("Looks like you are still working on something.\nOr have unsaved changes.");
            popup.setMessageText("Do you want to,");
            popup.addButton("Discard and Exit", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, () -> {
                popup.close();
                App.getWindowManager().cleanUp();
                Platform.exit();
            }, "danger-will-robinson");
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
                            Platform.runLater(() -> {
                                App.getWindowManager().bringToTop(openWindow, true);
                            });
                        });
            }, "dont-panic");
            popup.show();
        }
        else {
            App.getWindowManager().cleanUp();
            Platform.exit();
        }
    }

    /**
     * Shows the about window.
     * @param actionEvent event arguments.
     */
    @FXML
    private void showAbout(final ActionEvent actionEvent) {
        AboutView aboutWindow = new AboutView(window);
        aboutWindow.show();
    }

    /**
     * Generates a report to a specified location.
     * @param event The event that caused the report to be generated
     */
    @FXML
    public final void generateReport(final ActionEvent event) {
        ReportGeneratorView reportGenerator = new ReportGeneratorView();
        reportGenerator.show();
    }

    /**
     * Toggles a section of the toolbar based on the check menu item selected in the view menu.
     * @param event Clicking on an option in the tool bar section of the view menu.
     */
    @FXML
    private void toolBarToggle(final ActionEvent event) {
        toolBarController.toolBarToggle(event);
    }

    /**
     * Called when the undo menu item has been clicked.
     * @param event event arguments.
     */
    @FXML
    public final void undo(final ActionEvent event) {
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
    public final void redo(final ActionEvent event) {
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
    public final void revert(final ActionEvent event) {
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
                    reset();
                } catch (Exception e) {
                    ErrorReporter.get().reportError(e, "Something went wrong reverting the state of the organisation.");
                }
            }, "danger-will-robinson");
            if (UndoRedoManager.canRevert()) {
                popup.addButton("Save Changes", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -> {
                    // Let the user save the project
                    if (saveAs(null, false)) {
                        try {
                            UndoRedoManager.revert(0);
                            popup.close();
                            // Close all windows which are not the main app.
                            App.getWindowManager().cleanUp();
                            reset();
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
            }, "dont-panic");
            popup.show();
        }
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
    public final boolean save(final ActionEvent event) {
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
    public final boolean saveAs(final ActionEvent event) {
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
     * Shows a new create model window
     * @param type The type of class to create
     */
    public static void showCreateWindow(ModelType type) {
        showCreateWindow(type, null);
    }

    /**
     * Shows a new create model window
     * @param type The type of class to create
     * @param success The callback that should fire upon success
     */
    public static void showCreateWindow(ModelType type, Consumer<Model> success) {
        Class<? extends Model> clazz = ModelType.getTypeFromModel(type);

        try {
            final CreatorWindowView creatorWindow = new CreatorWindowView(clazz.newInstance(), null, null);
            creatorWindow.setCreateAction(model -> {
                if (success != null) {
                    success.accept(model);
                }

                if (creatorWindow != null) {
                    creatorWindow.dispose();
                }
            });
            creatorWindow.setCancelAction(
                    func -> {
                        if (creatorWindow != null) {
                            creatorWindow.dispose();
                        }
                    });
            creatorWindow.show();
        }
        catch (InstantiationException | IllegalAccessException e) {
            ErrorReporter.get().reportError(e, "Initialising a creation window failed");
        }
    }
}
