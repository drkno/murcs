package sws.murcs.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import sws.murcs.exceptions.NotReadyException;
import sws.murcs.reporting.LocalDateAdapter;

/**
 * Model of a sprint.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Sprint extends Model {
    /**
     * The start and end dates for the sprint.
     */
    @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class)
    private LocalDate startDate, endDate;

    /**
     * The release associated to this sprint.
     */
    private Release associatedRelease;

    /**
     * The stories in this sprint.
     */
    @XmlElementWrapper(name = "stories")
    @XmlElement(name = "story")
    @XmlIDREF
    private List<Story> stories = new ArrayList<>();

    /**
     * The backlog associated with this sprint.
     */
    @XmlIDREF
    private Backlog associatedBacklog;

    /**
     * The team who is working on this sprint.
     */
    @XmlIDREF
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
        commit("edit sprint");
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
        commit("edit sprint");
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

        commit("edit sprint");
    }

    /**
     * Removes a story from the sprint
     * @param story The story to remove from the sprint
     */
    public final void removeStory(final Story story) {
        stories.remove(story);
        commit("edit sprint");
    }

    /**
     * Get the backlog associated with this sprint.
     * @return the sprint backlog
     */
    public final Backlog getBacklog() {
        return associatedBacklog;
    }

    /**
     * Set the backlog associated to this sprint.
     * @param pBacklog the sprint backlog
     */
    public final void setBacklog(final Backlog pBacklog) {
        this.associatedBacklog = pBacklog;
        commit("edit sprint");
    }

    /**
     * Get the team associated with this sprint.
     * @return the sprint team
     */
    public final Team getTeam() {
        return team;
    }

    /**
     * Set the team associated with this sprint.
     * @param pTeam the sprint team
     */
    public final void setTeam(final Team pTeam) {
        this.team = pTeam;
        commit("edit sprint");
    }

    @Override
    public final boolean equals(final Object object) {
        if (!(object instanceof Sprint) || object == null) return false;
        Sprint sprint = (Sprint) object;
        return sprint.getShortName() != null && sprint.getShortName().equals(getShortName());
    }

    @Override
    public final int hashCode() {
        int c = 0;
        if (getShortName() != null) {
            c = getShortName().hashCode();
        }
        return getHashCodePrime() + c;
    }
}
