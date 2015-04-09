package sws.murcs.magic.tracking;

import java.util.ArrayList;

/**
 * Represents a the state of values at a period of time.
 */
public class Commit {
    private long commitNumber;
    private String message;
    private FieldValuePair[] fieldValuePairs;
    private ArrayList<TrackableObject> trackableObjects;

    /**
     * Creates a new commit.
     * @param commitNumber the unique commit number.
     * @param message the commit message to associate.
     * @param fieldValuePairs the set of fields and values to set.
     * @param trackableObjects objects that were being tracked.
     */
    protected Commit(long commitNumber, String message, FieldValuePair[] fieldValuePairs, ArrayList<TrackableObject> trackableObjects) {
        this.commitNumber = commitNumber;
        this.message = message;
        this.fieldValuePairs = fieldValuePairs;
        this.trackableObjects = trackableObjects;
    }

    /**
     * Gets the trackable objects associated with this commit.
     * @return associated objects.
     */
    public ArrayList<TrackableObject> getTrackableObjects() {
        return trackableObjects;
    }

    /**
     * Gets the unique number associated with this commit.
     * @return the commit number.
     */
    public long getCommitNumber() {
        return commitNumber;
    }

    /**
     * Gets the associated message with this commit.
     * @return the commit message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Applies the values in this commit to the objects.
     * @throws Exception if something went very wrong.
     */
    public void apply() throws Exception {
        for (FieldValuePair pair : fieldValuePairs) {
            pair.restoreValue();
        }
    }

    /**
     * Checks if another commit is identical to this one.
     * @param other other commit.
     * @return true if they are the same, false otherwise.
     */
    public boolean equals(Commit other) {
        if (fieldValuePairs.length != other.fieldValuePairs.length) return false;
        for (int i = 0; i < fieldValuePairs.length; i++) {
            if (!fieldValuePairs[i].equals(other.fieldValuePairs[i])) return false;
        }
        return true;
    }
}
