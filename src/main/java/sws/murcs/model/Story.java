package sws.murcs.model;

import sws.murcs.exceptions.CyclicDependencyException;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.helpers.DependenciesHelper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * A class representing a story in the backlog for a project.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Story extends Model {

    /**
     * Represents the current state of a story.
     */
    public enum StoryState {

        /**
         * Indicates that the story state has not yet been set.
         */
        None,

        /**
         * Indicates that a story is not yet ready to
         * be pulled into a sprint.
         */
        Ready
    }

    /**
     * Indicates the current state of the story
     * (e.g. ready, not ready, in progress)
     */
    @TrackableValue
    private StoryState storyState;

    /**
     * A list of the conditions that have to be met before this
     * story can be marked as done. This has been made a list
     * (as opposed to a Collection) as order is important.
     */
    @TrackableValue
    private List<AcceptanceCondition> acceptanceCriteria;

    /**
     * The person who created this story. This should not be changed after
     * initial creation.
     */
    @TrackableValue
    @XmlIDREF
    private Person creator;

    /**
     * Stories that must be complete before this story can be worked on.
     */
    @XmlElementWrapper(name = "dependencies")
    @XmlElement(name = "dependence")
    @XmlIDREF
    private Collection<Story> dependencies;

    /**
     * Creates and initializes a new story.
     */
    public Story() {
        acceptanceCriteria = new ArrayList<>();
        estimate = EstimateType.NOT_ESTIMATED;
        dependencies = new LinkedHashSet<>();
        storyState = StoryState.None;
    }

    /**
     * Gets the stories that this story immediately (not transitively)
     * requires to be complete before work can begin.
     * @return a collection of the immediate dependencies.
     */
    public final Collection<Story> getDependencies() {
        return Collections.unmodifiableCollection(dependencies);
    }

    /**
     * Adds a new Story that this Story requires to be complete before
     * work can begin.
     * @param dependentStory the new dependency.
     * @throws CyclicDependencyException when adding the new dependency would create a
     * dependency cycle.
     */
    public final void addDependency(final Story dependentStory) throws CyclicDependencyException {
        if (dependencies.contains(dependentStory)) {
            return;
        }
        if (DependenciesHelper.isReachable(dependentStory, this)) {
            throw new CyclicDependencyException(this, dependentStory);
        }
        dependencies.add(dependentStory);
        commit("edit story");
    }

    /**
     * Removes a Story that this Story requires to be complete before
     * work can begin.
     * @param dependentStory the dependency to remove.
     */
    public final void removeDependency(final Story dependentStory) {
        dependencies.remove(dependentStory);
        commit("edit story");
    }

    /**
     * The estimate for this story.
     */
    @TrackableValue
    private String estimate;

    /**
     * Gets an unmodifiable List containing all the Acceptance
     * Criteria for this story. To modify, use the dedicated
     * add and remove methods.
     * @return The acceptance criteria for this story
     */
    public final List<AcceptanceCondition> getAcceptanceCriteria() {
        return Collections.unmodifiableList(acceptanceCriteria);
    }

    /**
     * Adds a condition to the acceptance criteria for the story if it is not already one of the ACs. This is
     * not intended to stop ACs with the same text being added but to prevent the same object being added multiple
     * times.
     * @param condition The condition to add
     */
    public final void addAcceptanceCondition(final AcceptanceCondition condition) {
        if (!acceptanceCriteria.contains(condition)) {
            acceptanceCriteria.add(condition);

            //Make sure the new condition is tracked by UndoRedo
            UndoRedoManager.add(condition);
        }
        commit("edit acceptance criteria");
    }

    /**
     * Moves an Acceptance Condition to a new position in the list.
     * @param condition The condition to move
     * @param newPosition The new position
     */
    public final void repositionCondition(final AcceptanceCondition condition, final int newPosition) {
        acceptanceCriteria.remove(condition);
        acceptanceCriteria.add(newPosition, condition);

        commit("edit acceptance criteria");
    }

    /**
     * Removes a condition from the list of acceptance.
     * @param condition The condition to remove.
     */
    public final void removeAcceptanceCondition(final AcceptanceCondition condition) {
        acceptanceCriteria.remove(condition);
        // If we have no acceptance criteria then we shouldn't have an estimate or a story state other than none.
        if (acceptanceCriteria.size() == 0) {
            estimate = EstimateType.NOT_ESTIMATED;
            storyState = StoryState.None;
        }
        commit("edit acceptance criteria");
    }

    /**
     * Gets the current state of the story.
     * @return The current state of the story
     */
    public final StoryState getStoryState() {
        return storyState;
    }

    /**
     * Sets the current state of a story. We're trusting you don't
     * do something silly here. Don't let us down.
     * @param newState The new state for the story.
     */
    public final void setStoryState(final StoryState newState) {
        if (storyState == newState) {
            return;
        }
        storyState = newState;
        commit("edit story");
    }

    /**
     * Gets the creator of this story.
     * @return The creator
     */
    public final Person getCreator() {
        return creator;
    }

    /**
     * Sets the creator of the story.
     * This should not be changed after initially being set.
     * @param person The creator
     */
    public final void setCreator(final Person person) {
        creator = person;
        commit("edit story");
    }

    /**
     * Gets the estimate value.
     * @return The estimate value.
     */
    public final String getEstimate() {
        return estimate;
    }

    /**
     * Sets the estimate for the story.
     * @param newEstimate The estimate.
     */
    public final void setEstimate(final String newEstimate) {
        if (newEstimate.equals(estimate)) {
            return;
        }
//        // If you change the estimate type to not estimated, then None is the only valid story state
//        if (newEstimate.equals(EstimateType.NOT_ESTIMATED)) {
//            storyState = StoryState.None;
//        }
        estimate = newEstimate;
        commit("edit story");
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
        if (object == null || !(object instanceof Story)) {
            return false;
        }
        String shortNameO = ((Story) object).getShortName();
        String shortName = getShortName();
        if (shortName == null || shortNameO == null) {
            return shortName == shortNameO;
        }
        return shortName.toLowerCase().equals(shortNameO.toLowerCase());
    }
}
