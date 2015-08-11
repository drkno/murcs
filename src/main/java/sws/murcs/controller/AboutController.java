package sws.murcs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.model.Organisation;

/**
 * About window controller.
 */
public class AboutController {

    /**
     * The Version number of Murcs.
     */
    @FXML
    private Label versionNumber;
    /**
     * The Murcs logo.
     */
    @FXML
    private ImageView logoImage;
    /**
     * The close button.
     */
    @FXML
    private Button closeButton;
    /**
     * Murcs.
     */
    @FXML
    private Label messageTitleLabel;

    /**
     * The about window.
     */
    private Window window;

    /**
     * The About view stage.
     */
    private Stage stage;

    /**
     * The parent of the about window.
     */
    private Window parentWindow;

    /**
     * An attempt at getting the display scaling of text.
     */
    private final double rem = javafx.scene.text.Font.getDefault().getSize();

    /**
     * Empty constructor for FXML.
     */
    public AboutController() {
    }

    /**
     * Closes the About window.
     * @param actionEvent the event, not used.
     */
    @FXML
    private void close(final ActionEvent actionEvent) {
        window.close(window::parentToFront);
    }

    /**
     * Sets the stage of the about window.
     * @param pStage The Stage to set
     * @param pParentWindow The parent window of the About window.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public final void setupController(final Stage pStage, final Window pParentWindow) {
        stage = pStage;
        parentWindow = pParentWindow;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image iconImage = new Image(classLoader
                .getResourceAsStream(("sws/murcs/logo/Murcs_logo_transparent_background.png")));
        logoImage.setImage(iconImage);
        versionNumber.setText("Version: " + Organisation.getVersion());
        stage.setMinHeight(rem * 17);
        stage.setMinWidth(rem * 25);
        closeButton.getStyleClass().add("create-save-button");
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
