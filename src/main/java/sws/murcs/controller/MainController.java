package sws.murcs.controller;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sws.murcs.controller.controls.tabs.tabpane.DnDTabPane;
import sws.murcs.controller.controls.tabs.tabpane.DnDTabPaneFactory;
import sws.murcs.controller.controls.tabs.tabpane.skin.AddableDnDTabPaneSkin;
import sws.murcs.controller.editor.BacklogEditor;
import sws.murcs.controller.pipes.Navigable;
import sws.murcs.controller.pipes.NavigableTabController;
import sws.murcs.controller.pipes.Tabbable;
import sws.murcs.controller.pipes.ToolBarCommands;
import sws.murcs.controller.windowManagement.ShortcutManager;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;
import sws.murcs.internationalization.InternationalizationHelper;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Organisation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.reporting.ui.ReportGeneratorView;
import sws.murcs.view.AboutView;
import sws.murcs.view.App;
import sws.murcs.view.CreatorWindowView;
import sws.murcs.view.SearchView;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * A controller for the base pane.
 */
public class MainController implements UndoRedoChangeListener, ToolBarCommands, Navigable {
    /**
     * The main border pane for the application.
     */
    @FXML
    private BorderPane borderPaneMain;

    /**
     * The Menu bar for the application.
     */
    @FXML
    private MenuBar menuBar;

    /**
     * A menu that lets you choose the language.
     */
    @FXML
    private Menu languageMenu;

    /**
     * The Menu items for the main window.
     */
    @FXML
    private MenuItem fileQuit, undoMenuItem, redoMenuItem, open, save, saveAs, generateReport, addProject, newModel,
            addTeam, addPerson, addSkill, addRelease, addStory, addBacklog, revert, highlightToggle, addSprint,
            reportBug;

    /**
     * The menu item for hiding the sidebar.
     */
    @FXML
    private CheckMenuItem showHide;

    /**
     * The menu that contains the check menu items for toggling sections of the toolbar.
     */
    @FXML
    private Menu toolBarMenu;

    /**
     * The top toolbar and menu container.
     */
    @FXML
    private VBox titleVBox;

    /**
     * The main tab pane (where everything goes). We have to build
     * this, as it's a third party library.
     */
    private TabPane mainTabPane;

    /**
     * A collection of all the tabbable objects in all tabs in all windows.
     */
    private static Collection<Tabbable> tabs = new ArrayList<>();

    /**
     * The current controller for the toolbar.
     */
    private ToolBarController toolBarController;

    /**
     * The current tab controller.
     */
    private Tabbable currentTabbable;

    /**
     * Stores and instance of the main app window.
     */
    private Window window;

    /**
     * Initializes the form.
     */
    @FXML
    public final void initialize() {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }

        if (os != null && os.startsWith("Linux")) {
            /* Yes. I know. This is terrible.
               There is some random problem with the render that means black boxes appear.
               Sometimes one runLater is sufficient, but usually two are required. Why you
               may ask? I have no idea. Cause Java.
               1st runLater: (in theory) called after window is created.
               2nd runLater: (in theory) called after the window is shown. */
            Platform.runLater(() -> Platform.runLater(() -> {
                Stage stage = getWindow().getStage();
                stage.setWidth(stage.getWidth() + 1);
                stage.setHeight(stage.getHeight() + 1);
            }));
        }

        List<String> languages = InternationalizationHelper.getLanguages();
        for (String language : languages) {
            MenuItem lang = new MenuItem(language);
            lang.setOnAction((a) -> changeLanguage(language));
            languageMenu.getItems().add(lang);
        }

        Pane containerPane = buildDnDTabPane();
        borderPaneMain.setCenter(containerPane);

        //Get the tab pane and set it to our main pane.
        mainTabPane = (DnDTabPane) containerPane.getChildren().get(0);

        mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //If all the tabs have been closed, return.
            if (newValue == null) {
                return;
            }

            Tabbable tabbable = getTabbable(newValue);
            if (tabbable == null) {
                return;
            }

            showHide.setSelected(tabbable.sideBarVisible());
            showHide.setDisable(!tabbable.canToggleSideBar());

            toolBarController.setModelManagable(tabbable);

            currentTabbable = tabbable;
            toolBarController.updateBackForwardButtons();

            //Run this later so the model definitely saves before we change tab.
            Platform.runLater(() -> currentTabbable.update());
        });

        mainTabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            c.next();
            if (c.wasAdded()) {
                boolean isClosable = mainTabPane.getTabs().size() > 1;
                mainTabPane.getTabs().stream().forEach(t -> {
                    t.setClosable(isClosable);
                });
            }
            if (c.wasRemoved()) {
                if (mainTabPane.getTabs().size() == 1) {
                    mainTabPane.getTabs().stream().forEach(t -> {
                        t.setClosable(false);
                    });
                }
            }
        });

        loadToolbar();
        toolBarController.setLinkedController(this);
        toolBarController.setNavigable(this);
        toolBarController.setToolBarMenu(toolBarMenu);

        undoRedoNotification(ChangeState.Commit);
        UndoRedoManager.get().addChangeListener(this);

        addModelViewTab(mainTabPane);
    }

    /**
     * Sets the current language of the application.
     * @param language language to set the language to.
     */
    private void changeLanguage(final String language) {
        //Hacky save. Dion Vader says it's okay.
        borderPaneMain.requestFocus();
        InternationalizationHelper.setLanguage(language);
        Organisation org = PersistenceManager.getCurrent().getCurrentModel();
        if (org != null) {
            org.setCurrentLanguage(language);
        }
        Scene scene = borderPaneMain.getScene();
        MainController controller = App.loadRootNode();
        scene.setRoot(controller.getRootNode());

        App.getWindowManager().cleanUp();
        controller.window = window;
        controller.setupShortcuts();

        SearchView.restart();
    }

    /**
     * Builds a new DnDTabPane and returns it's container.
     * @return The container of the DnDTabPane
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private Pane buildDnDTabPane() {
        DnDTabPane tabPane = new DnDTabPane();

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabPane.setPrefWidth(200);
        tabPane.setPrefHeight(200);

        AnchorPane containerPane = new AnchorPane();
        containerPane.getChildren().add(tabPane);
        AnchorPane.setRightAnchor(tabPane, 0.0);
        AnchorPane.setTopAnchor(tabPane, 0.0);
        AnchorPane.setBottomAnchor(tabPane, 0.0);
        AnchorPane.setLeftAnchor(tabPane, 0.0);

        AddableDnDTabPaneSkin skin = new AddableDnDTabPaneSkin(containerPane, tabPane);
        skin.setTabFactory(pane -> {
            Tabbable t = addModelViewTab(pane, false);
            return t.getTab();
        });
        DnDTabPaneFactory.setup(DnDTabPaneFactory.FeedbackType.MARKER, containerPane, skin);
        skin.addDropListener((event, tab) -> {
            //If the event has already been accepted, we don't want to move the tab to a new window.
            if (event.isAccepted()) {
                return;
            }

            tab.setClosable(true);
            PointerInfo info = MouseInfo.getPointerInfo();
            Point awtPoint = info.getLocation();

            Point2D mousePos = new Point2D(awtPoint.getX(), awtPoint.getY());

            createWindow(mousePos, tab);
        });

        tabPane.setSkin(skin);
        return containerPane;
    }

    /**
     * Attempts to find the tabbable associated with a tab.
     * @param tab The tab
     * @return The tabbable (null if not found).
     */
    private Tabbable getTabbable(final Tab tab) {
        return tabs
                .stream()
                .filter(t -> tab.getContent().equals(t.getRoot())).findFirst()
                .orElse(null);
    }

    /**
     * Creates a new window, at the specified position.
     * @param mousePos The mouse position
     * @param tab The tab
     */
    private void createWindow(final Point2D mousePos, final Tab tab) {
        Tabbable tabbable = getTabbable(tab);
        tab.getTabPane().getTabs().remove(tab);

        Stage stage = new Stage();
        stage.setTitle(tabbable.getTitle().getValue());
        tabbable.getTitle().addListener((observable, oldValue, newValue) -> stage.setTitle(newValue));

        Pane containerPane = buildDnDTabPane();
        DnDTabPane tabPane = (DnDTabPane) containerPane.getChildren().get(0);
        tabPane.getTabs().add(tab);

        final NavigableTabController navigable = new NavigableTabController();
        navigable.setCurrentTab(tabbable);

        final Tabbable[] currentTab = new Tabbable[1];
        currentTab[0] = tabbable;

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                stage.close();
            }

            for (Tabbable t : tabs) {
                if (t.getTab() == newValue) {
                    navigable.setCurrentTab(t);
                    currentTab[0] = t;
                    break;
                }
            }
        });

        BorderPane root = new BorderPane();
        root.setMinWidth(borderPaneMain.getPrefWidth());
        root.setMinHeight(borderPaneMain.getPrefHeight());
        root.setCenter(containerPane);

        Scene scene = new Scene(root);
        scene.getStylesheets()
                .add(App.class
                        .getResource("/sws/murcs/styles/global.css")
                        .toExternalForm());
        stage.setScene(scene);

        //Add shortcuts
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.W, KeyCodeCombination.SHORTCUT_DOWN),
                () -> tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedItem()));
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.T, KeyCodeCombination.SHORTCUT_DOWN),
                () -> addModelViewTab(tabPane));

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo/logo_small.png")));
        stage.getIcons().add(iconImage);

        Window newWindow = new Window(stage, currentTab, window);
        newWindow.register();
        newWindow.addGlobalShortcutsToWindow();

        addNavigationShortcuts(newWindow.getStage(), navigable);

        stage.show();
        stage.setX(mousePos.getX());
        stage.setY(mousePos.getY());
        stage.setMinWidth(borderPaneMain.getPrefWidth());
        stage.setMinHeight(borderPaneMain.getPrefHeight());
    }

    /**
     * Adds the toolbar to the application window.
     */
    private void loadToolbar() {
        try {
            FXMLLoader loader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/ToolBar.fxml"));
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
    private void setupShortcuts() {
        //Menu item shortcuts
        // You should use SHORTCUT_DOWN as it uses the COMMAND key for Mac and the CTRL key for Windows
        ShortcutManager shortcutManager = App.getShortcutManager();

        //Global shortcuts
        revert.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN,
                KeyCombination.SHIFT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN,
                KeyCombination.SHIFT_DOWN), () -> revert(null));
        newModel.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN),
                () -> newModel(null));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                this::save);
        saveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN,
                KeyCombination.SHIFT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN,
                KeyCombination.SHIFT_DOWN), () -> saveAs(null));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN),
                () -> open(null));
        generateReport.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.G, KeyCombination.SHORTCUT_DOWN),
                () -> generateReport(null));
        reportBug.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN),
                () -> ErrorReporter.get().reportManually());
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN),
                () -> search(null));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.SPACE, KeyCombination.SHORTCUT_DOWN),
                () -> search(null));
        addProject.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Project, ((ModelViewController) currentTabbable)::selectItem));
        addTeam.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Team, ((ModelViewController) currentTabbable)::selectItem));
        addPerson.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Person, ((ModelViewController) currentTabbable)::selectItem));
        addSkill.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Skill, ((ModelViewController) currentTabbable)::selectItem));
        addRelease.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT5, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT5, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Release, ((ModelViewController) currentTabbable)::selectItem));
        addBacklog.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT6, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT6, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Backlog, ((ModelViewController) currentTabbable)::selectItem));
        addStory.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT7, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT7, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Story, ((ModelViewController) currentTabbable)::selectItem));
        addSprint.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT8, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DIGIT8, KeyCombination.SHORTCUT_DOWN),
                () -> showCreateWindow(ModelType.Sprint, ((ModelViewController) currentTabbable)::selectItem));
        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN),
                () -> undo(null));
        redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
                () -> redo(null));
        showHide.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.SHORTCUT_DOWN));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.H, KeyCombination.SHORTCUT_DOWN),
                () -> {
                    Object controller = App.getWindowManager().getTop().getController();
                    if (controller instanceof Tabbable[]) {
                        Tabbable[] modelController = (Tabbable[]) controller;
                        modelController[0].toggleSideBar(!modelController[0].sideBarVisible());
                    }
                    else {
                        currentTabbable.toggleSideBar(!currentTabbable.sideBarVisible());
                    }
                });
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.EQUALS),
                () -> currentTabbable.create());
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.DELETE),
                () -> currentTabbable.remove());
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN),
                () -> mainTabPane.getTabs().remove(mainTabPane.getSelectionModel().getSelectedItem()));
        shortcutManager.registerShortcut(new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN),
                () -> addModelViewTab(mainTabPane));

        //Make sure we can navigate from this window.
        addNavigationShortcuts(window.getStage(), this);
    }

    /**
     * Adds shortcuts for navigation to a stage.
     * @param stage The stage to add the shortcuts to
     * @param navigationController The navigation controller to use
     */
    private void addNavigationShortcuts(final Stage stage, final Navigable navigationController) {
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.SHORTCUT_DOWN),
                navigationController::goBack);
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.PERIOD, KeyCombination.SHORTCUT_DOWN),
                navigationController::goForward);

        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.SHORTCUT_DOWN,
                KeyCodeCombination.SHIFT_DOWN), () -> navigationController.navigateTo(ModelType.Project));
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHORTCUT_DOWN,
                KeyCodeCombination.SHIFT_DOWN), () -> navigationController.navigateTo(ModelType.Team));
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.SHORTCUT_DOWN,
                KeyCodeCombination.SHIFT_DOWN), () -> navigationController.navigateTo(ModelType.Person));
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.SHORTCUT_DOWN,
                KeyCodeCombination.SHIFT_DOWN), () -> navigationController.navigateTo(ModelType.Skill));
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT5, KeyCombination.SHORTCUT_DOWN,
                KeyCodeCombination.SHIFT_DOWN), () -> navigationController.navigateTo(ModelType.Release));
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT6, KeyCombination.SHORTCUT_DOWN,
                KeyCodeCombination.SHIFT_DOWN), () -> navigationController.navigateTo(ModelType.Backlog));
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT7, KeyCombination.SHORTCUT_DOWN,
                KeyCodeCombination.SHIFT_DOWN), () -> navigationController.navigateTo(ModelType.Story));
        stage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT8, KeyCombination.SHORTCUT_DOWN,
                KeyCodeCombination.SHIFT_DOWN), () -> navigationController.navigateTo(ModelType.Sprint));
    }

    /**
     * Adds a model view tab to the main pane.
     * @param tabPane The pane to add the tab to
     * @return The newly created tabbable
     */
    public ModelViewController addModelViewTab(final TabPane tabPane) {
        return addModelViewTab(tabPane, true);
    }

    /**
     * Adds a model view tab to the main pane.
     * @param tabPane The pane to add the tab to
     * @param addToPane Indicates whether the tab should be added to the pane
     * @return The newly created model controller.
     */
    public ModelViewController addModelViewTab(final TabPane tabPane, final boolean addToPane) {
        return (ModelViewController) addTab("/sws/murcs/ModelView.fxml", tabPane, addToPane);
    }

    /**
     * Adds a tab to the pane.
     * @param fxmlPath The path for the fxml to load
     * @param tabPane The tabpane to add the tab to
     * @param addToPane Indicates whether the tab should be automatically added to the tab pane.
     * @return The newly created tabbable
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public Tabbable addTab(final String fxmlPath, final TabPane tabPane, final boolean addToPane) {
        try {
            FXMLLoader loader = new AutoLanguageFXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Tabbable controller = loader.getController();
            controller.registerMainController(this);
            controller.setToolBarController(toolBarController);
            tabs.add(controller);

            Tab tabNode = new Tab();
            tabNode.setClosable(true);
            tabNode.setOnCloseRequest(e -> {
                Tab t = (Tab) e.getSource();
                Tabbable tabbable = getTabbable(t);
                tabs.remove(tabbable);
            });

            tabNode.setContent(controller.getRoot());
            controller.setTab(tabNode);

            Label tabLabel = new Label(controller.getTitle().getValue());
            tabLabel.setFocusTraversable(false);
            tabLabel.setMinWidth(30);
            tabLabel.setPrefWidth(100);
            tabLabel.setMaxWidth(100);
            controller.getTitle().addListener((observable, oldValue, newValue) -> tabLabel.setText(newValue));
            tabNode.setGraphic(tabLabel);

            tabNode.setOnClosed(e -> tabs.remove(controller));

            if (addToPane) {
                tabPane.getTabs().add(tabNode);
                tabPane.getSelectionModel().select(tabNode);
            }

            return controller;
        } catch (IOException e) {
            ErrorReporter.get().reportErrorSecretly(e, "Failed to load tabs");
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
        addModelViewTab(mainTabPane);
    }

    /**
     * Reports a bug to the developers.
     */
    @FXML
    public final void reportBug() {
        ErrorReporter.get().reportManually();
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
     * The function that is called to bring up the search window.
     * @param event Clicking the search button on the toolbar.
     */
    public final void search(final ActionEvent event) {
        SearchView.get().show(mainTabPane.getScene().getWindow());
    }


    /**
     * Updates the undo/redo menu to reflect the current undo/redo state.
     * @param change type of change that has been made
     */
    @Override
    public final void undoRedoNotification(final ChangeState change) {
        if (!UndoRedoManager.get().canRevert()) {
            revert.setDisable(true);
            toolBarController.updateRevertButton(true);
            String undoPrompt = InternationalizationHelper.translatasert("{Undo}...");
            undoMenuItem.setDisable(true);
            undoMenuItem.setText(undoPrompt);
            toolBarController.updateUndoButton(true, undoPrompt);
            App.removeTitleStar();
        }
        else {
            revert.setDisable(false);
            toolBarController.updateRevertButton(false);
            String translated = "";
            String[] strs = UndoRedoManager.get().getRevertMessage().split(" ");
            for (int i = 0; i < strs.length; i++) {
                if (i != 0) {
                    translated += " ";
                }
                translated += "{" + strs[i] + "}";
            }
            String undoPrompt = InternationalizationHelper.tryGet("Undo") + " "
                    + InternationalizationHelper.translatasert(translated);
            undoMenuItem.setDisable(false);
            undoMenuItem.setText(undoPrompt);
            toolBarController.updateUndoButton(false, undoPrompt);
            App.addTitleStar();
        }

        if (!UndoRedoManager.get().canRemake()) {
            redoMenuItem.setDisable(true);
            String redoPrompt = InternationalizationHelper.translatasert("{Redo}...");
            redoMenuItem.setText(redoPrompt);
            toolBarController.updateRedoButton(true, redoPrompt);
        }
        else {
            redoMenuItem.setDisable(false);
            String translated = "";
            String[] strs = UndoRedoManager.get().getRemakeMessage().split(" ");
            for (int i = 0; i < strs.length; i++) {
                if (i != 0) {
                    translated += " ";
                }
                translated += "{" + strs[i] + "}";
            }
            String redoPrompt = InternationalizationHelper.tryGet("Redo") + " "
                    + InternationalizationHelper.translatasert(translated);
            redoMenuItem.setText(redoPrompt);
            toolBarController.updateRedoButton(false, redoPrompt);
        }

        //Update all the tabs that are selected.
        for (Tabbable t : tabs) {
            TabPane pane = t.getTab().getTabPane();
            if (pane == null) {
                continue;
            }

            if (pane.getSelectionModel().getSelectedItem() == t.getTab()) {
                t.update();
            }
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
        setupShortcuts();
    }

    /**
     * Beings piping a "Create" event.
     * @param event The event that brought us here
     * @throws UnsupportedOperationException If the create operation requested is not supported.
     */
    @FXML
    private void create(final ActionEvent event) throws UnsupportedOperationException {
        if (!(event.getSource() instanceof MenuItem)) {
            throw new UnsupportedOperationException("event");
        }

        MenuItem source = (MenuItem) event.getSource();
        ModelType type = ModelType.parseString(source.getId());

        //If we couldn't parse from the id, try the menutext
        if (type == null) {
            type = ModelType.parseString(source.getText());
        }

        showCreateWindow(type, ((ModelViewController) currentTabbable)::selectItem);
    }

    /**
     * Handles the creation of a new Model.
     * @param event The event that causes this function to be called, namely clicking save.
     */
    @FXML
    private void newModel(final ActionEvent event) {
        if (UndoRedoManager.get().canRevert() || App.getWindowManager().getAllWindows().size() > 1) {
            GenericPopup popup = new GenericPopup(window);
            popup.setWindowTitle("{StillWorking}");
            popup.setTitleText("{LooksLikeStillWorking}");
            popup.setMessageText("{Doyouwantto}");
            popup.addButton("{DiscardThem}", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, () -> {
                popup.close();
                try {
                    // Close all windows which are not the main app.
                    App.getWindowManager().cleanUp();
                    createNewModel();
                } catch (Exception e) {
                    ErrorReporter.get().reportError(e, "Something went wrong creating a new organisation :(");
                }
            }, "danger-will-robinson");
            if (UndoRedoManager.get().canRevert()) {
                popup.addButton("{Save}", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -> {
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
            popup.addButton("{Cancel}", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, () -> {
                popup.close();
                App.getWindowManager().getAllWindows()
                        .stream()
                        .filter(openWindow -> openWindow.getController().getClass() != this.getClass())
                        .forEach(openWindow -> {
                            App.getWindowManager().bringToTop(openWindow, true);
                        });
            }, "everything-is-fine");
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
        UndoRedoManager.get().importModel(model);
        UndoRedoManager.get().forget();
        model.setCurrentLanguage(InternationalizationHelper.getCurrentLanguage());
        //We need to reset.
        reset();
    }

    /**
     * Opens a file.
     * @param event The event that caused the function to be called.
     */
    @FXML
    public final void open(final ActionEvent event) {
        if (UndoRedoManager.get().canRevert() || App.getWindowManager().getAllWindows().size() > 1) {
            GenericPopup popup = new GenericPopup(window);
            popup.setWindowTitle("{StillWorking}");
            popup.setTitleText("{LooksLikeStillWorking}");
            popup.setMessageText("{Doyouwantto}");
            popup.addButton("{DiscardThem}", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, () -> {
                if (openFile()) {
                    popup.close();
                    // Close all windows which are not the main app.
                    App.getWindowManager().cleanUp();
                }
            }, "danger-will-robinson");
            if (UndoRedoManager.get().canRevert()) {
                popup.addButton("{SaveThem}", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -> {
                    // Let the user save the project
                    if (save() && openFile()) {
                        popup.close();
                        // Close all windows which are not the main app.
                        App.getWindowManager().cleanUp();
                    }
                });
            }
            popup.addButton("{Cancel}", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, () -> {
                popup.close();
                App.getWindowManager().getAllWindows()
                        .stream()
                        .filter(openWindow -> openWindow.getController().getClass() != this.getClass())
                        .forEach(openWindow -> {
                            App.getWindowManager().bringToTop(openWindow, true);
                        });
            }, "everything-is-fine");
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
                    .add(new FileChooser.ExtensionFilter("Organisations (*.project)", "*.project"));
            fileChooser.setInitialDirectory(new File(PersistenceManager.getCurrent().getCurrentWorkingDirectory()));
            fileChooser.setTitle("Select Project");
            File file = fileChooser.showOpenDialog(App.getStage());
            if (file != null) {
                PersistenceManager.getCurrent().setCurrentWorkingDirectory(file.getParentFile().getAbsolutePath());
                Organisation model = PersistenceManager.getCurrent().loadModel(file.getName());
                if (model == null) {
                    throw new Exception("Organisation was not opened.");
                }
                PersistenceManager.getCurrent().setCurrentModel(model);
                UndoRedoManager.get().forget(true);
                UndoRedoManager.get().importModel(model);
                changeLanguage(model.getCurrentLanguage());
                reset();
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception e) {
            GenericPopup popup = new GenericPopup(window);
            popup.setTitleText("{OldorCorrupt}");
            popup.setMessageText("{FromOldVersion}");
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
        if (UndoRedoManager.get().canRevert() || App.getWindowManager().getAllWindows().size() > 1) {
            GenericPopup popup = new GenericPopup(window);
            popup.setWindowTitle("{StillWorking}");
            popup.setTitleText("{LooksLikeStillWorking}");
            popup.setMessageText("{Doyouwantto}");
            popup.addButton("{DiscardandExit}", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, () -> {
                popup.close();
                App.getWindowManager().cleanUp();
                Platform.exit();
            }, "danger-will-robinson");
            if (UndoRedoManager.get().canRevert()) {
                popup.addButton("{SaveandExit}", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -> {
                    // Let the user save the project
                    if (save()) {
                        popup.close();
                        App.getWindowManager().cleanUp();
                        Platform.exit();
                    }
                });
            }
            popup.addButton("{Cancel}", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, () -> {
                popup.close();
                App.getWindowManager().getAllWindows()
                        .stream()
                        .filter(openWindow -> openWindow.getController().getClass() != this.getClass())
                        .forEach(openWindow -> {
                            Platform.runLater(() -> {
                                App.getWindowManager().bringToTop(openWindow, true);
                            });
                        });
            }, "everything-is-fine");
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
     * Toggles a the side bar list.
     * @param event The event information
     */
    @FXML
    private void toggleItemListView(final ActionEvent event) {
        CheckMenuItem source = (CheckMenuItem) event.getSource();
        currentTabbable.toggleSideBar(source.isSelected());
    }

    /**
     * Called when the undo menu item has been clicked.
     * @param event event arguments.
     */
    @FXML
    public final void undo(final ActionEvent event) {
        try {
            if (!undoMenuItem.isDisable()) {
                UndoRedoManager.get().revert();
            }
        }
        catch (Exception e) {
            // Something went very wrong
            UndoRedoManager.get().forget();
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
            if (!redoMenuItem.isDisable()) {
                UndoRedoManager.get().remake();
            }
        }
        catch (Exception e) {
            // something went terribly wrong....
            UndoRedoManager.get().forget();
            ErrorReporter.get().reportError(e, "Undo-redo failed to remake");
        }
    }

    /**
     * Reverts the the model to its original save state.
     * @param event event arguments.
     */
    @FXML
    public final void revert(final ActionEvent event) {
        if (UndoRedoManager.get().canRevert() || App.getWindowManager().getAllWindows().size() > 1) {
            GenericPopup popup = new GenericPopup(window);
            popup.setWindowTitle("{Revert}");
            popup.setTitleText("{RevertChangesQuestion}");
            popup.setMessageText("{LooksLikeStillWorking} {UnsavedChangesWillBeLost}");
            popup.addButton("{RevertChanges}", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, () -> {
                try {
                    UndoRedoManager.get().revert(0);
                    popup.close();
                    // Close all windows which are not the main app.
                    App.getWindowManager().cleanUp();
                    reset();
                } catch (Exception e) {
                    ErrorReporter.get().reportError(e, "Something went wrong reverting the state of the organisation.");
                }
            }, "danger-will-robinson");
            if (UndoRedoManager.get().canRevert()) {
                popup.addButton("{SaveAs}", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -> {
                    // Let the user save the project
                    if (saveAs(null, false)) {
                        try {
                            UndoRedoManager.get().revert(0);
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
            popup.addButton("{Cancel}", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, () -> {
                popup.close();
                App.getWindowManager().getAllWindows()
                        .stream()
                        .filter(openWindow -> openWindow.getController().getClass() != this.getClass())
                        .forEach(openWindow -> {
                            App.getWindowManager().bringToTop(openWindow, true);
                        });
            }, "everything-is-fine");
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
                UndoRedoManager.get().forget();
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
                    UndoRedoManager.get().forget();
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
     * Shows a new create model window.
     * @param type The type of class to create
     */
    public static void showCreateWindow(final ModelType type) {
        showCreateWindow(type, null);
    }

    /**
     * Shows a new create model window.
     * @param type The type of class to create
     * @param success The callback that should fire upon success
     */
    public static void showCreateWindow(final ModelType type, final Consumer<Model> success) {
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

    @Override
    public void goForward() {
        currentTabbable.goForward();
    }

    @Override
    public void goBack() {
        currentTabbable.goBack();
    }

    @Override
    public boolean canGoForward() {
        return currentTabbable.canGoForward();
    }

    @Override
    public boolean canGoBack() {
        return currentTabbable.canGoBack();
    }

    @Override
    public void navigateTo(final Model model) {
        currentTabbable.navigateTo(model);
    }

    @Override
    public void navigateTo(final ModelType type) {
        currentTabbable.navigateTo(type);
    }

    @Override
    public void navigateToNewTab(final Model model) {
        ModelViewController controller = addModelViewTab(mainTabPane);
        controller.navigateTo(model);
    }

    /**
     * Gets the currently selected model type.
     * @return The current model type
     */
    public ModelType getCurrentModelType() {
        return currentTabbable.getCurrentModelType();
    }

    /**
     * Gets the root node of the controller.
     * @return The root node
     */
    public Parent getRootNode() {
        return borderPaneMain;
    }
}
