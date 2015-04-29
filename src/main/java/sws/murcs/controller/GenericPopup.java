package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.murcs.view.App;
import java.util.function.Consumer;

/**
 * Generic popup creator and controller
 */
public class GenericPopup extends AnchorPane {

    /***
     * Enum for specifying which side of the dialog you want the button to appear on.
     */
    public enum Position {
        LEFT,
        RIGHT
    }

    /**
     * Enum for specifying if a button should have a default action.
     */
    public enum Action {
        DEFAULT,
        CANCEL,
        NONE
    }

    private @FXML Label messageText;
    private @FXML Label messageTitle;

    private @FXML ImageView messageImage;
    //Contains left aligned buttons
    private @FXML HBox hBoxLeft;
    //Contains right align buttons
    private @FXML HBox hBoxRight;

    private @FXML GridPane contentPane;

    private Stage popupStage;
    private Scene popupScene;

    /***
     * Constructs a new Generic Popup. In order to use you need to at least set the message and add at least 1 button
     * some examples of how to use this include:
     *
     *      GenericPopup controller = new GenericPopup();
     *      controller.setMessageText("Test message");
     *      controller.addButton("Cancel", GenericPopup.Position.RIGHT, GenericPopup.Action.CANCEL, () -RIGHTARROW {controller.close(); return null;});
     *      controller.addButton("DEFAULT", GenericPopup.Position.RIGHT, GenericPopup.Action.DEFAULT, () -RIGHTARROW {controller.close(); return null;});
     *      controller.addButton("Thingy", GenericPopup.Position.LEFT, GenericPopup.Action.NONE, () -RIGHTARROW {controller.close(); return null;});
     *      controller.show();
     *
     * There are extra features, like you can add and image and title, change the window title as well
     *
     * NOTE: All lambdas passed into addButton need to include a return null; at the end (don't ask me why it's black
     * magic I tell you)
     */
    public GenericPopup() {
        this(null);
    }

    /***
     * Constructs a dialog from an exception.
     * @param exception The exception that you want to feed in to show the exception message.
     */
    public GenericPopup(Exception exception) {
        popupStage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/GenericPopup.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        popupScene = new Scene(this);
        popupStage.initOwner(App.stage);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setScene(popupScene);
        popupStage.setResizable(false);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo_small.png")));
        messageImage.setImage(iconImage);

        if (exception != null) {
            setMessageText(exception.getMessage());
            addOkButton(m -> this.close());
        }
    }


    /***
     * Adds a new button to the dialog. You must specify the text to go on the button, it's location on the dialog
     * (either the left hand side or the right hand side) and the function to call when it is clicked (this must be a
     * function that implements event notifier or just a lambda function with return null; at the end of it).
     * NOTE: Buttons stack on the left and right sides, therefore if you add two buttons on the left the first one added
     * will be the one closest to the left hand side, so keep that in mind.
     * @param buttonText The text on the button.
     * @param position The positioning of the button.
     * @param func The function to call when the button is clicked.
     * @param action Default action for button
     */
    public void addButton(String buttonText, Position position, Action action, Consumer func) {
        Button button = new Button(buttonText);
        button.setPrefSize(70, 25);
        //And this, is where the magic happens!
        button.setOnAction((a) -> {
            func.accept(null);
        });

        switch (action) {
            case DEFAULT:
                button.setDefaultButton(true);
                break;
            case CANCEL:
                button.setCancelButton(true);
                break;
            case NONE:
                break;
        }

        switch (position) {
            case LEFT:
                hBoxLeft.getChildren().add(button);
                break;
            case RIGHT:
                hBoxRight.getChildren().add(button);
                break;
        }
    }

    /***
     * Shows the dialog, this should be the last thing you call after setting up your dialog. If you have not set up a
     * title the dialog will automatically remove it and resize.
     */
    public void show() {
        if (messageTitle.getText().equals("Title")) {
            contentPane.getRowConstraints().get(0).setMinHeight(0);
            contentPane.getRowConstraints().get(0).setMaxHeight(0);

            messageImage.setVisible(false);
            messageTitle.setVisible(false);
            popupStage.setHeight(150);
        }
        popupStage.show();
    }

    /***
     * Closes the dialog.
     * Note: You may want to set up one of your buttons to call this, although if you use the addOkCancelButtons() with
     * only one lambda expression then the cancel button is automatically set to call this.
     */
    public void close() {
        popupStage.close();
    }

    /***
     * Set the message you want the the dialog to show, text will wrap but the dialog does not resize currently so don't
     * make it too long.
     * @param message The message you want to show on the dialog.
     */
    public void setMessageText(String message) {
        if (message == null) return;
        messageText.setText(message);
    }

    /***
     * Sets the title of the window (the bit that appears in the bar at the top)
     * @param title The window title.
     */
    public void setWindowTitle(String title) {
        popupStage.setTitle(title);
    }

    /***
     * Sets the title of the message (appears alongside the title image)
     * @param titleText The title of the message.
     */
    public void setTitleText(String titleText) {
        if (titleText == null) return;
        messageTitle.setText(titleText);
    }

    /***
     * Sets the image to appear beside the title.
     * NOTE: If you don't set the title text (not the window text) then this won't appear.
     * @param image image to set.
     */
    public void setTitleImage(Image image) {
        messageImage.setImage(image);
        messageImage.setFitWidth(50);
        messageImage.setFitHeight(50);
    }

    /***
     * Adds default OK Cancel Buttons, you specify what is supposed to happen for the ok button and the cancel button
     * remains it's default (closes the dialog)
     * @param okFunction The function you want to call on the ok button being clicked.
     */
    public void addOkCancelButtons(Consumer okFunction) {
        addOkCancelButtons(okFunction, m -> this.close());
    }

    /***
     * Adds default OK Cancel Buttons, you specify the functions for both the ok and cancel buttons when clicked
     * @param okFunction The function you want to call on ok button click
     * @param cancelFunction The function you want to call on cancel button click
     */
    public void addOkCancelButtons(Consumer okFunction, Consumer cancelFunction) {
        addButton("Cancel", Position.RIGHT, Action.CANCEL, cancelFunction);
        addButton("OK", Position.RIGHT, Action.DEFAULT, okFunction);
    }

    /***
     * Adds the default OK button with a specified function to call on it being clicked.
     * @param okFunction Function to call on ok button being clicked.
     */
    public void addOkButton(Consumer okFunction) {
        addButton("OK", Position.RIGHT, Action.DEFAULT, okFunction);
    }
}
