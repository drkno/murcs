package sws.murcs.magic.easyedit.fxml;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * A basic edit controller, providing methods of adding validators and
 * change listeners and notifying said listeners.
 *
 * Any FXML associated with this controller can optionally specify
 * an 'invalidNode' Node object, which can be of
 * any type. The invalid node will be hidden until the text in
 * 'numberText' is invalid whereupon it will be set visible till the node is
 * @param <T> The type of the class being edited.
 */
public abstract class BasicEditController<T> implements EditController<T> {
    protected ArrayList<Predicate<T>> validators = new ArrayList<>();
    protected ArrayList<ChangeListener<T>> changeListeners = new ArrayList<>();

    @FXML
    protected Node invalidNode;

    @Override
    public final void addValidator(final Predicate<T> predicate) {
        validators.add(predicate);
    }

    @Override
    public final void addChangeListener(final ChangeListener<T> listener) {
        changeListeners.add(listener);
    }

    /**
     * Notify all the change listeners that we've changed, if the new value is valid.
     * @param observable The observable that has changed
     * @param oldValue The old value
     * @param newValue The new value
     */
    protected final void notifyChanged(final ObservableValue<? extends T> observable, T oldValue, T newValue) {
        //If nothing has changed, there's no point in us doing anything is there?
        if (oldValue == newValue) {
            return;
        }

        //If our new value is not valid
        if (!isValid(newValue)) {
            //Show an invalid message and return
            showInvalid();
            return;
        }

        //If we have satisfied all the predicates, we must be valid!
        showValid();

        //Loop through the change listeners and notify them all
        for (ChangeListener<T> listener : changeListeners) {
            listener.changed(observable, oldValue, newValue);
        }
    }

    /**
     * Checks to see if a specified value is valid by our list of predicates.
     * @param value The value to verify
     * @return Whether the value is valid
     */
    protected final boolean isValid(T value) {
        //Check if we're valid. If any validator is false, we're invalid
        for (Predicate<T> predicate : validators) {
            if (!predicate.test(value)) {
                showInvalid();
                return false;
            }
        }
        return true;
    }

    /**
     * Called when the value in the GUI is changed to something Invalid.
     */
    protected final void showValid() {
        if (invalidNode == null) {
            return;
        }
        invalidNode.setVisible(false);
    }

    /**
     * Called when the value in the GUI is changed to something Valid.
     */
    protected final void showInvalid() {
        if (invalidNode == null) {
            return;
        }
        invalidNode.setVisible(true);
    }
}
