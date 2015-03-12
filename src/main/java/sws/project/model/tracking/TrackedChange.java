package sws.project.model.tracking;

import java.lang.reflect.Field;

public class TrackedChange {
    private String changeDescription;
    private Object origionalObject;
    private final Field[] changedFields;
    private final Object[] changedValues;
    private boolean undone;

    public TrackedChange(Object origionalObject, Field[] changedFields, Object[] changedValues, String changeDescription) {
        this.origionalObject = origionalObject;
        this.changedFields =  changedFields;
        this.changedValues = changedValues;
        this.undone = false;
        this.changeDescription = changeDescription;
    }

    public String getDescription() {
        return changeDescription;
    }

    public void undo() throws Exception {
        if (undone) {
            throw new Exception("Cannot undo if already undone!");
        }
        undone = true;
        toggleValues();
    }

    public void redo() throws Exception {
        if (!undone) {
            throw new Exception("Cannot redo if already redone!");
        }
        undone = false;
        toggleValues();
    }

    public void toggleValues() throws Exception {
        for (int i = 0; i < changedFields.length; i++) {
            Field field = changedFields[i];
            Object previousValue = field.get(origionalObject);
            field.set(origionalObject, changedValues[i]);
            changedValues[i] = previousValue;
        }
    }

    public Object getAffectedObject() {
        return origionalObject;
    }

    public Field[] getFields() {
        return changedFields;
    }

    public Object[] getValues() {
        return changedValues;
    }
}
