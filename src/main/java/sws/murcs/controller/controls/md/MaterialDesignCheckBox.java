package sws.murcs.controller.controls.md;

import com.sun.javafx.scene.control.skin.CheckBoxSkin;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Skin;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import sws.murcs.controller.JavaFXHelpers;

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
    @SuppressWarnings("checkstyle:magicnumber")
    public MaterialDesignCheckBox() {
        super();
        this.paddingProperty().setValue(new Insets(15));
        this.shapeProperty().setValue(new Circle(3));
        this.setBackground(new Background(
                new BackgroundFill(new Color(0, 0, 0, 0), new CornerRadii(1), new Insets(3, 3, 0, 0)))
        );
        rippleEffect = new MaterialDesignRippleEffect(this);
        rippleEffect.setSecondaryColour(JavaFXHelpers.hex2RGB("#2196f3", 0.5));
        rippleEffect.setAlternatesColour(true);
        rippleEffect.setSecondaryColourIsActive(true);
        rippleEffect.setCenterOnClick(false);
        rippleEffect.setRadius(0.005);
    }

    @Override
    protected final Skin<?> createDefaultSkin() {
        final CheckBoxSkin checkBoxSkin = new CheckBoxSkin(this);
        // Adding circleRipple as fist node of checkBox nodes to be on the bottom
        this.getChildren().add(0, rippleEffect.createRippleEffect());
        return checkBoxSkin;
    }
}
