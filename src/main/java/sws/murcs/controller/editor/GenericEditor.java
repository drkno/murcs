package sws.murcs.controller.editor;

import com.sun.javafx.css.StyleManager;
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
import sws.murcs.controller.pipes.FormErrors;
import sws.murcs.controller.pipes.Navigable;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Model;
import sws.murcs.view.App;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A generic class for making editing easier.
 * @param <T> The type of the editor (linked to the model)
 */
public abstract class GenericEditor<T extends Model> implements UndoRedoChangeListener, FormErrors {
    /**
     * The name for the default section of the form.
     */
    private final String defaultSectionName = "default";

    /**
     * A method of routing navigation events to the controller that owns
     * this editor.
     */
    private Navigable navigationManager;

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
     * Whether or not the editor is loaded.
     */
    protected boolean isLoaded;

    /**
     * Stores if a save changes button exists, preventing a new button being created
     * if one has already been created.
     */
    private boolean saveChangesButtonExists;

    /**
     * All the invalid sections in the form.
     */
    private Map<String, Collection<Map.Entry<Node, String>>> invalidNodes = new HashMap<>();

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
    public final void setModel(final T pModel) {
        if (pModel != null) {
            isLoaded = false;
            model = pModel;
            setIsCreationWindow(pModel.getShortName() == null);
            if (bottomBar != null) {
                setupSaveChangesButton();
            }
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
    public void undoRedoNotification(final ChangeState param) {
        if (param == ChangeState.Remake || param == ChangeState.Revert) {
            synchronized (StyleManager.getInstance()) {
                loadObject();
            }
        }
    }

    /**
     * Highlights errors on the form.
     */
    private void showErrors() {
        showErrors(defaultSectionName);
    }

    /**
     * Highlights errors on the form.
     * @param sectionName The name of the section to show the errors on
     */
    private void showErrors(final String sectionName) {
        ensureSectionExists(sectionName);

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
        Collection<Map.Entry<Node, String>> invalidInSection = invalidNodes.get(sectionName);
        for (Map.Entry<Node, String> entry : invalidInSection) {
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
     * Clears the errors on the default section.
     */
    public final void clearErrors() {
        clearErrors(defaultSectionName);
    }

    /**
     * Clears the errors on the form.
     * @param sectionName The name of the section to clear the errors on
     */
    public void clearErrors(final String sectionName) {
        ensureSectionExists(sectionName);

        boolean hideError = true;
        Collection<Map.Entry<Node, String>> invalidInSection = invalidNodes.get(sectionName);

        synchronized (StyleManager.getInstance()) {
            for (Map.Entry<Node, String> entry : invalidInSection) {
                entry.getKey().getStyleClass().removeAll(Collections.singleton("error"));
                entry.getKey().focusedProperty().removeListener(errorMessagePopoverListener);
                if (entry.getKey().isFocused()) {
                    hideError = false;
                }
            }
            invalidInSection.clear();
            if (hideError && errorMessagePopover != null) {
                errorMessagePopover.hide();
            }
            labelErrorMessage.setText("");
        }
    }

    /**
     * Ensures that a section exists on a form (if it doesn't it
     * will be added to the sections list).
     * @param sectionName The name of the section
     */
    private void ensureSectionExists(final String sectionName) {
        if (!invalidNodes.containsKey(sectionName)) {
            invalidNodes.put(sectionName, new ArrayList<>());
        }
    }

    /**
     * Saves the changes on the current form.
     */
    public final void saveChanges() {
        clearErrors();
        saveChangesAndErrors();
    }

    /**
     * Adds an error to the form and highlights the node that caused it.
     * @param invalidNode The node that has the problem
     * @param helpfulMessage A helpful message describing the problem.
     * @throws UnsupportedOperationException when an unhelpful error message is provided or
     * when no node is provided to work with.
     */
    public final void addFormError(final Node invalidNode, final String helpfulMessage) {
        addFormError(defaultSectionName, invalidNode, helpfulMessage);
    }

    /**
     * Adds an error to the form and highlights the node that caused it.
     * @param invalidNode The node that has the problem
     * @param helpfulMessage A helpful message describing the problem.
     * @param sectionName The name of the section to add the error to.
     * @throws UnsupportedOperationException when an unhelpful error message is provided or
     * when no node is provided to work with.
     */
    public final void addFormError(final String sectionName, final Node invalidNode, final String helpfulMessage) {
        ensureSectionExists(sectionName);

        if (invalidNode == null) {
            throw new UnsupportedOperationException("A node must be provided.");
        }

        if (helpfulMessage == null) {
            throw new UnsupportedOperationException("An error message must be provided.");
        }
        Collection<Map.Entry<Node, String>> invalidInSection = invalidNodes.get(sectionName);
        invalidInSection.add(new AbstractMap.SimpleEntry<>(invalidNode, helpfulMessage));
        synchronized (StyleManager.class) {
            showErrors(sectionName);
        }
    }

    /**
     * Loads the current model object into the form.
     */
    public abstract void loadObject();

    /**
     * Cleans up event handlers and stuff. (Please ignore the CheckStyle error here, it's wrong).
     */
    @SuppressWarnings("checkstyle:designforextension")
    public synchronized void dispose() {
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
    public synchronized void setupSaveChangesButton() {
        if (saveChangesButtonExists || getIsCreationWindow()) {
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

    /**
     * Is the editor loaded.
     * @return if the editor is loaded.
     */
    public final boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Gets the navigationManager for this controller.
     * @return The navigationManager
     */
    public Navigable getNavigationManager() {
        return navigationManager;
    }

    /**
     * Sets the navigationManager for this controller.
     * @param navigationManager The navigationManager.
     */
    public void setNavigationManager(final Navigable navigationManager) {
        this.navigationManager = navigationManager;
    }

    /**
     * Gets Dion's window from a node. This method is O(N) (where N is the number of windows
     * you have open). This method will return null if it can't find a Window.
     * @param node The node to try and find the window for.
     * @return The Window that is used in the WindowManager.
     */
    protected Window getWindowFromNode(final Node node) {
        return App.getWindowManager()
                .getAllWindows()
                .stream()
                .filter(w -> w.getStage() == node.getScene().getWindow())
                .findFirst()
                .orElse(null);
    }
}
