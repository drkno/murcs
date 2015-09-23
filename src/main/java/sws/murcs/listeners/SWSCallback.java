package sws.murcs.listeners;

/**
 * Base callback interface.
 * @param <T> the type of item.
 */
@FunctionalInterface
public interface SWSCallback<T> {
    /**
     * The function to change the item.
     * @param parameter the item to change.
     */
    void changeItem(T parameter);
}
