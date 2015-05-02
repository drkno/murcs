package sws.murcs.controller.editor;

import sws.murcs.listeners.ErrorMessageListener;

/**
 * Interface for contracts of an Editor.
 * @param <T> The type of the editor (linked to the model)
 */
public interface Editor<T> {

    /**
     * Changes need to be saved, when the view is different to the model.
     */
    void saveChanges();

    /**
     * Loads data when the model is different to the view.
     */
    void loadObject();

    /**
     * Sets up the view.
     */
    void initialize();

    /**
     * Sets a pCallback to call when an error occurs.
     * @param pCallback The pCallback to set
     */
    void setErrorCallback(ErrorMessageListener pCallback);

    /**
     * Show errors to the view.
     * An Error Callback is required to be set before using this method.
     * @param pMessage The pMessage to show as an error
     */
    void showErrors(String pMessage);

    /**
     * Clear errors on the view.
     * An Error Callback is required to be set before using this method.
     */
    void clearErrors();

    /**
     * Cleans up any references in order for the editor to be garbage collected.
     */
    void dispose();

    /**
     * Sets the model to model, an editor must have a model to model.
     * @param model The Model to model
     */
    void setModel(T model);

    /**
     * Gets the model.
     * @return The model
     */
    T getModel();

    /**
     * Gets the error callback.
     * @return The error callback listener
     */
    ErrorMessageListener getErrorCallback();
}
