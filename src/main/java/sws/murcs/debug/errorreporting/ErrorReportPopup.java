package sws.murcs.debug.errorreporting;

import com.sun.tools.javac.code.Attribute;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.view.App;

/**
 * Popup for reporting errors/bugs.
 */
public class ErrorReportPopup {

    /**
     * The main message text.
     */
    @FXML
    private Label messageDetailLabel;

    /**
     * The title of the message.
     */
    @FXML
    private Label messageTitleLabel;

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
     * Button used for reporting.
     */
    @FXML
    private Button reportButton;

    /**
     * The stage for the popup.
     */
    private Stage popupStage;

    /**
     * The window for the error reporter.
     */
    private Window window;

    /**
     * Creates a new ErrorReportPopup.
     * ErrorReportPopups are displayed to the user when something went wrong
     * which we want to then send data about back to the sws server.
     */
    public ErrorReportPopup() {
    }

    private void setStage(final Stage stage) {
        popupStage = stage;

        popupStage.initOwner(App.getStage());
        popupStage.setResizable(true);
        popupStage.initModality(Modality.NONE);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo_small.png")));
        messageImage.setImage(iconImage);
        popupStage.getIcons().add(iconImage);
    }

    @FXML
    private void initialize() {
        reportButton.getStyleClass().add("button-default");
        setType(ErrorType.Automatic);
    }

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
        stage.sizeToScene();
        stage.setMinHeight(root.getPrefHeight());
        stage.setMinWidth(root.getPrefWidth());
        stage.setHeight(root.getPrefHeight());
        stage.setWidth(root.getPrefWidth());
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
        popupStage.initModality(Modality.APPLICATION_MODAL);
        window = new Window(popupStage, this);
        window.register();
        window.addGlobalShortcutsToWindow();
        window.show();
        Platform.runLater(messageTitleLabel::requestFocus);
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
            default:
            case Automatic:
                setTitleText("Something went wrong.");
                setMessageText("An unexpected problem occurred. If you wish to report this problem to get it fixed, "
                        + "type how it occurred below and click 'Report'. Otherwise click 'Cancel'.\n\nIf the "
                        + "crash allows you to continue working, it is advised you save your data and restart the "
                        + "application before continuing.");
                break;
            case Manual:
                setTitleText("Feedback");
                setMessageText("Noticed something isn't right? Describe it below.\n\nNote: we will automatically "
                        + "receive a screenshot of what is currently displayed within the application for you.\n");
                break;
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
}
