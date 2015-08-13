package sws.murcs.controller.windowManagement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.input.KeyCombination;
import sws.murcs.listeners.GenericCallback;
import sws.murcs.view.App;

import java.util.TreeSet;

/**
 * Used for tracking global shortcuts.
 */
public class ShortcutManager {
    /**
     * A Map storing shortcuts to functions.
     */
    ObservableSet<Shortcut> accelerators;

    /**
     * Initialises the shortcut manager.
     */
    public ShortcutManager() {
        accelerators  = FXCollections.observableSet(new TreeSet<>());
        accelerators.addListener((SetChangeListener<Shortcut>) change -> {
            if (change.wasRemoved()) {
                for (Window windows : App.getWindowManager().getAllWindows()) {
                    windows.stage
                            .getScene()
                            .getAccelerators()
                            .remove(change.getElementRemoved().shortcutKeys);
                }
            }
            else if (change.wasAdded()) {
                for (Window windows : App.getWindowManager().getAllWindows()) {
                    windows.stage
                            .getScene()
                            .getAccelerators()
                            .put(change.getElementAdded().shortcutKeys, change.getElementAdded().callback::call);
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
        accelerators.stream().filter(shortcut -> shortcut.shortcutKeys == keyCombination).forEach(accelerators::remove);
    }

    /**
     * Removes a shortcut from the global list.
     * @param shortcut The short cut to remove
     */
    public final void removeShortcut(final Shortcut shortcut) {
        accelerators.remove(shortcut);
    }

}
