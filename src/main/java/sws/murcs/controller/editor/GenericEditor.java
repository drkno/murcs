package sws.murcs.controller.editor;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.InvalidFormException;
import sws.murcs.listeners.ErrorMessageListener;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A generic class for making editing easier.
 * @param <T> The type of the editor (linked to the model)
 */
public abstract class GenericEditor<T> implements UndoRedoChangeListener {

    /**
     * The type of model the editor is being used for.
     */
    private T model;

    /**
     * The label for showing error messages.
     */
    @FXML
    protected Label labelErrorMessage;

    /**
     * A collection of change listeners for an editor.
     */
    private ChangeListener changeListener;

    /**
     * Details whether or not the window is a creator for a new model or an editor.
     */
    private boolean isCreationWindow;

    /**
     * All the invalid sections in the form.
     */
    private Map<Node, String> invalidSections = new HashMap<>();

    /**
     * A generic editor for editing models.
     */
    public GenericEditor() {
        UndoRedoManager.addChangeListener(this);
    }

    public final void setModel(final Object pModel) {
        if (pModel != null) {
            model = (T) pModel;
        }
    }

    public final T getModel() {
        return model;
    }

    public final void undoRedoNotification(final ChangeState param) {
        if (param == ChangeState.Remake || param == ChangeState.Revert) {
            loadObject();
        }
    }

    private void showErrors(final InvalidFormException e) {
        invalidSections = e.getInvalidSections();
        StringBuilder errorMessageBuilder = new StringBuilder();
        for (Entry<Node, String> entry : invalidSections.entrySet()) {
            entry.getKey().getStyleClass().add("error");
            if (errorMessageBuilder.length() != 0) {
                errorMessageBuilder.append("\n");
            }
            errorMessageBuilder.append(entry.getValue());
        }
        labelErrorMessage.setText(errorMessageBuilder.toString());
    }

    public final void clearErrors() {
        for (Entry<Node, String> entry : invalidSections.entrySet()) {
            entry.getKey().getStyleClass().removeAll(Collections.singleton("error"));
        }
        labelErrorMessage.setText("");
    }

    public final void saveChanges() {
        clearErrors();
        try {
            saveChangesWithException();
        } catch (InvalidFormException e) {
            showErrors(e);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void loadObject();

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

    /**
     * Sets up the form with all its event handlers and things.
     */
    @FXML
    protected abstract void initialize();
}
