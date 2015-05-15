package sws.murcs.view;

import com.sun.javafx.sg.prism.NGShape;
import com.sun.org.apache.xpath.internal.operations.Mod;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sws.murcs.controller.AppController;
import sws.murcs.debug.sampledata.RelationalModelGenerator;
import sws.murcs.listeners.AppClosingListener;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Model;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.util.*;

/**
 * The main app class.
 */
public class App extends Application {

    /**
     * The main stage of the application.
     */
    private static Stage stage;
    /**
     * An list of listeners relating to the app closing.
     */
    private static ArrayList<AppClosingListener> listeners = new ArrayList<>();
    /**
     * The minimum height of the application.
     */
    private final int minimumApplicationHeight = 600;
    /**
     * The minimum width of the application.
     */
    private final int minimumApplicationWidth = 600;
    /**
     * The subString length to search over, when parsing debugging mode.
     */
    private static final int SUBSTRINGLENGTH = 3;
    /**
     * The instance of AppController that is the current controller for App.fxml.
     */
    private static AppController appController;

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

    /***
     * Starts up the application and sets the min window size to 600x400.
     * @param primaryStage The main Stage
     * @throws Exception A loading exception from loading the fxml
     */
    @Override
    public final void start(final Stage primaryStage) throws Exception {

        if (!PersistenceManager.CurrentPersistenceManagerExists()) {
            FilePersistenceLoader loader = new FilePersistenceLoader();
            PersistenceManager.Current = new PersistenceManager(loader);
        }

        // Loads the primary fxml and sets appController as its controller
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sws/murcs/App.fxml"));
        Parent parent = loader.load();
        appController = loader.getController();

        primaryStage.setScene(new Scene(parent));

        primaryStage.setTitle("Murcs");
        primaryStage.setOnCloseRequest(e -> notifyListeners(e));
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo_small.png")));
        primaryStage.getIcons().add(iconImage);

        // Set up max and min dimensions of main window
        primaryStage.setMinWidth(minimumApplicationWidth);
        primaryStage.setMinHeight(minimumApplicationHeight);

        primaryStage.show();
        stage = primaryStage;
    }

    /**
     * Call quit on all of the event listeners.
     * @param e Window event to consume to avoid the application quitting prematurely
     */
    private static void notifyListeners(final WindowEvent e) {
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
        PersistenceManager.Current = new PersistenceManager(new FilePersistenceLoader());
        UndoRedoManager.setDisabled(true);

        List<String> argsList = Arrays.asList(args);
        int debug = argsList.indexOf("debug");

        if (debug >= 0) {
            RelationalModelGenerator.Stress stressLevel = RelationalModelGenerator.Stress.Low;
            if (debug + 1 < args.length) {
                switch (args[debug + 1].substring(0, SUBSTRINGLENGTH).toLowerCase()) {
                    case "low": stressLevel = RelationalModelGenerator.Stress.Low; break;
                    case "med": stressLevel = RelationalModelGenerator.Stress.Medium; break;
                    case "hig": stressLevel = RelationalModelGenerator.Stress.High; break;
                    default: break;
                }
            }
            PersistenceManager.Current.setCurrentModel(new RelationalModelGenerator(stressLevel).generate());
        }
        else {
            //Give us an empty model
            PersistenceManager.Current.setCurrentModel(new RelationalModel());
        }

        UndoRedoManager.setDisabled(false);
        RelationalModel model = PersistenceManager.Current.getCurrentModel();
        try {
            UndoRedoManager.importModel(model);
        }
        catch (Exception e) {
            //There is a problem if this fails
            e.printStackTrace();
        }
        launch(args);
    }
}

