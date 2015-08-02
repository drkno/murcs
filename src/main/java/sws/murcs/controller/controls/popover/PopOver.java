package sws.murcs.controller.controls.popover;

import javafx.animation.FadeTransition;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import static java.util.Objects.requireNonNull;
import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;

/**
 * PopOver control.
 * Creates a popover window that can be attached to a control or floating.
 *
 * This control is heavily based and inspired by the one in ControlsFX.
 * Code that has been outright copied is available under the ControlsFX license available here:
 * https://bitbucket.org/controlsfx/controlsfx/src/31432521e87161299b59f9c67ceb5b9ed9ac6c36/license.txt?at=default
 * All remaining code is under the MIT license which is compatible with the above.
 */
public class PopOver extends PopupControl {

    /**
     * Default fade duration value.
     */
    private final double defaultFadeDuration = 0.2;

    /**
     * Default padding around the no-content label.
     */
    private final int defaultLabelPadding = 15;

    /**
     * Default offset value.
     */
    private final int defaultOffset = 4;

    /**
     * Default font and indent size.
     */
    private final int defaultSize = 8;

    /**
     * Duration to fade for while showing and hiding the control.
     */
    private double fadeDuration = defaultFadeDuration;

    /**
     * The root control of the PopOver.
     */
    private StackPane root;

    /**
     * Last X coordinate location.
     */
    private double lastX;

    /**
     * Last Y coordinate location.
     */
    private double lastY;

    /**
     * Property for if the window is detachable.
     */
    private BooleanProperty detachable;

    /**
     * Property for if the window is detached.
     */
    private BooleanProperty detached;

    /**
     * Property for the size of an arrow.
     */
    private DoubleProperty arrowSize;

    /**
     * Property for the size of an arrow indent.
     */
    private DoubleProperty arrowIndent;

    /**
     * Property for the size of the window corner radius.
     */
    private DoubleProperty cornerRadius;

    /**
     * Property for the location of an arrow.
     */
    private ObjectProperty<ArrowLocation> arrowLocation;

    /**
     * Property for the content of a window.
     */
    private ObjectProperty<Node> contentNode;

    /**
     * Listens for the hiding of the window.
     */
    private WeakInvalidationListener hideListener;

    /**
     * Listens for changes in the X position of the PopOver.
     */
    private WeakChangeListener<Number> xPositionListener;

    /**
     * Listens for changes in the Y position of the PopOver.
     */
    private WeakChangeListener<Number> yPositionListener;

    /**
     * Window that owns this PopOver control.
     */
    private Window ownerWindow;

    /**
     * Listener for the owner window closing.
     */
    private final EventHandler<WindowEvent> ownerWindowCloseListener;

    /**
     * Creates a PopOver with the provided content.
     * @param content content to show in the popover.
     */
    public PopOver(final Node content) {
        super();

        getRoot().getStylesheets().add(getClass().getResource("/sws/murcs/styles/popover.css").toExternalForm());
        getStyleClass().add("popover");

        if (content == null) {
            Label label = new Label("Please report a bug. Our clever developers clearly forgot to finish this bit.");
            label.setPadding(new Insets(0, defaultLabelPadding, 0, defaultLabelPadding));
            contentNodeProperty().set(label);
        }
        else {
            contentNodeProperty().set(content);
        }

        ownerWindowCloseListener = event -> ownerWindowClosing();
        xPositionListener = new WeakChangeListener<>(
                (value, oldX, newX) -> setX(getX() + (newX.doubleValue() - oldX.doubleValue())));
        yPositionListener = new WeakChangeListener<>(
                (value, oldY, newY) -> setY(getY() + (newY.doubleValue() - oldY.doubleValue())));

        hideListener = new WeakInvalidationListener(observable -> {
            if (!detachedProperty().get()) {
                fadeDuration = 0;
                hide();
            }
        });

        ChangeListener<Object> repositionListener = (value, oldObject, newObject) -> {
            if (isShowing() && !detachedProperty().get()) {
                show(getOwnerNode(), lastX, lastY);
                adjustWindowLocation();
            }
        };

        arrowSizeProperty().addListener(repositionListener);
        cornerRadiusProperty().addListener(repositionListener);
        arrowLocationProperty().addListener(repositionListener);
        arrowIndentProperty().addListener(repositionListener);
        detachedProperty().addListener(l -> setAutoHide(!detachableProperty().get()));

        setAnchorLocation(AnchorLocation.WINDOW_TOP_LEFT);
        setAutoHide(true);
    }

    @Override
    protected final Skin<?> createDefaultSkin() {
        return new PopOverSkin(this);
    }

    /**
     * Gets the root control of the PopOver that everything else is contained in.
     * @return the root control of the PopOver.
     */
    public final StackPane getRoot() {
        if (root == null) {
            root = new StackPane();
        }
        return root;
    }



    /**
     * Shows the PopOver control.
     * @param owner node to attach the PopOver to. If this is null it will not be attached.
     */
    public final void show(final Node owner) {
        show(owner, defaultOffset);
        ownerWindow = owner.getScene().getWindow();
        ownerWindow.setOnHiding(ownerWindowCloseListener);
    }

    /**
     * Shows the PopOver control.
     * @param owner node to attach the PopOver to. If this is null it will not be attached.
     * @param offset if this is negative it specifies the distance to the owner node.
     * When positive it is the number of pixels that the arrow will overlap with the owner.
     */
    public final void show(final Node owner, final double offset) {
        requireNonNull(owner);
        Bounds bounds = owner.localToScreen(owner.getBoundsInLocal());

        switch (arrowLocationProperty().get()) {
            case BOTTOM_CENTER:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                show(owner, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + offset);
                break;
            case LEFT_BOTTOM:
            case LEFT_CENTER:
            case LEFT_TOP:
                show(owner, bounds.getMaxX() - offset, bounds.getMinY() + bounds.getHeight() / 2);
                break;
            case RIGHT_BOTTOM:
            case RIGHT_CENTER:
            case RIGHT_TOP:
                show(owner, bounds.getMinX() + offset, bounds.getMinY() + bounds.getHeight() / 2);
                break;
            case TOP_CENTER:
            case TOP_LEFT:
            case TOP_RIGHT:
                show(owner, bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() - offset);
                break;
            default: break;
        }
    }

    @Override
    public final void show(final Window owner) {
        super.show(owner);
        ownerWindow = owner;
        ownerWindow.setOnHiding(ownerWindowCloseListener);
    }

    @Override
    public final void show(final Window theOwnerWindow, final double anchorX, final double anchorY) {
        super.show(ownerWindow, anchorX, anchorY);
        ownerWindow = theOwnerWindow;
        ownerWindow.setOnHiding(ownerWindowCloseListener);
    }

    /**
     * Show the PopOver at a specific location and associate it with an owner.
     * @param owner owner to associate with.
     * @param x x coordinate to display at.
     * @param y y coordinate to display at.
     */
    public final void show(final Node owner, final double x, final double y) {
        if (ownerWindow != null && isShowing()) {
            super.hide();
        }

        lastX = x;
        lastY = y;

        if (owner == null) {
            throw new IllegalArgumentException("owner can not be null");
        }

        if (ownerWindow != null) {
            ownerWindow.xProperty().removeListener(xPositionListener);
            ownerWindow.yProperty().removeListener(yPositionListener);
            ownerWindow.widthProperty().removeListener(hideListener);
            ownerWindow.heightProperty().removeListener(hideListener);
        }

        ownerWindow = owner.getScene().getWindow();
        ownerWindow.xProperty().addListener(xPositionListener);
        ownerWindow.yProperty().addListener(yPositionListener);
        ownerWindow.widthProperty().addListener(hideListener);
        ownerWindow.heightProperty().addListener(hideListener);

        setOnShown(evt -> {
            getScene().addEventHandler(MOUSE_CLICKED, mouseEvent -> {
                if (mouseEvent.getTarget().equals(getScene().getRoot())) {
                    if (!detachedProperty().get()) {
                        hide();
                    }
                }
            });
            adjustWindowLocation();
        });

        super.show(owner, x, y);

        Node skinNode = getSkin().getNode();
        skinNode.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(fadeDuration), skinNode);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        ownerWindow.setOnHiding(ownerWindowCloseListener);
    }

    /**
     * Ensures that the PopupControl is removed correctly when the owner window closes.
     */
    private void ownerWindowClosing() {
        fadeDuration = 0;
        hide();
    }

    @Override
    public final void hide() {
        if (isShowing()) {
            Node skinNode = getSkin().getNode();
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(fadeDuration), skinNode);
            fadeOut.setFromValue(skinNode.getOpacity());
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(evt -> {
                super.hide();
            });
            fadeOut.play();
        }
    }

    /**
     * Moves the window to the appropriate location given the previous setup.
     */
    private void adjustWindowLocation() {
        Bounds bounds = PopOver.this.getSkin().getNode().getBoundsInParent();

        switch (arrowLocationProperty().get()) {
            default:
            case TOP_CENTER:
            case TOP_LEFT:
            case TOP_RIGHT:
                setX(getX() + bounds.getMinX() - getXOffset());
                setY(getY() + bounds.getMinY() + arrowSizeProperty().get());
                break;
            case LEFT_TOP:
            case LEFT_CENTER:
            case LEFT_BOTTOM:
                setX(getX() + bounds.getMinX() + arrowSizeProperty().get());
                setY(getY() + bounds.getMinY() - getYOffset());
                break;
            case BOTTOM_CENTER:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                setX(getX() + bounds.getMinX() - getXOffset());
                setY(getY() - bounds.getMinY() - bounds.getMaxY() - 1);
                break;
            case RIGHT_TOP:
            case RIGHT_BOTTOM:
            case RIGHT_CENTER:
                setX(getX() - bounds.getMinX() - bounds.getMaxX() - 1);
                setY(getY() + bounds.getMinY() - getYOffset());
                break;
        }
    }

    /**
     * Gets the X offset value for the current window position and arrow location.
     * @return the x offset position.
     */
    private double getXOffset() {
        switch (arrowLocationProperty().get()) {
            case TOP_LEFT:
            case BOTTOM_LEFT:
                return cornerRadiusProperty().get() + arrowIndentProperty().get() + arrowSizeProperty().get();
            case TOP_CENTER:
            case BOTTOM_CENTER:
                return contentNodeProperty().get().prefWidth(-1) / 2;
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
                return contentNodeProperty().get().prefWidth(-1) - arrowIndentProperty().get()
                        - cornerRadiusProperty().get() - arrowSizeProperty().get();
            default: return 0;
        }
    }

    /**
     * Gets the Y offset value for the current window position and arrow location.
     * @return the y offset position.
     */
    private double getYOffset() {
        double prefContentHeight = contentNodeProperty().get().prefHeight(-1), a, b;

        switch (arrowLocationProperty().get()) {
            case LEFT_TOP:
            case RIGHT_TOP:
                return cornerRadiusProperty().get() + arrowIndentProperty().get() + arrowSizeProperty().get();
            case LEFT_CENTER:
            case RIGHT_CENTER:
                b = 2 * (cornerRadiusProperty().get()
                        + arrowIndentProperty().get() + arrowSizeProperty().get());
                return Math.max(prefContentHeight, b) / 2;
            case LEFT_BOTTOM:
            case RIGHT_BOTTOM:
                a = prefContentHeight - cornerRadiusProperty().get()
                        - arrowIndentProperty().get() - arrowSizeProperty().get();
                b =  arrowIndentProperty().get() + cornerRadiusProperty().get() + arrowSizeProperty().get();
                return Math.max(a, b);
            default: return 0;
        }
    }

    /**
     * Sets the duration for fade animations.
     * @param duration duration in seconds to fade for.
     */
    public final void setFadeDuration(final double duration) {
        fadeDuration = duration;
    }

    /**
     * Stores the content displayed within this PopOver window.
     * @return the content node property.
     */
    public final ObjectProperty<Node> contentNodeProperty() {
        if (contentNode == null) {
            contentNode = new SimpleObjectProperty<>(this, "contentNode");
        }
        return contentNode;
    }

    /**
     * Stores if the PopOver is detachable from its owner.
     * @return the detachable property.
     */
    public final BooleanProperty detachableProperty() {
        if (detachable == null) {
            detachable = new SimpleBooleanProperty(this, "detachable", true);
        }
        return detachable;
    }

    /**
     * Stores if the PopOver was detached from its owner. Defaults to true.
     * Note: value is meaningless if detachableProperty() is not true.
     * @return the detached property.
     */
    public final BooleanProperty detachedProperty() {
        if (detached == null) {
            detached = new SimpleBooleanProperty(this, "detached", false);
        }
        return detached;
    }

    /**
     * Controls the size of the arrow. Default to 12.
     * @return the arrow size property.
     */
    public final DoubleProperty arrowSizeProperty() {
        if (arrowSize == null) {
            arrowSize = new SimpleDoubleProperty(this, "arrowSize", defaultSize);
        }
        return arrowSize;
    }

    /**
     * Distance between the arrow and a corner of the popup. Defaults to 12.
     * @return the arrow indent property.
     */
    public final DoubleProperty arrowIndentProperty() {
        if (arrowIndent == null) {
            arrowIndent = new SimpleDoubleProperty(this, "arrowIndent", defaultSize);
        }
        return arrowIndent;
    }

    /**
     * The current corner radius. Defaults to 6.
     * @return the corner radius property.
     */
    public final DoubleProperty cornerRadiusProperty() {
        if (cornerRadius == null) {
            cornerRadius = new SimpleDoubleProperty(this, "cornerRadius", defaultSize / 2);
        }
        return cornerRadius;
    }

    /**
     * The current arrow location. Defaults to Left-Top.
     * @return the arrow location property.
     */
    public final ObjectProperty<ArrowLocation> arrowLocationProperty() {
        if (arrowLocation == null) {
            arrowLocation = new SimpleObjectProperty<>(this, "arrowLocation", ArrowLocation.LEFT_TOP);
        }
        return arrowLocation;
    }
}
