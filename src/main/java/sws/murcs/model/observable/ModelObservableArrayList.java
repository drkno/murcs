package sws.murcs.model.observable;

import sws.murcs.model.Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Observable ArrayList type with custom callback property for object toStrings().
 * Used to ensure changes to objects are instantly reflected in listeners.
 * @param <T> type of the list, expected to extend Model.
 */
public class ModelObservableArrayList<T extends Model>
        extends ObservableArrayList<T> implements Serializable, Comparator<T> {

    /**
     * Serializable backing field.
     * Required because JavaFX observable lists are not serializable.
     */
    private List<T> backingField;

    /**
     * Custom comparator so that sorting the list uses a different ordering.
     */
    private transient Comparator<T> comparator;

    /**
     * Default constructor.
     */
    public ModelObservableArrayList() {
        super();
    }

    /**
     * Clones from existing collection.
     * @param c original collection.
     */
    public ModelObservableArrayList(final Collection c) {
        super(c);
    }

    /**
     * Work around for Observable array lists not being serializable.
     * This method serializes a list.
     * @param out the object stream to write to.
     * @throws IOException if serialization fails.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        try {
            backingField = new ArrayList<>(this);
            out.defaultWriteObject();
            backingField = null; // prevent keeping duplicate
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
    private void readObject(final ObjectInputStream in) throws IOException {
        try {
            in.defaultReadObject();
            addAll(backingField);
            backingField = null; // prevent keeping duplicate
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public final int compare(final T model1, final T model2) {
        if (comparator != null) {
            return comparator.compare(model1, model2);
        }
        String shortName1 = model1.getShortName();
        String shortName2 = model2.getShortName();
        return shortName1.compareToIgnoreCase(shortName2);
    }

    /**
     * Sets a custom comparator so that the list will be sorted by
     * default in a custom ordering.
     * @param newComparator comparator to use, set to null to revert
     *                      to the default comparator.
     */
    public final void setComparator(final Comparator<T> newComparator) {
        comparator = newComparator;
    }
}
