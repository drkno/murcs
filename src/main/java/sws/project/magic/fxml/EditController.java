package sws.project.magic.fxml;

import javafx.beans.value.ChangeListener;

import java.util.function.Predicate;

/**
 * A common interface for the controllers of FXML GUI forms
 */
public interface EditController<T> {
    /**
     * Set the title of the edit controller
     * @param title The new title
     */
    void setTitle(String title);

    /**
     * Sets the editPaneGenerator of the edit controller
     * @param value The current editPaneGenerator of the edit controller
     */
    void setValue(T value);

    /**
     * Adds a validator to the form. If any predicate fails then
     * the change listener will not be fired.
     * @param predicate
     */
    void addValidator(Predicate<T> predicate);

    /**
     * Adds a new change listener to the form which will
     * be fired the next time the form has a valid editPaneGenerator
     * @param listener The change listener to add
     */
    void addChangeListener(ChangeListener<T> listener);

    /**
     * An array of the types supported by this edit controller
     * @return The supported types.
     */
    Class[] supportedTypes();
}
