package sws.murcs.controller.controls.md;

import com.sun.javafx.scene.control.skin.ButtonSkin;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import sws.murcs.internationalization.InternationalizationHelper;

/**
 * Code behind custom material design buttons.
 */
public class MaterialDesignButton extends Button {

    /**
     * The ripple effect for the toggleButton.
     */
    private MaterialDesignRippleEffect rippleEffect;

    /**
     * Constructor for MeterialDesignButton.
     * @param text The text to display on the face of the button
     */
    public MaterialDesignButton(final String text) {
        super(InternationalizationHelper.translatasert(text));

        rippleEffect = new MaterialDesignRippleEffect(this);
        getStyleClass().addAll("md-button");
    }

    @Override
    protected final Skin<?> createDefaultSkin() {
        final ButtonSkin buttonSkin = new ButtonSkin(this);
        // Adding circleRipple as fist node of button nodes to be on the bottom
        this.getChildren().add(0, rippleEffect.createRippleEffect());
        return buttonSkin;
    }

    /**
     * Sets the ripple colour of the button.
     * @param colour The new colour.
     */
    public final void setRippleColour(final Color colour) {
        rippleEffect.setSecondaryColour(colour, true);
    }
}
