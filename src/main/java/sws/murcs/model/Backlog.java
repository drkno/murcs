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
    private List<Story> stories = new ArrayList<>();

    /**
     * Gets a description of the project.
     * @return a description of the project
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the current project.
     * @param description The description of the project
     */
    public void setDescription(String description) {
        this.description = description;
        commit("edit backlog");
    }

    /**
     * Gets the stories attached to a backlog.
     * @return a list of all the stories attached to a backlog
     */
    public List<Story> getStories() {
        return stories;
    }

    /**
     * Add a story to the backlog.
     * @param story The story to be added
     */
    public void addStory(final Story story) {
        stories.add(story);
    }

    /**
     * Remove a story from the backlog.
     * @param story The story to be removed
     */
    public void removeStory(final Story story) {
        stories.remove(story);
    }
}
