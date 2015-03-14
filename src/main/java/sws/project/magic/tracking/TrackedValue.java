package sws.project.magic.tracking;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Tracks the current value of an object.
 */
public class TrackedValue {

    private final ArrayList<Field> _fields;
    private Object[] _values;

    /**
     * Instantiates a new tracked object.
     * @param initialObject The object to track.
     * @param fields Fields to track.
     * @throws Exception Exception if retrieving values failed due to unknown type.
     */
    public TrackedValue(Object initialObject, ArrayList<Field> fields) throws Exception {
        _fields = fields;
        _values = new Object[fields.size()];
        difference(initialObject, null, false);
    }

    /**
     * Determines the difference between the current object representation and the stored representation.
     * @param obj Object to get values from
     * @param description Description of the change
     * @param initialSave Save all fields rather than just changed.
     * @return A representation of the difference between these states
     * @throws Exception If retrieving the values of the current object state is not possible.
     */
    public TrackedChange difference(Object obj, String description, boolean initialSave) throws Exception {
        ArrayList<Field> changedFields = new ArrayList<>();
        ArrayList<Object> changedValues = new ArrayList<>();
        for (int i = 0; i < _fields.size(); i++) {
            Field field = _fields.get(i);
            field.setAccessible(true);
            Object value = field.get(obj);
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

            if (initialSave || value == null && _values[i] == null || !value.equals(_values[i])) {
                changedFields.add(field);
                changedValues.add(value);
                _values[i] = value;
            }
        }

        Field[] fields = new Field[_fields.size()];
        changedFields.toArray(fields);
        return new TrackedChange(obj, fields, changedValues.toArray(), description);
    }

    /**
     * Determines what the previous state was if it was lost.
     * @param original The object that the original value applied to.
     * @param previous The change before the previous state.
     * @param description The description of the change.
     * @return A representation of the previous state.
     */
    public TrackedChange dumpChange(Object original, TrackedChange previous, String description) {
        Field[] previousFields = previous.getFields();
        Object[] previousValues = previous.getValues();

        ArrayList<Field> changedFields = new ArrayList<>();
        ArrayList<Object> changedValues = new ArrayList<>();

        for (int i = 0; i < previousFields.length; i++) {
            int j;
            for (j = 0; j < _fields.size(); j++) {
                if (_fields.get(j).equals(previousFields[i])) break;
            }
            if (j >= _fields.size()) continue;
            if (previousValues[i] == null && _values[i] == null || !previousValues[i].equals(_values[i])) {
                changedFields.add(previousFields[i]);
                changedValues.add(_values[i]);
            }
        }

        previousFields = new Field[changedFields.size()];
        changedFields.toArray(previousFields);
        return new TrackedChange(original, previousFields, changedValues.toArray(), description);
    }

    /**
     * Applies the changes in a TrackedChange to this historical view of the object.
     * @param state new state of the object.
     */
    public void apply(TrackedChange state) {
        Field[] fields = state.getFields();
        Object[] values = state.getValues();

        for (int i = 0; i < fields.length; i++) {
            int index = _fields.indexOf(fields[i]);
            if (index >= 0) {
                _values[index] = values[i];
            }
            else {
                System.err.println("Could not update tracker history - this will only have affect" +
                        " if you attempt to do (not redo) after undoing.");
            }
        }
    }
}
