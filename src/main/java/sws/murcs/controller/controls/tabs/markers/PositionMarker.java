package sws.murcs.controller.controls.tabs.markers;

import com.sun.javafx.css.converters.PaintConverter;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A slightly modified version of the "PositionModifier" class found here:
 * https://github.com/sibvisions/javafx.DndTabPane
 *
 * Marker which can be used to show an insert position {@link javafx.scene.control.TabPane}.
 */
public final class PositionMarker extends Group {
	/**
	 * Create a new marker.
	 */
	@SuppressWarnings("checkstyle:magicnumber")
	public PositionMarker() {
		setMouseTransparent(true);
		getStyleClass().add("position-marker"); //$NON-NLS-1$
		Circle outer = new Circle(8);
		outer.setFill(Color.WHITE);

		getChildren().add(outer);

		Line l1 = new Line();
		l1.setStartX(0);
		l1.setStartY(6);
		l1.setEndX(0);
		l1.setEndY(40);
		l1.setStrokeWidth(7);
		l1.setStrokeLineCap(StrokeLineCap.ROUND);
		l1.setStroke(Color.WHITE);
		getChildren().add(l1);

		Circle c = new Circle(6);
		c.fillProperty().bind(fillProperty());
		getChildren().add(c);

		Circle inner = new Circle(3);
		inner.setFill(Color.WHITE);
		getChildren().add(inner);

		Line l2 = new Line();
		l2.setStartX(0);
		l2.setStartY(6);
		l2.setEndX(0);
		l2.setEndY(40);
		l2.setStrokeWidth(3);
		l2.setStrokeLineCap(StrokeLineCap.ROUND);
		l2.strokeProperty().bind(this.fill);
		getChildren().add(l2);

		setEffect(new DropShadow(3, Color.BLACK));
	}

    /**
     * The fill of the marker.
     */
	private final ObjectProperty<Paint> fill = new SimpleStyleableObjectProperty<>(FILL, this, "fill", Color.rgb(0, 139, 255)); //$NON-NLS-1$

	/**
	 * The property
	 * <p>
	 * The default color Color.rgb(0, 139, 255) <span style=
	 * "background-color: rgb(0, 139, 255); color: rgb(0, 139, 255); border-width: 1px;
     * border-color: black; border-style: solid; width: 15; height: 15;">__</span>
	 * </p>.
	 *
	 * @return the fill property of the marker
	 */
	public ObjectProperty<Paint> fillProperty() {
		return this.fill;
	}

	/**
	 * Set the fill of the marker
	 * <p>
	 * The default color Color.rgb(0, 139, 255) <span style=
	 * "background-color: rgb(0, 139, 255); color: rgb(0, 139, 255);
     * border-width: 1px; border-color: black; border-style: solid; width: 15; height: 15;">__</span>
	 * </p>.
	 *
	 * @param fill
	 *            the new fill
	 */
	public void setFill(final Paint fill) {
		fillProperty().set(fill);
	}

	/**
	 * Access the current fill
	 * <p>
	 * The default color Color.rgb(0, 139, 255) <span style=
	 * "background-color: rgb(0, 139, 255); color: rgb(0, 139, 255);
     * border-width: 1px; border-color: black; border-style: solid; width: 15; height: 15;">__</span>
	 * </p>.
	 *
	 * @return the current fill
	 */
	public Paint getFill() {
		return fillProperty().get();
	}

    /**
     * The css meta data for the marker.
     */
	private static final CssMetaData<PositionMarker, Paint> FILL = new CssMetaData<PositionMarker, Paint>("-fx-fill",
            PaintConverter.getInstance(), Color.rgb(0, 139, 255)) { //$NON-NLS-1$

		@Override
		public boolean isSettable(final PositionMarker node) {
			return !node.fillProperty().isBound();
		}

		@SuppressWarnings("unchecked")
		@Override
		public StyleableProperty<Paint> getStyleableProperty(final PositionMarker node) {
			return (StyleableProperty<Paint>) node.fillProperty();
		}

	};

    /**
     * A list of styles.
     */
	private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	static {

		final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Node.getClassCssMetaData());
		styleables.add(FILL);
		STYLEABLES = Collections.unmodifiableList(styleables);
	}

    /**
     * Gets the styles.
     * @return The styles.
     */
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return STYLEABLES;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}

}
