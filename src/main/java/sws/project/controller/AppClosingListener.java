package sws.project.controller;

import javafx.stage.WindowEvent;

/**
 * 17/03/2015
 */
public interface AppClosingListener {

    /**
     *
     * @param e Window close event to consume
     */
    public void quit(WindowEvent e);
}
