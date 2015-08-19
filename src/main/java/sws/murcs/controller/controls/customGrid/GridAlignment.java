package sws.murcs.controller.controls.customGrid;

import javafx.scene.input.KeyCode;

/**
 * An enum of all the possible alignments within the custom grid.
 */
public enum GridAlignment {

    /**
     * Aligns at the top.
     */
    UP(0, -1),

    /**
     * Aligns to the right.
     */
    RIGHT(1, 0),

    /**
     * Aligns at the bottom.
     */
    DOWN(0, 1),

    /**
     * Aligns to the left.
     */
    LEFT(-1, 0);

    /**
     * The y co-ordinate of the alignment.
     */
    private final int y;

    /**
     * The x co-ordinate of the alignment.
     */
    private final int x;

    /**
     * Creates a new grid alignment based on the pX and pY co-ordinates given.
     * @param pX the pX co-ordinate of the alignment
     * @param pY the pY co-ordinate of the alignment
     */
    GridAlignment(final int pX, final int pY) {
        this.x = pX;
        this.y = pY;
    }

    /**
     * Get the x co-ordinate of the alignment.
     * @return x co-ordinate of the alignment.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y co-ordinate of the alignment.
     * @return y co-ordinate of the alignment.
     */
    public int getY() {
        return y;
    }

    /**
     * Makes a string out of the alignment.
     * @return a string version of the alignment
     */
    @Override
    public String toString() {
        return "GridAlignment{" + "y=" + y + ", x=" + x + '}' + name();
    }

    /**
     * Gets the alignment based on the key code given. Valid keys are up, down, left, right.
     * @param keyCode The keycode of button pressed.
     * @return The corresponding alignment
     */
    public static GridAlignment valueFor(final KeyCode keyCode) {
        return valueOf(keyCode.name());
    }
}
