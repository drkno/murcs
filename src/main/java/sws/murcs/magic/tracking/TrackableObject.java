package sws.murcs.magic.tracking;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Keeps track of specified values so that the changes can be done/undone.
 */
public abstract class TrackableObject {
    private final Stack<ValueChange> _revisionUndoHistory = new Stack<>();
    private final Stack<ValueChange> _revisionRedoHistory = new Stack<>();
    private long _lastSaveTime;
    private TrackedState _currentState;

    /**
     * Instantiates a new ValueTracker.
     * This involves searching for fields annotated with @ValueTracker so any
     * annotations of this type added after this point using reflection will be
     * ignored.
     * It also sets the initial state of the value tracker.
     * commit(String, true) should be called in the constructor of the
     * object if the initial state needs to be tracked.
     */
    protected TrackableObject() {
        try {
            // get all annotated fields
            ArrayList<Field> trackableFields = new ArrayList<>();
            for (Field field : getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(TrackableValue.class)) {
                    trackableFields.add(field);
                }
            }
            _currentState = new TrackedState(this, trackableFields);
        }
        catch (Exception e) {
            System.err.println("Could not save current state as there is no state to save!");
            e.printStackTrace();
            // continue anyway so the program isn't unstable.
        }
    }

    /**
     * Saves the current state of the calling object.
     * NOTE: This assumes that either an initial save is not required or has already been made. If an
     * initial save is required (a save that tracks all fields) use commit(String, boolean)
     * instead.
     * @param changeDescription Description of the changes made since last save.
     */
    public void commit(String changeDescription) {
        commit(changeDescription, false);
    }

    /**
     * Saves the current state of the calling object.
     * @param changeDescription Description of the changes made since last save.
     * @param saveAll If set to true will save the values of all annotated fields regardless of
     *                    the current value or previous value.
     */
    public void commit(String changeDescription, boolean saveAll) {
        try {
            if (canRedo()) { // add current state to the undo stack, otherwise it will be lost
                ValueChange last = _revisionUndoHistory.peek();
                ValueChange change = last.getStateTracker().dumpChange(this, _revisionUndoHistory.peek(), last.getDescription());
                _revisionUndoHistory.push(change);
            }

            ValueChange value = _currentState.difference(this, changeDescription, saveAll);

            if (!value.isDifferent()) { // check a change was actually made
                if (canRedo()) { // it wasn't so restore the previous state
                    _revisionUndoHistory.pop();
                }
                return;
            }

            if (canRedo()) { // clear the undo stack. done here so restore is possible
                _revisionRedoHistory.clear();
            }

            // if less than wait time, changes to the same object can be merged
            long currentSaveTime = System.currentTimeMillis();
            if (canUndo() && currentSaveTime - _lastSaveTime < UndoRedoManager.getMergeWaitTime() &&
                    value.isEquivalent(_revisionUndoHistory.peek(), this)) {
                _revisionUndoHistory.peek().assimilate(value);
            }
            else {
                _revisionUndoHistory.push(value); // add new change to undo stack
                UndoRedoManager.commit(this);
            }
            _lastSaveTime = currentSaveTime;

            /*if (UndoRedoManager._maximumUndoRedoStackSize > 0 &&
                    _revisionUndoHistory.size() - 1 == UndoRedoManager._maximumUndoRedoStackSize) {
                _revisionUndoHistory.remove(0); // remove bottom item from stack if beyond bounds
            }*/

            // stop outgoing callbacks
            UndoRedoManager.abortCallbacks();
            UndoRedoManager.notifyListeners(_revisionUndoHistory.peek()); // notify change listeners
        }
        catch (Exception e) {
            System.err.println("Could not save current state as there is no state to save!");
            e.printStackTrace();
            // continue anyway so the program isn't unstable.
        }
    }

    /**
     * Removes the last item from the undo stack.
     */
    protected void removeLastUndoItem() {
        _revisionUndoHistory.remove(0);
    }

    /**
     * Updates the internal state tracker for the annotated fields of the affected
     * object to reflect the applied change.
     * @param state New state that the TrackedValue should reflect.
     */
    private void applyHistoryChange(ValueChange state) {
        TrackableObject obj = (TrackableObject)state.getAffectedObject();
        obj._currentState.apply(state); // apply the changes to the affected objects history tracker
    }

    /**
     * Undoes the most recent change that was saved.
     * @throws Exception If undo is not possible or critical internal error occurs.
     */
    protected void undo() throws Exception {
        if (!canUndo()) {
            throw new Exception("Undo is not possible as there are no saved undo states.");
        }

        if (!canRedo() && _revisionUndoHistory.size() > 1) {
            ValueChange current = _revisionUndoHistory.pop();
            _revisionRedoHistory.push(current);     // top object of undo stack is current state, so move it
        }

        ValueChange undoState = _revisionUndoHistory.pop();
        _revisionRedoHistory.push(undoState);
        applyHistoryChange(undoState);
        undoState.undo();
        UndoRedoManager.abortCallbacks();
        UndoRedoManager.notifyListeners(_revisionRedoHistory.peek(), 0);
    }

    /**
     * Gets the description for the next undo action.
     * @return a description.
     */
    protected String getUndoDescription() {
        return _revisionUndoHistory.peek().getDescription();
    }

    /**
     * Checks if undo is currently possible given the previously saved states.
     * @return True if undo is currently possible, false otherwise.
     */
    protected boolean canUndo() {
        return !_revisionUndoHistory.isEmpty() && (!_revisionRedoHistory.isEmpty() || _revisionUndoHistory.size() >= 1);
    }

    /**
     * Redoes the most recent change that was undone.
     * @throws Exception If redo is not possible or critical internal error occurs.
     */
    protected void redo() throws Exception {
        if (!canRedo()) {
            throw new Exception("Redo is not possible as there are no saved redo states.");
        }

        ValueChange redoState = _revisionRedoHistory.pop();
        _revisionUndoHistory.push(redoState);
        redoState.redo();
        applyHistoryChange(redoState);
        UndoRedoManager.abortCallbacks();
        UndoRedoManager.notifyListeners(_revisionUndoHistory.peek(), 0);
    }

    /**
     * Gets the description for the next redo action.
     * @return a description.
     */
    protected String getRedoDescription() {
        return _revisionRedoHistory.peek().getDescription();
    }

    /**
     * Checks if redo is currently possible given the previously undone states.
     * @return True if redo is currently possible, false otherwise.
     */
    protected boolean canRedo() {
        return !_revisionRedoHistory.isEmpty();
    }

    /**
     * Clears all saved states, making undo/redo impossible until a new saved state is created.
     * Useful for switching contexts and testing.
     */
    protected void reset() {
        _revisionUndoHistory.clear();
        _revisionRedoHistory.clear();
    }
}
