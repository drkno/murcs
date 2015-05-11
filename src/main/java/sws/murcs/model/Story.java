package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableValue;

/**
 * A class representing a story in the backlog for a project.
 */
public class Story extends Model {
    /**
     * A description of the story.
     */
    @TrackableValue
    private String description;

    /**
     * The person who created this story. This should not be changed after
     * initial creation.
     */
    @TrackableValue
    private Person creator;

    /**
     * Gets a description for the current story.
     * @return The description.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the description of the story.
     * @param newDescription The new description.
     */
    public final void setDescription(final String newDescription) {
        this.description = newDescription;
    }

    /**
     * Gets the creator of this story.
     * @return The creator
     */
    public final Person getCreator() {
        return creator;
    }

    /**
     * Sets the creator of the story.
     * This should not be changed after initially being set.
     * @param person The creator
     */
    public final void setCreator(final Person person) {
        this.creator = person;
    }

    @Override
    public final String toString() {
        return getShortName();
    }

    @Override
    public final int hashCode() {
        int c = 0;
        if (getShortName() != null) {
            c = getShortName().hashCode();
        }
        return getHashCodePrime() + c;
    }

    @Override
    public final boolean equals(final Object object) {
        if (object == null || !(object instanceof Story)) {
            return false;
        }
        String shortNameO = ((Story) object).getShortName();
        String shortName = getShortName();
        if (shortName == null || shortNameO == null) {
            return shortName == shortNameO;
        }
        return shortName.toLowerCase().equals(shortNameO.toLowerCase());
    }
}
