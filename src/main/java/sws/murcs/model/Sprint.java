package sws.murcs.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import sws.murcs.exceptions.InvalidParameterException;
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
     * @throws InvalidParameterException if the end date is before the start date or after the release date
     */
    public final void setEndDate(final LocalDate pEndDate) throws InvalidParameterException {
        validateDates(startDate, pEndDate, associatedRelease);

        this.endDate = pEndDate;
        commit("edit sprint");
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
     * @throws InvalidParameterException if the start date is after the end date
     */
    public final void setStartDate(final LocalDate pStartDate) throws InvalidParameterException {
        validateDates(pStartDate, endDate, associatedRelease);

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
     * @throws InvalidParameterException if the release is before the end date of the sprint
     */
    public final void setAssociatedRelease(final Release pAssociatedRelease) throws InvalidParameterException {
        validateDates(startDate, endDate, pAssociatedRelease);

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

    /**
     * Validates the dates associated with the sprint
     * @param end The end date
     * @param start The start date
     * @param rel The release
     * @throws InvalidParameterException if the dates overlap of the end date is after the release date
     */
    private void validateDates(final LocalDate start, final LocalDate end, final Release rel)
            throws InvalidParameterException {
        if (end != null && start != null && end.isBefore(start)) {
            throw new InvalidParameterException("Start date should not be before end date");
        }

        if (end != null && rel != null && end.isAfter(rel.getReleaseDate())) {
            throw new InvalidParameterException("The sprint should end before the release date");
        }
    }
}
