package sws.murcs.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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
import java.util.List;

/**
 * Main app class controller.
 */
public class AppController implements ViewUpdate<Model>, UndoRedoChangeListener {

    /**
     * The Menu items for the main window.
     */
    @FXML
    private MenuItem fileQuit, undoMenuItem, redoMenuItem, open, save, saveAs, generateReport, addProject, newModel,
            addTeam, addPerson, addSkill, addRelease, addStory, addBacklog, showHide, revert;
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
     * Initialises the GUI, setting up the the options in the choice box and populates the display list if necessary.
     * Put all initialisation of GUI in this function.
     */
    @FXML
    public final void initialize() {

        NavigationManager.setAppController(this);
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
            if (oldValue != newValue) {
                if (newValue == null) {
                    if (editorPane != null) {
                        editorPane.dispose();
                        editorPane = null;
                        contentPane.getChildren().clear();
                    }
                    return;
                }

                NavigationManager.navigateTo((Model) newValue);
                updateBackForwardButtons();

                if (oldValue == null) {
                    displayList.scrollTo(newValue);
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
        revert.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN
                , KeyCombination.SHIFT_DOWN));
        newModel.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN,
                KeyCombination.SHIFT_DOWN));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        generateReport.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));
        addProject.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
        addPerson.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.SHIFT_DOWN,
                KeyCombination.CONTROL_DOWN));
        addRelease.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        addSkill.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN,
                KeyCombination.CONTROL_DOWN));
        addTeam.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
        addBacklog.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN));
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
        if (new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHIFT_DOWN).match(event)) {
            addClicked(null);
        }
        if (new KeyCodeCombination(KeyCode.DELETE).match(event)) {
            removeClicked(null);
        }
        if (new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN).match(event)) {
            backClicked(null);
        }
        if (new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN).match(event)) {
            forwardClicked(null);
        }
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
                if (save()) {
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
            GenericPopup popup = new GenericPopup(e);
            popup.show();
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
            showSaveFailedDialog();
        }
        return false;
    }

    /**
     * Handles the creation of a new Model.
     * @param event The event that causes this function to be called, namely clicking save.
     */
    @FXML
    private void newModel(final ActionEvent event) {
        try {
            if (UndoRedoManager.canRevert()) {
                GenericPopup popup = new GenericPopup();
                popup.setWindowTitle("Unsaved Changes");
                popup.setTitleText("Do you wish to save changes?");
                popup.setMessageText("You have unsaved changes.");
                popup.addButton("Discard", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, m -> {
                    popup.close();
                    try {
                        createNewModel();
                    } catch (Exception e) {
                        GenericPopup errorPopup = new GenericPopup(e);
                        errorPopup.show();
                    }
                });
                popup.addButton("Save", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, m -> {
                    // Let the user save the project
                    if (save()) {
                        popup.close();
                        try {
                            createNewModel();
                        } catch (Exception e) {
                            showSaveFailedDialog();
                        }
                    }
                    else {
                        showSaveFailedDialog();
                    }
                });
                popup.addButton("Cancel", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, m -> popup.close());
                popup.show();
            }
            else {
                createNewModel();
            }
        }
        catch (Exception e) {
            showSaveFailedDialog();
        }
    }

    /**
     * Shows a failed save dialog.
     */
    private void showSaveFailedDialog() {
        GenericPopup errorPopup = new GenericPopup();
        String message = "Something went wrong saving";
        errorPopup.setTitleText("Something went wrong");
        errorPopup.setMessageText(message);
        errorPopup.addButton("Ok", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, v -> errorPopup.close());
        errorPopup.show();
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
     * Opens a specified project file, from a specified location.
     * @param event The event that caused the function to be called.
     */
    @FXML
    private void open(final ActionEvent event) {
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
        new ReportGeneratorView().show();
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
     * Reverts the the model to its original save state.
     * @param event event arguments.
     */
    @FXML
    private void revert(final ActionEvent event) {
        if (UndoRedoManager.canRevert()) {
            GenericPopup popup = new GenericPopup();
            popup.setWindowTitle("Revert Changes");
            popup.setTitleText("Do you wish to revert changes?");
            popup.setMessageText("You have unsaved changes.\n "
                    + "If you wish to save your current changes as a new file click \'Save As\'.");
            popup.addButton("Yes", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, m -> {
                popup.close();
                try {
                    UndoRedoManager.revert(0);
                } catch (Exception e) {
                    GenericPopup errorPopup = new GenericPopup(e);
                    errorPopup.show();
                }
                selectItem(null);
            });
            popup.addButton("Save As", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, m -> {
                // Let the user save the project
                if (saveAs(null, false)) {
                    popup.close();
                    try {
                        UndoRedoManager.revert(0);
                    } catch (Exception e) {
                        GenericPopup errorPopup = new GenericPopup(e);
                        errorPopup.show();
                    }
                }
                else {
                    showSaveFailedDialog();
                }
            });
            popup.addButton("No", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, m -> popup.close());
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
        GenericPopup popup = new GenericPopup();
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

        popup.addButton("Yes", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, v -> {
            popup.close();
            NavigationManager.clearHistory();
            Model item = (Model) displayList.getSelectionModel().getSelectedItem();
            model.remove(item);
            updateBackForwardButtons();
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
            displayList.scrollTo(0);
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
}
