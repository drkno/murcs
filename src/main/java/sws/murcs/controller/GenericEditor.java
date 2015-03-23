package sws.murcs.controller;

import sws.murcs.EventNotification;
import sws.murcs.model.Model;

/**
 * A generic class for making editing easier
 */
public abstract class GenericEditor<T> {

    protected T edit;
    protected EventNotification<Model> onSaved;

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
    public void setSavedCallback(EventNotification<Model> onSaved){
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
