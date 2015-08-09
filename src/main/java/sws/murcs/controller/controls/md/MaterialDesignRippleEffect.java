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
    private Color rippleColour;

    /**
     * The secondary colour for the ripple.
     */
    private Color secondaryColour;

    /**
     * The standard colour for the ripple.
     */
    private Color standardColour = new Color(0, 0, 0, rippleOpacity);

    /**
     * The region to inject the ripple effect into.
     */
    private Region region;

    /**
     * Centers the ripple on the coordinates of the click.
     */
    private boolean centerOnClick = true;

    /**
     * If the ripple colour should alternate between standard and secondary colour.
     */
    private boolean alternatesColour = false;

    /**
     * Sets if the secondary colour is the active colour.
     */
    private boolean secondaryColourIsActive = false;

    /**
     * Sets if the ripple effect should come from the click event or the center of the node
     * Defaults to click event.
     * @param pCenterOnClick If the ripple effect comes from the click event.
     */
    public final void setCenterOnClick(final boolean pCenterOnClick) {
        centerOnClick = pCenterOnClick;
    }

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
        circleRipple.setRadius(radius);
    }

    /**
     * Sets the standard ripple colour.
     * @param colour The colour of the ripple.
     */
    public final void setStandardColour(final Color colour) {
        standardColour = colour;
    }

    /**
     * Sets the alternative ripple colour.
     * @param colour The colour of the ripple.
     */
    public final void setSecondaryColour(final Color colour) {
        secondaryColour = colour;
    }

    /**
     * Sets the standard ripple colour.
     * And forces an update of the ripple colour.
     * @param colour The colour of the ripple.
     * @param forceUpdate If the ripple colour should be updated now.
     */
    public final void setStandardColour(final Color colour, final boolean forceUpdate) {
        standardColour = colour;
        if (forceUpdate) {
            rippleColour = standardColour;
        }
        updateColour();
    }

    /**
     * Sets the secondary colour.
     * And forces an update of the ripple colour.
     * @param colour The new secondary colour.
     * @param forceUpdate If the ripple colour should be updated now.
     */
    public final void setSecondaryColour(final Color colour, final boolean forceUpdate) {
        secondaryColour = colour;
        if (forceUpdate) {
            rippleColour = secondaryColour;
        }
        updateColour();
    }

    /**
     * Alternates the ripple color between standard and alternative colours.
     * @param pAlternatesColour If the ripple colour should alternate.
     */
    public final void setAlternatesColour(final boolean pAlternatesColour) {
        alternatesColour = pAlternatesColour;
    }

    /**
     * Makes the alternate colour active.
     * @param pAlternateColourIsActive if the alternate colour is active.
     */
    public final void setSecondaryColourIsActive(final boolean pAlternateColourIsActive) {
        secondaryColourIsActive = pAlternateColourIsActive;
        alternateColours();
    }

    /**
     * Creates a new ripple effect on the given region.
     * @param pRegion The region to create the ripple effect on.
     */
    public MaterialDesignRippleEffect(final Region pRegion) {
        region = pRegion;
        circleRipple = new Circle();
        rippleColour = standardColour;
        updateColour();
        circleRipple.setRadius(radius);
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

            if (alternatesColour) {
                alternateColours();
            }

            if (centerOnClick) {
                circleRipple.setCenterX(event.getX());
                circleRipple.setCenterY(event.getY());
            }
            else {
                Double x = (region.getBoundsInLocal().getMaxX() + region.getBoundsInLocal().getMinX()) / 2;
                Double y = (region.getBoundsInLocal().getMaxY() + region.getBoundsInLocal().getMinY()) / 2;
                circleRipple.setCenterX(x);
                circleRipple.setCenterY(y);
            }

            // Recalculate ripple size if size of button from last time was changed
            if (region.getWidth() != lastRippleWidth || region.getHeight() != lastRippleHeight) {
                lastRippleWidth = region.getWidth();
                lastRippleHeight = region.getHeight();

                rippleClip.setWidth(lastRippleWidth);
                rippleClip.setHeight(lastRippleHeight);

                if (region.getBackground() != null) {
                    try {
                        rippleClip.setArcHeight(region.getBackground().getFills().get(0).getRadii()
                                .getTopLeftHorizontalRadius());
                        rippleClip.setArcWidth(region.getBackground().getFills().get(0).getRadii()
                                .getTopLeftHorizontalRadius());
                        circleRipple.setClip(rippleClip);
                    } catch (Exception e) {
                        ErrorReporter.get().reportError(e, "Unable to set ripple of material design button");
                    }
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

    /**
     * Alternates the ripple colour.
     */
    private void alternateColours() {
        if (secondaryColourIsActive) {
            rippleColour = standardColour;
            secondaryColourIsActive = false;
        }
        else {
            rippleColour = secondaryColour;
            secondaryColourIsActive = true;
        }
        updateColour();
    }

    /**
     * Updates the colour of the ripple effect.
     */
    private void updateColour() {
        circleRipple.setFill(rippleColour);
    }
}
