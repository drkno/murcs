package sws.murcs.controller.controls.md;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import sws.murcs.debug.errorreporting.ErrorReporter;

/**
 * Creates a ripple effect for material Design elements.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class MaterialDesignRippleEffect {
    /**
     * The millisecond duration of the ripple effect.
     */
    private int duration = 250;

    /**
     * The opacity of the ripple itself.
     */
    private double rippleOpacity = 0.1;

    /**
     * The radius of the ripple effect.
     */
    private double radius = 0.15;

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
    private Duration rippleDuration = Duration.millis(duration);

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
    private Color rippleColour = new Color(0, 0, 0, rippleOpacity);

    /**
     * The region to inject the ripple effect into.
     */
    private Region region;

    /**
     * Sets the duration of the ripple effect.
     * Overrides the default of 250 milliseconds.
     * @param milliseconds The new duration.
     */
    public final  void setDuration(final int milliseconds) {
        duration = milliseconds;
        rippleDuration = Duration.millis(duration);
    }

    /**
     * Sets the opacity of the ripple effect.
     * Overrides the default of 0.1.
     * @param opacity the new opacity
     */
    public final void setRippleOpacity(final Double opacity) {
        rippleOpacity = opacity;
    }

    /**
     * Sets the radius of the ripple effect.
     * Overrides the default of 0.15.
     * @param pRadius The new radius
     */
    public final void setRadius(final Double pRadius) {
        radius = pRadius;
    }

    /**
     * Sets the ripple colour.
     * @param colour The colour of the ripple.
     */
    public final void setRippleColour(final Color colour) {
        circleRipple.setFill(colour);
    }

    /**
     * Creates a new ripple effect on the given region.
     * @param pRegion The region to create the ripple effect on.
     */
    public MaterialDesignRippleEffect(final Region pRegion) {
        region = pRegion;
        circleRipple = new Circle(radius, rippleColour);
    }

    /**
     * Creates the effect of the ripple.
     * @return The ripple effect.
     */
    public final Node createRippleEffect() {
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
            circleRipple.setRadius(radius);
        });

        region.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            parallelTransition.stop();
            parallelTransition.getOnFinished().handle(null);

            circleRipple.setCenterX(event.getX());
            circleRipple.setCenterY(event.getY());

            // Recalculate ripple size if size of button from last time was changed
            if (region.getWidth() != lastRippleWidth || region.getHeight() != lastRippleHeight) {
                lastRippleWidth = region.getWidth();
                lastRippleHeight = region.getHeight();

                rippleClip.setWidth(lastRippleWidth);
                rippleClip.setHeight(lastRippleHeight);

                try {
                    rippleClip.setArcHeight(region.getBackground().getFills().get(0).getRadii()
                            .getTopLeftHorizontalRadius());
                    rippleClip.setArcWidth(region.getBackground().getFills().get(0).getRadii()
                            .getTopLeftHorizontalRadius());
                    circleRipple.setClip(rippleClip);
                } catch (Exception e) {
                    ErrorReporter.get().reportError(e, "Unable to set ripple of material design button");
                }

                // Getting 45% of longest button's length, because we want edge of ripple effect always visible
                final double percentage = 0.45;
                double circleRippleRadius = Math.max(region.getHeight(), region.getWidth()) * percentage;
                final KeyValue keyValue =
                        new KeyValue(circleRipple.radiusProperty(), circleRippleRadius, Interpolator.EASE_OUT);
                final KeyFrame keyFrame = new KeyFrame(rippleDuration, keyValue);
                scaleRippleTimeline.getKeyFrames().clear();
                scaleRippleTimeline.getKeyFrames().add(keyFrame);
            }

            parallelTransition.playFromStart();
        });
        return circleRipple;
    }
}
