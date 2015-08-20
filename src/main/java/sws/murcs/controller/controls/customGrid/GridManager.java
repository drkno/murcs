package sws.murcs.controller.controls.customGrid;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The manager for the custom grid pane. Handles all events, like adding tiles to the grid and realigning the grid,
 * as well as setting up the main grid and handling events on the grid.
 */
public class GridManager extends Group {

    /**
     * The max number of tiles on the grid.
     */
    public static final int MAX_TILES = 2048;

    /**
     * The duration of animations on existing tiles.
     */
    private static final Duration ANIMATION_EXISTING_TILE = Duration.millis(65);

    /**
     * The duration of animations on newly added tiles.
     */
    private static final Duration ANIMATION_NEWLY_ADDED_TILE = Duration.millis(125);

    /**
     * The duration of animations for when tiles are merged.
     */
    private static final Duration ANIMATION_MERGED_TILE = Duration.millis(80);

    /**
     * Whether or not there are any tiles on animating on the grid.
     */
    private volatile boolean tilesAnimating = false;

    /**
     * The current locations on the grid in an x, y co-ordinate layout.
     */
    private final List<Location> locations = new ArrayList<>();

    /**
     * The grid's main layout of locations and tiles.
     */
    private final Map<Location, Tile> grid;

    /**
     * The tiles currently on the grid.
     */
    private final Set<Tile> mergedToBeRemoved = new HashSet<>();

    /**
     * The layout being used for the grid.
     */
    private final GridLayout gridLayout;

    /**
     * Used for managing layouts of the grid and setting up the size of the grid etc.
     */
    private final GridOperator gridOperator;

    /**
     * Creates a new grid manager.
     */
    public GridManager() {
        this(GridOperator.DEFAULT_GRID_SIZE);
    }

    /**
     * Creates a new grid manager with a grid of a specified size. The size is the number of rows and columns.
     * @param gridSize The size of the grid, cols and rows.
     */
    public GridManager(final int gridSize) {
        this.grid = new HashMap<>();

        gridOperator = new GridOperator(gridSize);
        gridLayout = new GridLayout(gridOperator);
        this.getChildren().add(gridLayout);

        gridLayout.clearGridProperty().addListener((ov, b, b1) -> {
            if (b1) {
                initialiseMainGrid();
            }
        });
        gridLayout.resetGridProperty().addListener((ov, b, b1) -> {
            if (b1) {
                setupGrid();
            }
        });

        initialiseMainGrid();
        setupGrid();
    }

    /**
     * Initializes all cells in grid map to being nothing.
     */
    private void initialiseMainGrid() {
        grid.clear();
        locations.clear();
        gridOperator.traverseGrid((x, y) -> {
            Location loc = new Location(x, y);
            locations.add(loc);
            grid.put(loc, null);
            return 0;
        });
    }

    /**
     * Sets up the grid for use.
     */
    @SuppressWarnings("CheckStyle")
    private void setupGrid() {
        Tile tile0 = Tile.newDefaultTile();
        List<Location> randomLocs = new ArrayList<>(locations);
        Collections.shuffle(randomLocs);
        Iterator<Location> locs = randomLocs.stream().limit(2).iterator();
        tile0.setLocation(locs.next());

        Tile tile1 = null;
        if (new Random().nextFloat() <= 0.8) { // gives 80% chance to add a second tile
            tile1 = Tile.newDefaultTile();
            if (tile1.getPriority() == 4 && tile0.getPriority() == 4) {
                tile1 = Tile.newTile(2);
            }
            tile1.setLocation(locs.next());
        }

        Arrays.asList(tile0, tile1).stream().filter(Objects::nonNull)
                .forEach(t -> grid.put(t.getLocation(), t));

        redrawTilesInGrid();

        gridLayout.loadGrid();
    }

    /**
     * Redraws all tiles in the grid.
     */
    private void redrawTilesInGrid() {
        grid.values().stream().filter(Objects::nonNull).forEach(gridLayout::addTile);
    }

    /**
     * Aligns all the tiles on the grid to the given alignment.
     * @param gridAlignment The alignment to change to.
     */
    private void alignTiles(final GridAlignment gridAlignment) {
        synchronized (grid) {
            if (tilesAnimating) {
                return;
            }
        }

        gridLayout.setTileProperties(0);
        mergedToBeRemoved.clear();
        ParallelTransition parallelTransition = new ParallelTransition();
        gridOperator.sortGrid(gridAlignment);
        final int tilesWereMoved = gridOperator.traverseGrid((x, y) -> {
            Location currentLoc = new Location(x, y);
            Location farthestLocation = findFarthestLocation(currentLoc, gridAlignment);
            Optional<Tile> opTile = optionalTile(currentLoc);

            AtomicInteger result = new AtomicInteger();
            Location nextLocation = farthestLocation.offset(gridAlignment);
            optionalTile(nextLocation).filter(t-> t.isMergeable(opTile) && !t.isMerged())
                    .ifPresent(t-> {
                        Tile tile = opTile.get();
                        t.merge(tile);
                        t.toFront();
                        grid.put(nextLocation, t);
                        grid.replace(currentLoc, null);

                        parallelTransition.getChildren().add(animateExistingTile(tile, t.getLocation()));
                        parallelTransition.getChildren().add(animateMergedTile(t));
                        mergedToBeRemoved.add(tile);

                        gridLayout.addToTileProperties(t.getPriority());

                        if (t.getPriority() == MAX_TILES) {
                            gridLayout.setSuccessLoadingGrid(true);
                        }
                        result.set(1);
                    });
            if (result.get() == 0 && opTile.isPresent() && !farthestLocation.equals(currentLoc)) {
                Tile tile = opTile.get();
                parallelTransition.getChildren().add(animateExistingTile(tile, farthestLocation));

                grid.put(farthestLocation, tile);
                grid.replace(currentLoc, null);

                tile.setLocation(farthestLocation);

                result.set(1);
            }

            return result.get();
        });

        gridLayout.animateSections();
        if (parallelTransition.getChildren().size() > 0) {
            parallelTransition.setOnFinished(e -> {
                gridLayout.getGridGroup().getChildren().removeAll(mergedToBeRemoved);

                grid.values().stream().filter(Objects::nonNull).forEach(Tile::clearMerge);

                Location randomAvailableLocation = findRandomAvailableLocation();
                if (randomAvailableLocation == null && mergeMovementsAvailable() == 0) {
                    gridLayout.setGridClose(true);
                } else if (randomAvailableLocation != null && tilesWereMoved > 0) {
                    synchronized (grid) {
                        tilesAnimating = false;
                    }
                    addAndAnimateTile(randomAvailableLocation);
                }
            });

            synchronized (grid) {
                tilesAnimating = true;
            }

            parallelTransition.play();
        }
    }

    /**
     * Allows using tiles from the map at some location, whether they
     * are null or not.
     * @param loc location of the tile
     * @return an optoinal containing null or a valid tile
     */
    private Optional<Tile> optionalTile(final Location loc) {
        return Optional.ofNullable(grid.get(loc));
    }

    /**
     * Searches for the farthest empty location where the current tile could go.
     * @param location of the tile
     * @param gridAlignment of movement
     * @return a location
     */
    @SuppressWarnings("CheckStyle")
    private Location findFarthestLocation(Location location, final GridAlignment gridAlignment) {
        Location farthest;

        do {
            farthest = location;
            location = farthest.offset(gridAlignment);
        } while (gridOperator.isValidLocation(location) && !optionalTile(location).isPresent());

        return farthest;
    }

    /**
     * Finds the number of pairs of tiles that can be merged.
     * This method is called only when the grid is full of tiles,
     * which makes the use of Optional unnecessary, but it could be used when the
     * grid is not full to find the number of pairs of mergeable tiles.
     * @return the number of pairs of tiles that can be merged
     */
    private int mergeMovementsAvailable() {
        final AtomicInteger pairsOfMergeableTiles = new AtomicInteger();

        Stream.of(GridAlignment.UP, GridAlignment.LEFT).parallel().forEach(direction ->
                gridOperator.traverseGrid((x, y) -> {
                Location currentLoc = new Location(x, y);
                optionalTile(currentLoc).ifPresent(t-> {
                    if (t.isMergeable(optionalTile(currentLoc.offset(direction)))) {
                        pairsOfMergeableTiles.incrementAndGet();
                    }
                });
                return 0;
            })
        );
        return pairsOfMergeableTiles.get();
    }

    /**
     * Finds a random location available on the grid or returns null if none exist.
     * @return a random location or <code>null</code> if there are no more
     * locations available
     */
    private Location findRandomAvailableLocation() {
        List<Location> availableLocations = locations.stream().filter(l -> grid.get(l) == null)
                .collect(Collectors.toList());

        if (availableLocations.isEmpty()) {
            return null;
        }

        Collections.shuffle(availableLocations);
        return availableLocations.get(new Random().nextInt(availableLocations.size()));
    }

    /**
     * Adds a tile to a location with a proper animation.
     * @param location The location to add the tile at.
     */
    private void addAndAnimateTile(final Location location) {
        Tile tile = gridLayout.addDefaultTile(location);
        grid.put(tile.getLocation(), tile);

        animateNewlyAddedTile(tile).play();
    }

    /**
     * Animation that creates a fade in effect when a tile is added to the grid
     * by increasing the tile scale from 0 to 100%.
     * @param tile to be animated
     * @return a scale transition
     */
    private ScaleTransition animateNewlyAddedTile(final Tile tile) {
        final ScaleTransition scaleTransition = new ScaleTransition(ANIMATION_NEWLY_ADDED_TILE, tile);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.setInterpolator(Interpolator.EASE_OUT);
        scaleTransition.setOnFinished(e -> {
            // after last movement on full grid, check if there are movements available
            if (this.grid.values().parallelStream().noneMatch(Objects::isNull) && mergeMovementsAvailable() == 0) {
                gridLayout.setGridClose(true);
            }
        });
        return scaleTransition;
    }

    /**
     * Animation that moves the tile from its previous location to a new location.
     * @param tile to be animated
     * @param newLocation new location of the tile
     * @return a timeline
     */
    private Timeline animateExistingTile(final Tile tile, final Location newLocation) {
        Timeline timeline = new Timeline();
        KeyValue kvX = new KeyValue(tile.layoutXProperty(),
                newLocation.getLayoutX(GridLayout.CELL_SIZE) - (tile.getMinHeight() / 2), Interpolator.EASE_OUT);
        KeyValue kvY = new KeyValue(tile.layoutYProperty(),
                newLocation.getLayoutY(GridLayout.CELL_SIZE) - (tile.getMinHeight() / 2), Interpolator.EASE_OUT);

        KeyFrame kfX = new KeyFrame(ANIMATION_EXISTING_TILE, kvX);
        KeyFrame kfY = new KeyFrame(ANIMATION_EXISTING_TILE, kvY);

        timeline.getKeyFrames().add(kfX);
        timeline.getKeyFrames().add(kfY);

        return timeline;
    }

    /**
     * Animation that creates a pop effect when two tiles merge
     * by increasing the tile scale to 120% at the middle, and then going back to 100%.
     * @param tile to be animated
     * @return a sequential transition.
     */
    @SuppressWarnings("CheckStyle")
    private SequentialTransition animateMergedTile(final Tile tile) {
        final ScaleTransition scale0 = new ScaleTransition(ANIMATION_MERGED_TILE, tile);
        scale0.setToX(1.2);
        scale0.setToY(1.2);
        scale0.setInterpolator(Interpolator.EASE_IN);

        final ScaleTransition scale1 = new ScaleTransition(ANIMATION_MERGED_TILE, tile);
        scale1.setToX(1.0);
        scale1.setToY(1.0);
        scale1.setInterpolator(Interpolator.EASE_OUT);

        return new SequentialTransition(scale0, scale1);
    }

    /**
     * Move the tiles according to a new alignment if there isn't an overlay showing.
     * @param gridAlignment The alignment to change to.
     */
    public final void move(final GridAlignment gridAlignment) {
        if (!gridLayout.isLayerOn().get()) {
            alignTiles(gridAlignment);
        }
    }

    /**
     * Sets the scale for the grid.
     * @param scale The scale of the grid
     */
    public final void setScale(final double scale) {
        this.setScaleX(scale);
        this.setScaleY(scale);
    }

    /**
     * Tries to change the grid layout again.
     */
    public final void tryAgain() {
        gridLayout.tryAgain();
    }

    /**
     * Sets the toolbar for the grid.
     * @param toolbar The toolbar.
     */
    public final void setToolBar(final HBox toolbar) {
        gridLayout.setToolBar(toolbar);
    }
}
