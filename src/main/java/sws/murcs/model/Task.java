package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A class for keeping track of a Task within a story.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Task extends TrackableObject implements Serializable, PersonMaintainer {

    /**
     * Serialisation ID for backwards compatible serialisation.
     */
    @XmlTransient
    private static final long serialVersionUID = 0L;

    /**
     * A prime number used in the hash code.
     */
    @XmlTransient
    private final int hashCodePrime = 43;

    /**
     * The hashcode of the object.
     */
    @XmlTransient
    private Integer hashCode = null;

    /**
     * The name associated with this Task.
     */
    @TrackableValue
    private String name;

    /**
     * The description of this Task.
     */
    @TrackableValue
    private String description;

    /**
     * The state that the task is currently in.
     */
    @TrackableValue
    private TaskState state = TaskState.NotStarted;

    /**
     * The estimated time for this task.
     */
    @TrackableValue
    private EstimateInfo estimateInfo = new EstimateInfo();

    /**
     * The people who are assigned to the task. These may just be the people overseeing its completion.
     */
    @TrackableValue
    private Collection<Person> assignees = new ArrayList<>();

    /**
     * The effort people have logged against this task.
     */
    @TrackableValue
    private List<EffortEntry> effortEntryLogs = new ArrayList<>();

    /**
     * Date this task was marked as done.
     */
    @TrackableValue
    private LocalDate completedDate;

    /**
     * Creates a new task.
     */
    public Task() {
        UndoRedoManager.get().add(estimateInfo);
    }

    /**
     * Gets this tasks name.
     * @return The name of this task
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets a new name to this task.
     * @param newName The new name
     */
    public final void setName(final String newName) {
        name = newName;
        commit("edit Task");
    }

    /**
     * Gets the description of this Task.
     * @return The description of this Task
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the newDescription of this Task.
     * @param newDescription The new newDescription
     */
    public final void setDescription(final String newDescription) {
        description = newDescription;
        commit("edit Task");
    }

    /**
     * Gets the current state of the task.
     * @return The current state.
     */
    public final TaskState getState() {
        return state;
    }

    /**
     * Sets the state for the task.
     * @param newState The state that it's being changed to.
     */
    public final void setState(final TaskState newState) {
        completedDate = newState == TaskState.Done ? LocalDate.now() : null;
        state = newState;
        commit("edit Task");
    }

    /**
     * Gets the date that this task was completed.
     * @return the date the task was completed or null if not completed.
     */
    public final LocalDate getCompletedDate() {
        return completedDate;
    }

    /**
     * Sets the date that this task was completed.
     * @param date new completion date.
     * @deprecated Do not use this method. This is only here as a hook for data generation.
     * Use of this method could result in unforeseen consequences.
     */
    @Deprecated
    public void setCompletedDate(final LocalDate date) {
        completedDate = date;
    }

    /**
     * Gets the estimated time information about this task.
     * @return The estimateInfo information for this task.
     */
    public EstimateInfo getEstimateInfo() {
        return estimateInfo;
    }

    /**
     * Gets the estimated time remaining for the task
     * at the current date.
     * @return The estimated time remaining.
     */
    public float getCurrentEstimate() {
        return estimateInfo.getCurrentEstimate();
    }

    /**
     * Sets the estimated time remaining for the task.
     * @param newEstimate The estimated time remaining
     */
    public void setCurrentEstimate(final float newEstimate) {
        if (newEstimate < 0 || Float.isInfinite(newEstimate)) {
            throw new NumberFormatException("Can't have a negative or infinite number for an estimate.");
        }
        this.estimateInfo.setCurrentEstimate(newEstimate);
    }

    /**
     * Sets the date that this task was estimated.
     * @deprecated This method is so that data generation can produce valid and useful data.
     * It should not be used for any other purpose. If it is, the behaviour is undefined.
     * @param estimationDate the new estimation date.
     */
    @Deprecated
    public void setEstimationDate(final LocalDate estimationDate) {
        Map<LocalDate, Float> estimates = estimateInfo.getEstimates();
        Map<LocalDate, Float> newEstimates = new HashMap<>();
        estimates.forEach((date, estimate) -> {
            newEstimates.put(estimationDate, estimate);
        });
        estimates.clear();
        estimates.putAll(newEstimates);
    }

    /**
     * Adds a given person to the list of people assigned to the task.
     * @param assignee The person to be assigned to the task.
     */
    public final void addAssignee(final Person assignee) {
        assignees.add(assignee);
        commit("edit Task");
    }

    /**
     * Removes an assigned person from the list of assigned people for the task.
     * @param assignee The assignee to remove.
     */
    public final void removeAssignee(final Person assignee) {
        if (assignees.contains(assignee)) {
            assignees.remove(assignee);
        }
        commit("edit Task");
    }

    /**
     * Gets the effort logged against this task.
     * @return The effort.
     */
    public final List<EffortEntry> getEffort() {
        return effortEntryLogs;
    }

    /**
     * Logs some effort.
     * @param effortEntry The effort to log
     */
    public final void logEffort(final EffortEntry effortEntry) {
        effortEntryLogs.add(effortEntry);

        commit("log effort");
    }

    /**
     * Unlogs some effort.
     * @param effortEntry The effort to remove
     */
    public final void unlogEffort(final EffortEntry effortEntry) {
        effortEntryLogs.remove(effortEntry);
        commit("remove effort");
    }

    /**
     * Gets whether or not the task is currently allocated by checking to see if anyone
     * is currently assigned to the task.
     * @return Whether or not the task has been allocated.
     */
    public final boolean isAllocated() {
        return assignees.size() > 0;
    }

    @Override
    public final boolean equals(final Object object) {
        if (!(object instanceof Task)) {
            return false;
        }
        boolean same;
        Task objectTask = (Task) object;
        String shortName = getName();
        String shortNameO = objectTask.getName();
        if (shortName == null || shortNameO == null) {
            same = Objects.equals(shortName, shortNameO);
        }
        else {
            same = shortName.equalsIgnoreCase(shortNameO);
        }
        same = same && objectTask.getCurrentEstimate() == getCurrentEstimate()
                && Objects.equals(objectTask.getDescription(), getDescription())
                && objectTask.getState().equals(getState())
                && objectTask.getAssigneesAsString().equals(getAssigneesAsString());
        return same;
    }

    @Override
    public final int hashCode() {
        //fixme "The hacks are strong with this one" - Dion Vader. This should probably be using a unique id generator
        //but as it is highly unlikely that a task will be made with exactly the same everything we'll leave it.
        if (hashCode == null) {
            int c = 0;
            if (getName() != null) {
                c = getName().hashCode()
                        + getDescription().hashCode()
                        + getState().hashCode()
                        + Float.hashCode(getCurrentEstimate())
                        + getAssigneesAsString().hashCode();
            }
            hashCode = hashCodePrime + c;
        }
        return hashCode;
    }

    @Override
    public final String toString() {
        return state + " (" + getCurrentEstimate() + "): " + name + " - " + description;
    }

    /**
     * Gets the list of all the people assigned to the task.
     * @return the list of all people assigned to the task.
     */
    public Collection<Person> getAssignees() {
        return assignees;
    }

    /**
     * Gets a string representation of all the people assigned to the task.
     * @return all the people assigned to the task seperated by a comma.
     */
    public String getAssigneesAsString() {
        return assignees.stream().map(Person::getShortName).collect(Collectors.joining(", "));
    }

    @Override
    public boolean addPerson(final Person person) {
        addAssignee(person);
        return true;
    }

    @Override
    public boolean removePerson(final Person person) {
        removeAssignee(person);
        return true;
    }

    @Override
    public Collection<Person> getPeople() {
        return getAssignees();
    }
}
