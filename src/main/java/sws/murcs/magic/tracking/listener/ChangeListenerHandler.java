package sws.murcs.magic.tracking.listener;

import java.lang.ref.WeakReference;

/**
 * Manages a handle to a change listener.
 * Not done the usual way because the UndoRedoManager exists for the entirety of the life
 * of the program, so failure to remove a listener would cause a [very large] memory leak.
 */
public class ChangeListenerHandler extends WeakReference<UndoRedoChangeListener> {
    /**
     * Performs a garbage collection, aimed at collecting former listeners.
     */
    public static void performGC() {
        try {
            System.gc();
        } catch (Exception e) {} // not much we can do
    }

    public ChangeListenerHandler(UndoRedoChangeListener referent) {
        super(referent);
    }

    /**
     * Notifies the listener if it still exists of the status change.
     * @param status the status.
     * @return true if the notification was successful and it still exists, false otherwise.
     */
    public boolean eventNotification(ChangeState status) {
        UndoRedoChangeListener changeListener = get();
        if (changeListener != null && !isEnqueued()) {
            changeListener.undoRedoNotification(status);
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof ChangeListenerHandler)) return false;
        Object handler = ((ChangeListenerHandler) other).get();
        return !(handler == null || !other.equals(this));
    }
}