package sws.murcs.magic.tracking;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * An object that is trackable by the UndoRedoManager.
 */
public abstract class TrackableObject {
    /**
     * Fields that can be tracked within this object.
     */
    private List<Field> trackedFields;

    /**
     * Commit to assimilate to if an assimilation has been started.
     */
    private Long assimilateTo;

    /**
     * Instantiates a new TrackableObject by getting annotated fields,
     * then adding this class for tracking.
     */
    protected TrackableObject() {
        initialiseTrackedFields();
    }

    /**
     * Gets all the annotated fields and adds them to the tracked fields list.
     */
    private void initialiseTrackedFields() {
        trackedFields = new ArrayList<>();
        Class clazz = getClass();
        while (clazz != TrackableObject.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(TrackableValue.class)) {
                    trackedFields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Gets all of the fields in this class that can be tracked.
     * @return an List of trackable fields.
     */
    protected final List<Field> getTrackedFields() {
        return trackedFields;
    }

    /**
     * Stops this object being tracked by the UndoRedoManager.
     */
    public final void stopTracking() {
        UndoRedoManager.remove(this);
    }

    /**
     * Wrapper around commit to deal with exceptions.
     * @param message commit message to use.
     * @return the commit number.
     */
    protected final long commit(final String message) {
        try {
            if (!UndoRedoManager.isAdded(this)) { // not yet tracked == no change in commit
                if (UndoRedoManager.getHead() == null) {
                    return 0;
                }
                else {
                    return UndoRedoManager.getHead().getCommitNumber();
                }
            }
            return UndoRedoManager.commit(message);
        }
        catch (Exception e) {
            // Something is very broken if we reach here
            UndoRedoManager.forget();
            System.err.println("UndoRedoManager broke with error:\n" + e.toString()
                    + "\nAs a precaution all history has been forgotten.");
            if (UndoRedoManager.getHead() == null) {
                return 0;
            }
            else {
                return UndoRedoManager.getHead().getCommitNumber();
            }
        }
    }

    /**
     * Starts the assimilation (merging commits process). All subsequent commits
     * from this point will be merged with the current head (or new head if no
     * commits have been made yet) when endAssimilation() is used.
     */
    protected final void startAssimilation() {
        if (UndoRedoManager.getHead() == null) {
            assimilateTo = 0L;
        }
        else {
            assimilateTo = UndoRedoManager.getHead().getCommitNumber();
        }
    }

    /**
     * Ends the assimilation process, merging all commits since startAssimilation()
     * was called. If startAssimilation() was not called, the behaviour is undefined.
     * At least one commit must be made to the undo/redo system before this is used.
     * @param commitMessage commit message to use on the assimilated commit.
     * @return the commit number of the assimilated commits.
     */
    protected final long endAssimilation(final String commitMessage) {
        try {
            if (UndoRedoManager.getDisable()) {
                return 0L;
            }
            UndoRedoManager.assimilate(assimilateTo);
            assimilateTo = null;
            commit(commitMessage);
            return UndoRedoManager.getHead().getCommitNumber();
        } catch (Exception e) {
            // This should never happen  because we have called commit before calling assimilate
            System.err.println("Exception during assimilation. "
                    + "Did you fail to call commit() before endAssimilation()?");
            e.printStackTrace();
            return 0L;
        }
    }
}
