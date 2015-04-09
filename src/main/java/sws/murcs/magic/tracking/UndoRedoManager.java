package sws.murcs.magic.tracking;

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

    /**
     * "Static" constructor, used so that values are always initialized
     * within a method that can have breakpoints set for debugging.
     */
    static {
        objectsList = new ArrayList<>();
        revertStack = new ArrayDeque<>();
        remakeStack = new ArrayDeque<>();
        commitNumber = 0;
        maximumCommits = -1;
    }

    /**
     * Adds an object to be tracked.
     * @param object new object to be tracked.
     */
    protected static void add(TrackableObject object) {
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
        ArrayList<FieldValuePair> pairs = new ArrayList<>();
        ArrayList<TrackableObject> trackableObjects = new ArrayList<>();
        for (TrackableObject object : objectsList) {
            trackableObjects.add(object);
            for (Field field : object.getTrackedFields()) {
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
            revertStack.pop();
        }

        if (maximumCommits >= 0 && revertStack.size() > maximumCommits) {
            revertStack.removeLast();
        }

        if (canRemake()) {
            remakeStack.clear();
        }
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
}
