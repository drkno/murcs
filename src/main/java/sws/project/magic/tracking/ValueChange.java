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
    protected ValueChange(Object originalObject, FieldValuePair[] changedFieldValues, String changeDescription) {
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
    protected void undo() throws Exception {
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
    protected void redo() throws Exception {
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

    /**
     * Detects if a change has actually been made.
     * @return true if a changes to values have been made, false otherwise.
     */
    public boolean isDifferent() {
        return changedFieldValues.length != 0;
    }

    @Override
    public String toString() {
        String resultString = "";
        for (FieldValuePair changedFieldValue : changedFieldValues) {
            resultString += changedFieldValue.toString() + "; ";
        }
        return resultString;
    }

    /**
     * Determines if a change is on the same fields of an object.
     * @param change change to compare.
     * @param affectedObject object to compare.
     * @return true if are on the same fields of an object, false otherwise
     */
    public boolean isEquivalent(ValueChange change, Object affectedObject) {
        boolean areSame = affectedObject == originalObject;
        areSame = areSame && change.getChangedFields().length == changedFieldValues.length;
        areSame = areSame && change.getDescription().equals(changeDescription);
        if (!areSame) return false;
        FieldValuePair[] fields = change.getChangedFields();
        for (int i = 0; i < changedFieldValues.length; i++) {
            if (!changedFieldValues[i].equals(fields[i])) return false;
        }
        return true;
    }

    /**
     * Merges the changes from a ValueChange into this one.
     * @param value value change to merge in.
     */
    public void assimilate(ValueChange value) {
        FieldValuePair[] fields = value.getChangedFields();
        for (int i = 0; i < changedFieldValues.length; i++) {
            changedFieldValues[i] = fields[i];
        }
    }
}
