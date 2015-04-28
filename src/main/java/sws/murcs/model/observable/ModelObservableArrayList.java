package sws.murcs.model.observable;

import com.sun.javafx.collections.ObservableListWrapper;
import sws.murcs.model.Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Observable ArrayList type with custom callback property for object toStrings().
 * Used to ensure changes to objects are instantly reflected in listeners.
 * @param <T> type of the list, expected to extend Model.
 */
public class ModelObservableArrayList<T extends Model> extends ObservableArrayList<T> implements Serializable {
    private ArrayList<T> backingField;

    /**
     * Work around for Observable array lists not being serializable.
     * This method serializes a list.
     * @param out the object stream to write to.
     * @throws IOException if serialization fails.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        try {
            // fixme: hack alert. reason: Java wont let you assign a field before calling super()
            Field f = ObservableListWrapper.class.getDeclaredField("backingList");
            f.setAccessible(true);
            backingField = (ArrayList<T>)f.get(this);
            out.defaultWriteObject();
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }
    
    /**
     * Work around for Observable array lists not being serializable.
     * This method reads the array from an object stream.
     * @param in object stream to read list from.
     * @throws IOException if deserialization failed.
     */
    private void readObject(ObjectInputStream in) throws IOException {
        try {
            // fixme: hack alert. reason: Java wont let you assign a field before calling super()
            in.defaultReadObject();
            Field f = ObservableListWrapper.class.getDeclaredField("backingList");
            f.setAccessible(true);
            f.set(this, backingField);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    //Todo activate for Display list order I,
    // Todo also the current implementation does not resort on editing the shortname of an object
//    @Override
//    public boolean add(T object) {
//        boolean result = super.add(object);
//        sort(new Comparator<T>() {
//            @Override
//            public int compare(T o1, T o2) {
//                String shortname1 = o1.getShortName().toLowerCase();
//                String shortname2 = o2.getShortName().toLowerCase();
//                return shortname1.compareTo(shortname2);
//            }
//        });
//        return result;
//    }
}
