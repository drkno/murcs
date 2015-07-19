package sws.murcs.debug.errorreporting;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import sws.murcs.view.App;

/**
 * Popup for reporting errors/bugs.
 */
public class ErrorReportPopup extends AnchorPane {

    /**
     * The main message text.
     */
    @FXML
    private Label messageText;

    /**
     * The title of the message.
     */
    @FXML
    private Label messageTitle;

    /**
     * The image that goes with the message.
     */
    @FXML
    private ImageView messageImage;

    /**
     * The main content pane.
     */
    @FXML
    private GridPane contentPane;

    /**
     * Text area to contain report details.
     */
    @FXML
    private TextArea reportDetails;

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
     * Creates a new ErrorReportPopup.
     * ErrorReportPopups are displayed to the user when something went wrong
     * which we want to then send data about back to the sws server.
     */
    public ErrorReportPopup() {
        popupStage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/ErrorReporting.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Scene popupScene = new Scene(this);
        popupScene.getStylesheets().add(getClass().getResource("/sws/murcs/styles/global.css").toExternalForm());
        popupStage.initOwner(App.getStage());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(popupScene);
        popupStage.setResizable(false);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo_small.png")));
        messageImage.setImage(iconImage);
        popupStage.getIcons().add(iconImage);

        reportButton.getStyleClass().add("button-default");

        setType(ErrorType.Automatic);
    }

    /**
     * Shows the dialog, this should be the last thing you call after setting up your dialog.
     * If you have not set up a title the dialog will automatically remove it and resize.
     */
    public final void show() {
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(App.getStage());
        popupStage.show();
        Platform.runLater(messageTitle::requestFocus);
    }

    /**
     * Sets what will be called when the report button is pressed.
     * @param report the callback.
     */
    public final void setReportListener(final ReportError report) {
        reportButton.setOnAction(a -> {
            close();
            report.sendReport(reportDetails.getText());
        });
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
     * Closes the dialog.
     * Note: You may want to set up one of your buttons to call this, although if you use the addOkCancelButtons()
     * with only one lambda expression then the cancel button is automatically set to call this.
     */
    @FXML
    public final void close() {
        popupStage.close();
    }

    /**
     * Set the message you want the the dialog to show.
     * @param message The message you want to show on the dialog.
     */
    private void setMessageText(final String message) {
        if (message == null) {
            return;
        }
        messageText.setText(message);
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
        messageTitle.setText(titleText);
    }
}
