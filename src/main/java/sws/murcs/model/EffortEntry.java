package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableObject;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Model object for effort spent on a Task.
 */
public class EffortEntry extends TrackableObject implements Serializable, PersonMaintainer {

    /**
     * The people who logged the effort.
     */
    private Collection<Person> people = new ArrayList<>();

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
     * Gets the people who logged this effort.
     * @return The people who logged this effort
     */
    public Collection<Person> getPeople() {
        return Collections.unmodifiableCollection(people);
    }

    /**
     * Adds a person to this effort entry.
     * @param person The new person
     * @return true, if adding was successful. false otherwise.
     */
    public boolean addPerson(final Person person) {
        return people.add(person);
    }

    /**
     * Removes a person from this effort entry.
     * @param person the person to remove.
     * @return true, if removing was successful. false otherwise.
     */
    public boolean removePerson(final Person person) {
        return people.remove(person);
    }

    /**
     * Gets the total effort logged by all members in this entry.
     * @return The effort logged
     */
    public float getEffort() {
        return effort * people.size();
    }

    /**
     * Sets the effort logged. This is the effort spent by any one
     * individual person who contributed to this entry.
     * @param effort The effort logged
     */
    public void setEffort(final float effort) {
        this.effort = effort;
    }

    /**
     * Gets the effort for any one individual who partook in the effort
     * this effort entry represents.
     * @return the set effort.
     */
    public final float getSetEffort() {
        return effort;
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

    /**
     * Gets the list of people for this effort entry and returns their short names as a comma seperated string.
     * @return as described in the above documentation.
     */
    public String getPeopleAsString() {
        return people.stream().map(Person::getShortName).sorted().collect(Collectors.joining(", "));
    }
}
