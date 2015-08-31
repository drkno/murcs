package sws.murcs.controller.controls.customGrid;

/**
 * Specifies a specific location based on x and y coordinates within the custom grid control.
 */
public class Location {

    /**
     * The x coordinate.
     */
    private final int x;

    /**
     * The y coordinate.
     */
    private final int y;

    /**
     * Creates a new location at a given x and y co-ordinate.
     * @param pX the pX coordinate
     * @param pY the pY coordinate
     */
    public Location(final int pX, final int pY) {
        this.x = pX;
        this.y = pY;
    }

    /**
     * Offsets the current location using a grid alignment as a guide.
     * @param gridAlignment The grid alignment to use for offsetting.
     * @return The new location.
     */
    public final Location offset(final GridAlignment gridAlignment) {
        return new Location(x + gridAlignment.getX(), y + gridAlignment.getY());
    }

    /**
     * Gets the x coordinate.
     * @return the x coordinate.
     */
    public final int getX() {
        return x;
    }

    /**
     * Gets the y coordinate.
     * @return the y coordinate.
     */
    public final int getY() {
        return y;
    }

    @Override
    public final String toString() {
        return "Location{" + "x=" + x + ", y=" + y + '}';
    }

    @SuppressWarnings("checkstyle:magicnumber")
    @Override
    public final int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        return hash;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Gets the layout of y based on the cell size of cells in the grid.
     * @param cellSize the cell size in the grid.
     * @return the layout position of y.
     */
    public final double getLayoutY(final int cellSize) {
        if (y == 0) {
            return cellSize / 2;
        }
        return (y * cellSize) + cellSize / 2;
    }

    /**
     * Gets the layout of x based on the cell size of cells in the grid.
     * @param cellSize the cell size in the grid.
     * @return the layout position of x.
     */
    public final double getLayoutX(final int cellSize) {
        if (x == 0) {
            return cellSize / 2;
        }
        return (x * cellSize) + cellSize / 2;
    }

    /**
     * If the location is valid for a certain grid size.
     * @param gridSize the grid size it is being tested against.
     * @return whether or not it is a valid location on the grid.
     */
    public final boolean isValidFor(final int gridSize) {
        return x >= 0 && x < gridSize && y >= 0 && y < gridSize;
    }

}
