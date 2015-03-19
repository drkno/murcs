package sws.project.controller;

import java.util.concurrent.Callable;

/**
 * A generic class for making editing easier
 */
public abstract class GenericEditor<T>{
    public T edit;
    protected Callable<Void> onSaved;

    /**
     * Sets the item that the form is editing
     * @param toEdit The thing to edit
     */
    public void setEdit(T toEdit){
        this.edit = toEdit;
    }

    /**
     * Sets the callback that is fired when the object is saved
     * @param onSaved
     */
    public void setSavedCallback(Callable<Void> onSaved){
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
}
