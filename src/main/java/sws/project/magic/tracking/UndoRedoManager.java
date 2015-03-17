package sws.project.magic.tracking;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Timer;

/**
 * Manages undo and redo operations.
 */
public class UndoRedoManager {
    private static Stack<TrackableObject> _masterUndoStack = new Stack<>();
    private static Stack<TrackableObject> _masterRedoStack = new Stack<>();
    private static ArrayList<StateSaveListener> _changeListeners = new ArrayList<>();
    private static Timer _callbackWaitTimer = new Timer(false);
    private static TrackingTask _latestTrackingTask;
    private static long _mergeWaitTime = 500;
    protected static int _maximumUndoRedoStackSize = -1;

    /**
     * Gets the current maximum size of the tracking stack. No more than this number of states can be saved.
     * @return Maximum stack size or less than or equal to 0 if infinite.
     */
    public static int getMaximumTrackingSize() {
        return _maximumUndoRedoStackSize - 1;
    }

    /**
     * Sets the current maximum size of the tracking stack. No more than this number of states can be saved.
     * @param maximumUndoRedoStackSize new maximum stack size or less than or equal to 0 if infinite is desired.
     */
    public static void setMaximumTrackingSize(int maximumUndoRedoStackSize) {
        _maximumUndoRedoStackSize = maximumUndoRedoStackSize + 1;
    }

    /**
     * Adds a listener that waits for a save.
     * @param listener listener to call on change.
     */
    public static void addSavedListener(StateSaveListener listener) {
        _changeListeners.add(listener);
    }

    /**
     * Notifies listeners that are waiting for history changes using default wait.
     * @param change change that was made
     */
    protected static void notifyListeners(ValueChange change) {
        notifyListeners(change, getMergeWaitTime());
    }

    /**
     * Notifies listeners that are waiting for history changes.
     * @param change change that was made.
     * @param wait time to wait before notification
     */
    protected static void notifyListeners(ValueChange change, long wait) {
        TrackingTask task = new TrackingTask(change, _changeListeners);
        _latestTrackingTask = task;
        _callbackWaitTimer.schedule(task, wait);
    }

    /**
     * Gets the wait time before changes to the same object become separate.
     * @return the time to wait.
     */
    public static long getMergeWaitTime() {
        return _mergeWaitTime;
    }

    /**
     * Sets the wait time before changes to the same object become separate.
     * @param mergeWaitTime new time to wait.
     */
    public static void setMergeWaitTime(long mergeWaitTime) {
        _mergeWaitTime = mergeWaitTime;
    }

    /**
     * Stops the callback timer so the application can quit.
     */
    public static void destroy() {
        _callbackWaitTimer.cancel();
    }

    protected static void abortCallbacks() {
        if (_latestTrackingTask != null) _latestTrackingTask.cancel();
        _callbackWaitTimer.purge();
    }

    public static void reset() {
        _masterUndoStack.forEach(object -> object.reset());
        _masterRedoStack.forEach(object -> object.reset());
        _masterUndoStack.clear();
        _masterRedoStack.clear();
    }

    /**
     * Undoes the most recent change that was saved.
     * @throws Exception If undo is not possible or critical internal error occurs.
     */
    public static void undo() throws Exception {
        TrackableObject object = _masterUndoStack.pop();
        object.undo();
        _masterRedoStack.push(object);
    }

    /**
     * Gets the description for the next undo action.
     * @return a description.
     */
    public static String getUndoDescription() {
        return _masterUndoStack.peek().getUndoDescription();
    }

    /**
     * Checks if undo is currently possible given the previously saved states.
     * @return True if undo is currently possible, false otherwise.
     */
    public static boolean canUndo() {
        if (_masterUndoStack.isEmpty()) {
            return false;
        }
        return _masterUndoStack.peek().canUndo();
    }

    /**
     * Redoes the most recent change that was undone.
     * @throws Exception If redo is not possible or critical internal error occurs.
     */
    public static void redo() throws Exception {
        TrackableObject object = _masterRedoStack.pop();
        object.redo();
        _masterUndoStack.push(object);
    }

    /**
     * Gets the description for the next redo action.
     * @return a description.
     */
    public static String getRedoDescription() {
        return _masterRedoStack.peek().getRedoDescription();
    }

    /**
     * Checks if redo is currently possible given the previously undone states.
     * @return True if redo is currently possible, false otherwise.
     */
    public static boolean canRedo() {
        if (_masterRedoStack.isEmpty()) {
            return false;
        }
        return _masterRedoStack.peek().canRedo();
    }

    /**
     * Adds a committed change to the stack.
     * @param object changed object
     */
    protected static void commit(TrackableObject object) {
        _masterUndoStack.push(object);

        if (UndoRedoManager._maximumUndoRedoStackSize > 0 &&
                _masterUndoStack.size() - 1 == UndoRedoManager._maximumUndoRedoStackSize) {
            TrackableObject obj = _masterUndoStack.pop();
            obj.removeLastUndoItem();
        }
    }
}
