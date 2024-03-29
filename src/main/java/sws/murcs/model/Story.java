package sws.murcs.model;

import sws.murcs.exceptions.CyclicDependencyException;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.internationalization.InternationalizationHelper;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.helpers.DependenciesHelper;
import sws.murcs.search.Searchable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class representing a story in the backlog for a project.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Story extends Model {

    /**
     * Serialisation ID for backwards compatible serialisation.
     */
    private static final long serialVersionUID = 0L;

    /**
     * Represents the current state of a story.
     */
    public enum StoryState {

        /**
         * Indicates that the story state has not yet been set.
         */
        None,

        /**
         * Indicates that a story is ready to be pulled into a sprint.
         */
        Ready,

        /**
         * Indicates that this story has been completed.
         */
        Done;

        @Override
        public String toString() {
            return InternationalizationHelper.tryGet(super.toString());
        }
    }

    /**
     * Indicates the current state of the story
     * (e.g. ready, not ready, in progress)
     */
    @Searchable
    @TrackableValue
    private StoryState storyState;

    /**
     * A list of the conditions that have to be met before this
     * story can be marked as done. This has been made a list
     * (as opposed to a Collection) as order is important.
     */
    @Searchable
    @TrackableValue
    @XmlElementWrapper(name = "acceptanceCriteria")
    @XmlElement(name = "acceptanceCriterion")
    private List<AcceptanceCondition> acceptanceCriteria;

    /**
     * The list of tasks associated with this story.
     */
    @Searchable
    @TrackableValue
    @XmlElementWrapper(name = "tasks")
    @XmlElement(name = "task")
    private List<Task> tasks;

    /**
     * The person who created this story. This should not be changed after
     * initial creation.
     */
    @Searchable
    @TrackableValue
    @XmlIDREF
    private Person creator;

    /**
     * Stories that must be complete before this story can be worked on.
     */
    @Searchable
    @XmlElementWrapper(name = "dependencies")
    @XmlElement(name = "dependence")
    @XmlIDREF
    private Collection<Story> dependencies;

    /**
     * Creates and initializes a new story.
     */
    public Story() {
        acceptanceCriteria = new ArrayList<>();
        tasks = new ArrayList<>();
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
            UndoRedoManager.get().add(condition);
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
        if (newEstimate.equals(EstimateType.INFINITE) || newEstimate.equals(EstimateType.NOT_ESTIMATED)) {
            // The story state must now be set to None.
            startAssimilation();
            setStoryState(StoryState.None);
            estimate = newEstimate;
            endAssimilation("edit story");
        }
        else {
            estimate = newEstimate;
            commit("edit story");
        }
    }

    /**
     * Adds a new task to the list of tasks the story has.
     * @param newTask The new task to add
     * @throws DuplicateObjectException If there's a duplicate....
     */
    public final void addTask(final Task newTask) throws DuplicateObjectException {
        if (!tasks.contains(newTask)) {
            tasks.add(newTask);
            UndoRedoManager.get().add(newTask);
        } else {
            throw new DuplicateObjectException("You can't add two of the same task to a story!");
        }
        commit("edit story");
    }

    /**
     * Removes the specified task from the list of tasks associated with the story.
     * @param task The task to be removed.
     */
    public final void removeTask(final Task task) {
        if (tasks.contains(task)) {
            tasks.remove(task);
            UndoRedoManager.get().remove(task);
            commit("edit story");
        }
    }

    /**
     * Gets the list of all the tasks associated with this story.
     * @return The list of all the tasks associated with this story.
     */
    public final List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Gets the estimation info for the story.
     * @return The estimation info for this task.
     */
    public EstimateInfo getEstimationInfo() {
        EstimateInfo estimatedTimeRemaining = new EstimateInfo();
        for (Task task : getTasks()) {
            estimatedTimeRemaining.mergeIn(task.getEstimateInfo());
        }
        return estimatedTimeRemaining;
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
        if (!(object instanceof Story)) {
            return false;
        }
        String shortNameO = ((Story) object).getShortName();
        String shortName = getShortName();
        if (shortName == null || shortNameO == null) {
            return shortName == shortNameO;
        }
        return shortName.equalsIgnoreCase(shortNameO);
    }

    /**
     * Gets the pair programming groups that occurred in this story.
     * @return the groups that peer programmed together.
     */
    @XmlElementWrapper(name = "pairs")
    @XmlElement(name = "pair")
    public final List<PeerProgrammingGroup> getPairProgrammingGroups() {
        List<List<EffortEntry>> effortEntries = getTasks().stream()
                .map(Task::getEffort)
                .collect(Collectors.toList());

        float total = effortEntries.stream().filter(e -> e.size() > 0)
                .map(e -> e.get(0).getEffort()).reduce((a, b) -> a + b).orElse(0f);

        Map<String, Float> map = new HashMap<>();
        effortEntries.stream().flatMap(Collection::stream)
                .filter(e -> e.getPeople().size() > 1).forEach(e -> {
            String name = e.getPeopleAsString();
            if (map.containsKey(name)) {
                map.put(name, map.get(name) + e.getSetEffort());
            } else {
                map.put(name, e.getSetEffort());
            }
        });

        return map.entrySet().stream()
                .map(e -> new PeerProgrammingGroup(e.getKey(), e.getValue(), total)).collect(Collectors.toList());
    }
}
