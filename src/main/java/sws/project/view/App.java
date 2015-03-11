package sws.project.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sws.project.model.Project;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;
import sws.project.model.persistence.loaders.FilePersistenceLoader;

import javax.management.relation.Relation;

/**
 * The main app class
 */
public class App extends Application{

    public static Stage stage;
    public static PersistenceManager persistenceManager = new PersistenceManager(new FilePersistenceLoader());
    public static RelationalModel model = new RelationalModel();

    /***
     * Starts up the application and sets the min window size to 600x400
     * @param primaryStage The main Stage
     * @throws Exception A loading exception from loading the fxml
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/sws/project/App.fxml"));
        primaryStage.setScene(new Scene(parent));

        primaryStage.setTitle("project");
        //Setting up max and min width of app
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.show();
        stage = primaryStage;
    }

    /***
     * Main function for starting the app
     * @param args Arguements passed into the main function (they're irrelevant currently)
     */
    public static void main(String[] args) {
        launch(args);
    }
}

