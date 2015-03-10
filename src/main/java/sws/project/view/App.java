package sws.project.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
* 11/03/2015
* @author Dion
*/
public class App extends Application{


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/project/App.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("project");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}

