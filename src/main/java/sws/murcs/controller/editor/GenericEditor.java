package sws.murcs.controller.editor;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import md.MaterialDesignButton;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
    private Label labelErrorMessage;

    /**
     * The container for the error message and save button
     */
    @FXML
    public HBox bottomBar;

    /**
     * Placebo button for saving.
     */
    private MaterialDesignButton saveButton;

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
    private Collection<Node> invalidNodes = new ArrayList<>();

    /**
     * A helpful error message telling you about all that's wrong
     * in the world.
     */
    private String errorMessage = "";

    /**
     * A generic editor for editing models.
     */
    public GenericEditor() {
        UndoRedoManager.addChangeListener(this);
    }

    /**
     * Sets the current model for the editor.
     * @param pModel The new model to edit
     */
    public final void setModel(final Object pModel) {
        if (pModel != null) {
            model = (T) pModel;
        }
    }

    /**
     * Gets the object that this form is editing.
     * @return The current model object
     */
    public final T getModel() {
        return model;
    }

    /**
     * Updates the form with an undo redo notification.
     * @param param event arguments.
     */
    public final void undoRedoNotification(final ChangeState param) {
        if (param == ChangeState.Remake || param == ChangeState.Revert) {
            loadObject();
        }
    }

    /**
     * Highlights errors on the form.
     */
    private void showErrors() {
        for (Node node : invalidNodes) {
            if (!node.getStyleClass().contains("error")) {
                node.getStyleClass().add("error");
            }
        }
        labelErrorMessage.setText(errorMessage);
    }

    /**
     * Clears the errors on the form.
     */
    public final void clearErrors() {
        for (Node node : invalidNodes) {
            node.getStyleClass().removeAll(Collections.singleton("error"));
        }
        errorMessage = "";
        invalidNodes.clear();

        labelErrorMessage.setText(errorMessage);
    }

    /**
     * Saves the changes on the current form.
     */
    public final void saveChanges() {
        clearErrors();
        saveChangesAndErrors();
    }

    /**
     * Highlights an error on the form.
     * @param invalidNode The invalid node
     */
    protected final void addFormError(final Node invalidNode) {
        addFormError(invalidNode, "");
    }

    /**
     * Adds an error message to the form.
     * @param helpfulMessage A helpful error message describing the problem.
     */
    protected final void addFormError(final String helpfulMessage) {
        addFormError(null, helpfulMessage);
    }

    /**
     * Adds an error to the form and highlights the node that caused it.
     * @param invalidNode The node that has the problem
     * @param helpfulMessage A helpful message describing the problem.
     */
    protected final void addFormError(final Node invalidNode, final String helpfulMessage) {
        if (invalidNode != null) {
            invalidNodes.add(invalidNode);
        }

        if (helpfulMessage == null || helpfulMessage.isEmpty()) {
            return;
        }

        if (errorMessage.length() > 0) {
            errorMessage += "\n";
        }
        errorMessage += helpfulMessage;
        showErrors();
    }

    /**
     * Loads the current model object into the form.
     */
    public abstract void loadObject();

    /**
     * Cleans up event handlers and stuff. (Please ignore the CheckStyle error here, it's wrong).
     */
    public void dispose() {
        setChangeListener(null);
        UndoRedoManager.removeChangeListener(this);
        setModel(null);
        clearErrors();
    }

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
     * Saves changes and tells the editor about any errors that have occurred.
     */
    protected abstract void saveChangesAndErrors();

    /**
     * Sets up the form with all its event handlers and things.
     */
    @FXML
    protected abstract void initialize();

    protected void setupSaveChangesButton() {
        saveButton = new MaterialDesignButton("Save Changes");
        saveButton.setPadding(new Insets(5, 0, 0, 0));
        saveButton.setRippleColor(Color.color(0.611, 0.8, 0.396));
        bottomBar.getChildren().add(saveButton);
        bottomBar.setMargin(saveButton, new Insets(5, 0, 0, 5));
    }
}
