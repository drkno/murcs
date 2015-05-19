package sws.murcs.model;

import org.apache.commons.lang.UnhandledException;
import sws.murcs.magic.tracking.TrackableValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Model of a Backlog.
 */
public class Backlog extends Model {
    /**
     * Track this value.
     */
    @TrackableValue
    private String description;

    /**
     * Track this value.
     */
    @TrackableValue
    private Person assignedPO = null;

    /**
     * Track this value.
     */
    @TrackableValue
    private List<Story> stories = new ArrayList<>();

    /**
     * Gets a description of the project.
     * @return a description of the project
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the current project.
     * @param description The description of the project
     */
    public final void setDescription(String description) {
        this.description = description;
        commit("edit backlog");
    }

    /**
     * Gets the stories attached to a backlog.
     * @return a list of all the stories attached to a backlog
     */
    public final List<Story> getStories() {
        return stories;
    }

    /**
     * Add a story to the backlog.
     * @param story The story to be added
     */
    public final void addStory(final Story story) {
        if (!stories.contains(story)) {
            stories.add(story);
        }
    }

    /**
     * Remove a story from the backlog.
     * @param story The story to be removed
     */
    public final void removeStory(final Story story) {
        stories.remove(story);
    }

    /**
     * Assign a product owner to the backlog.
     * @param po The product owner to be assigned
     */
    public final void setAssignedPO(final Person po) {
        assignedPO = po;
    }

    /**
     * Get the currently assigned product owner.
     * @return The assigned product owner
     */
    public final Person getAssignedPO() {
        return assignedPO;
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
        if (object == null || !(object instanceof Backlog)) {
            return false;
        }

        final String shortName = this.getShortName();
        final String shortNameOther = ((Backlog) object).getShortName();

        if (shortName == null || shortNameOther == null) {
            return shortName == shortNameOther;
        }

        return shortName.toLowerCase().equals(shortNameOther.toLowerCase());
    }

    /**
     * Returns the short name of the backlog.
     * @return Short name of the backlog
     */
    @Override
    public final String toString() {
        return getShortName();
    }
}
