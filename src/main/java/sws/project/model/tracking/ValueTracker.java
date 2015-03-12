package sws.project.model.tracking;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Stack;

public abstract class ValueTracker {
    private static final Stack<TrackedChange> _revisionUndoHistory = new Stack<>();
    private static final Stack<TrackedChange> _revisionRedoHistory = new Stack<>();

    private TrackedValue _currentState;

    public ValueTracker() {
        try {
            ArrayList<Field> trackableFields = new ArrayList<>();
            for (Field field : getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(TrackValue.class)) {
                    trackableFields.add(field);
                }
            }
            _currentState = new TrackedValue(this, trackableFields);
        }
        catch (Exception e) {
            System.err.println("Could not save current state as there is no state to save!");
            e.printStackTrace();
            // continue anyway so the program isn't unstable.
        }
    }

    protected void saveCurrentState(String changeDescription) {
        saveCurrentState(changeDescription, false);
    }

    protected void saveCurrentState(String changeDescription, boolean initialSave) {
        try {
            TrackedChange value = _currentState.difference(this, changeDescription, initialSave);
            _revisionUndoHistory.push(value);

            if (!_revisionRedoHistory.isEmpty()) {
                _revisionRedoHistory.clear();
            }
        }
        catch (Exception e) {
            System.err.println("Could not save current state as there is no state to save!");
            e.printStackTrace();
            // continue anyway so the program isn't unstable.
        }
    }

    private static void applyHistoryChange(TrackedChange state) {
        ValueTracker obj = (ValueTracker)state.getAffectedObject();
        obj._currentState.apply(state);
    }

    public static void undo() throws Exception {
        if (!canUndo()) {
            throw new Exception("Undo is not possible as there are no saved undo states.");
        }

        if (!canRedo()) {
            TrackedChange current = _revisionUndoHistory.pop();
            _revisionRedoHistory.push(current);
        }

        TrackedChange undoState = _revisionUndoHistory.pop();
        _revisionRedoHistory.push(undoState);
        undoState.undo();
        applyHistoryChange(undoState);
    }

    public static String getUndoDescription() {
        return _revisionUndoHistory.peek().getDescription();
    }

    public static boolean canUndo() {
        return !_revisionUndoHistory.isEmpty() && (!_revisionRedoHistory.isEmpty() || _revisionUndoHistory.size() >= 1);
    }

    public static void redo() throws Exception {
        if (!canRedo()) {
            throw new Exception("Redo is not possible as there are no saved redo states.");
        }

        TrackedChange redoState = _revisionRedoHistory.pop();
        _revisionUndoHistory.push(redoState);
        redoState.redo();
        applyHistoryChange(redoState);
    }

    public static String getRedoDescription() {
        return _revisionRedoHistory.peek().getDescription();
    }

    public static boolean canRedo() {
        return !_revisionRedoHistory.isEmpty();// && (!_revisionRedoHistory.isEmpty() || _revisionUndoHistory.size() == 1);
    }

    public static void reset() {
        _revisionUndoHistory.clear();
        _revisionRedoHistory.clear();
    }
}
