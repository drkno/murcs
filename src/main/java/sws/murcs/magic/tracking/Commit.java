package sws.murcs.magic.tracking;

import java.util.ArrayList;

/**
 * Represents a the state of values at a period of time.
 */
public class Commit {
    private long commitNumber;
    private String message;
    private ArrayList<FieldValuePair> fieldValuePairs;
    private ArrayList<TrackableObject> trackableObjects;

    /**
     * Creates a new commit.
     * @param newCommitNumber the unique commit number.
     * @param newMessage the commit message to associate.
     * @param newFieldValuePairs the set of fields and values to set.
     * @param newTrackableObjects objects that were being tracked.
     */
    protected Commit(final long newCommitNumber, final String newMessage, final ArrayList<FieldValuePair> newFieldValuePairs, final ArrayList<TrackableObject> newTrackableObjects) {
        this.commitNumber = newCommitNumber;
        this.message = newMessage;
        this.fieldValuePairs = newFieldValuePairs;
        this.trackableObjects = newTrackableObjects;
    }

    /**
     * Gets the trackable objects associated with this commit.
     * @return associated objects.
     */
    public final ArrayList<TrackableObject> getTrackableObjects() {
        return trackableObjects;
    }

    /**
     * Gets the unique number associated with this commit.
     * @return the commit number.
     */
    public final long getCommitNumber() {
        return commitNumber;
    }

    /**
     * Gets the associated message with this commit.
     * @return the commit message.
     */
    public final String getMessage() {
        return message;
    }

    /**
     * Applies the values in this commit to the objects.
     * @throws Exception if something went very wrong.
     */
    public final void apply() throws Exception {
        for (FieldValuePair pair : fieldValuePairs) {
            pair.restoreValue();
        }
    }

    /**
     * Checks if another commit is identical to this one.
     * @param other other commit.
     * @return true if they are the same, false otherwise.
     */
    final boolean equals(final Commit other) {
        if (fieldValuePairs.size() != other.fieldValuePairs.size()) {
            return false;
        }
        for (int i = 0; i < fieldValuePairs.size(); i++) {
            if (!fieldValuePairs.get(i).equals(other.fieldValuePairs.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adjusts the message contained with this commit.
     * @param newMessage message to replace it with.
     */
    public final void modifyMessage(final String newMessage) {
        message = newMessage;
    }

    /**
     * Gets the field value pairs that make up this commit.
     * @return field value pairs.
     */
    public ArrayList<FieldValuePair> getPairs() {
        return fieldValuePairs;
    }
}
