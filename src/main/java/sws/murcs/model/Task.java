package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

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
     * The hashcode of the object.
     */
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
     * The estimate in hours for the time it will take to complete the task.
     */
    @TrackableValue
    private float estimate;

    /**
     * The state that the task is currently in.
     */
    @TrackableValue
    private TaskState state = TaskState.NotStarted;

    /**
     * The people who are assigned to the task. These may just be the people overseeing its
     * completion.
     */
    @TrackableValue
    private Collection<Person> assignees = new ArrayList<>();

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
     * Gets the current estimate for the task. This is given in hours.
     * @return The current estimate for the task in hours.
     */
    public final float getEstimate() {
        return estimate;
    }

    /**
     * Sets the estimate for the task in hours.
     * @param newEstimate The new estimate for the task.
     */
    public final void setEstimate(final float newEstimate) {
        estimate = newEstimate;
        commit("edit Task");
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
        same = same && objectTask.getEstimate() == getEstimate()
                && Objects.equals(objectTask.getDescription(), getDescription())
                && objectTask.getState().equals(getState())
                && objectTask.getAssigneesAsString().equals(getAssigneesAsString());
        return same;
    }

    @Override
    public final int hashCode() {
        if (hashCode == null ) {
            int c = 0;
            if (getName() != null) {
                c = getName().hashCode()
                        + getDescription().hashCode()
                        + getState().hashCode()
                        + Float.hashCode(getEstimate())
                        + getAssigneesAsString().hashCode();
            }
            hashCode = hashCodePrime + c;
        }
        return hashCode;
    }

    @Override
    public final String toString() {
        return state + " (" + estimate + "): " + name + " - " + description;
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
}
