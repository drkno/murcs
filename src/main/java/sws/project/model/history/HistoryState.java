package sws.project.model.history;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class HistoryState {
    private Object origionalObject;
    private final ArrayList<Field> changedFields;
    private final ArrayList<Object> changedValues;
    private boolean undone;

    public HistoryState(Object origionalObject, ArrayList<Field> changedFields, ArrayList<Object> changedValues) {
        this.origionalObject = origionalObject;
        this.changedFields = changedFields;
        this.changedValues = changedValues;
        this.undone = false;
    }

    public String getDescription() {


    }

    public void undo() throws Exception {
        if (!undone) {
            throw new Exception("Cannot undo if already undone!");
        }
        undone = true;
        toggleValues();
    }

    public void redo() throws Exception {
        if (undone) {
            throw new Exception("Cannot redo if already redone!");
        }
        undone = false;
        toggleValues();
    }

    public void toggleValues() throws Exception {
        for (int i = 0; i < changedFields.size(); i++) {
            Field field = changedFields.get(i);
            Object previousValue = field.get(origionalObject);
            field.set(origionalObject, changedValues.get(i));
            changedValues.set(i, previousValue);
        }
    }
}
