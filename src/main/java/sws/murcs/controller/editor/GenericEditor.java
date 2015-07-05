package sws.murcs.controller.editor;

import javafx.beans.value.ChangeListener;
import sws.murcs.debug.errorreporting.ErrorReporter;
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
     * Details whether or not the window is a creator for a new model or an editor.
     */
    private boolean isCreationWindow;

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
        if (errorCallback != null) {
            errorCallback.notify("");
        }
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
     * Gets whether or not this editor is a creation window.
     * @return Whether or not this editor is a creation window
     */
    public final boolean getIsCreationWindow() {
        return isCreationWindow;
    }

    /**
     * Sets whether or not this editor is a creation window.
     * @param newIsCreationWindow The new value for whether or not this is a creation window
     */
    public final void setIsCreationWindow(final boolean newIsCreationWindow) {
        isCreationWindow = newIsCreationWindow;
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
            ErrorReporter.get().reportError(e, "Unhandled error occurred in an editor.");
        }
    }

    /**
     * Checks to see if the view Object is empty or the model Object is null,
     * or not equal to the view Object.
     * @param modelObject model Object to check against
     * @param viewObject view Object to check against
     * @return boolean true if objects are not equal or null, false otherwise
     */
    protected final boolean isNullOrNotEqual(final Object modelObject, final Object viewObject) {
        return viewObject == null || modelObject == null || !viewObject.equals(modelObject);
    }

    /**
     * Checks to see if the view Object is not equal to the model Object.
     * @param modelObject model Object to check against
     * @param viewObject view Object to check against
     * @return boolean true if objects are not equal, false otherwise
     */
    protected final boolean isNotEqual(final Object modelObject, final Object viewObject) {
        return viewObject == null || !(modelObject == null || viewObject.equals(modelObject));
    }

    /**
     * Saves changes and throws an Exception if an error occurs.
     * @throws Exception When an error occurs
     */
    protected abstract void saveChangesWithException() throws Exception;
}
