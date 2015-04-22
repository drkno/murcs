package sws.murcs.listeners;

import javafx.stage.WindowEvent;

/**
 * Interface for overriding window close events
 */
public interface AppClosingListener {

    /**
     * Override quit
     * @param e Window close event to consume
     */
    public void quit(WindowEvent e);
}
