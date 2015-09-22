package sws.murcs.controller.pipes;

import sws.murcs.model.Person;
import sws.murcs.model.PersonMaintainer;

/**
 * Contains all the functions that a parent editor of the assignee controller must implement.
 */
public interface AssigneeControllerParent {

    void addPerson(Person person);

    void removePerson(Person person);

    PersonMaintainer getMaintainer();
}
