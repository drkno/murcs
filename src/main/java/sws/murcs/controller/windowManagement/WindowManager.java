package sws.murcs.controller.windowManagement;


import javafx.collections.FXCollections;
import sws.murcs.controller.ModelViewController;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Manages the windows visible to the viewer, keeps the order of what windows are on top of each other.
 */
public class WindowManager {

    /**
     * Contains a list of windows on the screen.
     */
    private List<Window> windows;

    /**
     * Creates a new window manager.
     */
    public WindowManager() {
        windows = FXCollections.observableArrayList();
    }

    /**
     * Brings the window on top of all other windows.
     * @param window The window to bring to the top
     * @param moveWindow Moves the window on top of other windows.
     */
    public final void bringToTop(final Window window, final boolean moveWindow) {
        windows.remove(window);
        windows.add(0, window);
        if (moveWindow) {
            window.stage.toFront();
        }
    }

    /**
     * Sends a window to behind all other windows.
     * @param window The window to move to the back
     * @param moveWindow Moves the window behind all other windows.
     */
    public final void sendToBottom(final Window window, final boolean moveWindow) {
        int newIndex = windows.size() - 1;
        windows.remove(window);
        windows.add(newIndex, window);
        if (moveWindow) {
            window.stage.toBack();
        }
    }

    /**
     * Sends a window one position back.
     * @param window The window to move.
     * @param moveWindow Moves the window relative to other windows.
     */
    public final void sendBackwards(final Window window, final boolean moveWindow) {
        sendBackwards(window, 1, moveWindow);
    }

    /**
     * Sends a window back a given number of positions.
     * @param window The window to move.
     * @param howFar The number of positions to move the window.
     * @param moveWindow Moves the window relative to other windows.
     */
    public final void sendBackwards(final Window window, final int howFar, final boolean moveWindow) {
        int windowIndex = windows.indexOf(window);
        windows.remove(window);
        if (windowIndex + howFar > windows.size()) {
            windows.add(windows.size(), window);
        }
        else {
            windows.add(windowIndex + howFar, window);
        }
        if (moveWindow) {
            reOrderWindows();
        }
    }

    /**
     * Sends the window forward one position.
     * @param window The window to move.
     * @param moveWindow Moves the window relative to other windows.
     */
    public final void sendForwards(final Window window, final boolean moveWindow) {
        sendForwards(window, 1, moveWindow);
    }

    /**
     * Sends a window forward a given number of positions.
     * @param window The window to move.
     * @param howFar The number of positions to move the window.
     * @param moveWindow Moves the window relative to other windows.
     */
    public final void sendForwards(final Window window, final int howFar, final boolean moveWindow) {
        int windowIndex = windows.indexOf(window);
        windows.remove(window);
        if (windowIndex - howFar < 0) {
            windows.add(0, window);
        }
        else {
            windows.add(windowIndex - howFar, window);
        }
        if (moveWindow) {
            reOrderWindows();
        }
    }

    /**
     * Gets the front most window.
     * @return The window at the front.
     */
    public final Window getTop() {
         return windows.get(0);
    }

    /**
     * Removes a window from the manager.
     * @param window The window to remove
     */
    public final void removeWindow(final Window window) {
        windows.remove(window);
    }

    /**
     * Adds a window to the top most position in windows.
     * @param window new window to register.
     */
    public final void addWindow(final Window window) {
        windows.add(0, window);
        window.stage.toFront();
    }

    /**
     * Adds a window behind all other windows.
     * @param window The window to add.
     */
    public final void addWindowToBack(final Window window) {
        windows.add(windows.size(), window);
        reOrderWindows();
    }

    /**
     * Adds a window with a given position.
     * @param window The window to add.
     * @param pos The position to place it.
     */
    public final void addWindowWithPos(final Window window, final int pos) {
        windows.add(window);
        setWindowPosition(window, pos, true);
    }

    /**
     * Gets the position of the given window.
     * @param window The Window to get the position for.
     * @return The position.
     */
    public final int getWindowPosition(final Window window) {
        return windows.indexOf(window);
    }

    /**
     * Sets the position of a window.
     * @param window The window to change the position of.
     * @param newPosition The new position to set the window to.
     * @param moveWindow Moves the window relative to other windows.
     */
    public final void setWindowPosition(final Window window, final int newPosition, final boolean moveWindow) {
        windows.remove(window);
        if (newPosition <= 0) {
            windows.add(0, window);
        }
        else if (newPosition < windows.size()) {
            windows.add(newPosition, window);
        }
        else {
            windows.add(windows.size(), window);
        }
        if (moveWindow) {
            reOrderWindows();
        }
    }

    /**
     * Reorders how the list of windows is displayed, according to the order of the list.
     * With the first index of the list being the window shown at the top.
     */
    private void reOrderWindows() {
        for (int i = windows.size() - 1; i > -1; i--) {
            windows.get(i).getStage().toFront();
        }
    }

    /**
     * Gets a collection of unmodifiable windows.
     * @return windows.
     */
    public final Collection<Window> getAllWindows() {
        return Collections.unmodifiableCollection(windows);
    }

    /**
     * Gets the instance of the controller for a given window.
     * @param window The window to get the controller for.
     * @return the controller object.
     */
    public final Object getController(final Window window) {
        return window.controller;
    }

    /**
     * Cleans up all open windows which are not the main controller.
     */
    public final void cleanUp() {
        for (int i = 0; i < windows.size(); i++) {
            if (windows.get(i).getController().getClass() != ModelViewController.class) {
                windows.get(i).close();
                i--;
            }
        }
    }
}
