package sws.murcs.listeners;

/**
 * Callback for a change type.
 * @param <T> The type of object changed
 */
@FunctionalInterface
public interface ChangeCallback<T>  {
    /**
     * The function to change the item.
     * @param parameter the item to change.
     */
    void changeItem(T parameter);
}
