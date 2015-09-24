package sws.murcs.controller.pipes;

import sws.murcs.model.Person;
import sws.murcs.model.PersonMaintainer;

/**
 * Contains all the functions that a parent editor of the person manager controller must implement.
 */
public interface PersonManagerControllerParent {

    /**
     * Adds a person to the person maintainer in the controller parent.
     * @param person the person to add
     */
    void addPerson(Person person);

    /**
     * Removes a person in the person maintainer in the controller parent.
     * @param person the person to be removed
     */
    void removePerson(Person person);

    /**
     * Gets the person maintainer of the parent controller.
     * @return the person maintainer
     */
    PersonMaintainer getMaintainer();
}
