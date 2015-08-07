package sws.murcs.controller.controls.md;

import com.sun.javafx.scene.control.skin.CheckBoxSkin;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Skin;


/**
 * Creates a material design checkbox.
 */
public class MaterialDesignCheckBox extends CheckBox {

    /**
     * The ripple effect for the checkBox.
     */
    private MaterialDesignRippleEffect rippleEffect;

    /**
     * Creates a new Material design checkBox.
     */
    public  MaterialDesignCheckBox() {
       rippleEffect = new MaterialDesignRippleEffect(this);
    }

    @Override
    protected final Skin<?> createDefaultSkin() {
        final CheckBoxSkin checkBoxSkin = new CheckBoxSkin(this);
        // Adding circleRipple as fist node of checkBox nodes to be on the bottom
        this.getChildren().add(0, rippleEffect.createRippleEffect());
        return checkBoxSkin;
    }
}
