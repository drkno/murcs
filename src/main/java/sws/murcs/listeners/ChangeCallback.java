package sws.murcs.listeners;

import javafx.scene.control.Button;

/**
 * Callback for a change type.
 * @param <T> The type of object changed
 */
@FunctionalInterface
public interface ChangeCallback<T> extends SWSCallback<T> {
//    /**
//     * The function to change the item.
//     * @param parameter the item to change.
//     */
//    @Override
//    void changeItem(T parameter);
}
