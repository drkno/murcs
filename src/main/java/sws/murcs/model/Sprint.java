package sws.murcs.model;

import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.exceptions.MultipleSprintsException;
import sws.murcs.exceptions.NotReadyException;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.reporting.LocalDateAdapter;
import sws.murcs.search.Searchable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model of a sprint.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Sprint extends Model {

    /**
     * Serialisation ID for backwards compatible serialisation.
     */
    private static final long serialVersionUID = 0L;

    /**
     * The start and end dates for the sprint.
     */
    @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class)
    @TrackableValue
    @Searchable
    private LocalDate startDate, endDate;

    /**
     * The release associated to this sprint.
     */
    @TrackableValue
    @Searchable
    private Release associatedRelease;

    /**
     * The stories in this sprint.
     */
    @XmlElementWrapper(name = "stories")
    @XmlElement(name = "story")
    @XmlIDREF
    @TrackableValue
    @Searchable
    private List<Story> stories = new ArrayList<>();

    /**
     * The backlog associated with this sprint.
     */
    @XmlIDREF
    @TrackableValue
    @Searchable
    private Backlog associatedBacklog;

    /**
     * The team who is working on this sprint.
     */
    @XmlIDREF
    @TrackableValue
    @Searchable
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

        endDate = pEndDate;
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

        startDate = pStartDate;
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
        if (pAssociatedRelease != null) {
            validateDates(startDate, endDate, pAssociatedRelease);
        }

        associatedRelease = pAssociatedRelease;
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
     * Gets estimation info for the sprint.
     * @return The estimation info for the sprint.
     */
    public final EstimateInfo getEstimationInfo() {
        EstimateInfo info = new EstimateInfo();
        for (Story story : getStories()) {
            info.mergeIn(story.getEstimationInfo());
        }
        return info;
    }

    /**
     * Add a story to this sprint.
     * @param story The story to be added
     * @throws NotReadyException If the story added was not ready
     * @throws MultipleSprintsException when this story is already in another sprint
     */
    public final void addStory(final Story story) throws NotReadyException, MultipleSprintsException {
        if (story.getStoryState() != Story.StoryState.Ready) {
            throw new NotReadyException();
        }

        Sprint usage = UsageHelper.findBy(ModelType.Sprint, model -> model.getStories().contains(story));
        if (usage != null) {
            throw new MultipleSprintsException(usage, story);
        }
        if (!stories.contains(story)) {
            stories.add(story);
            commit("edit sprint");
        }
    }

    /**
     * Removes a story from the sprint.
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
        associatedBacklog = pBacklog;
        // Any time a new backlog is introduced should also clear the previous stories added from another backlog
        stories.clear();
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
        team = pTeam;
        commit("edit sprint");
    }

    @Override
    public final boolean equals(final Object object) {
        if (!(object instanceof Sprint)) {
            return false;
        }
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
     * Validates the dates associated with the sprint.
     * @param end The end date
     * @param start The start date
     * @param rel The release
     * @throws InvalidParameterException if the dates overlap of the end date is after the release date
     */
    private void validateDates(final LocalDate start, final LocalDate end, final Release rel)
            throws InvalidParameterException {
        if (end != null && start != null && end.isBefore(start)) {
            throw new InvalidParameterException("Start date should be before end date");
        }

        if (end != null && rel != null && end.isAfter(rel.getReleaseDate())) {
            throw new InvalidParameterException("The sprint should end before the release date");
        }
    }
}
