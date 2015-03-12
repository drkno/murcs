package sws.project.magic.fxml;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 *
 */
public abstract class BasicEditController<T> implements EditFormController<T> {
    protected ArrayList<Predicate<T>> validators = new ArrayList<>();
    protected ArrayList<ChangeListener<T>> changeListeners = new ArrayList<>();

    @Override
    public void addValidator(Predicate<T> predicate) {
        validators.add(predicate);
    }

    @Override
    public void addChangeListener(ChangeListener<T> listener) {
        changeListeners.add(listener);
    }

    /**
     * This is a default change listener for when our value is changed in the GUI
     * @return
     */
    protected ChangeListener<T> onChange(){
        return (observable, oldValue, newValue) -> {
            //Notify all the change listeners
            notifyChanged(observable, oldValue, newValue);
        };
    }

    /**
     * Notify all the change listeners that we've changed, if the new value is valid
     * @param observable The observable that has changed
     * @param oldValue The old value
     * @param newValue The new value
     */
    protected void notifyChanged(ObservableValue<? extends T> observable, T oldValue, T newValue){
        //If nothing has changed, there's no point in us doing anything is there?
        if (oldValue == newValue) return;

        //If our new value is not valid
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
     * Checks to see if a specified value is valid by our list of predicates
     * @param value The value to verify
     * @return Whether the value is valid
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
     * Called when the value in the GUI is changed to something Invalid
     */
    protected abstract void showValid();

    /**
     * Called when the value in the GUI is changed to something Valid
     */
    protected abstract void showInvalid();
}
