package sws.murcs.magic.tracking;

import sws.murcs.model.Model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Tracks an objects field and its associated value.
 */
public class FieldValuePair {
    private Field _field;
    private Object _value;
    private TrackableObject _trackableObject;

    /**
     * Creates a new field value pair.
     * @param field field to use.
     * @param source object to get value from.
     * @throws Exception when source does not have the field specified.
     */
    public FieldValuePair(Field field, TrackableObject source) throws Exception {
        _field = field;
        _trackableObject = source;
        _value = getValueFromObject(source, field);
    }

    /**
     * Restores the saved value to the object.
     * @throws Exception if something goes wrong.
     */
    public void restoreValue() throws Exception {
        if (_value instanceof Collection) {
            Collection list1 = (Collection) _value;
            Collection list2 = (Collection) _field.get(_trackableObject);

            Set add = new HashSet<>(list1);
            add.removeAll(list2);

            Set remove = new HashSet<>(list2);
            remove.removeAll(list1);

            list2.removeAll(remove);
            list2.addAll(add);
        }
        else {
            _field.set(_trackableObject, _value);

            /* fixme: todo: this is a workaround for refreshing the shortName in the side list
           if anything else is ever shown in that list this should be removed. */
            if (_field.getName().equals("shortName")) {
                ((Model)_trackableObject).getShortNameProperty().notifyChanged();
            }
        }
    }

    /**
     * Gets the stored field value.
     * @return the field.
     */
    public Field getField() {
        return _field;
    }

    /**
     * Sets the stored field value.
     * @param field new field.
     */
    protected void setField(Field field) {
        this._field = field;
    }

    /**
     * Gets the value of the field.
     * @return value of the field.
     */
    public Object getValue() {
        return _value;
    }

    /**
     * Sets the value of the field (in this representation).
     * @param value new value to set.
     */
    protected void setValue(Object value) {
        this._value = value;
    }

    /**
     * Gets a string representation of this object.
     * @return string representation.
     */
    @Override
    public String toString() {
        return _field.getName() + ": " + (_value == null ? "null" : _value.toString());
    }

    /**
     * Checks if this FieldValuePair uses the same field as another.
     * @param other FieldValuePair to check.
     * @return true if this FieldValuePair uses the same field as other.
     */
    public boolean equals(FieldValuePair other) {
        return other._field.equals(_field)
                && other._trackableObject.equals(_trackableObject)
                && Objects.equals(other._value, _value);
    }

    /**
     * Gets the value of an object.
     * @param object Object to retrieve value from.
     * @param field Field to get value from.
     * @return the value.
     * @throws Exception if the field does not exist in object.
     */
    private static Object getValueFromObject(Object object, Field field) throws Exception {
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
}
