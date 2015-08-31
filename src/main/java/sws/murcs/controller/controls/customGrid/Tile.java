package sws.murcs.controller.controls.customGrid;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

import java.util.Optional;
import java.util.Random;

/**
 * Is used for tiles on the custom grid.
 */
public final class Tile extends Label {

    /**
     * The priority of the tile, corresponding to how important it is to display.
     */
    private Integer priority;

    /**
     * The location on the grid.
     */
    private Location location;

    /**
     * Whether or not the tile has been merged with another tile.
     */
    private Boolean merged;

    /**
     * Creates a new default tile with a priority of either 2 or 4.
     * @return The new tile.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public static Tile newDefaultTile() {
        int priority = new Random().nextDouble() < 0.9 ? 2 : 4;
        return new Tile(priority);
    }

    /**
     * Creates a new tile with the given priority.
     * @param priority The priority of the tile
     * @return the new tile.
     */
    public static Tile newTile(final int priority) {
        return new Tile(priority);
    }

    /**
     * Constructor for the tile.
     * @param pPriority The priority of how important the tile is to show.
     */
    private Tile(final Integer pPriority) {
        final int squareSize = GridLayout.CELL_SIZE - 13;
        setMinSize(squareSize, squareSize);
        setMaxSize(squareSize, squareSize);
        setPrefSize(squareSize, squareSize);
        setAlignment(Pos.CENTER);

        this.priority = pPriority;
        this.merged = false;
        setText(pPriority.toString());
        getStyleClass().addAll("grid-label", "grid-tile-" + pPriority);
    }

    /**
     * Merges a given tile with the tile the method is called on.
     * Basically merges the priority of the tiles so that they are both displayed.
     * @param another the tile being merged into this tile.
     */
    public void merge(final Tile another) {
        getStyleClass().remove("grid-tile-" + priority);
        this.priority += another.getPriority();
        setText(priority.toString());
        merged = true;
        getStyleClass().add("grid-tile-" + priority);
    }

    /**
     * Gets the priority of the tile.
     * @return the tile's priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Gets the location of the tile on the grid.
     * @return the location of the tile
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the location of the tile on the grid.
     * @param pLocation the new location of the tile.
     */
    public void setLocation(final Location pLocation) {
        this.location = pLocation;
    }

    @Override
    public String toString() {
        return "Tile{" + "value=" + priority + ", location=" + location + '}';
    }

    /**
     * Whether or not the tile has been merged.
     * @return whether or not the tile has been merged.
     */
    public boolean isMerged() {
        return merged;
    }

    /**
     * Clears the tile having been merged.
     */
    public void clearMerge() {
        merged = false;
    }

    /**
     * Gets whether or not the tile is able to be merged with another tile.
     * Merging can only happen for tiles that have the same priority.
     * @param anotherTile the tile to merge.
     * @return whether or not the tile can be merged.
     */
    public boolean isMergeable(final Optional<Tile> anotherTile) {
        return anotherTile.filter(t->t.getPriority().equals(getPriority())).isPresent();
    }
}
