package sws.murcs.magic.tracking;

import javafx.beans.Observable;
import sws.murcs.debug.errorreporting.ErrorReporter;
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

    /**
     * Field value pair that created this one.
     * Used for propagating updates.
     */
    private FieldValuePair parent;

    /**
     * This FVP represents an old value.
     */
    private boolean representsOldValue;

    /**
     * Creates a new field value pair.
     * @param objectField field to use.
     * @param source object to get value from.
     * @throws Exception when source does not have the field specified.
     */
    protected FieldValuePair(final Field objectField, final TrackableObject source) throws Exception {
        this.field = objectField;
        trackableObject = source;
        value = getValueFromObject(source, objectField);
    }

    /**
     * Creates a new field value pair.
     * @param field field to use.
     * @param objectValue value of the object.
     * @param trackableObject object to use.
     * @param parent field value pair that created this one.
     * @param isOldValue sets if this represents an old value.
     */
    private FieldValuePair(final Field field, final Object objectValue,
                           final TrackableObject trackableObject, final FieldValuePair parent,
                           final boolean isOldValue) {
        this.field = field;
        this.value = objectValue;
        this.trackableObject = trackableObject;
        this.parent = parent;
        this.representsOldValue = isOldValue;
    }

    /**
     * Restores the saved value to the object.
     * @throws Exception if something goes wrong.
     */
    protected final void restoreValue() throws Exception {
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
    protected final Field getField() {
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
    protected final Object getValue() {
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

    /**
     * Gets the object that this field value pair represents the value of.
     * @return the object that this FVP represents.
     */
    protected final TrackableObject getObject() {
        return trackableObject;
    }

    /**
     * Performs an update of this FVP to the latest model value. Will also update
     * the creator FVP if applicable.
     * @return null if this FVP is already up to date.
     * Otherwise will return an array, 2 elements in size with the following values:
     * [0] - a FieldValuePair representing the old value of the object.
     * [1] - a FieldValuePair representing the new value of the object.
     */
    protected FieldValuePair[] update() {
        Object currentValue = null;
        try {
            currentValue = getValueFromObject(trackableObject, field);
        }
        catch (Exception e) {
            ErrorReporter.get().reportError(e, "Could not get value from object even though we have successfully "
                    + "done so before.");
        }

        if (Objects.equals(value, currentValue)) {
            return null;
        }
        Object oldValue = value;
        value = currentValue;
        return new FieldValuePair[] {
            new FieldValuePair(field, oldValue, trackableObject, this, true),
            new FieldValuePair(field, currentValue, trackableObject, this, false)
        };
    }

    /**
     * This FVP represents the old value of the object.
     * @return true if it does, false otherwise.
     */
    protected boolean isOldValue() {
        return representsOldValue;
    }
}
