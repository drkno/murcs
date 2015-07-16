package sws.murcs.reporting.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.murcs.view.App;

/**
 * Sets up the report generator view.
 */
public class ReportGeneratorView {

    /**
     * Sets up a report generator window.
     */
    public ReportGeneratorView() {
    }

    /**
     * Creates a new form for creating a new object of the specified type.
     */
    public final void show() {
        try {
            // Load the view
            FXMLLoader loader = new FXMLLoader(ReportGeneratorController
                    .class
                    .getResource("/sws/murcs/reporting/ReportGenerator.fxml"));
            Parent root = loader.load();

            // Set up the controller and give it the necessary parameters
            ReportGeneratorController controller = loader.getController();

            // Set up the stage
            Stage stage = new Stage();
            stage.setResizable(false);
            controller.setStage(stage);
            if (root == null) {
                return;
            }
            Scene scene = new Scene(root);
            scene.getStylesheets()
                    .add(getClass()
                    .getResource("/sws/murcs/styles/errors.css")
                    .toExternalForm());
            scene.getStylesheets()
                    .add(getClass()
                    .getResource("/sws/murcs/styles/materialDesign/materialDesignButton.css")
                    .toExternalForm());
            scene.getStylesheets()
                    .add("http://fonts.googleapis.com/css?family=Roboto:400,100,100italic,300,300italic,400italic,500,"
                            + "500italic,700,700italic,900,900italic");
            scene.getStylesheets()
                    .add(getClass()
                    .getResource("/sws/murcs/styles/materialDesign/globalStyles.css")
                    .toExternalForm());
            scene.getStylesheets()
                    .add(getClass()
                    .getResource("/sws/murcs/styles/materialDesign/comboBox.css")
                    .toExternalForm());
            scene.getStylesheets()
                    .add(getClass()
                    .getResource("/sws/murcs/styles/materialDesign/listView.css")
                    .toExternalForm());
//            scene.getStylesheets()
//                    .add(getClass()
//                    .getResource("/sws/murcs/styles/materialDesign/listCell.css")
//                    .toExternalForm());
            stage.setScene(scene);

            // Give the stage a name and icon
            stage.setTitle("Generate Report");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo_small.png")));
            stage.getIcons().add(iconImage);

            // Set modality of the stage on top of the App
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(App.getStage());

            stage.show();
        }
        catch (Exception e) {
            System.err.println("Something went wrong loading the report generator window");
            e.printStackTrace();
        }
    }
}
