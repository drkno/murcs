package sws.murcs.listeners;

/**
 * Callback interface to update the model when an item is removed from within a cell.
 *
 * @param <T> The type of model that is being removed.
 */
public interface RemoveCallback<T> {

    /**
     * The function to remove the item.
     * @param parameter the item to remove.
     */
    void removeItem(T parameter);
}
