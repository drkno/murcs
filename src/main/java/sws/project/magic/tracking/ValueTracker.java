package sws.project.magic.tracking;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Stack;
import java.util.function.Predicate;

/**
 * Keeps track of specified values so that the changes can be done/undone.
 */
public abstract class ValueTracker {
    private static final Stack<TrackedChange> _revisionUndoHistory = new Stack<>();
    private static final Stack<TrackedChange> _revisionRedoHistory = new Stack<>();
    private static ArrayList<Predicate<TrackedChange>> changeListeners = new ArrayList<>();
    private static int maximumUndoRedoStackSize = -1;
    private TrackedValue _currentState;

    /**
     * Instantiates a new ValueTracker.
     * This involves searching for fields annotated with @ValueTracker so any
     * annotations of this type added after this point using reflection will be
     * ignored.
     * It also sets the initial state of the value tracker.
     * saveCurrentState(String, true) should be called in the constructor of the
     * object if the initial state needs to be tracked.
     */
    protected ValueTracker() {
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

    /**
     * Saves the current state of the calling object.
     * NOTE: This assumes that either an initial save is not required or has already been made. If an
     * initial save is required (a save that tracks all fields) use saveCurrentState(String, boolean)
     * instead.
     * @param changeDescription Description of the changes made since last save.
     */
    protected void saveCurrentState(String changeDescription) {
        saveCurrentState(changeDescription, false);
    }

    /**
     * Saves the current state of the calling object.
     * @param changeDescription Description of the changes made since last save.
     * @param initialSave If set to true will save the values of all annotated fields regardless of
     *                    the current value or previous value.
     */
    protected void saveCurrentState(String changeDescription, boolean initialSave) {
        try {
            if (canRedo()) {
                TrackedChange change = _currentState.dumpChange(this, _revisionUndoHistory.peek(),
                        _revisionRedoHistory.peek().getDescription());
                _revisionUndoHistory.push(change);
                _revisionRedoHistory.clear();
            }

            TrackedChange value = _currentState.difference(this, changeDescription, initialSave);
            _revisionUndoHistory.push(value);

            if (maximumUndoRedoStackSize > 0 && _revisionUndoHistory.size() - 1 == maximumUndoRedoStackSize) {
                _revisionUndoHistory.remove(0);
            }

            notifyListeners(_revisionUndoHistory.peek());
        }
        catch (Exception e) {
            System.err.println("Could not save current state as there is no state to save!");
            e.printStackTrace();
            // continue anyway so the program isn't unstable.
        }
    }

    /**
     * Updates the internal state tracker for the annotated fields of the affected
     * object to reflect the applied change.
     * @param state New state that the TrackedValue should reflect.
     */
    private static void applyHistoryChange(TrackedChange state) {
        ValueTracker obj = (ValueTracker)state.getAffectedObject();
        obj._currentState.apply(state);
    }

    /**
     * Undoes the most recent change that was saved.
     * @throws Exception If undo is not possible or critical internal error occurs.
     */
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
        applyHistoryChange(undoState);
        undoState.undo();
    }

    /**
     * Gets the description for the next undo action.
     * @return a description.
     */
    public static String getUndoDescription() {
        return _revisionUndoHistory.peek().getDescription();
    }

    /**
     * Checks if undo is currently possible given the previously saved states.
     * @return True if undo is currently possible, false otherwise.
     */
    public static boolean canUndo() {
        return !_revisionUndoHistory.isEmpty() && (!_revisionRedoHistory.isEmpty() || _revisionUndoHistory.size() >= 1);
    }

    /**
     * Redoes the most recent change that was undone.
     * @throws Exception If redo is not possible or critical internal error occurs.
     */
    public static void redo() throws Exception {
        if (!canRedo()) {
            throw new Exception("Redo is not possible as there are no saved redo states.");
        }

        TrackedChange redoState = _revisionRedoHistory.pop();
        _revisionUndoHistory.push(redoState);
        redoState.redo();
        applyHistoryChange(redoState);
    }

    /**
     * Gets the description for the next redo action.
     * @return a description.
     */
    public static String getRedoDescription() {
        return _revisionRedoHistory.peek().getDescription();
    }

    /**
     * Checks if redo is currently possible given the previously undone states.
     * @return True if redo is currently possible, false otherwise.
     */
    public static boolean canRedo() {
        return !_revisionRedoHistory.isEmpty();
    }

    /**
     * Clears all saved states, making undo/redo impossible until a new saved state is created.
     * Useful for switching contexts and testing.
     */
    public static void reset() {
        _revisionUndoHistory.clear();
        _revisionRedoHistory.clear();
    }

    /**
     * Gets the current maximum size of the tracking stack. No more than this number of states can be saved.
     * @return Maximum stack size or less than or equal to 0 if infinite.
     */
    public static int getMaximumTrackingSize() {
        return maximumUndoRedoStackSize - 1;
    }

    /**
     * Sets the current maximum size of the tracking stack. No more than this number of states can be saved.
     * @param maximumUndoRedoStackSize new maximum stack size or less than or equal to 0 if infinite is desired.
     */
    public static void setMaximumTrackingSize(int maximumUndoRedoStackSize) {
        ValueTracker.maximumUndoRedoStackSize = maximumUndoRedoStackSize + 1;
    }

    /**
     * Adds a listener that waits for a save.
     * @param listener listener to call on change.
     */
    public static void addSavedListener(Predicate<TrackedChange> listener) {
        changeListeners.add(listener);
    }

    /**
     * Notifies listeners that are waiting for history changes.
     * @param change change that was made
     */
    private static void notifyListeners(TrackedChange change) {
        for (Predicate<TrackedChange> listener : changeListeners) {
            listener.test(change);
        }
    }
}
