package sws.murcs.model.observable;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.Observable;
import sws.murcs.model.Model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Observable ArrayList type with custom callback property for object toStrings().
 * Used to ensure changes to objects are instantly reflected in listeners.
 * @param <T> type of the list, expected to extend Model.
 */
public class ObservableArrayList<T extends Model> extends ObservableListWrapper<T> {

    /**
     * Creates a new empty ModelObservableArrayList with the default callback.
     */
    public ObservableArrayList() {
        super(new ArrayList<>(), param -> new Observable[] {param.getShortNameProperty()});
    }

    /**
     * Clones an existing collection into this ModelObservableArrayList with the default callback.
     * Note: used in Undo/Redo, it is important it has this method signature.
     * @param c collection to clone.
     */
    public ObservableArrayList(final Collection<T> c) {
        super(new ArrayList<>(c), param -> new Observable[]{param.getShortNameProperty()});
    }
}
