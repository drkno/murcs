package sws.project.magic;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 */
public class TestApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FooBar test = new FooBar();
        test.setFoo("foo");
        test.setBar("bar");

        Parent content = EditPaneGenerator.generatePane(test);

        Scene scene = new Scene(content, 800, 480);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
