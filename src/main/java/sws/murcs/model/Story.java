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
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the story.
     * @param description The new description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the creator of this story
     * @return The creator
     */
    public Person getCreator() {
        return creator;
    }

    /**
     * Sets the creator of the story.
     * This should not be changed after initially being set.
     * @param creator
     */
    public void setCreator(Person creator) {
        this.creator = creator;
    }
}
