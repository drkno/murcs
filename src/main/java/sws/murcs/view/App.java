package sws.murcs.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sws.murcs.debug.sampledata.RelationalModelGenerator;
import sws.murcs.listeners.AppClosingListener;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        Parent parent = FXMLLoader.load(getClass().getResource("/sws/murcs/App.fxml"));
        primaryStage.setScene(new Scene(parent));

        primaryStage.setTitle("Murcs");
        primaryStage.setOnCloseRequest(e -> notifyListeners(e));

        // Set up max and min dimensions of main window.
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
     * @param args Arguments passed into the main function (they're irrelevant currently)
     */
    public static void main(String[] args) {
        PersistenceManager.Current = new PersistenceManager(new FilePersistenceLoader());
        UndoRedoManager.setDisabled(true);

        List<String> argsList = Arrays.asList(args);
        int debug = argsList.indexOf("debug");

        if (debug >= 0) {
            RelationalModelGenerator.Stress stressLevel = RelationalModelGenerator.Stress.Low;
            if (debug+1 < args.length) {
                switch (args[debug+1]) {
                    case "low": stressLevel = RelationalModelGenerator.Stress.Low; break;
                    case "med": stressLevel = RelationalModelGenerator.Stress.Medium; break;
                    case "high": stressLevel = RelationalModelGenerator.Stress.High; break;
                }
            }
            PersistenceManager.Current.setCurrentModel(new RelationalModelGenerator(stressLevel).generate());
        } else {
            //Give us an empty model
            PersistenceManager.Current.setCurrentModel(new RelationalModel());
        }

        UndoRedoManager.setDisabled(false);
        RelationalModel model = PersistenceManager.Current.getCurrentModel();
        UndoRedoManager.add(model);
        model.getPeople().forEach(p -> UndoRedoManager.add(p));
        model.getTeams().forEach(t -> UndoRedoManager.add(t));
        model.getSkills().forEach(k -> UndoRedoManager.add(k));
        model.getProjects().forEach(l -> UndoRedoManager.add(l));
        model.getReleases().forEach(r -> UndoRedoManager.add(r));
        try{UndoRedoManager.commit("Initial State");} catch (Exception e) {}
        launch(args);
    }
}

