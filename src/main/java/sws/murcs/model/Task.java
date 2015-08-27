package sws.murcs.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.collections.map.HashedMap;
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
     * The people who are assigned to the task. These may just be the people overseeing its
     * completion.
     */
    private Collection<Person> assignees = new ArrayList<>();

    /**
     * A map of the time remaining on specific days
     */
    @TrackableValue
    private Map<LocalDate, Float> estimates = new HashedMap();

    /**
     * The effort people have logged against this task.
     */
    private Collection<Effort> logs = new ArrayList<>();

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
     * Gets the estimate for the current day.
     * @return The estimate for today
     */
    public final float getEstimate() {
        return getEstimate(LocalDate.now());
    }

    /**
     * Gets the current estimate for the task. This is given in hours.
     * @param day The day to get the estimate for
     * @return The current estimate for the task in hours.
     */
    public final float getEstimate(final LocalDate day) {
        LocalDate lastDate = null;
        for (LocalDate estimateDate : estimates.keySet()) {
            //If the estimate date is after our last date and before the day we're looking for
            if ((estimateDate.isBefore(day)
                    && (lastDate == null || lastDate.isBefore(estimateDate)))
                    || estimateDate.isEqual(day)) {
                lastDate = estimateDate;
            }
        }

        //If this day is before we have any estimates, return 0. Otherwise return the last
        //date before the day we asked for
        if (lastDate != null) {
            return estimates.get(lastDate);
        } else {
            return 0;
        }
    }

    /**
     * Updates the estimate for the current day.
     * @param newEstimate The new estimate for today
     */
    public final void setEstimate(final float newEstimate) {
        setEstimate(newEstimate, LocalDate.now());
    }

    /**
     * Sets the estimate for the task in hours.
     * @param newEstimate The new estimate for the task.
     * @param day The day you want to change the estimate for.
     */
    public final void setEstimate(final float newEstimate, final LocalDate day) {
        LocalDate previousEstimateDate = null;

        for (LocalDate estimateDate : estimates.keySet()) {
            if ((estimateDate.isBefore(day)
                    && (previousEstimateDate == null || previousEstimateDate.isBefore(estimateDate)))
                    || estimateDate.isEqual(day)) {
                previousEstimateDate = estimateDate;
            }
        }

        float difference = newEstimate;
        if (previousEstimateDate != null) {
            difference = newEstimate - estimates.get(previousEstimateDate);
        }

        //Either update the estimate or add in the new estimate
        estimates.put(day, newEstimate);

        //Update all the estimates after our new one
        for (LocalDate estimateDate : estimates.keySet()) {
            if (estimateDate.isAfter(day)) {
                float currentEstimate = estimates.get(estimateDate);
                //Make sure we only have positive or zero estimates
                estimates.put(estimateDate, Math.max(0, currentEstimate + difference));
            }
        }

        commit("edit task");
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
        return state + " (" + getEstimate() + "): " + name + " - " + description;
    }

    public Collection<Person> getAssignees() {
        return assignees;
    }

    public String getAssigneesAsString() {
        return assignees.stream().map(Person::getShortName).collect(Collectors.joining(", "));
    }
}
