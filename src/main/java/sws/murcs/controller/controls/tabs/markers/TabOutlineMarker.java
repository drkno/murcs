package sws.murcs.controller.controls.tabs.markers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeType;

import com.sun.javafx.css.converters.PaintConverter;

/**
 * A slightly modified version of the class found here:
 * https://github.com/sibvisions/javafx.DndTabPane
 *
 * Marks a Tab-Position.
 */
public final class TabOutlineMarker extends Group {
	/**
	 * The container bounds.
	 */
	private Bounds containerBounds;

	/**
	 * The reference bounds.
	 */
	private Bounds referenceBounds;

	/**
	 * Indicates whether this is "before".
	 */
	private boolean before;

	/**
	 * Create a new tab outline.
	 *
	 * @param containerBounds
	 *            the bounds of the container
	 * @param referenceBounds
	 *            the bounds of the reference tab
	 * @param before
	 *            <code>true</code> to mark the insert point before reference
	 *            bounds
	 */
	public TabOutlineMarker(final Bounds containerBounds, final Bounds referenceBounds, final boolean before) {
		this.containerBounds = containerBounds;
		this.referenceBounds = referenceBounds;
		updateBounds(containerBounds, referenceBounds, before);
		getStyleClass().add("tab-outline-marker"); //$NON-NLS-1$
	}

	/**
	 * Update the tab outline.
	 *
	 * @param containerBounds
	 *            the bounds of the container
	 * @param referenceBounds
	 *            the bounds of the reference tab
	 * @param before
	 *            <code>true</code> to mark the insert point before reference
	 *            bounds
	 */
	public void updateBounds(final Bounds containerBounds, final Bounds referenceBounds, final boolean before) {
		if (containerBounds.equals(this.containerBounds)
				&& referenceBounds.equals(this.referenceBounds)
				&& before == this.before) {
			return;
		}

		this.containerBounds = containerBounds;
		this.referenceBounds = referenceBounds;
		this.before = before;

		Polyline pl = new Polyline();

		Bounds _referenceBounds = referenceBounds;

		if (before) {
			_referenceBounds = new BoundingBox(Math.max(0,
					_referenceBounds.getMinX() - _referenceBounds.getWidth() / 2),
					_referenceBounds.getMinY(), _referenceBounds.getWidth(),
					_referenceBounds.getHeight());
		}
		else {
			_referenceBounds = new BoundingBox(Math.max(0,
					_referenceBounds.getMaxX() - _referenceBounds.getWidth() / 2),
					_referenceBounds.getMinY(), _referenceBounds.getWidth(),
					_referenceBounds.getHeight());
		}

		pl.getPoints().addAll(
		// -----------------
		// top
		// -----------------
		// start
				Double.valueOf(0.0), Double.valueOf(_referenceBounds.getMaxY()),

				// tab start
				Double.valueOf(_referenceBounds.getMinX()), Double.valueOf(_referenceBounds.getMaxY()),

				// // tab start top
				Double.valueOf(_referenceBounds.getMinX()), Double.valueOf(_referenceBounds.getMinY()),

				// tab end right
				Double.valueOf(_referenceBounds.getMaxX()), Double.valueOf(_referenceBounds.getMinY()),

				// tab end bottom
				Double.valueOf(_referenceBounds.getMaxX()), Double.valueOf(_referenceBounds.getMaxY()),

				// end
				Double.valueOf(containerBounds.getMaxX()), Double.valueOf(_referenceBounds.getMaxY()),

				// -----------------
				// right
				// -----------------
				Double.valueOf(containerBounds.getMaxX()), Double.valueOf(containerBounds.getMaxY()),

				// -----------------
				// bottom
				// -----------------
				Double.valueOf(containerBounds.getMinX()), Double.valueOf(containerBounds.getMaxY()),

				// -----------------
				// left
				// -----------------
				Double.valueOf(containerBounds.getMinX()), Double.valueOf(_referenceBounds.getMaxY()));
		pl.strokeProperty().bind(fillProperty());
		pl.setStrokeWidth(3);
		pl.setStrokeType(StrokeType.INSIDE);
		getChildren().setAll(pl);
	}

	/**
	 * The fill property of the outline marker.
	 */
	private final ObjectProperty<Paint> fill = new SimpleStyleableObjectProperty<>(FILL, this, "fill", Color.ORANGE); //$NON-NLS-1$

	/**
	 * The fill property
	 *
	 * <p>
	 * The default color {@link Color#ORANGE} <span style=
	 * "background-color: orange; color: orange; border-width: 1px;
	 * border-color: black; border-style: solid; width: 15; height: 15;">__</span>
	 * </p>.
	 *
	 * @return the property
	 */
	public ObjectProperty<Paint> fillProperty() {
		return this.fill;
	}

	/**
	 * Set a new fill
	 * <p>
	 * The default color {@link Color#ORANGE} <span style=
	 * "background-color: orange; color: orange; border-width: 1px;
	 * border-color: black; border-style: solid; width: 15; height: 15;">__</span>
	 * </p>.
	 *
	 * @param fill
	 *            the fill
	 */
	public void setFill(Paint fill) {
		fillProperty().set(fill);
	}

	/**
	 * Get the current fill
	 * <p>
	 * The default color {@link Color#ORANGE} <span style=
	 * "background-color: orange; color: orange; border-width: 1px;
	 * border-color: black; border-style: solid; width: 15; height: 15;">__</span>
	 * </p>.
	 *
	 * @return the current fill
	 */
	public Paint getFill() {
		return fillProperty().get();
	}

	/**
	 * The CSS meta data.
	 */
	private static final CssMetaData<TabOutlineMarker, Paint> FILL = new CssMetaData<TabOutlineMarker, Paint>("-fx-fill", PaintConverter.getInstance(), Color.ORANGE) { //$NON-NLS-1$

		@Override
		public boolean isSettable(TabOutlineMarker node) {
			return !node.fillProperty().isBound();
		}

		@SuppressWarnings("unchecked")
		@Override
		public StyleableProperty<Paint> getStyleableProperty(TabOutlineMarker node) {
			return (StyleableProperty<Paint>) node.fillProperty();
		}

	};

	/**
	 * All the stylable elements of the marker.
	 */
	private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	static {
		final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Group.getClassCssMetaData());
		styleables.add(FILL);
		STYLEABLES = Collections.unmodifiableList(styleables);
	}

	/**
	 * The stylable elements.
	 * @return The stylable elements.
	 */
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return STYLEABLES;
	}

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		return getClassCssMetaData();
	}
}
