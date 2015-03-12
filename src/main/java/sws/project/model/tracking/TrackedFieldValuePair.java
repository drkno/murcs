package sws.project.model.tracking;

import java.lang.reflect.Field;

/**
 * A Field and its associated value to be tracked.
 */
public class TrackedFieldValuePair {
    /**
     * Generates a new TrackedFieldValuePair for tracking the value of a field.
     * @param field Field for which the value will be tracked.
     * @param value Value of the field.
     */
    public TrackedFieldValuePair(Field field, Object value) {
        this.field = field;
        this.value = value;
    }

    /**
     * Gets the field value of this TrackedFieldValuePair.
     * @return the field.
     */
    public Field getField() {
        return field;
    }

    /**
     * Sets the field value of this TrackedFieldValuePair.
     * @param field the new field.
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * Gets the value of this TrackedFieldValuePair.
     * @return the value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value of this TrackedFieldValuePair.
     * @param value new value.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    private Field field;
    private Object value;
}
