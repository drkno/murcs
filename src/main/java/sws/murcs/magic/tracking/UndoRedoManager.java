package sws.murcs.magic.tracking;

import sws.murcs.magic.tracking.listener.ChangeListenerHandler;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Organisation;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Manages undo and redo operations.
 */
public final class UndoRedoManager {
    /**
     * Un-used constructor as this is a utility class.
     */
    private UndoRedoManager() {
    }

    /**
     * The head object, which represents the current state.
     */
    private static Commit head;
    /**
     * The revert stack, represents all states that can be reverted to.
     */
    private static Deque<Commit> revertStack = new ArrayDeque<>();
    /**
     * The remake stack, represents all states that can be remaked to.
     */
    private static Deque<Commit> remakeStack = new ArrayDeque<>();
    /**
     * List of objects that are currently tracked by the UndoRedoManager.
     */
    private static List<TrackableObject> objectsList = new ArrayList<>();
    /**
     * The last commit number that was used.
     */
    private static long commitNumber = 0;
    /**
     * Maximum number of commits that can be reverted to. -1 for infinity.
     */
    private static long maximumCommits = -1;
    /**
     * Listeners that have subscribed to the UndoRedoManager.
     */
    private static List<ChangeListenerHandler> changeListeners = new ArrayList<>();
    /**
     * If the UndoRedoManager is disabled.
     */
    private static boolean disabled = false;

    /**
     * Adds an object to be tracked.
     * @param object new object to be tracked.
     */
    public static void add(final TrackableObject object) {
        objectsList.add(object);
    }

    /**
     * Removes an object from tracking.
     * @param object object to be removed from tracking.
     */
    public static void remove(final TrackableObject object) {
        objectsList.remove(object);
    }

    /**
     * Saves the current state so that it can be restored at a later point in time.
     * @param message description of changes since last commit.
     * @return the unique commit number.
     * @throws Exception if an internal error occurs while committing.
     */
    public static long commit(final String message) throws Exception {
        if (disabled) {
            return -1;
        }

        boolean canRevert = canRevert(); // prevent repetitive calls to canRevert()
        List<FieldValuePair> lastFieldValuePairs;
        if (canRevert) {
            lastFieldValuePairs = revertStack.peek().getPairs();
        }
        else {
            lastFieldValuePairs = null;
        }
        List<FieldValuePair> pairs = new ArrayList<>();
        List<TrackableObject> trackableObjects = new ArrayList<>();
        for (TrackableObject object : objectsList) {
            trackableObjects.add(object);
            List<Field> fields = object.getTrackedFields();
            for (Field field : fields) {
                FieldValuePair pair = new FieldValuePair(field, object);
                if (canRevert) {
                    int index = lastFieldValuePairs.indexOf(pair);
                    if (index >= 0) {
                        pair = lastFieldValuePairs.get(index);
                    }
                }
                pairs.add(pair);
            }
        }
        if (head != null) {
            revertStack.push(head);
        }
        head = new Commit(commitNumber, message, pairs, trackableObjects);
        if (canRevert() && head.equals(revertStack.peek())) {
            Commit last = revertStack.pop();
            if (!last.getMessage().contains(head.getMessage())) {
                head.modifyMessage(head.getMessage() + ", " + last.getMessage());
            }
        }

        if (maximumCommits >= 0 && revertStack.size() > maximumCommits) {
            revertStack.removeLast();
        }

        if (canRemake()) {
            remakeStack.clear();
        }
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
     * @param deleteSavedObjects true to forget about current objects added for tracking, false otherwise.
     */
    public static void forget(final boolean deleteSavedObjects) {
        revertStack.clear();
        remakeStack.clear();
        if (deleteSavedObjects) {
            objectsList.clear();
            head = null;
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
     * @param revertCommitNumber commit number to revert to.
     * @throws Exception if an internal error occurs during the operation.
     */
    public static void revert(final long revertCommitNumber) throws Exception {
        while (!revertStack.isEmpty()) {
            remakeStack.push(head);
            Commit commit = revertStack.pop();
            commit.apply();
            objectsList = commit.getTrackableObjects();
            head = commit;
            if (commit.getCommitNumber() == revertCommitNumber) {
                break;
            }
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
        if (canRevert()) {
            return head.getMessage();
        }
        else {
            return null;
        }
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
     * @param remakeCommitNumber commit number to remake to.
     * @throws Exception if an internal error occurs during the operation.
     */
    public static void remake(final long remakeCommitNumber) throws Exception {
        while (!remakeStack.isEmpty()) {
            revertStack.push(head);
            Commit commit = remakeStack.pop();
            commit.apply();
            objectsList = commit.getTrackableObjects();
            head = commit;
            if (commit.getCommitNumber() == remakeCommitNumber) {
                break;
            }
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
        if (canRemake()) {
            return remakeStack.peek().getMessage();
        }
        else {
            return null;
        }
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
     * @param newMaximumCommits new maximum number of commits.
     * This can be negative (defaults to -1) for infinite commits,
     * or greater or equal to zero for a set number.
     */
    public static void setMaximumCommits(final long newMaximumCommits) {
        UndoRedoManager.maximumCommits = newMaximumCommits;
    }

    /**
     * Adds a listener for a change in state (eg commit, revert or remake performed)
     * that will be notified if such a change occurs.
     * @param eventListener the event listener to add.
     */
    public static void addChangeListener(final UndoRedoChangeListener eventListener) {
        if (disabled) {
            return;
        }
        ChangeListenerHandler changeListenerHandler = new ChangeListenerHandler(eventListener);
        changeListeners.add(changeListenerHandler);
    }

    /**
     * Removes an change listener.
     * @param eventListener listener to remove.
     */
    public static void removeChangeListener(final UndoRedoChangeListener eventListener) {
        if (disabled) {
            return;
        }
        ChangeListenerHandler listener = new ChangeListenerHandler(eventListener);
        changeListeners.remove(listener);
    }

    /**
     * Notifies listeners that a change has occurred.
     * @param changeType the type of change that occurred.
     */
    private static void notifyListeners(final ChangeState changeType) {
        if (changeListeners.size() == 0) {
            return;
        }
        //ChangeListenerHandler.performGC(); Disabled as forcing a GC is very sloooowwwww
        for (int i = 0; i < changeListeners.size(); i++) {
            if (!changeListeners.get(i).eventNotification(changeType)) {
                changeListeners.remove(i);
                i--;
            }
        }
    }

    /**
     * Clears all listeners.
     */
    public static void forgetListeners() {
        changeListeners.clear();
    }

    /**
     * Sets the disabled state of the Undo/Redo manager.
     * @param isDisabled new state.
     */
    public static void setDisabled(final boolean isDisabled) {
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
     * @param assimilateCommitNumber commit number to remove until.
     * @throws Exception If you use this method when remake is possible.
     */
    public static void assimilate(final long assimilateCommitNumber) throws Exception {
        if (head == null || head.getCommitNumber() == assimilateCommitNumber) {
            return;
        }
        if (canRemake()) {
            throw new Exception("Cannot assimilate while remake is possible.");
        }
        while (!revertStack.isEmpty()) {
            if (revertStack.peek().getCommitNumber() == assimilateCommitNumber) {
                break;
            }
            revertStack.pop();
        }
        if (canRevert()) {
            head = revertStack.pop();
        }
        notifyListeners(ChangeState.Assimilate);
    }

    /**
     * Imports a organisation, so that it can be tracked by the undo/redo manager.
     * WARNING: will forget about anything relating to previous models or objects that
     * are currently being tracked.
     * @param model model to import.
     * @throws Exception when committing the changes fail.
     */
    public static void importModel(final Organisation model) throws Exception {
        forget(true);
        UndoRedoManager.add(model);
        model.getPeople().forEach(p -> UndoRedoManager.add(p));
        model.getTeams().forEach(t -> UndoRedoManager.add(t));
        model.getSkills().forEach(k -> UndoRedoManager.add(k));
        model.getProjects().forEach(l -> UndoRedoManager.add(l));
        model.getReleases().forEach(r -> UndoRedoManager.add(r));
        commit("open project");
    }

    /**
     * Checks if a TrackableObject is currently added to the UndoRedoManager.
     * @param object TrackableObject to check.
     * @return true if object is tracked, false otherwise.
     */
    public static boolean isAdded(final TrackableObject object) {
        return objectsList.contains(object);
    }
}
