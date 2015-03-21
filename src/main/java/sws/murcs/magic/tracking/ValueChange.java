package sws.murcs.magic.tracking;

import java.lang.reflect.Field;

/**
 * Tracks an individual change occurrence that can be undone/redone.
 */
public class ValueChange {
    private String _changeDescription;
    private Object _originalObject;
    private final FieldValuePair[] _changedFieldValues;
    private boolean _undone;
    private TrackedState _stateTracker;

    /**
     * Instantiates a new TrackedChange that tracks an individual change occurrence.
     * @param originalObject The object that the change occurred on.
     * @param changedFieldValues The fields that had values changed were changed.
     * @param changeDescription Description of the changes made.
     * @param source Source state tracker.
     */
    protected ValueChange(Object originalObject, FieldValuePair[] changedFieldValues, String changeDescription, TrackedState source) {
        this._originalObject = originalObject;
        this._changedFieldValues = changedFieldValues;
        this._undone = false;
        this._changeDescription = changeDescription;
        this._stateTracker = source;
    }

    /**
     * Gets the description of the change.
     * @return the description.
     */
    public String getDescription() {
        return _changeDescription;
    }

    /**
     * Undoes this change.
     * @throws Exception if undo cannot be performed.
     */
    protected void undo() throws Exception {
        if (_undone) {
            throw new Exception("Cannot undo if already undone!");
        }
        _undone = true;
        toggleValues();
    }

    /**
     * Redoes this change.
     * @throws Exception If this change has not been undone.
     */
    protected void redo() throws Exception {
        if (!_undone) {
            throw new Exception("Cannot redo if already redone!");
        }
        _undone = false;
        toggleValues();
    }

    /**
     * Switches the values stored with those of the object.
     * @throws Exception if reflection fails - which can only occur if something drastically, horribly, horrendously
     * wrong happened at this point.
     */
    private void toggleValues() throws Exception {
        for (FieldValuePair changedFieldValue : _changedFieldValues) {
            Field field = changedFieldValue.getField();
            Object previousValue = field.get(_originalObject);
            field.set(_originalObject, changedFieldValue.getValue());
            changedFieldValue.setValue(previousValue);
        }
    }

    /**
     * Gets the object that is affected by this change.
     * @return the object.
     */
    public Object getAffectedObject() {
        return _originalObject;
    }

    /**
     * Gets the fields that are affected by this change and the associated values.
     * @return an array of affected fields.
     */
    public FieldValuePair[] getChangedFields() {
        return _changedFieldValues;
    }

    /**
     * Detects if a change has actually been made.
     * @return true if a changes to values have been made, false otherwise.
     */
    public boolean isDifferent() {
        return _changedFieldValues.length != 0;
    }

    @Override
    public String toString() {
        String resultString = "";
        for (FieldValuePair changedFieldValue : _changedFieldValues) {
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
        boolean areSame = affectedObject == _originalObject;
        areSame = areSame && change.getChangedFields().length == _changedFieldValues.length;
        areSame = areSame && change.getDescription().equals(_changeDescription);
        if (!areSame) return false;
        FieldValuePair[] fields = change.getChangedFields();
        for (int i = 0; i < _changedFieldValues.length; i++) {
            if (!_changedFieldValues[i].equals(fields[i])) return false;
        }
        return true;
    }

    /**
     * Merges the changes from a ValueChange into this one.
     * @param value value change to merge in.
     */
    public void assimilate(ValueChange value) {
        FieldValuePair[] fields = value.getChangedFields();
        for (int i = 0; i < _changedFieldValues.length; i++) {
            _changedFieldValues[i] = fields[i];
        }
    }

    /**
     * Gets the StateTracker which created this ValueChange.
     * @return the StateTracker.
     */
    protected TrackedState getStateTracker() {
        return _stateTracker;
    }
}
