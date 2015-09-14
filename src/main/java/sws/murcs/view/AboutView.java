package sws.murcs.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.murcs.controller.AboutController;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.debug.errorreporting.ErrorReporter;

/**
 * The About window view.
 */
public class AboutView {

    /**
     * The parent window.
     */
    private Window parentWindow;

    /**
     * Sets up a report generator window.
     * @param pParentWindow The parent window.
     */
    public AboutView(final Window pParentWindow) {
        parentWindow = pParentWindow;
    }

    /**
     * Creates a new form for creating a new object of the specified type.
     */
    public final void show() {
        try {
            // Load the view
            FXMLLoader loader = new AutoLanguageFXMLLoader(AboutController
                    .class
                    .getResource("/sws/murcs/About.fxml"));
            Parent root = loader.load();

            // Set up the controller and give it the necessary parameters
            AboutController controller = loader.getController();

            // Set up the stage
            Stage stage = new Stage();
            stage.setResizable(true);
            controller.setupController(stage, parentWindow);
            if (root == null) {
                return;
            }
            Scene scene = new Scene(root);
            scene.getStylesheets()
                    .add(getClass()
                            .getResource("/sws/murcs/styles/global.css")
                            .toExternalForm());

            // Give the stage a name and icon
            stage.setTitle("About");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo/logo_small.png")));
            stage.getIcons().add(iconImage);
            stage.setScene(scene);

            stage.initOwner(App.getStage());
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.sizeToScene();
            controller.setUpWindow();
            controller.show();
        }
        catch (Exception e) {
            ErrorReporter.get().reportError(e, "Something went wrong loading the about window");
        }
    }
}
