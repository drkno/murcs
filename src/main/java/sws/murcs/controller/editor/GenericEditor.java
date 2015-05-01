package sws.murcs.controller.editor;

import sws.murcs.exceptions.CustomException;
import sws.murcs.listeners.ErrorMessageListener;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;


/**
 * A generic class for making editing easier
 */
public abstract class GenericEditor<T> implements UndoRedoChangeListener, Editor{

    protected T edit;
    protected ErrorMessageListener errorCallback;

    public GenericEditor() {
        UndoRedoManager.addChangeListener(this);
    }

    /**
     * Sets the item that the form is editing
     * @param toEdit The thing to edit
     */
    public void setEdit(T toEdit){
        this.edit = toEdit;
    }

    @Override
    public void undoRedoNotification(ChangeState param) {
        if (param == ChangeState.Remake || param == ChangeState.Revert) loadObject();
    }

    @Override
    public void setErrorCallback(ErrorMessageListener callback) {
        errorCallback = callback;
    }

    @Override
    public void showErrors(String message) {
        errorCallback.notify(message);
    }

    @Override
    public void clearErrors() {
        errorCallback.notify("");
    }

    @Override
    public void saveChanges() {
        try {
            saveChangesWithException();
            clearErrors();
        }
        catch (Exception e){
            handleException(e);
        }
    }

    /**
     * Checks to see if the Exception is a custom type
     * @param e Exception to check
     */
    private void handleException(Exception e) {
        if (e instanceof CustomException) {
            showErrors(e.getMessage());
        }
        else {
            e.printStackTrace();
        }
    }

    /**
     * Checks to see if the view Object is empty or the model Object is null or not equal to the view Object
     * @param modelObject model Object to check against
     * @param viewObject view Object to check against
     * @return boolean if checks are true
     */
    protected boolean isNotEqualOrIsEmpty(Object modelObject, Object viewObject) {
        return viewObject == null || modelObject == null || !viewObject.equals(modelObject);
    }

    /**
     * Checks to see if the view Object is not equal to the model Object
     * @param modelObject model Object to check against
     * @param viewObject view Object to check against
     * @return boolean if checks are true
     */
    protected boolean isNotEqual(Object modelObject, Object viewObject) {
        return viewObject == null || (modelObject != null && !viewObject.equals(modelObject));
    }

    /**
     * Saves changes and throws an Exception if an error occurs
     * @throws Exception When an error occurs
     */
    protected abstract void saveChangesWithException() throws Exception;
}
