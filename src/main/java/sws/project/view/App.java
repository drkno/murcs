package sws.project.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sws.project.controller.AppClosingListener;
import sws.project.magic.tracking.UndoRedoManager;
import sws.project.model.persistence.PersistenceManager;
import sws.project.model.persistence.loaders.FilePersistenceLoader;

import java.util.ArrayList;

/**
 * The main app class
 */
public class App extends Application{

    public static Stage stage;
    private static ArrayList<AppClosingListener> listeners = new ArrayList<>();

    /***
     * Starts up the application and sets the min window size to 600x400
     * @param primaryStage The main Stage
     * @throws Exception A loading exception from loading the fxml
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        if (!PersistenceManager.CurrentPersistenceManagerExists()) {
            FilePersistenceLoader loader = new FilePersistenceLoader();
            PersistenceManager.Current = new PersistenceManager(loader);
        }

        Parent parent = FXMLLoader.load(getClass().getResource("/sws/project/App.fxml"));
        primaryStage.setScene(new Scene(parent));

        primaryStage.setTitle("project");
        primaryStage.setOnCloseRequest(e -> notifyListeners(e));
        //Setting up max and min width of app
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.show();
        stage = primaryStage;
    }

    /**
     * Call quit on all of the event listeners
     * @param e Window event to consume to avoid the application quitting prematurely
     */
    private static void notifyListeners(WindowEvent e) {
        listeners.forEach(l -> l.quit(e));
    }

    /**
     * Adds a listener to the list of listeners
     * @param listener to add to list of listeners
     */
    public static void addListener(AppClosingListener listener) {
        listeners.add(listener);
    }

    /**
     * Main function for starting the app
     * @param args Arguements passed into the main function (they're irrelevant currently)
     */
    public static void main(String[] args) {
        launch(args);
        UndoRedoManager.destroy();
    }
}
