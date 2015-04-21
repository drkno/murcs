package sws.murcs.listeners;

/**
 * Callback interface to update the list view
 */
public interface ViewUpdate<T> {
    void updateListView(T param);
}
