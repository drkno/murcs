package sws.project.magic.tracking;

import java.lang.reflect.Field;

/**
 * Tracks an individual change occurence that can be undone/redone.
 */
public class TrackedChange {
    private String changeDescription;
    private Object origionalObject;
    private final Field[] changedFields;
    private final Object[] changedValues;
    private boolean undone;

    /**
     * Instantiates a new TrackedChange that tracks an individual change occurence.
     * @param origionalObject The object that the change occured on.
     * @param changedFields The fields that were changed.
     * @param changedValues The values that were changed.
     * @param changeDescription Description of the changes made.
     */
    public TrackedChange(Object origionalObject, Field[] changedFields, Object[] changedValues, String changeDescription) {
        this.origionalObject = origionalObject;
        this.changedFields =  changedFields;
        this.changedValues = changedValues;
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
     * @throws Exception if reflection fails - which can only occur if something drastically, horribly, horrendusly
     * wrong happened at this point.
     */
    private void toggleValues() throws Exception {
        for (int i = 0; i < changedFields.length; i++) {
            Field field = changedFields[i];
            Object previousValue = field.get(origionalObject);
            field.set(origionalObject, changedValues[i]);
            changedValues[i] = previousValue;
        }
    }

    /**
     * Gets the object that is affected by this change.
     * @return the object.
     */
    public Object getAffectedObject() {
        return origionalObject;
    }

    /**
     * Gets the fields that are affected by this change.
     * @return an array of affected fields.
     */
    public Field[] getFields() {
        return changedFields;
    }

    /**
     * Gets the values of the fields that are affected by this change.
     * @return an array of values of the affected fields.
     */
    public Object[] getValues() {
        return changedValues;
    }
}
