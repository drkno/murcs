package sws.murcs.controller.windowManagement;

import sws.murcs.listeners.GenericCallback;

/**
 * Controllers implemented by this interface ensure that they can be properly managed by the window manager.
 */
public interface Manageable {

    /**
     * Requires a close method to be implement to close the window safely.
     * @param callback The function to call.
     */
    void close(GenericCallback callback);

    /**
     * Ensures that the window is managed by the stage manager.
     */
    void register();

    /**
     * Ensures that the controller is responsible for showing the window.
     */
    void show();
}
