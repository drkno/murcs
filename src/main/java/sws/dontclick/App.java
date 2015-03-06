package sws.dontclick;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main gui
 *
 * Created by jayha_000 on 3/2/2015.
 */
public class App extends Application {


    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("/testwindow.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Don't Press The Goddamn Button");
        primaryStage.show();
    }

    public static void main(String[] args) {launch(args);}
}
