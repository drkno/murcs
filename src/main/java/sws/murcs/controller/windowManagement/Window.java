package sws.murcs.controller.windowManagement;

import javafx.stage.Stage;

/**
 * A Wrapper class for linking stages and controllers together, so that they can be managed.
 */
public class Window {

    /**
     * The stage of the window.
     */
   protected Stage stage;

    /**
     * The controller for the window.
     */
    protected Object controller;

    /**
     * Creates a new window containing a stage and a controller.
     * @param pStage The stage.
     * @param pController The controller.
     */
    public Window(final  Stage pStage, final Object pController) {
        stage = pStage;
        controller = pController;
    }


    /**
     * Gets the stage of the window.
     * @return The stage.
     */
    public final Stage getStage() {
        return stage;
    }

    /**
     * Gets the controller of the window.
     * @return The controller.
     */
    public final Object getController() {
        return controller;
    }
}
