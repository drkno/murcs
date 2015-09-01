package sws.murcs.controller.controls.customGrid;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * A custom grid layout for handling grids better than the built in GridPane.
 */
public class GridLayout extends Group {

    /**
     * The size of cells within the grid.
     */
    public static final int CELL_SIZE = 128;

    /**
     * The width of the borders.
     */
    private static final int BORDER_WIDTH = 8;

    /**
     * The height of the top of the grid.
     */
    private static final int TOP_HEIGHT = 92;

    /**
     * The height of the gap between the top of the grid and the rest of the grid.
     */
    private static final int GAP_HEIGHT = 50;

    /**
     * The height of the toolbar on the grid.
     */
    private static final int TOOLBAR_HEIGHT = 80;

    /**
     * The current number of tiles within the grid.
     */
    private final IntegerProperty gridCurrentTilesProperty = new SimpleIntegerProperty(0);

    /**
     * The max number of tiles within the grid.
     */
    private final IntegerProperty gridMaxTilesProperty = new SimpleIntegerProperty(0);

    /**
     * The min number of tiles in the grid.
     */
    private final IntegerProperty gridMinTilesProperty = new SimpleIntegerProperty(0);

    /**
     * Whether or not the grid successfully loads.
     */
    private final BooleanProperty gridSuccessLoadingProperty = new SimpleBooleanProperty(false);

    /**
     * Whether or not the grid is closed, or collapsed.
     */
    private final BooleanProperty closeGridProperty = new SimpleBooleanProperty(false);

    /**
     * Whether or not to try reloading the content in the grid.
     */
    private final BooleanProperty retryGridLoadProperty = new SimpleBooleanProperty(false);

    /**
     * Whether or not the popup layer is currently showing.
     */
    private final BooleanProperty layerOnProperty = new SimpleBooleanProperty(false);

    /**
     * Whether or not to reset the grids layout.
     */
    private final BooleanProperty resetGrid = new SimpleBooleanProperty(false);

    /**
     * Whether or not the grid has been cleared.
     */
    private final BooleanProperty clearGrid = new SimpleBooleanProperty(false);

    /**
     * Whether or not the grid is currently open.
     */
    private final BooleanProperty openGrid = new SimpleBooleanProperty(false);

    /**
     * Current time used for some testing of performance within the grid.
     */
    private LocalTime time;

    /**
     * The timer that is used for some of the animations.
     */
    private Timeline timer;

    /**
     * String for the time when it is used in the grid, mainly for performance testing.
     */
    private final StringProperty clock = new SimpleStringProperty("00:00:00");

    /**
     * Formatter for the time when it is used in the grid.
     */
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

    //Grid's main elements
    /**
     * The main VBox used within the grid.
     */
    private final VBox mainVBox = new VBox(0);

    /**
     * The group used to collect all the tiles together.
     */
    private final Group gridGroup = new Group();

    /**
     * The HBox at the top used for headings or tiles etc.
     */
    private final HBox hTop = new HBox(0);

    /**
     * The VBox used for current selected tiles within the grid.
     */
    private final VBox selectionBox = new VBox(-5);

    /**
     * The label for the selections.
     */
    private final Label selectionLabel = new Label("0");

    /**
     * Secondary label for sub headings in the selections.
     */
    private final Label secondSelectionLabel = new Label("0");

    /**
     * The currently selected label.
     */
    private final Label currentSelectionLabel = new Label();

    /**
     * The HBox used for the overlay in the grid.
     */
    private final HBox overlay = new HBox();

    /**
     * The VBox used for text in the overlay.
     */
    private final VBox txtOverlay = new VBox(10);

    /**
     * The main text for the overlay.
     */
    private final Label lOvrText = new Label();

    /**
     * The subtext for the overlay.
     */
    private final Label lOvrSubText = new Label();

    /**
     * The HBox for the buttons in the overlay.
     */
    private final HBox buttonsOverlay = new HBox();

    /**
     * Button used to try and reload the content of the grid.
     */
    private final Button bTry = new Button("Try again");

    /**
     * Continue using the grid when an overlay has come over the top.
     */
    private final Button bContinue = new Button("Keep going");

    /**
     * Continue using the grid when an overlay has come of the top and the wording is
     * phrased in such a way that no is required.
     */
    private final Button bContinueNo = new Button("No, keep going");

    /**
     * Ok button for the overlay.
     */
    private final Button bOk = new Button("Ok");

    /**
     * The toolbar for the grid.
     */
    private final HBox hToolbar = new HBox();

    /**
     * Label for the current time when it is used within the grid.
     */
    private final Label lblTime = new Label();

    /**
     * Whether or not the time line for animations should be stopped.
     */
    private Timeline timerPause;

    /**
     * The width of the grid.
     */
    private final int gridWidth;

    /**
     * The grid operator for the grid.
     */
    private final GridOperator gridOperator;

    /**
     * Creates a new grid layout based on the given grid operator.
     * @param grid the grid operator to use when setting up the grid.
     */
    public GridLayout(final GridOperator grid) {
        this.gridOperator  =  grid;
        gridWidth = CELL_SIZE * grid.getGridSize() + BORDER_WIDTH * 2;

        createLabels();
        createGrid();

        initGridProperties();
    }

    /**
     * Create labels on the grid.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void createLabels() {
        Label lblTitle = new Label(new String(Base64.getDecoder().decode(GridController.TITLE_HASHCODE)));
        lblTitle.getStyleClass().addAll("grid-label", "grid-title-2");
        HBox hFill = new HBox();
        HBox.setHgrow(hFill, Priority.ALWAYS);
        hFill.setAlignment(Pos.CENTER);

        VBox titleSection = new VBox();
        HBox innerTitleSection = new HBox(5);

        selectionBox.setAlignment(Pos.CENTER);
        selectionBox.getStyleClass().add("grid-vbox");
        Label lblTit = new Label(new String(Base64.getDecoder().decode("U0NPUkU=")));
        lblTit.getStyleClass().addAll("grid-label", "grid-title");
        selectionLabel.getStyleClass().addAll("grid-label", "grid-number");
        selectionLabel.textProperty().bind(gridCurrentTilesProperty.asString());
        selectionBox.getChildren().addAll(lblTit, selectionLabel);

        VBox vRecord = new VBox(-5);
        vRecord.setAlignment(Pos.CENTER);
        vRecord.getStyleClass().add("grid-vbox");
        Label lblTitBest = new Label("BEST");
        lblTitBest.getStyleClass().addAll("grid-label", "grid-title");
        secondSelectionLabel.getStyleClass().addAll("grid-label", "grid-number");
        secondSelectionLabel.textProperty().bind(gridMaxTilesProperty.asString());
        vRecord.getChildren().addAll(lblTitBest, secondSelectionLabel);
        innerTitleSection.getChildren().addAll(selectionBox, vRecord);
        VBox vFill = new VBox();
        VBox.setVgrow(vFill, Priority.ALWAYS);
        titleSection.getChildren().addAll(innerTitleSection, vFill);

        hTop.getChildren().addAll(lblTitle, hFill, titleSection);
        hTop.setMinSize(gridWidth, TOP_HEIGHT);
        hTop.setPrefSize(gridWidth, TOP_HEIGHT);
        hTop.setMaxSize(gridWidth, TOP_HEIGHT);

        mainVBox.getChildren().add(hTop);

        HBox hTime = new HBox();
        hTime.setMinSize(gridWidth, GAP_HEIGHT);
        hTime.setAlignment(Pos.BOTTOM_RIGHT);
        lblTime.getStyleClass().addAll("grid-label", "grid-time");
        lblTime.textProperty().bind(clock);
        timer = new Timeline(new KeyFrame(Duration.ZERO, e-> {
            clock.set(LocalTime.now().minusNanos(time.toNanoOfDay()).format(fmt));
        }), new KeyFrame(Duration.seconds(1)));
        timer.setCycleCount(Animation.INDEFINITE);
        hTime.getChildren().add(lblTime);

        mainVBox.getChildren().add(hTime);
        getChildren().add(mainVBox);

        currentSelectionLabel.getStyleClass().addAll("grid-label", "grid-number-2");
        currentSelectionLabel.setAlignment(Pos.CENTER);
        //noinspection CheckStyle
        currentSelectionLabel.setMinWidth(100);
        getChildren().add(currentSelectionLabel);
    }

    /**
     * Creates a new cell in the grid.
     * @param x x position in the grid.
     * @param y y position in the grid.
     * @return the new cell
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private Rectangle createCell(final int x, final int y) {
        final double arcSize = CELL_SIZE / 6d;
        Rectangle cell = new Rectangle(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        // provide default style in case css are not loaded
        cell.setFill(Color.WHITE);
        cell.setStroke(Color.GREY);
        cell.setArcHeight(arcSize);
        cell.setArcWidth(arcSize);
        cell.getStyleClass().add("gridception-cell");
        return cell;
    }

    /**
     * Creates the grid controls.
     */
    private void createGrid() {

        gridOperator.traverseGrid((i, j)-> {
            gridGroup.getChildren().add(createCell(i, j));
            return 0;
        });

        gridGroup.getStyleClass().add("gridception");
        gridGroup.setManaged(false);
        gridGroup.setLayoutX(BORDER_WIDTH);
        gridGroup.setLayoutY(BORDER_WIDTH);

        HBox hBottom = new HBox();
        hBottom.getStyleClass().add("grid-backGrid");
        hBottom.setMinSize(gridWidth, gridWidth);
        hBottom.setPrefSize(gridWidth, gridWidth);
        hBottom.setMaxSize(gridWidth, gridWidth);

        // Clip hBottom to keep the dropshadow effects within the hBottom
        Rectangle rect = new Rectangle(gridWidth, gridWidth);
        hBottom.setClip(rect);
        hBottom.getChildren().add(gridGroup);

        mainVBox.getChildren().add(hBottom);

        // toolbar

        HBox hPadding =  new HBox();
        hPadding.setMinSize(gridWidth, TOOLBAR_HEIGHT);
        hPadding.setPrefSize(gridWidth, TOOLBAR_HEIGHT);
        hPadding.setMaxSize(gridWidth, TOOLBAR_HEIGHT);

        hToolbar.setAlignment(Pos.CENTER);
        hToolbar.getStyleClass().add("grid-backGrid");
        hToolbar.setMinSize(gridWidth, TOOLBAR_HEIGHT);
        hToolbar.setPrefSize(gridWidth, TOOLBAR_HEIGHT);
        hToolbar.setMaxSize(gridWidth, TOOLBAR_HEIGHT);

        mainVBox.getChildren().add(hPadding);
        mainVBox.getChildren().add(hToolbar);
    }

    /**
     * Sets the toolbar on the grid layout.
     * @param toolbar the toolbar to be set.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public final void setToolBar(final HBox toolbar) {
        toolbar.disableProperty().bind(layerOnProperty);
        toolbar.spacingProperty().bind(Bindings.divide(mainVBox.widthProperty(), 10));
        hToolbar.getChildren().add(toolbar);
    }

    /**
     * Tries to reload the grid and all the elements in it.
     */
    public final void tryAgain() {
        if (!retryGridLoadProperty.get()) {
            retryGridLoadProperty.set(true);
        }
    }

    /**
     * The function called when the reload grid button is selected.
     */
    private void btnTryAgain() {
        timerPause.stop();
        layerOnProperty.set(false);
        reloadGrid();
    }

    /**
     * The function called when you want to resume use of the grid and dismiss the overlay.
     */
    private void resume() {
        timerPause.stop();
        layerOnProperty.set(false);
        retryGridLoadProperty.set(false);
        timer.play();
    }

    /**
     * An overlay to be displayed when closing the grid.
     */
    private final Overlay closedListener = new Overlay(new String(Base64.getDecoder().decode("WW91IHdpbiE=")),
            "", bContinue, bTry, "grid-overlay1", "grid-lbl1", true);

    /**
     * Class used to creat the overlay on the grid.
     */
    private class Overlay implements ChangeListener<Boolean> {

        /**
         * The buttons on the overlay.
         */
        private final Button btn1, btn2;

        /**
         * The message and warning on the overlay.
         */
        private final String message, warning;

        /**
         * The styles to be applied to the overlay.
         */
        private final String style1, style2;

        /**
         * Whether or not it should pause items on the grid.
         */
        private final boolean pause;

        /**
         * Creates a new overlay for the grid.
         * @param pMessage the message to be show.
         * @param pWarning any warnings to be show.
         * @param leftButton the button on the left
         * @param rightButton the button on the right
         * @param firstStyle the first style to be applied to the overlay
         * @param secondStyle the second style to be applied to the overlay
         * @param pPause whether or no it should pause items on the grid.
         */
        public Overlay(final String pMessage, final String pWarning, final Button leftButton, final Button rightButton,
                       final String firstStyle, final String secondStyle, final boolean pPause) {
            this.message = pMessage;
            this.warning = pWarning;
            this.btn1 = leftButton;
            this.btn2 = rightButton;
            this.style1 = firstStyle;
            this.style2 = secondStyle;
            this.pause = pPause;
        }

        @Override
        @SuppressWarnings("checkstyle:magicnumber")
        public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue,
                            final Boolean newValue) {
            if (newValue) {
                timer.stop();
                if (pause) {
                    timerPause.play();
                }
                overlay.getStyleClass().setAll("grid-overlay", style1);
                lOvrText.setText(message);
                lOvrText.getStyleClass().setAll("grid-label", style2);
                lOvrSubText.setText(warning);
                lOvrSubText.getStyleClass().setAll("grid-label", "grid-lblWarning");
                lOvrSubText.setWrapText(true);
                lOvrSubText.setMaxWidth(450);
                lOvrSubText.setTextAlignment(TextAlignment.CENTER);
                txtOverlay.getChildren().setAll(lOvrText, lOvrSubText);
                buttonsOverlay.getChildren().setAll(btn1);
                if (btn2 != null) {
                    buttonsOverlay.getChildren().add(btn2);
                }
                if (!layerOnProperty.get()) {
                    GridLayout.this.getChildren().addAll(overlay, buttonsOverlay);
                    layerOnProperty.set(true);
                }
            }
        }
    }

    /**
     * Initialises the basic buttons and other properties on the grid, such as listeners.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void initGridProperties() {

        overlay.setMinSize(gridWidth, gridWidth);
        overlay.setAlignment(Pos.CENTER);
        overlay.setTranslateY(TOP_HEIGHT + GAP_HEIGHT);

        overlay.getChildren().setAll(txtOverlay);
        txtOverlay.setAlignment(Pos.CENTER);

        buttonsOverlay.setAlignment(Pos.CENTER);
        buttonsOverlay.setTranslateY(TOP_HEIGHT + GAP_HEIGHT + gridWidth / 2);
        buttonsOverlay.setMinSize(gridWidth, gridWidth / 2);
        buttonsOverlay.setSpacing(10);

        bTry.getStyleClass().add("grid-button");
        bTry.setOnTouchPressed(e -> btnTryAgain());
        bTry.setOnAction(e -> btnTryAgain());
        bTry.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.SPACE)) {
                btnTryAgain();
            }
        });

        bContinue.getStyleClass().add("grid-button");
        bContinue.setOnTouchPressed(e -> resume());
        bContinue.setOnMouseClicked(e -> resume());
        bContinue.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.SPACE)) {
                resume();
            }
        });

        bContinueNo.getStyleClass().add("grid-button");
        bContinueNo.setOnTouchPressed(e -> resume());
        bContinueNo.setOnMouseClicked(e -> resume());
        bContinueNo.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.SPACE)) {
                resume();
            }
        });

        bOk.getStyleClass().add("grid-button");
        bOk.setOnTouchPressed(e -> okPress());
        bOk.setOnMouseClicked(e -> okPress());
        bOk.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.SPACE)) {
               okPress();
            }
        });

        timerPause = new Timeline(new KeyFrame(Duration.seconds(1),
                e-> { time = time.plusNanos(1_000_000_000); }));
        timerPause.setCycleCount(Animation.INDEFINITE);

        gridSuccessLoadingProperty.addListener(closedListener);
        //These strings are this long because they're a hash because of a serialisation problem with Java.
        closeGridProperty.addListener(new Overlay(new String(Base64.getDecoder().decode("R2FtZSBvdmVyIQ==")),
                "", bTry, null, "grid-overlay-over", "grid-lblOver", false));
        retryGridLoadProperty.addListener(new Overlay("Try Again?",
                new String(Base64.getDecoder().decode("Q3VycmVudCBnYW1lIHdpbGwgYmUgcmVzZXQ=")), bTry, bContinueNo,
                "grid-overlay-over", "grid-lblOver", true));
        openGrid.addListener(new Overlay("Welcome",
                new String(Base64.getDecoder().decode("SSBzZWUgeW91J3ZlIGZvdW5kIG91ciBzZWNyZXQgZ2FtZSwgaGF2ZSBmdW4uIFRo"
                        + "ZSBtYWpvcml0eSBvZiB0aGUgc291cmNlIGNvZGUgY29tZXMgZnJvbSBodHRwczovL2dpdGh1Yi5jb20vYnJ1bm9ib3Jn"
                        + "ZXMvZngyMDQ4")), bOk, null, "grid-overlay-over", "grid-lbl1Over", true));

        gridCurrentTilesProperty.addListener((ov, i, i1) -> {
            if (i1.intValue() > gridMaxTilesProperty.get()) {
                gridMaxTilesProperty.set(i1.intValue());
            }
        });

        layerOnProperty.addListener((ov, b, b1)-> {
            if (!b1) {
                getChildren().removeAll(overlay, buttonsOverlay);
                // Keep the focus on the grid when the layer is removed:
                getParent().requestFocus();
            } else {
                // Set focus on the first button
                buttonsOverlay.getChildren().get(0).requestFocus();
            }
        });

    }

    /**
     * Function called when ok button is clicked.
     */
    private void okPress() {
        layerOnProperty.setValue(false);
        timerPause.stop();
        timer.play();
    }

    /**
     * Function called to clear the grid.
     */
    private void clearGrid() {
        gridGroup.getChildren().removeIf(c->c instanceof Tile);
        getChildren().removeAll(overlay, buttonsOverlay);

        clearGrid.set(false);
        resetGrid.set(false);
        layerOnProperty.set(false);
        gridCurrentTilesProperty.set(0);
        gridSuccessLoadingProperty.set(false);
        closeGridProperty.set(false);
        retryGridLoadProperty.set(false);

        clearGrid.set(true);
    }

    /**
     * Function called to reload the content of the grid.
     */
    private void reloadGrid() {
        clearGrid();
        resetGrid.set(true);
    }

    /**
     * Animate the sections of the grid.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public final void animateSections() {
        if (gridMinTilesProperty.get() == 0) {
            return;
        }

        final Timeline timeline = new Timeline();
        currentSelectionLabel.setText("+" + gridMinTilesProperty.getValue().toString());
        currentSelectionLabel.setOpacity(1);
        double posX = selectionBox.localToScene(selectionBox.getWidth() / 2d, 0).getX();
        currentSelectionLabel.setTranslateX(0);
        currentSelectionLabel.setTranslateX(currentSelectionLabel.sceneToLocal(posX, 0).getX()
                - currentSelectionLabel.getWidth() / 2d);
        currentSelectionLabel.setLayoutY(20);
        final KeyValue kvO = new KeyValue(currentSelectionLabel.opacityProperty(), 0);
        final KeyValue kvY = new KeyValue(currentSelectionLabel.layoutYProperty(), 100);

        Duration animationDuration = Duration.millis(600);
        final KeyFrame kfO = new KeyFrame(animationDuration, kvO);
        final KeyFrame kfY = new KeyFrame(animationDuration, kvY);

        timeline.getKeyFrames().add(kfO);
        timeline.getKeyFrames().add(kfY);

        timeline.play();
    }

    /**
     * Add a title to the grid pane.
     * @param tile the title to add to the grid.
     */
    public final void addTile(final Tile tile) {
        double layoutX = tile.getLocation().getLayoutX(CELL_SIZE) - (tile.getMinWidth() / 2);
        double layoutY = tile.getLocation().getLayoutY(CELL_SIZE) - (tile.getMinHeight() / 2);

        tile.setLayoutX(layoutX);
        tile.setLayoutY(layoutY);
        gridGroup.getChildren().add(tile);
    }

    /**
     * Add a default tile at the given location.
     * @param location the location of the tile.
     * @return the new tile.
     */
    public final Tile addDefaultTile(final Location location) {
        Tile tile = Tile.newDefaultTile();
        tile.setLocation(location);

        double layoutX = tile.getLocation().getLayoutX(CELL_SIZE) - (tile.getMinWidth() / 2);
        double layoutY = tile.getLocation().getLayoutY(CELL_SIZE) - (tile.getMinHeight() / 2);

        tile.setLayoutX(layoutX);
        tile.setLayoutY(layoutY);
        tile.setScaleX(0);
        tile.setScaleY(0);

        gridGroup.getChildren().add(tile);

        return tile;
    }

    /**
     * Get the group that contains all the elements in the grid.
     * @return the group with the elements in.
     */
    public final Group getGridGroup() {
        return gridGroup;
    }

    /**
     * Loads the grid and it's content.
     */
    public final void loadGrid() {
        time = LocalTime.now();
        timer.playFromStart();
        openGrid.set(true);
    }

    /**
     * Sets the min tile properties on the grid.
     * @param tiles min tiles.
     */
    public final void setTileProperties(final int tiles) {
        gridMinTilesProperty.set(tiles);
    }

    /**
     * Adds to the tile properties, regarding min tiles and current tiles.
     * @param tiles tiles to add.
     */
    public final void addToTileProperties(final int tiles) {
        gridMinTilesProperty.set(gridMinTilesProperty.get() + tiles);
        gridCurrentTilesProperty.set(gridCurrentTilesProperty.get() + tiles);
    }

    /**
     * Sets whether or not the grid is closing.
     * @param gridClose whether the grid is closing or not.
     */
    public final void setGridClose(final boolean gridClose) {
        closeGridProperty.set(gridClose);
    }

    /**
     * Sets success for loading the grid.
     * @param success whether or not the grid loaded successfully.
     */
    public final void setSuccessLoadingGrid(final boolean success) {
        if (!gridSuccessLoadingProperty.get()) {
            gridSuccessLoadingProperty.set(success);
        }
    }

    /**
     * Whether the overlay is showing.
     * @return whether the overlay is showing
     */
    public final BooleanProperty isLayerOn() {
        return layerOnProperty;
    }

    /**
     * Whether the grid has been reset.
     * @return whether the grid has been reset.
     */
    public final BooleanProperty resetGridProperty() {
        return resetGrid;
    }

    /**
     * Whether the grid has been cleared.
     * @return whether the grid has been cleared.
     */
    public final BooleanProperty clearGridProperty() {
        return clearGrid;
    }
}
