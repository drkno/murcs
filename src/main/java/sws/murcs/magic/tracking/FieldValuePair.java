package sws.murcs.magic.tracking;

import javafx.beans.Observable;
import sws.murcs.model.Model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Manages the tracking of an object and its associated value.
 * This includes operations to get the objects current value
 * and to restore a previous saved value.
 */
public class FieldValuePair {
    /**
     * Field that this FieldValuePair represents.
     */
    private Field field;
    /**
     * Value that this FieldValuePair represents.
     */
    private Object value;
    /**
     * TrackableObject that this FieldValuePair holds the value/field of.
     */
    private TrackableObject trackableObject;

    private FieldValuePair parent;

    /**
     * Creates a new field value pair.
     * @param objectField field to use.
     * @param source object to get value from.
     * @throws Exception when source does not have the field specified.
     */
    public FieldValuePair(final Field objectField, final TrackableObject source) throws Exception {
        this.field = objectField;
        trackableObject = source;
        value = getValueFromObject(source, objectField);
    }

    private FieldValuePair(Field field, Object objectValue, TrackableObject trackableObject, FieldValuePair parent) {
        this.field = field;
        this.value = objectValue;
        this.trackableObject = trackableObject;
        this.parent = parent;
    }

    /**
     * Restores the saved value to the object.
     * @throws Exception if something goes wrong.
     */
    public final void restoreValue() throws Exception {
        if (value instanceof Collection) {
            Collection collection = (Collection) value;
            if (value instanceof Observable) {
                // If Observable, changes need to be done through the add/remove
                // methods so any listeners get correctly fired
                Collection currentCollection = (Collection) field.get(trackableObject);
                Set add = new HashSet<>(collection);
                add.removeAll(currentCollection);
                Set remove = new HashSet<>(currentCollection);
                remove.removeAll(collection);
                currentCollection.removeAll(remove);
                currentCollection.addAll(add);
            }
            else {
                // Otherwise we need to shallow-copy the Collection. Using the existing
                // Collection in the FieldValuePair would result in pass-by-reference issues
                Object[] constructorArgs = {collection};
                Class clazz = value.getClass();
                Constructor constructor = clazz.getConstructor(Collection.class);
                Collection newCollection = (Collection) constructor.newInstance(constructorArgs);
                field.set(trackableObject, newCollection);
            }

        }
        else {
            field.set(trackableObject, value);

            /* fixme: todo: this is a workaround for refreshing the shortName in the side list
           if anything else is ever shown in that list this should be removed. */
            if (field.getName().equals("shortName")) {
                ((Model) trackableObject).getShortNameProperty().notifyChanged();
            }
        }
        parent.value = value;
    }

    /**
     * Gets the stored field value.
     * @return the field.
     */
    public final Field getField() {
        return field;
    }

    /**
     * Sets the stored field value.
     * @param newField new field.
     */
    protected final void setField(final Field newField) {
        this.field = newField;
    }

    /**
     * Gets the value of the field.
     * @return value of the field.
     */
    public final Object getValue() {
        return value;
    }

    /**
     * Sets the value of the field (in this representation).
     * @param newValue new value to set.
     */
    protected final void setValue(final Object newValue) {
        this.value = newValue;
    }

    /**
     * Gets a string representation of this object.
     * @return string representation.
     */
    @Override
    public final String toString() {
        if (value == null) {
            return field.getName() + ": " + "null";
        }
        else {
            return field.getName() + ": " + value.toString();
        }
    }

    /**
     * Checks if this FieldValuePair uses the same field as another.
     * @param other FieldValuePair to check.
     * @return true if this FieldValuePair uses the same field as other.
     */
    public final boolean equals(final FieldValuePair other) {
        return other.field.equals(field)
                && other.trackableObject.equals(trackableObject)
                && Objects.equals(other.value, value);
    }

    /**
     * Implementation of hashCode(). Standard hashCode is sufficient for
     * indexing this method.
     * @return a unique hash code.
     */
    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    /**
     * Gets the value of an object.
     * @param object Object to retrieve value from.
     * @param field Field to get value from.
     * @return the value.
     * @throws Exception if the field does not exist in object.
     */
    private static Object getValueFromObject(final Object object, final Field field) throws Exception {
        field.setAccessible(true);
        Object value = field.get(object);
        if (value instanceof Collection) {
            Class<?> clazz = value.getClass();
            Constructor<?> ctor = clazz.getConstructor(Collection.class);
            value = ctor.newInstance(value);
        }
        else if (value instanceof Map) {
            Class<?> clazz = value.getClass();
            Constructor<?> ctor = clazz.getConstructor(Map.class);
            value = ctor.newInstance(value);
        }
        return value;
    }

    public final TrackableObject getObject() {
        return trackableObject;
    }

    public FieldValuePair[] update() {
        try {
            Object currentValue = getValueFromObject(trackableObject, field);
            if (Objects.equals(value, currentValue)) {
                return null;
            }
            Object oldValue = value;
            value = currentValue;
            return new FieldValuePair[] {
                new FieldValuePair(field, oldValue, trackableObject, this),
                new FieldValuePair(field, currentValue, trackableObject, this)
            };
        }
        catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
