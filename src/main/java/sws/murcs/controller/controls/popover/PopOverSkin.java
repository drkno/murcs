package sws.murcs.controller.controls.popover;

/**
 * PopOverSkin is a very slightly (to conform to checkstyle) modified
 * version of PopOverSkin from ControlsFX.
 * As such SOME OF THE JAVADOC MAY NOT BE STRICTLY RELEVANT.
 *
 * Copyright (c) 2013 - 2015, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.VLineTo;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.MAX_VALUE;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.scene.control.ContentDisplay.GRAPHIC_ONLY;
import static javafx.scene.paint.Color.YELLOW;

/**
 * Implements a skin for PopOver controls.
 */
public class PopOverSkin implements Skin<PopOver> {

    /**
     * Class to use for the detached style.
     */
    private static final String DETACHED_STYLE_CLASS = "detached";

    /**
     * X offset value of the PopOver control.
     */
    private double xOffset;

    /**
     * Y offset value of the PopOver control.
     */
    private double yOffset;

    /**
     * Determines if the PopOver control has been torn off an owner.
     */
    private boolean tornOff;

    /**
     * Path to use for the object edge.
     */
    private Path path;

    /**
     * Path to use for clip coordinates.
     */
    private Path clip;

    /**
     * BorderPane to contain the content of the PopOver control.
     */
    private BorderPane content;

    /**
     * Stack pane to contain the title and close icon.
     */
    private StackPane titlePane;

    /**
     * Stack pane to contain the BorderPane for content and the StackPane for the title.
     */
    private StackPane stackPane;

    /**
     * Location where dragging started from.
     */
    private Point2D dragStartLocation;

    /**
     * The PopOver control being skinned.
     */
    private PopOver popOver;

    /**
     * Creates a new PopOver skin for a PopOver control.
     * @param thePopOver control to make the skin for.
     */
    public PopOverSkin(final PopOver thePopOver) {
        popOver = thePopOver;
        stackPane = thePopOver.getRoot();
        stackPane.setPickOnBounds(false);
        Bindings.bindContent(stackPane.getStyleClass(), thePopOver.getStyleClass());
        stackPane.minWidthProperty().bind(
                Bindings.add(Bindings.multiply(2, thePopOver.arrowSizeProperty()),
                        Bindings.add(
                                Bindings.multiply(2,
                                        thePopOver.cornerRadiusProperty()),
                                Bindings.multiply(2,
                                        thePopOver.arrowIndentProperty()))));

        stackPane.minHeightProperty().bind(stackPane.minWidthProperty());

        Label closeIcon = new Label();
        closeIcon.setGraphic(createCloseIcon());
        closeIcon.setMaxSize(MAX_VALUE, MAX_VALUE);
        closeIcon.setContentDisplay(GRAPHIC_ONLY);
        closeIcon.visibleProperty().bind(thePopOver.detachedProperty());
        closeIcon.getStyleClass().add("icon"); //$NON-NLS-1$
        closeIcon.setAlignment(CENTER_LEFT);
        closeIcon.getGraphic().setOnMouseClicked(evt -> thePopOver.hide());
        titlePane = new StackPane();
        titlePane.getChildren().add(closeIcon);
        titlePane.getStyleClass().add("title"); //$NON-NLS-1$
        content = new BorderPane();
        content.setCenter(thePopOver.contentNodeProperty().get());
        content.getStyleClass().add("content"); //$NON-NLS-1$

        if (thePopOver.detachedProperty().get()) {
            if (thePopOver.detachedCloseButtonProperty().get()) {
                content.setTop(titlePane);
            }
            thePopOver.getStyleClass().add(DETACHED_STYLE_CLASS);
            content.getStyleClass().add(DETACHED_STYLE_CLASS);
        }

        InvalidationListener updatePathListener = observable -> updatePath();
        getPopupWindow().xProperty().addListener(updatePathListener);
        getPopupWindow().yProperty().addListener(updatePathListener);
        thePopOver.arrowLocationProperty().addListener(updatePathListener);
        thePopOver.contentNodeProperty().addListener(
                (value, oldContent, newContent) -> content
                        .setCenter(newContent));
        thePopOver.detachedProperty()
                .addListener((value, oldDetached, newDetached) -> {

                    if (newDetached) {
                        thePopOver.getStyleClass().add(DETACHED_STYLE_CLASS);
                        content.getStyleClass().add(DETACHED_STYLE_CLASS);
                        content.setTop(titlePane);

                        switch (getSkinnable().arrowLocationProperty().get()) {
                            case LEFT_TOP:
                            case LEFT_CENTER:
                            case LEFT_BOTTOM:
                                thePopOver.setX(
                                        thePopOver.getX() + thePopOver.arrowSizeProperty().get());
                                break;
                            case TOP_LEFT:
                            case TOP_CENTER:
                            case TOP_RIGHT:
                                thePopOver.setY(
                                        thePopOver.getY() + thePopOver.arrowSizeProperty().get());
                                break;
                            default:
                                break;
                        }
                    } else {
                        thePopOver.getStyleClass().remove(DETACHED_STYLE_CLASS);
                        content.getStyleClass().remove(DETACHED_STYLE_CLASS);
                        content.setTop(null);
                    }

                    thePopOver.sizeToScene();

                    updatePath();
                });

        path = new Path();
        path.getStyleClass().add("border"); //$NON-NLS-1$
        path.setManaged(false);

        clip = new Path();

        /*
         * The clip is a path and the path has to be filled with a color.
         * Otherwise clipping will not work.
         */
        clip.setFill(YELLOW);

        createPathElements();
        updatePath();

        final EventHandler<MouseEvent> mousePressedHandler = evt -> {
            if (thePopOver.detachableProperty().get() || thePopOver.detachedProperty().get()) {
                tornOff = false;

                xOffset = evt.getScreenX();
                yOffset = evt.getScreenY();

                dragStartLocation = new Point2D(xOffset, yOffset);
            }
        };

        final EventHandler<MouseEvent> mouseReleasedHandler = evt -> {
            PopOver p = getSkinnable();
            if (tornOff && !p.detachedProperty().get()) {
                tornOff = false;

                if (p.detachableProperty().get()) {
                    p.detachedProperty().set(true);
                }
            }
        };

        final int twenty = 20;
        final EventHandler<MouseEvent> mouseDragHandler = evt -> {
            if (thePopOver.detachableProperty().get() || thePopOver.detachedProperty().get()) {
                double deltaX = evt.getScreenX() - xOffset;
                double deltaY = evt.getScreenY() - yOffset;

                Window window = getSkinnable().getScene().getWindow();

                window.setX(window.getX() + deltaX);
                window.setY(window.getY() + deltaY);

                xOffset = evt.getScreenX();
                yOffset = evt.getScreenY();

                if (dragStartLocation.distance(xOffset, yOffset) > twenty) {
                    tornOff = true;
                    updatePath();
                } else if (tornOff) {
                    tornOff = false;
                    updatePath();
                }
            }
        };

        stackPane.setOnMousePressed(mousePressedHandler);
        stackPane.setOnMouseDragged(mouseDragHandler);
        stackPane.setOnMouseReleased(mouseReleasedHandler);

        stackPane.getChildren().add(path);
        stackPane.getChildren().add(content);

        content.setClip(clip);
    }

    @Override
    public final Node getNode() {
        return stackPane;
    }

    @Override
    public final PopOver getSkinnable() {
        return popOver;
    }

    @Override
    public void dispose() {
    }

    /**
     * Creates the close icon for the PopOver window.
     * @return the close icon of the PopOver window.
     */
    private Node createCloseIcon() {
        Group group = new Group();
        group.getStyleClass().add("graphics"); //$NON-NLS-1$

        final int six = 6, four = 4, eight = 8;
        Circle circle = new Circle();
        circle.getStyleClass().add("circle"); //$NON-NLS-1$
        circle.setRadius(six);
        circle.setCenterX(six);
        circle.setCenterY(six);
        group.getChildren().add(circle);

        Line line1 = new Line();
        line1.getStyleClass().add("line"); //$NON-NLS-1$
        line1.setStartX(four);
        line1.setStartY(four);
        line1.setEndX(eight);
        line1.setEndY(eight);
        group.getChildren().add(line1);

        Line line2 = new Line();
        line2.getStyleClass().add("line"); //$NON-NLS-1$
        line2.setStartX(eight);
        line2.setStartY(four);
        line2.setEndX(four);
        line2.setEndY(eight);
        group.getChildren().add(line2);

        return group;
    }

    /**
     * Position to move to.
     */
    private MoveTo moveTo;

    /**
     * Curves for each of the corners.
     */
    private QuadCurveTo topCurveTo, rightCurveTo, bottomCurveTo, leftCurveTo;

    /**
     * Horizontal lines for each of the edges.
     */
    private HLineTo lineBTop, lineETop, lineHTop, lineKTop;

    /**
     * More lines for the top edges.
     */
    private LineTo lineCTop, lineDTop, lineFTop, lineGTop, lineITop, lineJTop;

    /**
     * More lines for the right edges.
     */
    private VLineTo lineBRight, lineERight, lineHRight, lineKRight;

    /**
     * Additional lines for the right edges.
     */
    private LineTo lineCRight, lineDRight, lineFRight, lineGRight, lineIRight, lineJRight;

    /**
     * Lines on the bottom.
     */
    private HLineTo lineBBottom, lineEBottom, lineHBottom, lineKBottom;

    /**
     * Additional lines on the bottom.
     */
    private LineTo lineCBottom, lineDBottom, lineFBottom, lineGBottom, lineIBottom, lineJBottom;

    /**
     * Lines on the left side.
     */
    private VLineTo lineBLeft, lineELeft, lineHLeft, lineKLeft;

    /**
     * Additional lines on the left side.
     */
    private LineTo lineCLeft, lineDLeft, lineFLeft, lineGLeft, lineILeft, lineJLeft;

    /**
     * Creates the required path elements for skinning the PopupControl.
     * Blame checkstyle for the wall of code.
     */
    private void createPathElements() {
        DoubleProperty centerYProperty = new SimpleDoubleProperty();
        DoubleProperty centerXProperty = new SimpleDoubleProperty();
        DoubleProperty leftEdgeProperty = new SimpleDoubleProperty();
        DoubleProperty leprp = new SimpleDoubleProperty();
        DoubleProperty topEdgeProperty = new SimpleDoubleProperty();
        DoubleProperty teprp = new SimpleDoubleProperty();
        DoubleProperty rightEdgeProperty = new SimpleDoubleProperty();
        DoubleProperty remrp = new SimpleDoubleProperty();
        DoubleProperty bottomEdgeProperty = new SimpleDoubleProperty();
        DoubleProperty bemrp = new SimpleDoubleProperty();
        DoubleProperty cornerProperty = getSkinnable().cornerRadiusProperty();
        DoubleProperty asp = getSkinnable().arrowSizeProperty();
        DoubleProperty aip = getSkinnable().arrowIndentProperty();
        centerYProperty.bind(Bindings.divide(stackPane.heightProperty(), 2));
        centerXProperty.bind(Bindings.divide(stackPane.widthProperty(), 2));
        leprp.bind(Bindings.add(leftEdgeProperty, getSkinnable().cornerRadiusProperty()));
        teprp.bind(Bindings.add(topEdgeProperty, getSkinnable().cornerRadiusProperty()));
        rightEdgeProperty.bind(stackPane.widthProperty());
        remrp.bind(Bindings.subtract(rightEdgeProperty, getSkinnable().cornerRadiusProperty()));
        bottomEdgeProperty.bind(stackPane.heightProperty());
        bemrp.bind(Bindings.subtract(bottomEdgeProperty, getSkinnable().cornerRadiusProperty()));
        moveTo = new MoveTo();
        moveTo.xProperty().bind(leprp);
        moveTo.yProperty().bind(topEdgeProperty);
        lineBTop = new HLineTo();
        lineBTop.xProperty().bind(Bindings.add(leprp, aip));
        lineCTop = new LineTo();
        lineCTop.xProperty().bind(Bindings.add(lineBTop.xProperty(), asp));
        lineCTop.yProperty().bind(Bindings.subtract(topEdgeProperty, asp));
        lineDTop = new LineTo();
        lineDTop.xProperty().bind(Bindings.add(lineCTop.xProperty(), asp));
        lineDTop.yProperty().bind(topEdgeProperty);
        lineETop = new HLineTo();
        lineETop.xProperty().bind(Bindings.subtract(centerXProperty, asp));
        lineFTop = new LineTo();
        lineFTop.xProperty().bind(centerXProperty);
        lineFTop.yProperty().bind(Bindings.subtract(topEdgeProperty, asp));
        lineGTop = new LineTo();
        lineGTop.xProperty().bind(Bindings.add(centerXProperty, asp));
        lineGTop.yProperty().bind(topEdgeProperty);
        lineHTop = new HLineTo();
        lineHTop.xProperty().bind(Bindings.subtract(Bindings.subtract(remrp, aip), Bindings.multiply(asp, 2)));
        lineITop = new LineTo();
        lineITop.xProperty().bind(Bindings.subtract(Bindings.subtract(remrp, aip), asp));
        lineITop.yProperty().bind(Bindings.subtract(topEdgeProperty, asp));
        lineJTop = new LineTo();
        lineJTop.xProperty().bind(Bindings.subtract(remrp, aip));
        lineJTop.yProperty().bind(topEdgeProperty);
        lineKTop = new HLineTo();
        lineKTop.xProperty().bind(remrp);
        rightCurveTo = new QuadCurveTo();   // right
        rightCurveTo.xProperty().bind(rightEdgeProperty);
        rightCurveTo.yProperty().bind(Bindings.add(topEdgeProperty, cornerProperty));
        rightCurveTo.controlXProperty().bind(rightEdgeProperty);
        rightCurveTo.controlYProperty().bind(topEdgeProperty);
        lineBRight = new VLineTo();
        lineBRight.yProperty().bind(Bindings.add(teprp, aip));
        lineCRight = new LineTo();
        lineCRight.xProperty().bind(Bindings.add(rightEdgeProperty, asp));
        lineCRight.yProperty().bind(Bindings.add(lineBRight.yProperty(), asp));
        lineDRight = new LineTo();
        lineDRight.xProperty().bind(rightEdgeProperty);
        lineDRight.yProperty().bind(Bindings.add(lineCRight.yProperty(), asp));
        lineERight = new VLineTo();
        lineERight.yProperty().bind(Bindings.subtract(centerYProperty, asp));
        lineFRight = new LineTo();
        lineFRight.xProperty().bind(Bindings.add(rightEdgeProperty, asp));
        lineFRight.yProperty().bind(centerYProperty);
        lineGRight = new LineTo();
        lineGRight.xProperty().bind(rightEdgeProperty);
        lineGRight.yProperty().bind(Bindings.add(centerYProperty, asp));
        lineHRight = new VLineTo();
        lineHRight.yProperty().bind(Bindings.subtract(Bindings.subtract(bemrp, aip), Bindings.multiply(asp, 2)));
        lineIRight = new LineTo();
        lineIRight.xProperty().bind(Bindings.add(rightEdgeProperty, asp));
        lineIRight.yProperty().bind(Bindings.subtract(Bindings.subtract(bemrp, aip), asp));
        lineJRight = new LineTo();
        lineJRight.xProperty().bind(rightEdgeProperty);
        lineJRight.yProperty().bind(Bindings.subtract(bemrp, aip));
        lineKRight = new VLineTo();
        lineKRight.yProperty().bind(bemrp);
        bottomCurveTo = new QuadCurveTo();  // bottom
        bottomCurveTo.xProperty().bind(remrp);
        bottomCurveTo.yProperty().bind(bottomEdgeProperty);
        bottomCurveTo.controlXProperty().bind(rightEdgeProperty);
        bottomCurveTo.controlYProperty().bind(bottomEdgeProperty);
        lineBBottom = new HLineTo();
        lineBBottom.xProperty().bind(Bindings.subtract(remrp, aip));
        lineCBottom = new LineTo();
        lineCBottom.xProperty().bind(Bindings.subtract(lineBBottom.xProperty(), asp));
        lineCBottom.yProperty().bind(Bindings.add(bottomEdgeProperty, asp));
        lineDBottom = new LineTo();
        lineDBottom.xProperty().bind(Bindings.subtract(lineCBottom.xProperty(), asp));
        lineDBottom.yProperty().bind(bottomEdgeProperty);
        lineEBottom = new HLineTo();
        lineEBottom.xProperty().bind(Bindings.add(centerXProperty, asp));
        lineFBottom = new LineTo();
        lineFBottom.xProperty().bind(centerXProperty);
        lineFBottom.yProperty().bind(Bindings.add(bottomEdgeProperty, asp));
        lineGBottom = new LineTo();
        lineGBottom.xProperty().bind(Bindings.subtract(centerXProperty, asp));
        lineGBottom.yProperty().bind(bottomEdgeProperty);
        lineHBottom = new HLineTo();
        lineHBottom.xProperty().bind(Bindings.add(Bindings.add(leprp, aip), Bindings.multiply(asp, 2)));
        lineIBottom = new LineTo();
        lineIBottom.xProperty().bind(Bindings.add(Bindings.add(leprp, aip), asp));
        lineIBottom.yProperty().bind(Bindings.add(bottomEdgeProperty, asp));
        lineJBottom = new LineTo();
        lineJBottom.xProperty().bind(Bindings.add(leprp, aip));
        lineJBottom.yProperty().bind(bottomEdgeProperty);
        lineKBottom = new HLineTo();
        lineKBottom.xProperty().bind(leprp);
        leftCurveTo = new QuadCurveTo();    // left
        leftCurveTo.xProperty().bind(leftEdgeProperty);
        leftCurveTo.yProperty().bind(Bindings.subtract(bottomEdgeProperty, cornerProperty));
        leftCurveTo.controlXProperty().bind(leftEdgeProperty);
        leftCurveTo.controlYProperty().bind(bottomEdgeProperty);
        lineBLeft = new VLineTo();
        lineBLeft.yProperty().bind(Bindings.subtract(bemrp, aip));
        lineCLeft = new LineTo();
        lineCLeft.xProperty().bind(Bindings.subtract(leftEdgeProperty, asp));
        lineCLeft.yProperty().bind(Bindings.subtract(lineBLeft.yProperty(), asp));
        lineDLeft = new LineTo();
        lineDLeft.xProperty().bind(leftEdgeProperty);
        lineDLeft.yProperty().bind(Bindings.subtract(lineCLeft.yProperty(), asp));
        lineELeft = new VLineTo();
        lineELeft.yProperty().bind(Bindings.add(centerYProperty, asp));
        lineFLeft = new LineTo();
        lineFLeft.xProperty().bind(Bindings.subtract(leftEdgeProperty, asp));
        lineFLeft.yProperty().bind(centerYProperty);
        lineGLeft = new LineTo();
        lineGLeft.xProperty().bind(leftEdgeProperty);
        lineGLeft.yProperty().bind(Bindings.subtract(centerYProperty, asp));
        lineHLeft = new VLineTo();
        lineHLeft.yProperty().bind(Bindings.add(Bindings.add(teprp, aip), Bindings.multiply(asp, 2)));
        lineILeft = new LineTo();
        lineILeft.xProperty().bind(Bindings.subtract(leftEdgeProperty, asp));
        lineILeft.yProperty().bind(Bindings.add(Bindings.add(teprp, aip), asp));
        lineJLeft = new LineTo();
        lineJLeft.xProperty().bind(leftEdgeProperty);
        lineJLeft.yProperty().bind(Bindings.add(teprp, aip));
        lineKLeft = new VLineTo();
        lineKLeft.yProperty().bind(teprp);
        topCurveTo = new QuadCurveTo();
        topCurveTo.xProperty().bind(leprp);
        topCurveTo.yProperty().bind(topEdgeProperty);
        topCurveTo.controlXProperty().bind(leftEdgeProperty);
        topCurveTo.controlYProperty().bind(topEdgeProperty);
    }

    /**
     * Gets the window of the PopupControl.
     * @return the window of the PopupControl.
     */
    private Window getPopupWindow() {
        return getSkinnable().getScene().getWindow();
    }

    /**
     * Shows the arrow in a specified location.
     * @param location location to show the arrow.
     * @return if showing the arrow was successful.
     */
    private boolean showArrow(final ArrowLocation location) {
        ArrowLocation arrowLocation = getSkinnable().arrowLocationProperty().get();
        return location.equals(arrowLocation) && !getSkinnable().detachedProperty().get()
                && !tornOff;
    }

    /**
     * Updates the path used to draw the arrow.
     */
    private void updatePath() {
        List<PathElement> elements = new ArrayList<>();
        elements.add(moveTo);

        if (showArrow(ArrowLocation.TOP_LEFT)) {
            elements.add(lineBTop);
            elements.add(lineCTop);
            elements.add(lineDTop);
        }
        if (showArrow(ArrowLocation.TOP_CENTER)) {
            elements.add(lineETop);
            elements.add(lineFTop);
            elements.add(lineGTop);
        }
        if (showArrow(ArrowLocation.TOP_RIGHT)) {
            elements.add(lineHTop);
            elements.add(lineITop);
            elements.add(lineJTop);
        }
        elements.add(lineKTop);
        elements.add(rightCurveTo);

        if (showArrow(ArrowLocation.RIGHT_TOP)) {
            elements.add(lineBRight);
            elements.add(lineCRight);
            elements.add(lineDRight);
        }
        if (showArrow(ArrowLocation.RIGHT_CENTER)) {
            elements.add(lineERight);
            elements.add(lineFRight);
            elements.add(lineGRight);
        }
        if (showArrow(ArrowLocation.RIGHT_BOTTOM)) {
            elements.add(lineHRight);
            elements.add(lineIRight);
            elements.add(lineJRight);
        }
        elements.add(lineKRight);
        elements.add(bottomCurveTo);

        if (showArrow(ArrowLocation.BOTTOM_RIGHT)) {
            elements.add(lineBBottom);
            elements.add(lineCBottom);
            elements.add(lineDBottom);
        }
        if (showArrow(ArrowLocation.BOTTOM_CENTER)) {
            elements.add(lineEBottom);
            elements.add(lineFBottom);
            elements.add(lineGBottom);
        }
        if (showArrow(ArrowLocation.BOTTOM_LEFT)) {
            elements.add(lineHBottom);
            elements.add(lineIBottom);
            elements.add(lineJBottom);
        }
        elements.add(lineKBottom);
        elements.add(leftCurveTo);

        if (showArrow(ArrowLocation.LEFT_BOTTOM)) {
            elements.add(lineBLeft);
            elements.add(lineCLeft);
            elements.add(lineDLeft);
        }
        if (showArrow(ArrowLocation.LEFT_CENTER)) {
            elements.add(lineELeft);
            elements.add(lineFLeft);
            elements.add(lineGLeft);
        }
        if (showArrow(ArrowLocation.LEFT_TOP)) {
            elements.add(lineHLeft);
            elements.add(lineILeft);
            elements.add(lineJLeft);
        }
        elements.add(lineKLeft);
        elements.add(topCurveTo);

        path.getElements().setAll(elements);
        clip.getElements().setAll(elements);
    }
}
