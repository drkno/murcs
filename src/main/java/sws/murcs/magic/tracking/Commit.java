package sws.murcs.magic.tracking;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Represents a the state of values within the model at a period of time.
 * Values are represented by storing what they were at a particular point in time.
 * Only values that have changed or are about to change are stored for efficiency.
 */
public class Commit {

    /**
     * Commit number of this commit.
     */
    private long commitNumber;

    /**
     * Message of this commit.
     */
    private String message;

    /**
     * Fields and their values associated with this commit.
     */
    private Collection<FieldValuePair> fieldValuePairs;

    /**
     * Fields that were added in this commit.
     */
    private Collection<Map.Entry<TrackableObject, FieldValuePair>> added;

    /**
     * Fields that were removed in this commit.
     */
    private Collection<Map.Entry<TrackableObject, FieldValuePair>> removed;

    /**
     * Creates a new commit.
     * @param newCommitNumber the unique commit number.
     * @param newMessage the commit message to associate.
     * @param newFieldValuePairs the set of fields and values to set.
     * @param addedFields fields that were added in this commit.
     * @param removedFields fields that were removed in this commit.
     */
    protected Commit(final long newCommitNumber, final String newMessage,
                     final Collection<FieldValuePair> newFieldValuePairs,
                     final Collection<Map.Entry<TrackableObject, FieldValuePair>> addedFields,
                     final Collection<Map.Entry<TrackableObject, FieldValuePair>> removedFields) {
        commitNumber = newCommitNumber;
        message = newMessage;
        fieldValuePairs = newFieldValuePairs;
        added = addedFields;
        removed = removedFields;
    }

    /**
     * Gets the fields that were added in this commit.
     * @return fields that were added.
     */
    public final Collection<Map.Entry<TrackableObject, FieldValuePair>> getAddedFields() {
        return Collections.unmodifiableCollection(added);
    }

    /**
     * Gets the fields that were removed in this commit.
     * @return fields that were removed.
     */
    public final Collection<Map.Entry<TrackableObject, FieldValuePair>> getRemovedFields() {
        return Collections.unmodifiableCollection(removed);
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
    public final Collection<FieldValuePair> getPairs() {
        return Collections.unmodifiableCollection(fieldValuePairs);
    }

    /**
     * Adds a FieldValuePair to this commit.
     * @param fieldValuePair pair to add.
     */
    public void addPair(final FieldValuePair fieldValuePair) {
        fieldValuePairs.add(fieldValuePair);
    }
}
