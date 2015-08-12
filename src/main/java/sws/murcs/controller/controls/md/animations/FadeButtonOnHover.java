package sws.murcs.controller.controls.md.animations;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.util.Duration;


/**
 * Created by Dion on 12/08/2015.
 */
public class FadeButtonOnHover {

    private final Button button;
    private final Parent hoverTarget;
    private final Duration fadeDuration;

    public FadeButtonOnHover(final Button pButton, final Parent pHoverTarget) {
        this(pButton, pHoverTarget, Duration.millis(250));
    }

    public FadeButtonOnHover(final Button pButton, final Parent pHoverTarget, final Duration pFadeDuration) {
        button = pButton;
        hoverTarget = pHoverTarget;
        fadeDuration = pFadeDuration;
    }

    public void setupEffect() {
        button.setOpacity(0);

        final FadeTransition fadeIn = new FadeTransition(fadeDuration, button);
        fadeIn.setAutoReverse(false);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        final FadeTransition fadeOut = new FadeTransition(fadeDuration, button);
        fadeOut.setAutoReverse(false);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        hoverTarget.setOnMouseEntered(t -> {
            fadeIn.playFromStart();
        });

        hoverTarget.setOnMouseExited(t -> {
            fadeOut.playFromStart();
        });
    }
}
