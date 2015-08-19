package sws.murcs.controller.controls.customGrid;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * The custom grid pane that contains the custom elements.
 */
public class CustomGridPane extends StackPane {

    /**
     * The grid manager that manges the layout of the grid.
     */
    private GridManager gridManager;

    /**
     * The margin on the grid.
     */
    private static final int MARGIN = 36;

    /**
     * The general font size of text in the grid.
     */
    private static final int FONT_SIZE = 10;

    static {
        Font.loadFont(GridController.class.getResource("/sws/murcs/controller/controls/customGrid/ClearSans-Bold.ttf")
                .toExternalForm(), FONT_SIZE);
    }

    /**
     * Constructs the custom grid pane and sets up all the event handlers.
     */
    public CustomGridPane() {
        gridManager = new GridManager();
        gridManager.setToolBar(createToolBar());
        Bounds bounds = gridManager.getLayoutBounds();

        getChildren().add(gridManager);

        getStyleClass().addAll("grid-root");
        ChangeListener<Number> resize = (ov, v, v1) -> {
            double scale = Math.min((getWidth() - MARGIN) / bounds.getWidth(),
                    (getHeight() - MARGIN) / bounds.getHeight());
            gridManager.setScale(scale);
            gridManager.setLayoutX((getWidth() - bounds.getWidth()) / 2d);
            gridManager.setLayoutY((getHeight() - bounds.getHeight()) / 2d);
        };
        widthProperty().addListener(resize);
        heightProperty().addListener(resize);

        addKeyHandler(this);
        addSwipeHandlers(this);
        setFocusTraversable(true);
        this.setOnMouseClicked(e -> requestFocus());
    }

    /**
     * Adds an event handler for different keys being pressed.
     * @param node The node the handler is being added to.
     */
    private void addKeyHandler(final Node node) {
        node.setOnKeyPressed(ke -> {
            KeyCode keyCode = ke.getCode();
            if (keyCode.isArrowKey()) {
                GridAlignment gridAlignment = GridAlignment.valueFor(keyCode);
                shiftAlignment(gridAlignment);
            }
        });
    }

    /**
     * Adds swipe handlers to a given node.
     * @param node The node that the swipe handlers are being added to.
     */
    private void addSwipeHandlers(final Node node) {
        node.setOnSwipeUp(e -> shiftAlignment(GridAlignment.UP));
        node.setOnSwipeRight(e -> shiftAlignment(GridAlignment.RIGHT));
        node.setOnSwipeLeft(e -> shiftAlignment(GridAlignment.LEFT));
        node.setOnSwipeDown(e -> shiftAlignment(GridAlignment.DOWN));
    }

    /**
     * Shifts the alignment of the grid pane.
     * @param gridAlignment The alignment being shifted to.
     */
    private void shiftAlignment(final GridAlignment gridAlignment) {
        gridManager.move(gridAlignment);
    }

    /**
     * Creates a toolbar for the custom grid pane.
     * @return The HBox that contains the toolbar.
     */
    private HBox createToolBar() {
        HBox toolbar = new HBox();
        toolbar.setAlignment(Pos.CENTER);
        //noinspection CheckStyle
        toolbar.setPadding(new Insets(10.0));
        Button btItem = createButtonItem("mStart", "Try Again", t-> gridManager.tryAgain());
        toolbar.getChildren().add(btItem);
        return toolbar;
    }

    /**
     * Create a button from the given string symbol, text and event handler.
     * @param symbol The symbol on the button.
     * @param text The text on the button.
     * @param t The event handler for when the button is clicked.
     * @return The new button item.
     */
    private Button createButtonItem(final String symbol, final String text, final EventHandler<ActionEvent> t) {
        Button g = new Button();
        //noinspection CheckStyle
        g.setPrefSize(40, 40);
        g.setId(symbol);
        g.setOnAction(t);
        g.setTooltip(new Tooltip(text));
        return g;
    }

    /**
     * Gets the margin of the custom grid pane.
     * @return The grid pane's margin.
     */
    public static int getMargin() {
        return MARGIN;
    }
}
