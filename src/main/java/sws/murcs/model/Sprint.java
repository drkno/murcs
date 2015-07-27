package sws.murcs.model;

import sws.murcs.exceptions.NotReadyException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model of a sprint.
 */
public class Sprint extends Model {
    /**
     * The start and end dates for the sprint.
     */
    private LocalDate startDate, endDate;

    /**
     * The release associated to this sprint.
     */
    private Release associatedRelease;

    /**
     * The stories in this sprint.
     */
    private List<Story> stories = new ArrayList<>();

    /**
     * The backlog associated with this sprint.
     */
    private Backlog backlog;

    /**
     * The team who is working on this sprint.
     */
    private Team team;

    /**
     * Get the end date for a sprint.
     * @return The end date
     */
    public final LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Set the end date for a sprint.
     * @param pEndDate The end date
     */
    public final void setEndDate(final LocalDate pEndDate) {
        this.endDate = pEndDate;
    }

    /**
     * Get the start date for a sprint.
     * @return The start date
     */
    public final LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Set the start date for a sprint.
     * @param pStartDate The start date
     */
    public final void setStartDate(final LocalDate pStartDate) {
        this.startDate = pStartDate;
    }

    /**
     * Get the release associated with this sprint.
     * @return the release
     */
    public final Release getAssociatedRelease() {
        return associatedRelease;
    }

    /**
     * Set the release associated with this sprint.
     * @param pAssociatedRelease the new associated release
     */
    public final void setAssociatedRelease(final Release pAssociatedRelease) {
        this.associatedRelease = pAssociatedRelease;
    }

    /**
     * Return the stories in this sprint.
     * @return the stories
     */
    public final List<Story> getStories() {
        return Collections.unmodifiableList(stories);
    }

    /**
     * Add a story to this sprint.
     * @param story The story to be added
     * @throws NotReadyException If the story added was not ready
     */
    public final void addStory(final Story story) throws NotReadyException {
        if (story.getStoryState() != Story.StoryState.Ready) {
            throw new NotReadyException();
        }
        stories.add(story);
    }
}
