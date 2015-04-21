package sws.murcs.controller;

import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.magic.tracking.UndoRedoManager;


/**
 * A generic class for making editing easier
 */
public abstract class GenericEditor<T> implements UndoRedoChangeListener {

    protected T edit;
    protected ViewUpdate onSaved;

    public GenericEditor() {
        UndoRedoManager.addChangeListener(this);
    }

    @Override
    public void undoRedoNotification(ChangeState param) {
        if (param == ChangeState.Remake || param == ChangeState.Revert) updateFields();
    }

    public abstract void updateFields();

    /**
     * Sets the item that the form is editing
     * @param toEdit The thing to edit
     */
    public void setEdit(T toEdit){
        this.edit = toEdit;
    }

    /**
     * Sets the callback that is fired when the object is saved
     * @param onSaved callback to set
     */
    public void setSavedCallback(ViewUpdate onSaved){
        this.onSaved = onSaved;
    }

    /**
     * Loads the object into the form. Implementation details
     * are up to the user
     */
    public abstract void load();

    /**
     * Updates the model in the form
     * @throws Exception Any exceptions within the form
     */
    public abstract void update() throws Exception;

    /**
     * Deals with Exceptions that the update method throws and shows the appropriate message to the user
     */
    public abstract void updateAndHandle();
}
