package sws.murcs.magic.tracking;

/**
 * ChangeListener callback for UndoRedo.
 */
public interface UndoRedoChangeListener {
    /**
     * Notifies a listener about an event.
     * @param param event arguments.
     */
    void undoRedoNotification(int param);
}
