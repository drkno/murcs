package sws.murcs.controller.editor;

import javafx.beans.value.ChangeListener;
import sws.murcs.exceptions.CustomException;
import sws.murcs.listeners.ErrorMessageListener;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;

/**
 * A generic class for making editing easier.
 * @param <T> The type of the editor (linked to the model)
 */
public abstract class GenericEditor<T> implements UndoRedoChangeListener, Editor {

    /**
     * The type of model the editor is being used for.
     */
    private T model;
    /**
     * The error callback.
     */
    private ErrorMessageListener errorCallback;

    /**
     * A collection of change listeners for an editor.
     */
    private ChangeListener changeListener;

    /**
     * A generic editor for editing models.
     */
    public GenericEditor() {
        UndoRedoManager.addChangeListener(this);
    }

    @Override
    public final void setModel(final Object pModel) {
        if (pModel != null) {
            model = (T) pModel;
        }
    }

    @Override
    public final T getModel() {
        return model;
    }

    @Override
    public final void setErrorCallback(final ErrorMessageListener pCallback) {
        errorCallback = pCallback;
    }

    @Override
    public final ErrorMessageListener getErrorCallback() {
        return errorCallback;
    }

    @Override
    public final void undoRedoNotification(final ChangeState param) {
        if (param == ChangeState.Remake || param == ChangeState.Revert) {
            loadObject();
        }
    }

    @Override
    public final void showErrors(final String pMessage) {
        errorCallback.notify(pMessage);
    }

    @Override
    public final void clearErrors() {
        errorCallback.notify("");
    }

    @Override
    public final void saveChanges() {
        try {
            saveChangesWithException();
            clearErrors();
        }
        catch (Exception e) {
            handleException(e);
        }
    }

    @Override
    public abstract void dispose();

    /**
     * Sets the change listener.
     * @param newChangeListener new listener to set.
     */
    protected final void setChangeListener(final ChangeListener newChangeListener) {
        changeListener = newChangeListener;
    }

    /**
     * Gets the change listener.
     * @return The change listener
     */
    protected final ChangeListener getChangeListener() {
        return changeListener;
    }

    /**
     * Checks to see if the Exception is a custom type.
     * @param e Exception to check
     */
    private void handleException(final Exception e) {
        if (e instanceof CustomException) {
            showErrors(e.getMessage());
        }
        else {
            e.printStackTrace();
        }
    }

    /**
     * Checks to see if the view Object is empty or the model Object is null,
     * or not equal to the view Object.
     * @param modelObject model Object to check against
     * @param viewObject view Object to check against
     * @return boolean true if objects are not equal or null, false otherwise
     */
    protected final boolean isNotEqualOrIsEmpty(final Object modelObject, final Object viewObject) {
        return viewObject == null || modelObject == null || !viewObject.equals(modelObject);
    }

    /**
     * Checks to see if the view Object is not equal to the model Object.
     * @param modelObject model Object to check against
     * @param viewObject view Object to check against
     * @return boolean true if objects are not equal, false otherwise
     */
    protected final boolean isNotEqual(final Object modelObject, final Object viewObject) {
        return viewObject == null || (modelObject != null && !viewObject.equals(modelObject));
    }

    /**
     * Saves changes and throws an Exception if an error occurs.
     * @throws Exception When an error occurs
     */
    protected abstract void saveChangesWithException() throws Exception;
}
