package sws.murcs.helpfulHints;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * Controls the helpful hints in view when there is an empty organisation.
 */
public class HelpfulHintsController {

    /**
     * The main boarderPane.
     */
    @FXML
    private BorderPane hintPane;

    /**
     * The hints label.
     */
    @FXML
    private Label hintLabel;

    /**
     * The duration of the fade in and out.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private final Duration fadeDuration = Duration.millis(500);

    /**
     * The pause duration for how long the text stays visible.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private final Duration pauseDuration = Duration.millis(3000);

    /**
     * The model to extract the hints from.
     */
    private HelpfulHintsView model;

    /**
     * The animation for fading in and out the hint.
     */
    private SequentialTransition hintsAnimation;

    /**
     * Initializes parameters during controller loading from the fxml.
     */
    @FXML
    private void initialize() {
        setupHintAnimation();
    }

    /**
     * Sets upd the hints initially.
     * Required to be called after creation.
     */
    protected void setup() {
        hintLabel.setTextAlignment(TextAlignment.CENTER);
        hintLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        hintLabel.setText(model.getHint());
    }

    /**
     * Sets up the animation for hints.
     */
    private void setupHintAnimation() {
        FadeTransition fadeIn = new FadeTransition(fadeDuration, hintPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(fadeDuration, hintPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(event -> {
            hintLabel.setText(model.getHint());
        });

        PauseTransition pauseTransition = new PauseTransition(pauseDuration);

        hintsAnimation = new SequentialTransition(fadeIn, pauseTransition, fadeOut);
        hintsAnimation.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * Plays the animation.
     */
    protected final void play() {
        hintsAnimation.play();
    }

    /**
     * Stops and hides the animation.
     */
    protected final void hide() {
        hintsAnimation.stop();
        hintLabel.setOpacity(0);
    }

    /**
     * Sets the model to get hints from.
     * Required when setting up the controller.
     * @param pModel The model for hint.
     */
    public final void setModel(final HelpfulHintsView pModel) {
        model = pModel;
    }
}
