package sws.murcs.helpfulHints;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.debug.sampledata.GenerationHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wooll on 9/09/2015.
 */
public class HelpfulHintsController {

    @FXML
    private BorderPane hintPane;
    @FXML
    private Label hintLabel;

    private Duration fadeDuration = Duration.millis(500);

    private Duration pauseDuration = Duration.millis(3000);

    private HelpfulHintsView model;
    private SequentialTransition hintsAnimation;

    @FXML
    private void initialize() {
        setupFadeTransitions();
    }

    protected void setup() {
        hintLabel.setText(model.getHint());
    }

    private void setupFadeTransitions() {
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

    protected void play() {
        hintsAnimation.play();
    }

    protected void hide() {
        hintsAnimation.stop();
        hintLabel.setOpacity(0);
    }

    public void setModel(final HelpfulHintsView pModel) {
        model = pModel;
    }
}
