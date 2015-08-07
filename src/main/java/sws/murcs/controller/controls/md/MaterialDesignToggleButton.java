package sws.murcs.controller.controls.md;

import com.sun.javafx.scene.control.skin.ToggleButtonSkin;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

/**
 * Material design toggle button class.
 */
public class MaterialDesignToggleButton extends ToggleButton {
    /**
     * The ripple effect for the toggleButton.
     */
    private MaterialDesignRippleEffect rippleEffect;

    /**
     * Creates a new Material Design Toggle Button with the given text.
     * @param text The text displayed on the button.
     */
    public MaterialDesignToggleButton(final String text) {
        super(text);
        rippleEffect = new MaterialDesignRippleEffect(this);
        getStyleClass().addAll("md-button");
    }

    @Override
    protected final Skin<?> createDefaultSkin() {
        final ToggleButtonSkin buttonSkin = new ToggleButtonSkin(this);
        // Adding circleRipple as fist node of button nodes to be on the bottom
        this.getChildren().add(0, rippleEffect.createRippleEffect());
        return buttonSkin;
    }

    /**
     * Sets the ripple colour of the toggle button.
     * @param colour the new ripple colour.
     */
    public final void setRippleColour(final Color colour) {
        rippleEffect.setRippleColour(colour);
    }
}
