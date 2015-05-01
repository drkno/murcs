package sws.murcs.controller;

import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;


/**
 * A generic class for making editing easier.
 * @param <T> The type of the editor (linked to the model)
 */
public abstract class GenericEditor<T> implements UndoRedoChangeListener {

    /**
     * The type of model the editor is being used for.
     */
    protected T edit;

    /**
     * A generic editor for editing models.
     */
    public GenericEditor() {
        UndoRedoManager.addChangeListener(this);
    }

    @Override
    public final void undoRedoNotification(final ChangeState param) {
        if (param == ChangeState.Remake || param == ChangeState.Revert) {
            updateFields();
        }
    }

    /**
     * The function that will update all the fields in the editor.
     */
    public abstract void updateFields();

    /**
     * Sets the item that the form is editing.
     * @param toEdit The thing to edit
     */
    final void setEdit(final T toEdit) {
        this.edit = toEdit;
    }

    /**
     * Loads the object into the form. Implementation details
     * are up to the user.
     */
    public abstract void load();

    /**
     * Updates the model in the form.
     * @throws Exception Any exceptions within the form
     */
    public abstract void update() throws Exception;

    /**
     * Deals with Exceptions that the update method throws and shows the appropriate message to the user.
     */
    public abstract void updateAndHandle();
}
