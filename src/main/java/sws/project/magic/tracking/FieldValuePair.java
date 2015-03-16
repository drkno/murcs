package sws.project.magic.tracking;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * Tracks an objects field and its associated value.
 */
public class FieldValuePair {
    private Field _field;
    private Object _value;

    /**
     * Creates a new field value pair.
     * @param field field to use.
     * @param value value to use.
     */
    public FieldValuePair(Field field, Object value) {
        _field = field;
        _value = value;
    }

    /**
     * Creates a new field value pair.
     * @param valueSource source object to retrieve value from.
     * @param field field to get value from.
     * @throws Exception if the value does not exist in the provided object.
     */
    public FieldValuePair(Object valueSource, Field field) throws Exception {
        _field = field;
        _value = getValueFromObject(valueSource, field);
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
    public void setField(Field field) {
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
    public void setValue(Object value) {
        this._value = value;
    }

    /**
     * Gets a string representation of this object.
     * @return string representation.
     */
    @Override
    public String toString() {
        return _field.getName() + ": " + _value.toString();
    }

    /**
     * Checks if this FieldValuePair uses the same field as another.
     * @param other object to check.
     * @return true if this FieldValuePair uses the same field as other.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof FieldValuePair && ((FieldValuePair)other)._field.equals(_field);
    }

    /**
     * Gets the value of an object.
     * @param object Object to retrieve value from.
     * @param field Field to get value from.
     * @return the value.
     * @throws Exception if the field does not exist in object.
     */
    public static Object getValueFromObject(Object object, Field field) throws Exception {
        field.setAccessible(true);
        Object value = field.get(object);
        if (value instanceof Collection) {
            Class<?> clazz = value.getClass();
            Constructor<?> ctor = clazz.getConstructor(Collection.class);
            value = ctor.newInstance(new Object[] { value });
        }
        else if (value instanceof Map) {
            Class<?> clazz = value.getClass();
            Constructor<?> ctor = clazz.getConstructor(Map.class);
            value = ctor.newInstance(new Object[] { value });
        }
        return value;
    }
}
