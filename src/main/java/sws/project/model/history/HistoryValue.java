package sws.project.model.history;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class HistoryValue {

    private final ArrayList<Field> _fields;
    private Object[] _values;

    public HistoryValue(Object initialObject, ArrayList<Field> fields) throws Exception {
        _fields = fields;
        _values = new Object[fields.size()];
        GetDifference(initialObject);
    }

    public HistoryState GetDifference(Object obj) throws Exception {
        ArrayList<Field> changedFields = new ArrayList<>();
        ArrayList<Object> changedValues = new ArrayList<>();
        for (int i = 0; i < _fields.size(); i++) {
            Field field = _fields.get(i);
            Object value = field.get(obj);
            if (!value.equals(_values[i])) {
                changedFields.add(field);
                changedValues.add(value);
                _values[i] = value;
            }
        }
        return new HistoryState(changedFields, changedValues);;
    }
}
