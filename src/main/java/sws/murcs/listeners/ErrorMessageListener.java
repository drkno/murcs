package sws.murcs.listeners;

/**
 * Notifies if there is an error message.
 */
public interface ErrorMessageListener {

    /**
     * Notifies any listeners with a message.
     * @param message The message to notify with.
     */
    void notify(String message);
}
