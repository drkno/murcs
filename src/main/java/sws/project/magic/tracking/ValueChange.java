package sws.project.magic.tracking;

import java.lang.reflect.Field;

/**
 * Tracks an individual change occurrence that can be undone/redone.
 */
public class ValueChange {
    private String changeDescription;
    private Object originalObject;
    private final FieldValuePair[] changedFieldValues;
    private boolean undone;

    /**
     * Instantiates a new TrackedChange that tracks an individual change occurrence.
     * @param originalObject The object that the change occurred on.
     * @param changedFieldValues The fields that had values changed were changed.
     * @param changeDescription Description of the changes made.
     */
    public ValueChange(Object originalObject, FieldValuePair[] changedFieldValues, String changeDescription) {
        this.originalObject = originalObject;
        this.changedFieldValues = changedFieldValues;
        this.undone = false;
        this.changeDescription = changeDescription;
    }

    /**
     * Gets the description of the change.
     * @return the description.
     */
    public String getDescription() {
        return changeDescription;
    }

    /**
     * Undoes this change.
     * @throws Exception if undo cannot be performed.
     */
    public void undo() throws Exception {
        if (undone) {
            throw new Exception("Cannot undo if already undone!");
        }
        undone = true;
        toggleValues();
    }

    /**
     * Redoes this change.
     * @throws Exception If this change has not been undone.
     */
    public void redo() throws Exception {
        if (!undone) {
            throw new Exception("Cannot redo if already redone!");
        }
        undone = false;
        toggleValues();
    }

    /**
     * Switches the values stored with those of the object.
     * @throws Exception if reflection fails - which can only occur if something drastically, horribly, horrendously
     * wrong happened at this point.
     */
    private void toggleValues() throws Exception {
        for (FieldValuePair changedFieldValue : changedFieldValues) {
            Field field = changedFieldValue.getField();
            Object previousValue = field.get(originalObject);
            field.set(originalObject, changedFieldValue.getValue());
            changedFieldValue.setValue(previousValue);
        }
    }

    /**
     * Gets the object that is affected by this change.
     * @return the object.
     */
    public Object getAffectedObject() {
        return originalObject;
    }

    /**
     * Gets the fields that are affected by this change and the associated values.
     * @return an array of affected fields.
     */
    public FieldValuePair[] getChangedFields() {
        return changedFieldValues;
    }

    @Override
    public String toString() {
        String resultString = "";
        for (FieldValuePair changedFieldValue : changedFieldValues) {
            resultString += changedFieldValue.toString() + "; ";
        }
        return resultString;
    }
}
