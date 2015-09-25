package sws.murcs.model;

import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.model.observable.ModelObservableArrayList;
import sws.murcs.search.Searchable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model of a Backlog. A backlog is basically a group of stories created by a Person. This group of stories can be
 * prioritised and rearranged depending on which of the stories are more important and therefore need to be addressed
 * first.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Backlog extends Model {

    /**
     * Serialisation ID for backwards compatible serialisation.
     */
    private static final long serialVersionUID = 0L;

    /**
     * The PO who is assigned to the backlog.
     */
    @Searchable
    @TrackableValue
    @XmlIDREF
    private Person assignedPO;

    /**
     * The list of prioritised stories that are in this backlog.
     */
    @Searchable
    @TrackableValue
    @XmlElementWrapper(name = "prioritisedStories")
    @XmlElement(name = "story")
    private List<Story> prioritisedStories;

    /**
     * The list of unprioritised stories within this backlog.
     */
    @Searchable
    @TrackableValue
    @XmlElementWrapper(name = "unprioritisedStories")
    @XmlElement(name = "story")
    private List<Story> unprioritisedStories;

    /**
     * The type of estimation used for this backlog. Defaults to Fibonacci
     */
    @Searchable
    @TrackableValue
    private EstimateType estimateType;

    /**
     * Stories in the backlog workspace.
     */
    @TrackableValue
    private List<Story> workspaceStories;

    /**
     * The constructor for the backlog. Initialises the lists within the backlog.
     */
    public Backlog() {
        prioritisedStories = new ArrayList<>();
        unprioritisedStories = new ArrayList<>();
        workspaceStories = new ModelObservableArrayList<>();
        estimateType = EstimateType.Fibonacci;
    }

    /**
     * Gets all of the stories associated with this backlog.
     * @return a list of all the stories attached to this backlog.
     */
    public final List<Story> getAllStories() {
        List<Story> allStories = new ArrayList<>();
        allStories.addAll(prioritisedStories);
        allStories.addAll(unprioritisedStories);
        return allStories;
    }

    /**
     * Get only the prioritised stories within this backlog.
     * @return the prioritised stories
     */
    public final List<Story> getPrioritisedStories() {
        return prioritisedStories;
    }

    /**
     * Get only those stories with no priority.
     * @return the unprioritised stories
     */
    public final List<Story> getUnprioritisedStories() {
        return unprioritisedStories;
    }

    /**
     * Modify a story in the backlog by updating it's priority.
     * @param story The story to be modified or added.
     * @param priority The priority of the story i.e. where in the list it should be.
     * @throws CustomException thrown if there are errors modifying the story, for instance if it didn't exist.
     */
    public final void modifyStory(final Story story, final Integer priority) throws CustomException {
        if (getAllStories().contains(story)) {
            changeStoryPriority(story, priority);
        } else {
            throw new InvalidParameterException("Story not contained within the backlog");
        }
    }

    /**
     * Attempts to add a story to the backlog.
     * If story is already in the Backlog, it will be ignored.
     * @param story The story to be added.
     * @param priority The priority of the story i.e. where in the list it should be.
     * This should be an integer greater than or equal to 0.
     * @throws CustomException thrown if there are any errors adding a story (i.e. if it's already in the backlog).
     */
    public final void addStory(final Story story, final Integer priority) throws CustomException {
        if (getAllStories().contains(story)) {
            throw new DuplicateObjectException("Story is already within the backlog");
        }
        if (priority == null) {
            if (!unprioritisedStories.contains(story)) {
                unprioritisedStories.add(story);
            }
        }
        else if (priority < 1) {
            throw new InvalidParameterException("Priority less than one");
        }
        else if (priority > prioritisedStories.size()) {
            prioritisedStories.add(story);
        }
        else {
            prioritisedStories.add(priority - 1, story);
        }
        commit("edit backlog");
    }

    /**
     * Change the priority of a story in the backlog. Assumes the story is already within the backlog.
     * @param story The story involved.
     * @param priority The new priority of that story
     * @throws CustomException throws if there are problems changing the story's priority (i.e. give a priority of -1)
     */
    public final void changeStoryPriority(final Story story, final Integer priority) throws CustomException {
        final Integer currentStoryPriority = getStoryPriority(story);
        if (priority == null) {
            if (prioritisedStories.contains(story)) {
                prioritisedStories.remove(story);
            }
            if (!unprioritisedStories.contains(story)) {
                unprioritisedStories.add(0, story);
            }
        }
        else if (!Objects.equals(currentStoryPriority, priority)) {
            final int adjPriority = priority - 1;
            if (priority < 1) {
                throw new InvalidParameterException("Priority less than one");
            }
            else if (priority > prioritisedStories.size()) {
                // check to see if the story is already in the prioritised stories
                // if it is then remove it so it can be added to the end of the list.
                if (prioritisedStories.contains(story)) {
                    prioritisedStories.remove(story);
                }
                prioritisedStories.add(story);
                if (unprioritisedStories.contains(story)) {
                    unprioritisedStories.remove(story);
                }
            }
            else {
                if (prioritisedStories.contains(story)) {
                    Story swap = prioritisedStories.get(adjPriority);
                    if (swap != null) {
                        prioritisedStories.set(adjPriority, story);
                        prioritisedStories.set(currentStoryPriority - 1, swap);
                    }
                }
                else if (!prioritisedStories.contains(story)) {
                    prioritisedStories.add(adjPriority, story);
                    if (unprioritisedStories.contains(story)) {
                        unprioritisedStories.remove(story);
                    }
                }
            }
        }
        commit("edit backlog");
    }

    /**
     * Get the priority of a story in the backlog.
     * @param story The story involved.
     * @return The current priority of that story. Null if story is unassigned or not in the backlog.
     */
    public final int getStoryPriority(final Story story) {
        if (prioritisedStories.contains(story)) {
            return prioritisedStories.indexOf(story) + 1;
        }
        return -1;
    }

    /**
     * Return the lowest priority story (highest number).
     * @return The lowest priority story.
     */
    public final Integer getLowestPriorityStory() {
        return prioritisedStories.size() + 1;
    }

    /**
     * Remove a story from the backlog. If the story isn't contained within the Backlog it is ignored.
     * @param story The story to be removed
     */
    public final void removeStory(final Story story) {
        if (prioritisedStories.contains(story)) {
            prioritisedStories.remove(story);
        }
        else if (unprioritisedStories.contains(story)) {
            unprioritisedStories.remove(story);
        }
        if (workspaceStories.contains(story)) {
            workspaceStories.remove(story);
        }
        story.setEstimate(EstimateType.NOT_ESTIMATED);
        story.setStoryState(Story.StoryState.None);
        commit("edit backlog");
    }

    /**
     * Assign a product owner to the backlog.
     * @param po The product owner to be assigned
     * @exception InvalidParameterException thrown if the po is null or the Person attempting to be assigned does not
     * have the PO skill.
     */
    public final void setAssignedPO(final Person po) throws InvalidParameterException {
        InvalidParameterException.validate("Assigned PO", po);
        assignedPO = po;
        commit("edit backlog");
    }

    /**
     * Get the currently assigned product owner.
     * @return The assigned product owner
     */
    public final Person getAssignedPO() {
        return assignedPO;
    }

    /**
     * Get the current estimate type.
     * @return The estimate type.
     */
    public final EstimateType getEstimateType() {
        return estimateType;
    }

    /**
     * Sets the new estimate type. This will cascade and attempt to update the estimate on all stories belonging
     * to this backlog.
     * @param newEstimateType the new estimate type.
     */
    public final void setEstimateType(final EstimateType newEstimateType) {
        if (this.estimateType == newEstimateType) {
            return;
        }

        startAssimilation();

        EstimateType oldEstimateType = this.estimateType;
        this.estimateType = newEstimateType;
        commit("edit backlog");

        for (Story story : getAllStories()) {
            String newEstimate = oldEstimateType.convert(newEstimateType, story.getEstimate());
            story.setEstimate(newEstimate);
        }

        endAssimilation("edit backlog");
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

        final String shortName = getShortName();
        final String shortNameOther = ((Backlog) object).getShortName();

        if (shortName == null || shortNameOther == null) {
            return Objects.equals(shortName, shortNameOther);
        }

        return shortName.toLowerCase().equals(shortNameOther.toLowerCase());
    }

    /**
     * Gets the stories in the workspace.
     * @return The stories workspace.
     */
    public List<Story> getWorkspaceStories() {
        return workspaceStories;
    }

    /**
     * Adds a story to the workspace if it does not already contain it.
     * @param story The story to add to the workspace.
     * @return If a story was added to the workspace.
     */
    public boolean addToWorkspaceStories(final Story story) {
        if (getAllStories().contains(story)) {
            boolean addSuccess = workspaceStories.add(story);
            if (addSuccess) {
                commit("Story added to workspace");
            }
            return addSuccess;
        }
        return false;
    }

    /**
     * Removes a story from the workspace.
     * @param story The story to remove.
     */
    public void removeStoryFromWorkspace(final Story story) {
        workspaceStories.remove(story);
        commit("Story removed from workspace");
    }
}
