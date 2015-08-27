package sws.murcs.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;

/**
 * A class for keeping track of a Task within a story.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Task extends TrackableObject implements Serializable {

    /**
     * A prime number used in the hash code.
     */
    @XmlTransient
    private final int hashCodePrime = 43;

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
    private TaskState state;

    /**
     * The estimated time for this task.
     */
    @TrackableValue
    private EstimateInfo estimateInfo = new EstimateInfo();

    /**
     * The people who are assigned to the task. These may just be the people overseeing its
     * completion.
     */
    private Collection<Person> assignees = new ArrayList<>();

    /**
     * The effort people have logged against this task.
     */
    @TrackableValue
    private List<Effort> effortLogs = new ArrayList<>();

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
        state = newState;
        commit("edit Task");
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
     * Sets the estimated time remaining for the task
     * @param newEstimate The estimated time remaining
     */
    public void setCurrentEstimate(final float newEstimate) {
        this.estimateInfo.setEstimateForDay(newEstimate, LocalDate.now());
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
    }

    /**
     * Gets the effort logged against this task.
     * @return The effort.
     */
    public final List<Effort> getEffort() {
        return effortLogs;
    }

    /**
     * Logs some effort.
     * @param effort The effort to log
     */
    public final void logEffort(Effort effort) {
        effortLogs.add(effort);

        commit("log effort");
    }

    /**
     * Unlogs some effort.
     * @param effort The effort to remove
     */
    public final void unlogEffort(Effort effort) {
        effortLogs.remove(effort);

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
        String shortName = getName();
        String shortNameO = ((Task) object).getName();
        if (shortName == null || shortNameO == null) {
            return Objects.equals(shortName, shortNameO);
        }
        return shortName.equalsIgnoreCase(shortNameO.toLowerCase());
    }

    @Override
    public final int hashCode() {
        int c = 0;
        if (getName() != null) {
            c = getName().hashCode();
        }
        return hashCodePrime + c;
    }

    @Override
    public final String toString() {
        return state + " (" + getCurrentEstimate() + "): " + name + " - " + description;
    }

    public Collection<Person> getAssignees() {
        return assignees;
    }

    public String getAssigneesAsString() {
        return assignees.stream().map(Person::getShortName).collect(Collectors.joining(", "));
    }
}
