package sws.murcs.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sws.murcs.arguments.ArgumentsManager;
import sws.murcs.controller.MainController;
import sws.murcs.controller.windowManagement.ShortcutManager;
import sws.murcs.controller.windowManagement.WindowManager;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.debug.sampledata.OrganisationGenerator;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;
import sws.murcs.internationalization.InternationalizationHelper;
import sws.murcs.listeners.AppClosingListener;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Organisation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The main app class.
 */
@SuppressWarnings("ALL")
public class App extends Application {

    /**
     * If the application is on the style manager thread.
     */
    private static boolean onStyleManagerThread = false;

    /**
     * Default window title to use.
     */
    public static final String DEFAULT_WINDOW_TITLE = "- Untitled -";

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
     * The update version of the current running version of Java. (i.e. if you're on 8u25 this would be 25).
     */
    public static final int JAVA_UPDATE_VERSION = Integer.parseInt(System.getProperty("java.version")
            .split("_")[1].split("-")[0]);

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
     * Dion Vader.
     */
    private static boolean vader = false;

    /**
     * Gets if the app is running on the style manager thread.
     * @return if on style manager thread
     */
    public static boolean getOnStyleManagerThread() {
        return onStyleManagerThread;
    }

    /**
     * Sets if on the style manager thread.
     * @param pOnStyleMangerThread if on the style manager thread
     */
    public static void setOnStyleManagerThread(final boolean pOnStyleMangerThread) {
        onStyleManagerThread = pOnStyleMangerThread;
    }

    /**
     * Gets the shortcut manager.
     * @return The shortcut manager.
     */
    public static ShortcutManager getShortcutManager() {
        return shortcutManager;
    }

    /**
     * Gets the main controller for the App.
     * @return The main controller
     */
    public static MainController getMainController() {
        return mainController;
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
        final String finalTitle = title;
        Platform.runLater(() -> {
            stage.setTitle(finalTitle);
        });
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
        mainController = createWindow(primaryStage);
        if (vader) {
            URL url = App.class.getResource("/sws/murcs/imperialMarch.mp3");
            Media hit = new Media(url.toString());
            MediaPlayer mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.play();
        }
    }

    /**
     * Creates a new MainWindow.
     * @param window The stage to load the window onto
     * @return The main controller for the window
     */
    public static MainController createWindow(final Stage window) {
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

        MainController controller = loadRootNode();

        Scene scene = new Scene(controller.getRootNode());
        Font.loadFont(App.class.getResource("/sws/murcs/styles/fonts/Roboto/Roboto-Regular.ttf").toExternalForm(), 18.0);
        scene.getStylesheets()
                .add(App.class
                        .getResource("/sws/murcs/styles/global.css")
                        .toExternalForm());
        window.setScene(scene);
        InternationalizationHelper.setLanguage("English");
        window.setTitle(DEFAULT_WINDOW_TITLE);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo/logo_small.png")));
        window.getIcons().add(iconImage);

        // Set up max and min dimensions of main window
        window.setMinWidth(MINIMUM_APPLICATION_WIDTH);
        window.setMinHeight(MINIMUM_APPLICATION_HEIGHT);

        controller.show();
        return controller;
    }

    public static MainController loadRootNode() {
        // Loads the primary fxml and sets mainController as its controller
        FXMLLoader loader = new AutoLanguageFXMLLoader();
        loader.setResources(InternationalizationHelper.getCurrentResources());
        loader.setLocation(App.class.getResource("/sws/murcs/MainView.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            //We should never hit this, if we managed to start the application
            ErrorReporter.get().reportErrorSecretly(e, "Couldn't open a MainWindow :'(");
        }
        MainController controller = loader.getController();
        return controller;
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
     * Add sample data to the application.
     * @param values options that were passed via the CLI to this argument.
     * @throws Exception when generating data failed.
     */
    private static void sampleData(final List<String> values) throws Exception {
        OrganisationGenerator.Stress stressLevel = OrganisationGenerator.Stress.Low;
        if (values.size() == 1) {
            if (values.get(0).length() < SUBSTRINGLENGTH) {
                throw new Exception("Invalid debug keyword. Try low, medium or high.");
            }
            switch (values.get(0).substring(0, SUBSTRINGLENGTH).toLowerCase()) {
                case "low":
                    stressLevel = OrganisationGenerator.Stress.Low;
                    break;
                case "med":
                    stressLevel = OrganisationGenerator.Stress.Medium;
                    break;
                case "hig":
                    stressLevel = OrganisationGenerator.Stress.High;
                    break;
                default:
                    break;
            }
        }
        else if (values.size() > 1) {
            throw new Exception("Invalid use of the debug option.");
        }
        PersistenceManager.getCurrent().setCurrentModel(new OrganisationGenerator(stressLevel).generate());
    }

    /**
     * Save a sample project to the current directory.
     * @param values options that were passed to this argument.
     * @throws Exception when saving a sample project failed.
     */
    private static void saveSample(final List<String> values) throws Exception {
        if (values.size() != 0) {
            throw new Exception("Invalid use of the sample option.");
        }

        try {
            String fileLocation = "sample.project";
            PersistenceManager.getCurrent().saveModel(fileLocation);
        }
        catch (Exception e) {
            System.err.println("Could not save sample project.");
        }
        System.exit(0);
    }

    /**
     * Main function for starting the app.
     * @param args Arguments passed into the main function (they're irrelevant currently)
     */
    public static void main(final String[] args) {
        System.setProperty("prism.lcdtext", "false");
        PersistenceManager.setCurrent(new PersistenceManager(new FilePersistenceLoader()));
        UndoRedoManager.get().setDisabled(true);

        ArgumentsManager.get().registerArgument("d", "debug", "Generates sample data for use while debugging.\n\t"
                + "  Combine with \"high\", \"med\" and \"low\" to customise data generated.", App::sampleData);
        ArgumentsManager.get().registerArgument("n", "numbering", "For use with debug. Enables numbers on randomly "
                + "generated model types.", v -> OrganisationGenerator.isNumbering(true));
        ArgumentsManager.get().registerArgument("s", "sample", "Generates a sample file for use while testing.",
                App::saveSample);
        ArgumentsManager.get().registerArgument("v", "vader", "Enables Dion Vader mode.", opts -> vader = true);
        ArgumentsManager.get().parseArguments(args);

        try {
            Organisation model = PersistenceManager.getCurrent().getCurrentModel();
            if (PersistenceManager.getCurrent().getCurrentModel() == null) {
                //Give us an empty model
                model = new Organisation();
                PersistenceManager.getCurrent().setCurrentModel(model);
            }
            UndoRedoManager.get().setDisabled(false);
            UndoRedoManager.get().importModel(model);
        }
        catch (Exception e) {
            // There is a BIG problem if this fails
            ErrorReporter.get().reportErrorSecretly(e, "Importing model failed in main()");
            return;
        }
        launch(args);
    }
}

