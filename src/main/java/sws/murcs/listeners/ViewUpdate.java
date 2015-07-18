package sws.murcs.listeners;

/**
 * Callback interface to update the list view.
 *
 * @param <T> The type of the view update.
 */
public interface ViewUpdate<T> {

    /**
     * The function to select an item.
     * @param parameter The item to select
     */
    void selectItem(T parameter);
}
