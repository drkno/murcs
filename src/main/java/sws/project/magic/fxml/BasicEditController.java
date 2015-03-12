package sws.project.magic.fxml;

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
 * any type. The invalid node will be hidden until the text in 'numberText' is invalid
 * whereupon it will be set visible till the node is
 */
public abstract class BasicEditController<T> implements EditController<T> {
    protected ArrayList<Predicate<T>> validators = new ArrayList<>();
    protected ArrayList<ChangeListener<T>> changeListeners = new ArrayList<>();

    @FXML
    protected Node invalidNode;

    @Override
    public void addValidator(Predicate<T> predicate) {
        validators.add(predicate);
    }

    @Override
    public void addChangeListener(ChangeListener<T> listener) {
        changeListeners.add(listener);
    }

    /**
     * Notify all the change listeners that we've changed, if the new editPaneGenerator is valid
     * @param observable The observable that has changed
     * @param oldValue The old editPaneGenerator
     * @param newValue The new editPaneGenerator
     */
    protected void notifyChanged(ObservableValue<? extends T> observable, T oldValue, T newValue){
        //If nothing has changed, there's no point in us doing anything is there?
        if (oldValue == newValue) return;

        //If our new editPaneGenerator is not valid
        if (!isValid(newValue)) {
            //Show an invalid message and return
            showInvalid();
            return;
        }

        //If we have satisfied all the predicates, we must be valid!
        showValid();

        //Loop through the change listeners and notify them all
        for (ChangeListener<T> listener : changeListeners){
            listener.changed(observable, oldValue, newValue);
        }
    }

    /**
     * Checks to see if a specified editPaneGenerator is valid by our list of predicates
     * @param value The editPaneGenerator to verify
     * @return Whether the editPaneGenerator is valid
     */
    protected boolean isValid(T value){
        //Check if we're valid. If any validator is false, we're invalid
        for (Predicate<T> predicate : validators){
            if (!predicate.test(value)) {
                showInvalid();
                return false;
            }
        }
        return true;
    }

    /**
     * Called when the editPaneGenerator in the GUI is changed to something Invalid
     */
    protected void showValid(){
        if (invalidNode == null) return;
        invalidNode.setVisible(false);
    }

    /**
     * Called when the editPaneGenerator in the GUI is changed to something Valid
     */
    protected void showInvalid(){
        if (invalidNode == null) return;
        invalidNode.setVisible(true);
    }
}
