package sws.murcs.controller.pipes;

import sws.murcs.model.Person;

/**
 * Contains all the functions that a parent editor of the assignee controller must implement.
 */
public interface AssigneeControllerParent {

    void addPerson(Person person);

    void removePerson(Person person);
}
