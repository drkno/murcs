package sws.murcs.controller.editor;

import sws.murcs.listeners.ErrorMessageListener;

/**
 * Interface for contracts of an Editor
 */
public interface Editor<T> {

    /**
     * Changes need to be saved, when the view is different to the model
     */
    void saveChanges();

    /**
     * Loads data when the model is different to the view
     */
    void loadObject();

    /**
     * Sets up the view
     */
    void initialize();

    /**
     * Sets a callback to call when an error occurs
     */
    void setErrorCallback(ErrorMessageListener callback);

    /**
     * Show errors to the view
     * An Error Callback is required to be set before using this method
     */
    void showErrors(String message);

    /**
     * Clear errors on the view
     * An Error Callback is required to be set before using this method
     */
    void clearErrors();

    /**
     * Cleans up any references in order for the editor to be garbage collected
     */
    void dispose();

    /**
     * Sets the model to model, an editor must have a model to model
     * @param model The Model to model
     */
    void setModel(T model);
}
