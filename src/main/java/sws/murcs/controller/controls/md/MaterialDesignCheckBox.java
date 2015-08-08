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
     * @param text The text next to the checkBox
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public MaterialDesignCheckBox(final String text) {
        super();
        this.paddingProperty().setValue(new Insets(15));
        this.shapeProperty().setValue(new Circle(3));
        this.setBackground(new Background(
                new BackgroundFill(new Color(0, 0, 0, 0), new CornerRadii(1), new Insets(0, 0, 0, -2)))
        );
        rippleEffect = new MaterialDesignRippleEffect(this);
        rippleEffect.setCenterOnClick(false);
        rippleEffect.setRadius(0.005);
    }

    /**
     * Creates a new Material design checkBox.
     */
    public MaterialDesignCheckBox() {
        this(null);
    }

    @Override
    protected final Skin<?> createDefaultSkin() {
        final CheckBoxSkin checkBoxSkin = new CheckBoxSkin(this);
        // Adding circleRipple as fist node of checkBox nodes to be on the bottom
        this.getChildren().add(0, rippleEffect.createRippleEffect());
        return checkBoxSkin;
    }
}
