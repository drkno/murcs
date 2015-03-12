package sws.project.model.tracking;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TrackedValue {

    private final ArrayList<Field> _fields;
    private Object[] _values;

    public TrackedValue(Object initialObject, ArrayList<Field> fields) throws Exception {
        _fields = fields;
        _values = new Object[fields.size()];
        difference(initialObject, null, false);
    }

    public TrackedChange difference(Object obj, String description, boolean initialSave) throws Exception {
        ArrayList<Field> changedFields = new ArrayList<>();
        ArrayList<Object> changedValues = new ArrayList<>();
        for (int i = 0; i < _fields.size(); i++) {
            Field field = _fields.get(i);
            field.setAccessible(true);
            Object value = field.get(obj);
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

    public void apply(TrackedChange state) {
        Field[] fields = state.getFields();
        Object[] values = state.getValues();

        for (int i = 0; i < fields.length; i++) {
            int index = _fields.indexOf(fields[i]);
            if (index >= 0) {
                _values[index] = values[i];
            }
        }
    }
}
