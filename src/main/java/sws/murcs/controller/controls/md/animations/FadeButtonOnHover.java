package sws.murcs.controller.controls.md.animations;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.util.Duration;


/**
 * Creates and opacity fade effect on buttons.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class FadeButtonOnHover {

    /**
     * The button to fade in and out.
     */
    private Button button;

    /**
     * The target which the button should fade on.
     */
    private Parent hoverTarget;

    /**
     * The duration of the fade.
     */
    private Duration fadeDuration;

    /**
     * Delay before fading in milliseconds.
     */
    private double delay = 250;

    /**
     * If the starting fade should be delayed.
     */
    private boolean delayStart = true;

    /**
     * If the endin fade should be delayed.
     */
    private boolean delayEnd = false;

    /**
     * Creates a fade on hover effect for a button.
     * @param pButton the button to fade.
     * @param pHoverTarget the hover target.
     */
    public FadeButtonOnHover(final Button pButton, final Parent pHoverTarget) {
        this(pButton, pHoverTarget, Duration.millis(250));
    }

    /**
     * Creates a fade on hover effect for a button.
     * @param pButton the button to fade.
     * @param pHoverTarget the hover target.
     * @param pFadeDuration the duration of the fade.
     */
    public FadeButtonOnHover(final Button pButton, final Parent pHoverTarget, final Duration pFadeDuration) {
        button = pButton;
        hoverTarget = pHoverTarget;
        fadeDuration = pFadeDuration;
    }

    /**
     * Sets the delay before a fade.
     * @param pDelay The delay before fading.
     */
    public final void setDelay(final double pDelay) {
        delay = pDelay;
    }

    /**
     * Sets If the start of the fade in should be delayed.
     * @param pDelayStart if the start of the fade in is delayed.
     */
    public final void delayStartFade(final boolean pDelayStart) {
        delayStart = pDelayStart;
    }

    /**
     * Sets if the start of the fade out is delayed.
     * @param pDelayEnd if the start of the fade out is delayed.
     */
    public final void delayEndFade(final boolean pDelayEnd) {
        delayEnd = pDelayEnd;
    }

    /**
     * Sets up the effect on the button.
     */
    public final void setupEffect() {
        button.setOpacity(0.1);

        final FadeTransition fadeInEmbedded = new FadeTransition(fadeDuration, button);
        fadeInEmbedded.setAutoReverse(false);
        fadeInEmbedded.setFromValue(0.1);
        fadeInEmbedded.setToValue(1);

        final FadeTransition fadeOutEmbedded = new FadeTransition(fadeDuration, button);
        fadeOutEmbedded.setAutoReverse(false);
        fadeOutEmbedded.setFromValue(1);
        fadeOutEmbedded.setToValue(0.1);

        final FadeTransition fadeIn = new FadeTransition(fadeDuration, button);
        fadeIn.setAutoReverse(false);
        fadeIn.setFromValue(0.1);
        fadeIn.setToValue(1);

        final FadeTransition fadeOut = new FadeTransition(fadeDuration, button);
        fadeOut.setAutoReverse(false);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0.1);

        final PauseTransition pause = new PauseTransition(Duration.millis(delay));

        SequentialTransition seqFadeIn = new SequentialTransition(pause, fadeInEmbedded);
        SequentialTransition seqFadeOut = new SequentialTransition(pause, fadeOutEmbedded);
        hoverTarget.setOnMouseEntered(t -> {
            if (delayEnd) {
                seqFadeOut.stop();
            }
            if (delayStart) {
                if (delayEnd) {
                    fadeInEmbedded.setFromValue(fadeOutEmbedded.getFromValue());
                }
                seqFadeIn.play();
            }
            else {
                if (delayEnd) {
                    fadeIn.setFromValue(fadeOutEmbedded.getFromValue());
                }
                fadeIn.playFromStart();
            }
        });

        hoverTarget.setOnMouseExited(t -> {
            if (delayStart) {
                seqFadeIn.stop();
            }
            if (delayEnd) {
                if (delayStart) {
                    fadeOutEmbedded.setFromValue(fadeInEmbedded.getByValue());
                }
                seqFadeOut.play();
            }
            else {
                if (delayStart) {
                    fadeOut.setFromValue(fadeInEmbedded.getByValue());
                }
                fadeOut.playFromStart();
            }
        });
    }
}
