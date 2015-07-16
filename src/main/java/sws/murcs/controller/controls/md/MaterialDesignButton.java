package sws.murcs.controller.controls.md;

import com.sun.javafx.scene.control.skin.ButtonSkin;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import sws.murcs.debug.errorreporting.ErrorReporter;

/**
 * Code behind custom material design buttons.
 */
public class MaterialDesignButton extends Button {

    /**
     * The millisecond duration of the ripple effect.
     */
    private static final int DURATION = 250;

    /**
     * The opacity of the ripple itself.
     */
    private static final double RIPPLE_OPACITY = 0.11;

    /**
     * The radius of the ripple effect.
     */
    private static final double RADIUS = 0.1;

    /**
     * The ripple effect for the circle.
     */
    private Circle circleRipple;

    /**
     * The edge of the ripple.
     */
    private Rectangle rippleClip = new Rectangle();

    /**
     * The time duration of the ripple effect.
     */
    private Duration rippleDuration = Duration.millis(DURATION);

    /**
     * The height of the final ripple.
     */
    private double lastRippleHeight = 0;

    /**
     * The width of the final ripple.
     */
    private double lastRippleWidth = 0;

    /**
     * the colour of the ripple.
     */
    private Color rippleColour = new Color(0, 0, 0, RIPPLE_OPACITY);

    /**
     * Constructor for MeterialDesignButton.
     * @param text The text to display on the face of the button
     */
    public MaterialDesignButton(final String text) {
        super(text);

        getStyleClass().addAll("md-button");

        createRippleEffect();
    }

    @Override
    protected final Skin<?> createDefaultSkin() {
        final ButtonSkin buttonSkin = new ButtonSkin(this);
        // Adding circleRipple as fist node of button nodes to be on the bottom
        this.getChildren().add(0, circleRipple);
        return buttonSkin;
    }

    /**
     * Creates the effect of the ripple.
     */
    private void createRippleEffect() {
        circleRipple = new Circle(RADIUS, rippleColour);
        circleRipple.setOpacity(0.0);
        // Optional box blur on ripple - smoother ripple effect
        final int three = 3;
        circleRipple.setEffect(new BoxBlur(three, three, 2));

        // Fade effect bit longer to show edges on the end
        final FadeTransition fadeTransition = new FadeTransition(rippleDuration, circleRipple);
        fadeTransition.setInterpolator(Interpolator.EASE_OUT);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        final Timeline scaleRippleTimeline = new Timeline();

        final SequentialTransition parallelTransition = new SequentialTransition();
        parallelTransition.getChildren().addAll(
                scaleRippleTimeline,
                fadeTransition
        );

        parallelTransition.setOnFinished(event1 -> {
            circleRipple.setOpacity(0.0);
            circleRipple.setRadius(RADIUS);
        });

        this.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            parallelTransition.stop();
            parallelTransition.getOnFinished().handle(null);

            circleRipple.setCenterX(event.getX());
            circleRipple.setCenterY(event.getY());

            // Recalculate ripple size if size of button from last time was changed
            if (getWidth() != lastRippleWidth || getHeight() != lastRippleHeight) {
                lastRippleWidth = getWidth();
                lastRippleHeight = getHeight();

                rippleClip.setWidth(lastRippleWidth);
                rippleClip.setHeight(lastRippleHeight);

                try {
                    rippleClip.setArcHeight(this.getBackground().getFills().get(0).getRadii()
                            .getTopLeftHorizontalRadius());
                    rippleClip.setArcWidth(this.getBackground().getFills().get(0).getRadii()
                            .getTopLeftHorizontalRadius());
                    circleRipple.setClip(rippleClip);
                } catch (Exception e) {
                    ErrorReporter.get().reportError(e, "Unable to set ripple of material design button");
                }

                // Getting 45% of longest button's length, because we want edge of ripple effect always visible
                final double percentage = 0.45;
                double circleRippleRadius = Math.max(getHeight(), getWidth()) * percentage;
                final KeyValue keyValue =
                        new KeyValue(circleRipple.radiusProperty(), circleRippleRadius, Interpolator.EASE_OUT);
                final KeyFrame keyFrame = new KeyFrame(rippleDuration, keyValue);
                scaleRippleTimeline.getKeyFrames().clear();
                scaleRippleTimeline.getKeyFrames().add(keyFrame);
            }

            parallelTransition.playFromStart();
        });
    }

    /**
     * Sets the ripple colour.
     * @param colour The colour of the ripple.
     */
    public final void setRippleColour(final Color colour) {
        circleRipple.setFill(colour);
    }

}
