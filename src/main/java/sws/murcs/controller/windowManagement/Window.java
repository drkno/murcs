package sws.murcs.controller.windowManagement;

import javafx.stage.Stage;
import sws.murcs.listeners.GenericCallback;
import sws.murcs.view.App;

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
     * The owner of the parent of the controller.
     */
    protected Window parentWindow;

    /**
     * Creates a new window containing a stage and a controller.
     * @param pStage The stage.
     * @param pController The controller.
     */
    public Window(final  Stage pStage, final Object pController) {
        this(pStage, pController, null);
    }

     /**
      * Creates a new window containing a stage and a controller.
      * @param pStage The stage.
      * @param pController The controller.
      * @param pParentWindow The the parent of the windows controller.
      */
     public Window(final  Stage pStage, final Object pController, final Window pParentWindow) {
         stage = pStage;
         controller = pController;
         parentWindow = pParentWindow;
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

    /**
     * Gets the parent controller of the window.
     * @return The parent controller.
     */
    public final Object getParentWindow() {
        return parentWindow;
    }

    /**
     * Override for when we don't want to pass a callback.
     */
    public final void close() {
        close(() -> { });
    }

    /**
     * Ensures that the window is closed properly.
     * @param callback A function to call after the stage has closed.
     */
    public final void close(final GenericCallback callback) {
        App.getWindowManager().removeWindow(this);
        stage.close();
        callback.call();
    }

    /**
     * Registers a window with the window manager.
     * And ensures that the default on closed request is handled by the window manager.
     */
    public final void register() {
        App.getWindowManager().addWindow(this);
        stage.setOnCloseRequest((event -> {
            App.getWindowManager().removeWindow(this);
        }));
        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("fire");
            System.out.println(this);
                if (newValue) {
                    System.out.println("accept focus");
                    System.out.println(App.getWindowManager().getAllWindows());
                    App.getWindowManager().bringToTop(this);
                    System.out.println(App.getWindowManager().getAllWindows());
                }
            });
    }

    /**
     * Adds global shortcuts to window.
     */
    public final void addGlobalShortcutsToWindow() {
        App.getShortcutManager().addAllShortcutsToWindow(this);
    }

    /**
     * Shows a stage.
     */
    public final void show() {
        stage.show();
    }

    /**
     * Brings the parentWindow to the front.
     */
    public final void parentToFront() {
        System.out.println("send parent to front");
        if (parentWindow != null) {
            System.out.println("sending to front");
            parentWindow.stage.toFront();
        }
    }
}
