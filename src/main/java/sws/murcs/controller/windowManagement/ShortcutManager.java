package sws.murcs.controller.windowManagement;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCombination;
import sws.murcs.listeners.GenericCallback;
import sws.murcs.view.App;

/**
 * Used for tracking global shortcuts.
 */
public class ShortcutManager
{
    /**
     * A Map storing shortcuts to functions.
     */
    ObservableList<Shortcut> accelerators;


    /**
     * Initialises the shortcut manager.
     */
    public ShortcutManager() {
        accelerators  = FXCollections.observableArrayList();
        accelerators.addListener((ListChangeListener<Shortcut>) change -> {
            change.next();
            if (change.wasRemoved()) {
                for (Shortcut shortcut: change.getRemoved()) {
                    for (Window windows : App.getWindowManager().getAllWindows()) {
                        windows.stage
                                .getScene()
                                .getAccelerators()
                                .remove(shortcut.shortcutKeys);
                    }
                }
            }
            else if (change.wasAdded()) {
                for (Shortcut shortcut: change.getAddedSubList()) {
                    for (Window windows : App.getWindowManager().getAllWindows()) {
                        windows.stage
                                .getScene()
                                .getAccelerators()
                                .put(shortcut.shortcutKeys, shortcut.callback::call);
                    }
                }
            }
        });
    }

    /**
     * Registers a new shortcut.
     * @param keyCombination the shortcut.
     * @param callback the function to call.
     */
    public final void registerShortcut(final KeyCombination keyCombination, final GenericCallback callback) {
        accelerators.add(new Shortcut(keyCombination, callback));
    }

    /**
     * Registers a new shortcut.
     * @param shortcut the shortcut.
     */
    public final void registerShortcut(final Shortcut shortcut) {
        accelerators.add(shortcut);
    }

    /**
     * Assigns all shortcuts to a window.
     * Used when initialising a new window.
     * @param window The window to add shortcuts to.
     */
    public final void addAllShortcutsToWindow(final Window window) {
        for (Shortcut entry : accelerators) {
            window.stage.getScene().getAccelerators().put(entry.shortcutKeys, entry.callback::call);
        }
    }

    /**
     * Removes a shortcut from the global list.
     * @param keyCombination The short cut to remove
     */
    public final void removeShortcut(final KeyCombination keyCombination) {
        for (Shortcut shortcut: accelerators) {
            if (shortcut.shortcutKeys == keyCombination) {
                accelerators.remove(shortcut);
            }
        }
    }

    /**
     * Removes a shortcut from the global list.
     * @param shortcut The short cut to remove
     */
    public final void removeShortcut(final Shortcut shortcut) {
        accelerators.remove(shortcut);
    }

}
