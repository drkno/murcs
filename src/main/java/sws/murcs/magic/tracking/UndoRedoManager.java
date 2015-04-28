package sws.murcs.magic.tracking;

import sws.murcs.magic.tracking.listener.ChangeListenerHandler;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Manages undo and redo operations.
 */
public class UndoRedoManager {
    private static Commit head;
    private static ArrayDeque<Commit> revertStack;
    private static ArrayDeque<Commit> remakeStack;
    private static ArrayList<TrackableObject> objectsList;
    private static long commitNumber;
    private static long maximumCommits;
    private static ArrayList<ChangeListenerHandler> changeListeners;
    private static boolean disabled;

    /**
     * "Static" constructor, used so that values are always initialized
     * within a method that can have breakpoints set for debugging.
     */
    static {
        objectsList = new ArrayList<>();
        revertStack = new ArrayDeque<>();
        remakeStack = new ArrayDeque<>();
        changeListeners = new ArrayList<>();
        commitNumber = 0;
        maximumCommits = -1;
        disabled = false;
    }

    /**
     * Adds an object to be tracked.
     * @param object new object to be tracked.
     */
    public static void add(TrackableObject object) {
        objectsList.add(object);
    }

    /**
     * Removes an object from tracking.
     * @param object object to be removed from tracking.
     */
    public static void remove(TrackableObject object) {
        objectsList.remove(object);
    }

    /**
     * Saves the current state so that it can be restored at a later point in time.
     * @param message description of changes since last commit.
     * @return the unique commit number.
     * @throws Exception if an internal error occurs while committing.
     */
    public static long commit(String message) throws Exception {
        if (disabled) return -1;
        ArrayList<FieldValuePair> pairs = new ArrayList<>();
        ArrayList<TrackableObject> trackableObjects = new ArrayList<>();
        for (TrackableObject object : objectsList) {
            trackableObjects.add(object);
            ArrayList<Field> fields = object.getTrackedFields();
            for (Field field : fields) {
                pairs.add(new FieldValuePair(field, object));
            }
        }
        FieldValuePair[] pairsArray = new FieldValuePair[pairs.size()];
        pairs.toArray(pairsArray);
        if (head != null) {
            revertStack.push(head);
        }
        head = new Commit(commitNumber, message, pairsArray, trackableObjects);
        if (canRevert() && head.equals(revertStack.peek())) {
            Commit last = revertStack.pop();
            if (!last.getMessage().contains(head.getMessage())) {
                head.modifyMessage(head.getMessage() + ", " + last.getMessage());
            }
        }

        if (maximumCommits >= 0 && revertStack.size() > maximumCommits) {
            revertStack.removeLast();
        }

        if (canRemake()) remakeStack.clear();
        notifyListeners(ChangeState.Commit);

        return commitNumber++;
    }

    /**
     * Forgets about the current commits.
     * Does NOT stop tracking currently added objects.
     */
    public static void forget() {
        forget(false);
    }

    /**
     * Forgets about the current commits.
     * @param savedObjects true to forget about current objects added for
     *                     tracking, false otherwise.
     */
    public static void forget(boolean savedObjects) {
        revertStack.clear();
        remakeStack.clear();
        head = null;
        if (savedObjects) {
            objectsList.clear();
        }
        notifyListeners(ChangeState.Forget);
    }

    /**
     * Reverts the current changes to the last commit sequentially.
     * @throws Exception if an internal error occurs during the operation.
     */
    public static void revert() throws Exception {
        revert(revertStack.peek().getCommitNumber());
    }

    /**
     * Reverts to a specified commit or the initial commit
     * if the specified commit is not found.
     * @param commitNumber commit number to revert to.
     * @throws Exception if an internal error occurs during the operation.
     */
    public static void revert(long commitNumber) throws Exception {
        while (!revertStack.isEmpty()) {
            remakeStack.push(head);
            Commit commit = revertStack.pop();
            commit.apply();
            objectsList = commit.getTrackableObjects();
            head = commit;
            if (commit.getCommitNumber() == commitNumber) break;
        }
        notifyListeners(ChangeState.Revert);
    }

    /**
     * Checks if the revert (undo) operation is available.
     * @return true if remake can be done, false otherwise.
     */
    public static boolean canRevert() {
        return !revertStack.isEmpty();
    }

    /**
     * Gets the message associated with the first revert (undo) commit.
     * @return the commit message or null if cannot revert.
     */
    public static String getRevertMessage() {
        return canRevert() ? head.getMessage() : null;
    }

    /**
     * Remakes (redoes) the current changes from the next commit sequentially.
     * @throws Exception if an internal error occurs during the operation.
     */
    public static void remake() throws Exception {
        remake(remakeStack.peek().getCommitNumber());
    }

    /**
     * Remakes (redoes) to a specified commit or the final commit
     * if the specified commit is not found.
     * @param commitNumber commit number to remake to.
     * @throws Exception if an internal error occurs during the operation.
     */
    public static void remake(long commitNumber) throws Exception {
        while (!remakeStack.isEmpty()) {
            revertStack.push(head);
            Commit commit = remakeStack.pop();
            commit.apply();
            objectsList = commit.getTrackableObjects();
            head = commit;
            if (commit.getCommitNumber() == commitNumber) break;
        }
        notifyListeners(ChangeState.Remake);
    }

    /**
     * Checks if the remake (redo) operation is available.
     * @return true if remake can be done, false otherwise.
     */
    public static boolean canRemake() {
        return !remakeStack.isEmpty();
    }

    /**
     * Gets the message associated with the first remake (redo) commit.
     * @return the commit message or null if cannot remake.
     */
    public static String getRemakeMessage() {
        return canRemake() ? remakeStack.peek().getMessage() : null;
    }

    /**
     * Gets the latest commit.
     * @return the latest commit.
     */
    public static Commit getHead() {
        return head;
    }

    /**
     * Reverts the current state to the latest commit.
     * @throws Exception if an error occurs during this operation.
     */
    public static void revertToHead() throws Exception {
        head.apply();
    }

    /**
     * Gets the maximum number of commits that can be made before commits are forgotten.
     * This can be negative (defaults to -1) for infinite commits, or greater or equal
     * to zero for a set number.
     * @return The maximum number of commits
     */
    public static long getMaximumCommits() {
        return maximumCommits;
    }

    /**
     * Sets the Maximum number of commits that can be made before commits are forgotten.
     * @param maximumCommits new maximum number of commits.
     *                       This can be negative (defaults to -1) for infinite commits,
     *                       or greater or equal to zero for a set number.
     */
    public static void setMaximumCommits(long maximumCommits) {
        UndoRedoManager.maximumCommits = maximumCommits;
    }

    /**
     * Adds a listener for a change in state (eg commit, revert or remake performed)
     * that will be notified if such a change occurs.
     * @param eventListener the event listener to add.
     */
    public static void addChangeListener(UndoRedoChangeListener eventListener) {
        if (disabled) return;
        ChangeListenerHandler changeListenerHandler = new ChangeListenerHandler(eventListener);
        changeListeners.add(changeListenerHandler);
    }

    /**
     * Removes an change listener.
     * @param eventListener listener to remove.
     */
    public static void removeChangeListener(UndoRedoChangeListener eventListener) {
        if (disabled) return;
        ChangeListenerHandler listener = new ChangeListenerHandler(eventListener);
        changeListeners.remove(listener);
    }

    /**
     * Notifies listeners that a change has occurred.
     * @param changeType the type of change that occurred.
     */
    private static void notifyListeners(ChangeState changeType) {
        if (changeListeners.size() == 0) return;
        //ChangeListenerHandler.performGC(); Disabled as forcing a GC is very sloooowwwww
        for (int i = 0; i < changeListeners.size(); i++) {
            if (!changeListeners.get(i).eventNotification(changeType)) {
                changeListeners.remove(i);
                i--;
            }
        }
    }

    /**
     * Sets the disabled state of the Undo/Redo manager.
     * @param isDisabled new state.
     */
    public static void setDisabled(boolean isDisabled) {
        disabled = isDisabled;
    }

    /**
     * Sets if the Undo/Redo manager is disabled.
     * @return current disabled state.
     */
    public static boolean getDisable() {
        return disabled;
    }

    /**
     * Removes history until a specific commit number.
     * WARNING: this is the nuclear option, you will not get history back.
     * @param commitNumber commit number to remove until.
     * @throws Exception If you use this method when remake is possible.
     */
    public static void assimilate(long commitNumber) throws Exception {
        if (canRemake()) throw new Exception("Cannot assimilate while remake is possible.");
        if (head.getCommitNumber() == commitNumber)return;
        while (!revertStack.isEmpty()) {
            if (revertStack.peek().getCommitNumber() == commitNumber)break;
            revertStack.pop();
        }
        if (canRevert())
            head = revertStack.pop();
        notifyListeners(ChangeState.Assimilate);
    }
}
