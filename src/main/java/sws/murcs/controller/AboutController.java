package sws.murcs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import sws.murcs.controller.controls.customGrid.GridController;
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
     * Current index for the key combination.
     */
    private int combinationIndex = 0;

    /**
     * The list of key codes for the combination.
     */
    private KeyCode[] combination = new KeyCode[]{
            KeyCode.UP,
            KeyCode.UP,
            KeyCode.DOWN,
            KeyCode.DOWN,
            KeyCode.LEFT,
            KeyCode.RIGHT,
            KeyCode.LEFT,
            KeyCode.RIGHT,
            KeyCode.B,
            KeyCode.A
    };

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
        stage.setMinHeight(200);
        stage.setMinWidth(300);
        closeButton.getStyleClass().add("create-save-button");
    }

    /**
     * Creates a new custom grid.
     */
    private void createCustomGrid() {
        GridController grid = new GridController();
        grid.show();
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
        stage.getScene().setOnKeyPressed(event -> {
            if (event.getCode().equals(combination[combinationIndex])) {
                combinationIndex++;
                if (combinationIndex == combination.length) {
                    combinationIndex = 0;
                    createCustomGrid();
                }
            }
            else {
                combinationIndex = 0;
            }
        });
    }
}
