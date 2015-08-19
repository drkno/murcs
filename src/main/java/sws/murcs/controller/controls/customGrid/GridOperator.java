package sws.murcs.controller.controls.customGrid;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A class used for sorting out the layout of the grid and going through each location on the grid.
 */
public class GridOperator {

    /**
     * The default size of the grid. Both col and rows.
     */
    public static final int DEFAULT_GRID_SIZE = 4;

    /**
     * The current size of the grid.
     */
    private final int gridSize;

    /**
     * List of all the integers to traverse for x co-ordinates in the grid.
     */
    private final List<Integer> traversalX;

    /**
     * List of all the integers to traverse for y co-ordinates in the grid.
     */
    private final List<Integer> traversalY;

    /**
     * Sets up a new grid operator given a specific grid size.
     * @param pGridSize the grid size (is both rows and cols)
     */
    public GridOperator(final int pGridSize) {
        this.gridSize = pGridSize;
        this.traversalX = IntStream.range(0, pGridSize).boxed().collect(Collectors.toList());
        this.traversalY = IntStream.range(0, pGridSize).boxed().collect(Collectors.toList());
    }

    /**
     * Sorts the grid given a grid alignment.
     * @param gridAlignment the grid alignment.
     */
    @SuppressWarnings("CheckStyle")
    public final void sortGrid(final GridAlignment gridAlignment) {
        Collections.sort(traversalX, gridAlignment.equals(GridAlignment.RIGHT) ? Collections.reverseOrder()
                : Integer::compareTo);
        Collections.sort(traversalY, gridAlignment.equals(GridAlignment.DOWN) ? Collections.reverseOrder()
                : Integer::compareTo);
    }

    /**
     * Traverses over the grid using a specific function to apply changes to the grid as it goes over it.
     * @param func The function to apply changes to the grid layout.
     * @return 0 if failure, 1 if success.
     */
    public final int traverseGrid(final IntBinaryOperator func) {
        AtomicInteger at = new AtomicInteger();
        traversalX.forEach(t_x -> traversalY.forEach(t_y -> at.addAndGet(func.applyAsInt(t_x, t_y))));

        return at.get();
    }

    /**
     * Gets the current size of the grid.
     * @return The current size of the grid.
     */
    public final int getGridSize() {
        return gridSize;
    }

    /**
     * Gets whether or not the given location is valid or not given the size of the grid.
     * @param loc the location to check.
     * @return whether or not it is a valid location on the current grid.
     */
    public final boolean isValidLocation(final Location loc) {
        return loc.getX() >= 0 && loc.getX() < gridSize && loc.getY() >= 0 && loc.getY() < gridSize;
    }

}
