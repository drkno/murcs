package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableObject;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Model object for effort spent on a Task.
 */
public class Effort extends TrackableObject implements Serializable {

    /**
     * The Person who logged the effort.
     */
    private Person person;

    /**
     * The amount of effort spent, may be a time measurement.
     */
    private float effort;

    /**
     * The description of the work done.
     */
    private String description;

    /**
     * The day the work was done.
     */
    private LocalDate date;

    /**
     * Gets the person.
     * @return The person
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Sets the person.
     * @param person The new person
     */
    public void setPerson(final Person person) {
        this.person = person;
    }

    /**
     * Gets the effort logged.
     * @return The effort logged
     */
    public float getEffort() {
        return effort;
    }

    /**
     * Sets the effort logged.
     * @param effort The effort logged
     */
    public void setEffort(final float effort) {
        this.effort = effort;
    }

    /**
     * Gets the description.
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets a new description.
     * @param description The new description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the date this effort is logged for.
     * @return The date this effort is logged for.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date this effort should be logged for.
     * @param theDate The date this effort should be logged for.
     */
    public void setDate(final LocalDate theDate) {
        date = theDate;
    }
}
