package sws.murcs.model;

import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
     * the description of the Backlog.
     */
    @TrackableValue
    private String description;

    /**
     * The PO who is assigned to the backlog.
     */
    @TrackableValue
    private Person assignedPO;

    /**
     * The list of stories that are in this backlog.
     */
    @TrackableValue
    @XmlElementWrapper(name = "stories")
    @XmlElement(name = "story")
    private List<Story> stories;

    /**
     * The list of unprioritised stories within this backlog.
     */
    @TrackableValue
    private List<Story> unprioritisedStories;

    /**
     * The constructor for the backlog. Initialises the lists within the backlog.
     */
    public Backlog() {
        stories = new ArrayList<>();
        unprioritisedStories = new ArrayList<>();
    }

    /**
     * Gets the description of the backlog.
     * @return a description of the backlog.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the description of the backlog.
     * @param newDescription The description of the backlog.
     */
    public final void setDescription(final String newDescription) {
        description = newDescription;
        commit("edit backlog");
    }

    /**
     * Gets all of the stories associated with this backlog.
     * @return a list of all the stories attached to this backlog.
     */
    public final List<Story> getAllStories() {
        final List<Story> allStories = new ArrayList<>();
        allStories.addAll(stories);
        allStories.addAll(unprioritisedStories);
        return allStories;
    }

    /**
     * Get only the prioritised stories within this backlog.
     * @return the prioritised stories
     */
    public final List<Story> getStories() {
        return stories;
    }

    /**
     * Get only those stories with no priority.
     * @return the unPrioritised stories
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
                if (stories.contains(story)) {
                    stories.remove(story);
                }
            }
        }
        else if (priority < 0) {
            throw new InvalidParameterException("Priority less than zero");
        }
        else if (priority >= stories.size()) {
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

    /**
     * Change the priority of a story in the backlog. Assumes the story is already within the backlog.
     * @param story The story involved.
     * @param priority The new priority of that story
     * @throws CustomException throws if there any exceptions modifying the stories priorities.
     */
    private void changeStoryPriority(final Story story, final Integer priority) throws CustomException {
        final Integer currentStoryPriority = getStoryPriority(story);
        if (priority == null) {
            if (stories.contains(story)) {
                stories.remove(story);
            }
            if (!unprioritisedStories.contains(story)) {
                unprioritisedStories.add(0, story);
            }
        }
        else if (!Objects.equals(currentStoryPriority, priority)) {
            if (priority < 0) {
                throw new InvalidParameterException("Priority less than zero");
            }
            else if (priority >= stories.size()) {
                // check to see if the story is already in the prioritised stories
                // if it is then remove it so it can be added to the end of the list.
                if (stories.contains(story)) {
                    stories.remove(story);
                }
                stories.add(story);
                if (unprioritisedStories.contains(story)) {
                    unprioritisedStories.remove(story);
                }
            }
            else {
                if (stories.contains(story)) {
                    Story swap = stories.get(priority);
                    if (swap != null) {
                        stories.set(priority, story);
                        stories.set(currentStoryPriority, swap);
                    }
                }
                else if (!stories.contains(story)) {
                    stories.add(priority, story);
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
     * @return The current priority of that story. -1 if story is unassigned or not in the backlog.
     */
    public final Integer getStoryPriority(final Story story) {
        if (stories.contains(story)) {
            return stories.indexOf(story);
        }
        return null;
    }

    /**
     * Return the lowest priority story (highest number).
     * @return The lowest priority story.
     */
    public final Integer getLowestPriorityStory() {
        return stories.size();
    }

    /**
     * Remove a story from the backlog.
     * @param story The story to be removed
     */
    public final void removeStory(final Story story) {
        if (stories.contains(story)) {
            stories.remove(story);
        }
        else if (unprioritisedStories.contains(story)) {
            unprioritisedStories.remove(story);
        }
        commit("edit backlog");
    }

    /**
     * Assign a product owner to the backlog.
     * @param po The product owner to be assigned
     * @exception CustomException thrown if the po is null.
     */
    public final void setAssignedPO(final Person po) throws CustomException {
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
}
