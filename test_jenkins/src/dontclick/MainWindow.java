package dontclick;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by jayha_000 on 3/2/2015.
 */
public class MainWindow extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("dontclick/testwindow.fxml"));

        Scene scene = new Scene(root, 300, 275);

        stage.setTitle("Don't Press The Goddamn Button");
        stage.setScene(scene);
        stage.show();
    }
}
