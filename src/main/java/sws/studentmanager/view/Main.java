package sws.studentmanager.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application.
 *
 * @author dpv11@uclive.ac.nz (Daniel van Wichen)
 */
public class Main extends Application {

    public static String filePath = System.getProperty("user.home") + "/students.json";
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Student Manager");
        try {
            initRootLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    /**
     * Initializes the root layout.
     */
    private void initRootLayout() throws IOException {
            AnchorPane rootLayout = FXMLLoader.load(getClass().getResource("/main.fxml"));
            stage.setScene(new Scene(rootLayout));

    }
}
