package sws.murcs.model.observable;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.Observable;
import sws.murcs.model.Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Observable ArrayList type with custom callback property for object toStrings().
 * Used to ensure changes to objects are instantly reflected in listeners.
 * @param <T> type of the list, expected to extend Model.
 */
public class ModelObservableArrayList<T extends Model> extends ObservableListWrapper<T> implements Serializable {
    private ArrayList<T> underlyingList;

    /**
     * Creates a new empty ModelObservableArrayList with the default callback.
     */
    public ModelObservableArrayList() {
        super(new ArrayList(), param -> new Observable[] {param.getToStringProperty()});
    }

    /**
     * Clones an existing collection into this ModelObservableArrayList with the default callback.
     * Note: used in Undo/Redo, it is important it has this method signature.
     * @param c collection to clone.
     */
    public ModelObservableArrayList(Collection c) {
        super(new ArrayList<>(c), param -> new Observable[] {param.getToStringProperty()});
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        ArrayList<T> arrayList = null;
        try {
            // fixme: hack alert. reason: Java wont let you assign a field before calling super()
            Field f = ObservableListWrapper.class.getDeclaredField("backingList");
            f.setAccessible(true);
            arrayList = (ArrayList<T>)f.get(this);
            out.writeObject(arrayList);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            System.out.println(1);
            Field f = ObservableListWrapper.class.getDeclaredField("backingList");
            System.out.println(2);
            f.setAccessible(true);
            System.out.println(3);
            f.set(this, in.readObject());
            System.out.println(4);
        }
        catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            throw new IOException(e);
        }
    }
}
