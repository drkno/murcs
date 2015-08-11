package sws.murcs.controller.editor;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import sws.murcs.controller.JavaFXHelpers;
import sws.murcs.controller.controls.md.MaterialDesignButton;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * A generic class for making editing easier.
 * @param <T> The type of the editor (linked to the model)
 */
public abstract class GenericEditor<T> implements UndoRedoChangeListener {

    /**
     * The type of model the editor is being used for.
     */
    protected T model;

    /**
     * The label for showing error messages.
     */
    @FXML
    private Label labelErrorMessage;

    /**
     * The container for the error message and save button.
     */
    @FXML
    private HBox bottomBar;

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
    protected boolean isCreationWindow;

    /**
     * Stores if a save changes button exists, preventing a new button being created
     * if one has already been created.
     */
    private boolean saveChangesButtonExists;

    /**
     * All the invalid sections in the form.
     */
    private Collection<Map.Entry<Node, String>> invalidNodes = new ArrayList<>();

    /**
     * Padding to use within the error message popover.
     */
    private final Insets errorMessagePopoverPadding = new Insets(0, 15, 0, 15);

    /**
     * Error message PopOver.
     */
    private PopOver errorMessagePopover;

    /**
     * Listener for focus on error fields.
     */
    private ChangeListener<Boolean> errorMessagePopoverListener;

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
        if (errorMessagePopover == null) {
            Label errorLabel = new Label();
            errorLabel.setPadding(errorMessagePopoverPadding);
            errorMessagePopover = new PopOver(errorLabel);
            errorMessagePopover.detachableProperty().setValue(false);
            errorMessagePopover.autoHideProperty().setValue(false);

            errorMessagePopoverListener = (observable, oldValue, newValue) -> {
                if (!newValue && observable != null && errorMessagePopoverListener != null) {
                    observable.removeListener(errorMessagePopoverListener);
                    errorMessagePopover.hide();
                }
            };
        }

        String errorMessage = "";
        for (Map.Entry<Node, String> entry : invalidNodes) {
            if (!entry.getKey().getStyleClass().contains("error")) {
                entry.getKey().getStyleClass().add("error");

                if (entry.getKey().isFocused()) {
                    Label errorLabel = (Label) errorMessagePopover.contentNodeProperty().get();
                    errorLabel.setText(entry.getValue());
                    errorMessagePopover.show(entry.getKey());
                    entry.getKey().focusedProperty().addListener(errorMessagePopoverListener);
                }
            }
            errorMessage += entry.getValue() + "\n";
        }
        if (errorMessage.length() > 2) {
            labelErrorMessage.setText(errorMessage.substring(0, errorMessage.length() - 1));
        }
    }

    /**
     * Clears the errors on the form.
     */
    public final void clearErrors() {
        boolean hideError = true;
        for (Map.Entry<Node, String> entry : invalidNodes) {
            entry.getKey().getStyleClass().removeAll(Collections.singleton("error"));
            entry.getKey().focusedProperty().removeListener(errorMessagePopoverListener);
            if (entry.getKey().isFocused()) {
                hideError = false;
            }
        }
        invalidNodes.clear();
        if (hideError && errorMessagePopover != null) {
            errorMessagePopover.hide();
        }
        labelErrorMessage.setText("");
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
     * @throws UnsupportedOperationException when an unhelpful error message is provided or
     * when no node is provided to work with.
     */
    protected final void addFormError(final Node invalidNode, final String helpfulMessage) {
        if (invalidNode == null) {
            throw new UnsupportedOperationException("A node must be provided.");
        }

        if (helpfulMessage == null || helpfulMessage.isEmpty()) {
            throw new UnsupportedOperationException("An error message must be provided.");
        }

        invalidNodes.add(new AbstractMap.SimpleEntry<>(invalidNode, helpfulMessage));
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
        // don't dispose of errorMessagePopover, as it is a window in its own right
        // it needs to decide if that is appropriate by itself
        errorMessagePopoverListener = null;
        saveButton = null;
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

    /**
     * Adds a save changes placebo button to the editor panes.
     * Will not add a new button if one already exists.
     */
    protected final void setupSaveChangesButton() {
        if (saveChangesButtonExists) {
            return; // prevent an existing button being added.
        }
        saveChangesButtonExists = true;
        saveButton = new MaterialDesignButton("Save Changes");
        final int pad = 5;
        saveButton.setPadding(new Insets(pad, 0, 0, 0));
        saveButton.setRippleColour(JavaFXHelpers.hex2RGB("#9CCC65"));
        bottomBar.getChildren().add(saveButton);
        HBox.setMargin(saveButton, new Insets(pad, 0, 0, pad));
    }

    /**
     * Gets the save changes button so that it can be externally manipulated.
     * @return the save changes button.
     */
    public final Button getSaveChangesButton() {
        return saveButton;
    }
}
