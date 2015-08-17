package sws.murcs.view;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sws.murcs.controller.MainController;
import sws.murcs.controller.windowManagement.ShortcutManager;
import sws.murcs.controller.windowManagement.WindowManager;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.debug.sampledata.OrganisationGenerator;
import sws.murcs.listeners.AppClosingListener;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Organisation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The main app class.
 */
@SuppressWarnings("ALL")
public class App extends Application {

    /**
     * Default window title to use.
     */
    private static final String DEFAULT_WINDOW_TITLE = "- Untitled -";

    /**
     * The main stage of the application.
     */
    private static Stage stage;

    /**
     * An list of listeners relating to the app closing.
     */
    private static List<AppClosingListener> listeners = new ArrayList<>();

    /**
     * The minimum height of the application.
     */
    private static final int MINIMUM_APPLICATION_HEIGHT = 700;

    /**
     * The minimum width of the application.
     */
    private static final int MINIMUM_APPLICATION_WIDTH = 900;

    /**
     * The subString length to search over, when parsing debugging mode.
     */
    private static final int SUBSTRINGLENGTH = 3;

    /**
     * The current main controller.
     */
    private static MainController mainController;

    /**
     * The manager for all windows.
     */
    private static WindowManager windowManager;

    /**
     * The manager for global shortcuts.
     */
    private static ShortcutManager shortcutManager;

    /**
     * Gets the shortcut manager.
     * @return The shortcut manager.
     */
    public static ShortcutManager getShortcutManager() {
        return shortcutManager;
    }

    /**
     * Gets the window manager.
     * @return The window manager
     */
    public static WindowManager getWindowManager() {
        return windowManager;
    }

    /**
     * Gets the stage of App.
     * @return The App stage
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Sets the stage of App.
     * @param pStage The new App stage
     */
    public static void setStage(final Stage pStage) {
        stage = pStage;
    }

    /**
     * Changes the title of the window.
     * @param newTitle The new title to use for the window.
     * If the newTitle has a file extension, the file extension will be removed.
     */
    public static void setWindowTitle(final String newTitle) {
        if (stage == null) {
            return;
        }
        int index = newTitle.lastIndexOf('.');
        String title = newTitle;
        if (index >= 0) {
            title = newTitle.substring(0, index);
        }
        stage.setTitle(title);
    }

    /**
     * Adds a star to the start of the window title.
     * If there is already a star, the window title will remain unchanged.
     */
    public static void addTitleStar() {
        if (stage == null) {
            return;
        }
        // for off thread rendering
        Platform.runLater(() -> {
            String title = stage.getTitle();
            if (title.charAt(0) != '*') {
                title = '*' + title;
                stage.setTitle(title);
            }
        });
    }

    /**
     * Removes a star from the beginning window title.
     * If no star exists, the window title will remain unchanged.
     */
    public static void removeTitleStar() {
        if (stage == null) {
            return;
        }
        Platform.runLater(() -> {
            String title = stage.getTitle();
            if (title.charAt(0) == '*') {
                title = title.substring(1);
                stage.setTitle(title);
            }
        });
    }

    /***
     * Starts up the application and sets the min window size to 600x400.
     * @param primaryStage The main Stage
     * @throws Exception A loading exception from loading the fxml
     */
    @Override
    public final void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle(DEFAULT_WINDOW_TITLE);
        setStage(primaryStage);
        createWindow(primaryStage);
    }

    /**
     * Creates a new MainWindow.
     * @param window The stage to load the window onto
     */
    public static void createWindow(final Stage window) {
        if (!PersistenceManager.currentPersistenceManagerExists()) {
            FilePersistenceLoader loader = new FilePersistenceLoader();
            PersistenceManager.setCurrent(new PersistenceManager(loader));
        }

        if (windowManager == null) {
            windowManager = new WindowManager();
        }

        if (shortcutManager == null) {
            shortcutManager = new ShortcutManager();
        }

        // Loads the primary fxml and sets mainController as its controller
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource("/sws/murcs/MainView.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            //We should never hit this, if we managed to start the application
            ErrorReporter.get().reportErrorSecretly(e, "Couldn't open a MainWindow :'(");
        }
        mainController = loader.getController();

        Scene scene = new Scene(parent);
        scene.getStylesheets()
                .add(App.class
                        .getResource("/sws/murcs/styles/global.css")
                        .toExternalForm());
        window.setScene(scene);
        window.setTitle(DEFAULT_WINDOW_TITLE);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo/logo_small.png")));
        window.getIcons().add(iconImage);

        // Set up max and min dimensions of main window
        window.setMinWidth(MINIMUM_APPLICATION_WIDTH);
        window.setMinHeight(MINIMUM_APPLICATION_HEIGHT);

        mainController.show();
    }

    /**
     * Call quit on all of the event listeners.
     * @param e Window event to consume to avoid the application quitting prematurely
     */
    public static void notifyListeners(final WindowEvent e) {
        listeners.forEach(l -> l.quit(e));
    }

    /**
     * Adds a listener to the list of listeners.
     * @param listener to add to list of listeners
     */
    public static void addListener(final AppClosingListener listener) {
        listeners.add(listener);
    }

    /**
     * Main function for starting the app.
     * @param args Arguments passed into the main function (they're irrelevant currently)
     */
    public static void main(final String[] args) {
        ErrorReporter.setup(args);
        PersistenceManager.setCurrent(new PersistenceManager(new FilePersistenceLoader()));
        UndoRedoManager.setDisabled(true);

        List<String> argsList = Arrays.asList(args);
        int debug = argsList.indexOf("debug");

        if (debug >= 0) {
            OrganisationGenerator.Stress stressLevel = OrganisationGenerator.Stress.Low;
            if (debug + 1 < args.length) {
                switch (args[debug + 1].substring(0, SUBSTRINGLENGTH).toLowerCase()) {
                    case "low": stressLevel = OrganisationGenerator.Stress.Low; break;
                    case "med": stressLevel = OrganisationGenerator.Stress.Medium; break;
                    case "hig": stressLevel = OrganisationGenerator.Stress.High; break;
                    default: break;
                }
            }
            PersistenceManager.getCurrent().setCurrentModel(new OrganisationGenerator(stressLevel).generate());
        }
        else {
            //Give us an empty model
            PersistenceManager.getCurrent().setCurrentModel(new Organisation());
        }

        int sample = argsList.indexOf("sample");
        if (sample >= 0) {
            String fileLocation = "sample.project";
            try {
                PersistenceManager.getCurrent().saveModel(fileLocation);
                System.exit(0);
            }
            catch (Exception e) {
                System.err.println("Could not save sample project.");
            }
        }

        UndoRedoManager.setDisabled(false);
        Organisation model = PersistenceManager.getCurrent().getCurrentModel();
        try {
            UndoRedoManager.importModel(model);
        }
        catch (Exception e) {
            // There is a BIG problem if this fails
            ErrorReporter.get().reportErrorSecretly(e, "Importing model failed in main()");
            return;
        }
        launch(args);
    }
}

