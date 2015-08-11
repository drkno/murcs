package sws.murcs.debug.errorreporting;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.murcs.controller.controls.md.MaterialDesignCheckBox;
import sws.murcs.controller.windowManagement.Window;

/**
 * Popup for reporting errors/bugs.
 */
public class ErrorReportPopup {

    /**
     * Several labels used in the feedback window.
     */
    @FXML
    private Label messageTitleLabel, messageDetailLabel, screenShotWarningLabel, screenshotLabel;

    /**
     * The image that goes with the message.
     */
    @FXML
    private ImageView messageImage;

    /**
     * Text area to contain report details.
     */
    @FXML
    private TextArea detailTextArea;

    /**
     * Buttons for feedback window.
     */
    @FXML
    private Button reportButton, cancelButton;

    /**
     * The stage for the popup.
     */
    private Stage popupStage;

    /**
     * The window for the error reporter.
     */
    private Window window;

    /**
     * Checkbox for adding screenshots to submission.
     */
    private MaterialDesignCheckBox checkBox;

    /**
     * The root of the error reporter popUp.
     */
    @FXML
    private AnchorPane root;

    /**
     * Creates a new ErrorReportPopup.
     * ErrorReportPopups are displayed to the user when something went wrong
     * which we want to then send data about back to the sws server.
     */
    public ErrorReportPopup() {
    }

    /**
     * Sets the stage and its properties.
     * @param stage The stage to set.
     */
    private void setStage(final Stage stage) {
        popupStage = stage;
        popupStage.setResizable(true);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo/logo_small.png")));
        messageImage.setImage(iconImage);
        popupStage.getIcons().add(iconImage);
    }

    /**
     * Initializes the controller.
     */
    @FXML
    private void initialize() {
        reportButton.getStyleClass().add("button-default");
        setType(ErrorType.Automatic);
    }

    /**
     * Creates and new instance of the error reporter.
     * @return An instance of the error reporter.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public static ErrorReportPopup newErrorReporter() {
        Stage stage = new Stage();

        FXMLLoader loader = new FXMLLoader(ErrorReportPopup.class.getResource("/sws/murcs/ErrorReporting.fxml"));
        AnchorPane root;
        try {
            root = loader.load();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        ErrorReportPopup controller = loader.getController();
        controller.setStage(stage);

        Scene popupScene = new Scene(root);
        stage.setScene(popupScene);
        stage.setMinHeight(root.getPrefHeight());
        stage.setMinWidth(root.getPrefWidth());
        stage.setHeight(500);
        stage.setWidth(600);
        popupScene.getStylesheets()
                .add(ErrorReportPopup.class
                        .getResource("/sws/murcs/styles/global.css")
                        .toExternalForm());

        return controller;
    }

    /**
     * Shows the dialog, this should be the last thing you call after setting up your dialog.
     * If you have not set up a title the dialog will automatically remove it and resize.
     */
    public final void show() {
        swapForMDElements();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        window = new Window(popupStage, this);
        window.register();
        window.addGlobalShortcutsToWindow();
        window.show();
        Platform.runLater(messageTitleLabel::requestFocus);
    }

    /**
     * Adds a material design checkBox to the form.
     */
    private void swapForMDElements() {
        checkBox = new MaterialDesignCheckBox();
        checkBox.setSelected(true);
        screenshotLabel.setGraphic(checkBox);
        screenshotLabel.setContentDisplay(ContentDisplay.LEFT);
        screenshotLabel.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                checkBox.fireEvent(event);
                checkBox.fire();
                event.consume();
            }
        });
    }

    /**
     * Sets what will be called when the report button is pressed.
     * @param report the callback.
     */
    public final void setReportListener(final ReportError report) {
        reportButton.setOnAction(a -> {
            window.close();
            report.sendReport(detailTextArea.getText());
        });
    }

    /**
     * Setups up the default close method.
     */
    @FXML
    public final void close() {
        window.close();
    }

    /**
     * Sets the type of reporting dialog that will be shown.
     * @param type type to set the dialog to.
     */
    public final void setType(final ErrorType type) {
        switch (type) {
            case Automatic:
                setTitleText("Something went wrong.");
                setMessageText("An unexpected problem occurred. If you wish to report this problem to get it fixed, "
                        + "type how it occurred below and click 'Report'. Otherwise click 'Cancel'.\n\nIf the "
                        + "crash allows you to continue working, it is advised you save your data and restart the "
                        + "application before continuing.");
                setScreenShotWarningText("Note that screenshots may contain sensitive or confidential data!!\n"
                       + "If possible please include them as this will help us debug the issue.\n"
                       + "Please act responsibly.");
                break;
            case Manual:
                setTitleText("Feedback");
                setMessageText("Noticed something isn't right? Describe it below.\n");
                setScreenShotWarningText("Note that screenshots may contain sensitive or confidential data!!\n"
                        + "If possible please include them as this will help us debug the issue.\n"
                        + "Please act responsibly.");
                break;
            default:
        }
    }

    /**
     * Set the message you want the the dialog to show.
     * @param message The message you want to show on the dialog.
     */
    private void setMessageText(final String message) {
        if (message == null) {
            return;
        }
        messageDetailLabel.setText(message);
    }

    /**
     * Sets the title of the message
     * (appears alongside the title image).
     * @param titleText The title of the message.
     */
    private void setTitleText(final String titleText) {
        if (titleText == null) {
            return;
        }
        messageTitleLabel.setText(titleText);
    }

    /**
     * Checks if the error report is meant to contain screenshots.
     * @return if include screenshots.
     */
    public final boolean submitScreenShots() {
        return checkBox.isSelected();
    }

    /**
     * Sets the screenShot warning label.
     * @param screenShotWarningText The warning.
     */
    public final void setScreenShotWarningText(final String screenShotWarningText) {
        if (screenShotWarningText == null) {
            return;
        }
        screenShotWarningLabel.setText(screenShotWarningText);
    }
}
