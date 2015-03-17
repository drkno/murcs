package sws.project.controller;

import javafx.stage.WindowEvent;

/**
 * Interface for overriding window close events
 * 17/03/2015
 */
public interface AppClosingListener {

    /**
     * Override quit
     * @param e Window close event to consume
     */
    public void quit(WindowEvent e);
}
