package sws.project.magic;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A simple example application
 */
public class ExampleApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Example test = new Example();
        test.setFoo("foo");
        test.setBar(5);
        test.setTestBoolean(false);

        Parent content = EditFormGenerator.generatePane(test);

        Scene scene = new Scene(content, 800, 480);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
