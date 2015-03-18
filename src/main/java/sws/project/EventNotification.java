package sws.project;

/**
 * Generic callback interface.
 * @param <T> parameter type.
 */
public interface EventNotification<T> {
    /**
     * Notifies a listener about an event.
     * @param param event arguments.
     */
    void eventNotification(T param);
}
