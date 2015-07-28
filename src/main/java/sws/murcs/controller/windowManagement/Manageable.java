package sws.murcs.controller.windowManagement;

/**
 * Controllers implemented by this interface ensure that they can be properly managed by the window manager.
 */
public interface Manageable {

    /**
     * Requires a close method to be implement to close the window safely.
     */
    void close();

    /**
     * Sets up the close event, to clean up after the window manager.
     */
    void setCloseEvent();

    /**
     * Ensures that the window is managed byt the stage manager.
     * @param window The window to be managed
     */
    void register(Window window);

    /**
     * Ensures that the controller is responsible for showing the window.
     */
    void show();
}
