package sws.murcs.controller.editor;

import sws.murcs.exceptions.ImperialException;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Backlog;

/**
 * The estimation workspace for manipulating stories in a backlog.
 */
public class EstimationWorkspace extends GenericEditor<Backlog> implements UndoRedoChangeListener {

    @Override
    public void undoRedoNotification(ChangeState param) {

    }

    @Override
    public void loadObject() {
        throw new ImperialException();
    }

    @Override
    protected void saveChangesAndErrors() {
        throw new ImperialException();
    }

    @Override
    protected void initialize() {
//        throw new ImperialException();
    }
}
