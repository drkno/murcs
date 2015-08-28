package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Objects;

/**
 * A class for keeping track of a Task within a story.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Task extends TrackableObject implements Serializable {

    /**
     * Serialisation ID for backwards compatible serialisation.
     */
    private static final long serialVersionUID = 0L;

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
     * The estimate in hours for the time it will take to complete the task.
     */
    @TrackableValue
    private float estimate;

    /**
     * The state that the task is currently in.
     */
    @TrackableValue
    private TaskState state;

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
        return state + " (" + estimate + "): " + name + " - " + description;
    }
}
