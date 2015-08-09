package sws.murcs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import sws.murcs.controller.windowManagement.Window;

/**
 * About window controller.
 */
public class AboutController {
    @FXML
    private ImageView messageImage;
    @FXML
    private Button closeButton;
    @FXML
    private Label messageTitleLabel;
    @FXML
    private TextArea detailTextArea;
    @FXML
    private Label messageDetailLabel;

    private Window window;

    private Stage stage;
    private Window parentWindow;

    /**
     * Empty constructor for FXML.
     */
    public AboutController() {
    }

    @FXML
    private void close(ActionEvent actionEvent) {
        window.close(window::parentToFront);
    }

    /**
     * Sets the stage of the about window.
     * @param pStage The Stage to set
     */
    public final void setupController(final Stage pStage, final Window pParentWindow) {
        stage = pStage;
        parentWindow = pParentWindow;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo_small.png")));
        messageImage.setImage(iconImage);
    }

    /**
     * Shows the about window.
     */
    public final void show() {
        window.show();
    }

    /**
     * Creates a window that can be managed.
     */
    public final void setUpWindow() {
        window = new Window(stage, this, parentWindow);
        window.register();
    }
}
