package sws.murcs.magic.tracking;

import java.lang.ref.WeakReference;

public class ChangeListenerHandler extends WeakReference<UndoRedoChangeListener> {
    public ChangeListenerHandler(UndoRedoChangeListener referent) {
        super(referent);
    }

    public boolean eventNotification(int status) {
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
        if (handler == null || !other.equals(this)) return false;
        return true;
    }
}