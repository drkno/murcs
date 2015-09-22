package sws.murcs.model;

import java.util.Collection;

/**
 * An interface implemented by objects that maintain a list of people. This is used so that we can use the
 * PersonManagerController with these model objects.
 */
public interface PersonMaintainer {

    /**
     * Adds a person to the person maintainer.
     * @param person the person to add
     * @return whether or not the person got added correctly
     */
    boolean addPerson(Person person);

    /**
     * Removes a person from the person maintainer.
     * @param person the person to be removed
     * @return whether or not the person was removed correctly.
     */
    boolean removePerson(Person person);

    /**
     * Gets the list of all people in the person maintainer.
     * @return as described above.
     */
    Collection<Person> getPeople();
}
