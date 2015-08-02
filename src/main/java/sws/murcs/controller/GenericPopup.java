package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.listeners.GenericCallback;
import sws.murcs.view.App;

/**
 * Generic popup creator and controller.
 */
public class GenericPopup extends AnchorPane {

    /**
     * This window of the popup.
     */
    private Window window;

    /**
     * Enum for specifying which side of the dialog you want the button to appear on.
     */
    public enum Position {

        /**
         * The left side of the window.
         */
        LEFT,

        /**
         * The right side of the window.
         */
        RIGHT
    }

    /**
     * Enum for specifying if a button should have a default action.
     */
    public enum Action {

        /**
         * Sets the button to be linked with the enter key.
         */
        DEFAULT,

        /**
         * Sets to be linked with esc key.
         */
        CANCEL,

        /**
         * No action.
         */
        NONE
    }

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
     * Contains left aligned buttons.
     */
    @FXML
    private HBox hBoxLeft;

    /**
     * Contains right align buttons.
     */
    @FXML
    private HBox hBoxRight;

    /**
     * The main content pane.
     */
    @FXML
    private GridPane contentPane;

    /**
     * The stage for the popup.
     */
    private Stage popupStage;

    /**
     * The scene for the popup.
     */
    private Scene popupScene;

    /**
     * Whether or not there are any buttons defined in the popup.
     */
    private boolean buttonsDefined;

    /**
     * Default button width.
     */
    private final int defaultButtonWidth = 70;

    /**
     * Default button height.
     */
    private final int defaultButtonHeight = 25;

    /**
     * Default popUp height.
     */
    private final int defaultPopUpHeight = 150;

    /**
     * Constructs a new Generic Popup. In order to use you need
     * to at least set the message and add at least 1 button
     * some examples of how to use this include:
     *
     * GenericPopup ctrl = new GenericPopup();
     * ctrl.setMessageText("Test message");
     * ctrl.addButton("Cancel", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, () -&gt; {ctrl.close();});
     * ctrl.addButton("DEFAULT", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -&gt; {ctrl.close();});
     * ctrl.addButton("Thingy", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, () -&gt; {ctrl.close();});
     * ctrl.show();
     *
     * There are extra features, like you can add and image
     * and title, change the window title as well
     *
     */
    public GenericPopup() {
        this(null);
    }

    /**
     * Constructs a dialog from an exception.
     * @param exception The exception that you want to feed in to show the exception message.
     */
    public GenericPopup(final Exception exception) {
        popupStage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/GenericPopup.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        }
        catch (Exception e) {
            ErrorReporter.get().reportError(e, "Generic popup failed to load");
        }
        popupScene = new Scene(this);
        popupStage.initOwner(App.getStage());
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(popupScene);
        popupStage.setResizable(true);
        popupScene.getStylesheets().add(getClass().getResource("/sws/murcs/styles/global.css").toExternalForm());

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo_small.png")));
        messageImage.setImage(iconImage);
        popupStage.getIcons().add(iconImage);
        popupStage.sizeToScene();
        setupWindow();

        if (exception != null) {
            setMessageText(exception.getMessage());
            addOkButton(window::close);
        }
    }

    /**
     * Sets up a window for the popup.
     */
    private void setupWindow() {
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(App.getStage());
        window = new Window(popupStage, this);
        window.register();
    }


    /**
     * Adds a new button to the dialog. You must specify the text to go on the button, it's location on the dialog
     * (either the left hand side or the right hand side) and the function to call when it is clicked
     * NOTE: Buttons stack on the left and right sides, therefore if you add two buttons on the left
     * the first one added will be the one closest to the left hand side, so keep that in mind.
     * @param buttonText The text on the button.
     * @param position The positioning of the button.
     * @param func The function to call when the button is clicked.
     * @param action Default action for button
     */
    public final void addButton(final String buttonText,
                                final Position position,
                                final Action action,
                                final GenericCallback func) {
        Button button = new Button(buttonText);
        //button.setPrefSize(defaultButtonWidth, defaultButtonHeight);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        //And this, is where the magic happens!
        button.setOnAction((a) -> func.call());

        switch (action) {
            case DEFAULT:
                button.setDefaultButton(true);
                button.getStyleClass().add("button-default");
                break;
            case CANCEL:
                button.getStyleClass().add("button-cancel");
                button.setCancelButton(true);
                break;
            case NONE:
                break;
            default:
                break;
        }

        switch (position) {
            case LEFT:
                hBoxLeft.getChildren().add(button);
                break;
            case RIGHT:
                hBoxRight.getChildren().add(button);
                break;
            default:
                break;
        }

        buttonsDefined = true;
    }

    /**
     * Shows the dialog, this should be the last thing you call after setting up your dialog.
     * If you have not set up a title the dialog will automatically remove it and resize.
     */
    public final void show() {
        if (messageTitle.getText().equals("Title")) {
            contentPane.getRowConstraints().get(0).setMinHeight(0);
            contentPane.getRowConstraints().get(0).setMaxHeight(0);

            messageImage.setVisible(false);
            messageTitle.setVisible(false);
            //popupStage.setHeight(defaultPopUpHeight);
        }

        if (!buttonsDefined) {
            addOkButton(window::close);
        }
        window.show();
    }

    /**
     * Closes the dialog window.
     */
    public final void close() {
        window.close();
    }

    /**
     * Set the message you want the the dialog to show.
     * @param message The message you want to show on the dialog.
     */
    public final void setMessageText(final String message) {
        if (message == null) {
            return;
        }
        messageText.setText(message);
    }

    /**
     * Sets the title of the window.
     * (the bit that appears in the bar at the top)
     * @param title The window title.
     */
    public final void setWindowTitle(final String title) {
        popupStage.setTitle(title);
    }

    /**
     * Sets the title of the message
     * (appears alongside the title image).
     * @param titleText The title of the message.
     */
    public final void setTitleText(final String titleText) {
        if (titleText == null) {
            return;
        }
        messageTitle.setText(titleText);
    }

    /**
     * Sets the image to appear beside the title.
     * NOTE: If you don't set the title text (not the window text) then this won't appear.
     * @param image image to set.
     */
    public final void setTitleImage(final Image image) {
        messageImage.setImage(image);
    }

    /**
     * Adds default OK Cancel Buttons. you specify what is supposed to happen for the ok button and the cancel button
     * remains it's default (closes the dialog)
     * @param okFunction The function you want to call on the ok button being clicked.
     */
    public final void addOkCancelButtons(final GenericCallback okFunction) {
        addOkCancelButtons(okFunction, window::close);
    }

    /**
     * Adds default Yes No Buttons. You specify what is supposed to happen for the Yes button and the No button
     * remains it's default (closes the dialog).
     * @param yesFunction The function you want to call on the yes button being clicked.
     */
    public final void addYesNoButtons(final GenericCallback yesFunction) {
        addYesNoButtons(yesFunction, window::close);
    }

    /**
     * Adds default OK Cancel Buttons.
     * you specify the functions for both the ok and cancel buttons when clicked.
     * @param okFunction The function you want to call on ok button click
     * @param cancelFunction The function you want to call on cancel button click
     */
    public final void addOkCancelButtons(final GenericCallback okFunction, final GenericCallback cancelFunction) {
        addButton("Cancel", Position.RIGHT, Action.CANCEL, cancelFunction);
        addButton("OK", Position.RIGHT, Action.DEFAULT, okFunction);
    }

    /**
     * Adds default Yes No Buttons.
     * You specify the functions for both the yes and no buttons when clicked.
     * @param yesFunction The function you want to call on yes button click.
     * @param noFunction The function you want to call on no button click.
     */
    public final void addYesNoButtons(final GenericCallback yesFunction, final GenericCallback noFunction) {
        addButton("Yes", Position.RIGHT, Action.DEFAULT, yesFunction);
        addButton("No", Position.RIGHT, Action.CANCEL, noFunction);
    }

    /**
     * Adds the default OK button with a specified function to call on it being clicked.
     * @param okFunction Function to call on ok button being clicked.
     */
    public final void addOkButton(final GenericCallback okFunction) {
        addButton("OK", Position.RIGHT, Action.DEFAULT, okFunction);
    }
}
