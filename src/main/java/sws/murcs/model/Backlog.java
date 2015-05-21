package sws.murcs.model;

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
     * Track this value.
     */
    @TrackableValue
    private List<Story> unprioritisedStories = new ArrayList<>();

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
    public final void setDescription(final String description) {
        this.description = description;
        commit("edit backlog");
    }

    /**
     * Gets the stories attached to a backlog.
     * @return a list of all the stories attached to a backlog
     */
    public final List<Story> getAllStories() {
        final List allStories = new ArrayList<>();
        allStories.addAll(stories);
        allStories.addAll(unprioritisedStories);
        return allStories;
    }

    /**
     * Get only the prioritised stories in a backlog.
     * @return the prioritised stories
     */
    public final List<Story> getStories() {
        return stories;
    }

    /**
     * Get only those stories with no priority.
     * @return the unprioritised stories
     */
    public final List<Story> getUnprioritisedStories() {
        return unprioritisedStories;
    }

    /**
     * Add a story to the backlog. If story is already in backlog then its priority will just be updated.
     * @param story The story to be modified
     * @param priority The priority of the story i.e. where in the list it should be
     */
    public final void modifyStory(final Story story, final Integer priority) {
        if (getAllStories().contains(story)) {
            modifyStoryPriority(story, priority);
        } else {
            addStory(story, priority);
        }
    }

    /**
     * Add a story to the backlog. If story is already in the stories then it is ignored along with its priority.
     * @param story The story to be added
     * @param priority The priority of the story i.e. where in the list it should be.
     */
    public final void addStory(final Story story, final Integer priority) {
        if (!getAllStories().contains(story)) {
            if (priority == null) {
                if (!unprioritisedStories.contains(story)) {
                    unprioritisedStories.add(story);
                    if (stories.contains(story)) {
                        stories.remove(story);
                    }
                }
            }
            else if (priority < 0) {
                throw new IndexOutOfBoundsException("priority less than zero");
            }
            else if (priority > stories.size()) {
                stories.add(story);
                if (unprioritisedStories.contains(story)) {
                    unprioritisedStories.remove(story);
                }
            }
            else {
                stories.add(priority, story);
                if (unprioritisedStories.contains(story)) {
                    unprioritisedStories.remove(story);
                }
            }
            commit("edit backlog");
        }
    }

    /**
     * Change the priority of a story in the backlog, must be in the backlog for anything to happen.
     * @param story The story involved.
     * @param priority The new priority of that story
     */
    public final void modifyStoryPriority(final Story story, final Integer priority) {
        if (getAllStories().contains(story)) {
            final Integer currentStoryPriority = getStoryPriority(story);
            if (priority == null) {
                if (stories.contains(story)) {
                    stories.remove(story);
                }
                if (!unprioritisedStories.contains(story)) {
                    unprioritisedStories.add(story);
                }
            } else if (currentStoryPriority != priority) {
                if (priority < 0) {
                    throw new IndexOutOfBoundsException("priority less than zero");
                } else if (priority > stories.size()) {
                    if (stories.contains(story)) {
                        stories.add(story);
                    }
                    if (unprioritisedStories.contains(story)) {
                        unprioritisedStories.remove(story);
                    }
                } else {
                    if (stories.contains(story)) {
                        stories.add(story);
                    }
                    if (unprioritisedStories.contains(story)) {
                        unprioritisedStories.remove(story);
                    }
                }
            }
            commit("edit backlog");
        }
    }

    /**
     * Get the priority of a story in the backlog.
     * @param story The story involved
     * @return The current priority of that story. Null if story is unassigned or not in the backlog.
     */
    public final Integer getStoryPriority(final Story story) {
        if (!stories.contains(story)) {
            return stories.indexOf(story);
        } else {
            return null;
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
