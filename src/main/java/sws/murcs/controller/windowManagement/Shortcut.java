package sws.murcs.controller.windowManagement;

import javafx.scene.input.KeyCombination;
import sws.murcs.listeners.GenericCallback;


/**
 * A instance of a shortcut which contains a key combination and a callback function.
 */
public class Shortcut implements Comparable<Shortcut> {
    /**
     * The shortcut key combination.
     */
    protected KeyCombination shortcutKeys;
    /**
     * The function to call when the shortcut is triggered.
     */
    protected GenericCallback callback;

    /**
     * Creates a new shortcut.
     * @param pKeyCombination The short cut keys.
     * @param pCallback The function to call.
     */
    public Shortcut(final KeyCombination pKeyCombination, final GenericCallback pCallback) {
        shortcutKeys = pKeyCombination;
        callback = pCallback;
    }

    @Override
    public final int compareTo(final Shortcut s) {
        if (this.shortcutKeys == s.shortcutKeys) {
            return 0;
        }
        else {
            return -1;
        }
    }
}
