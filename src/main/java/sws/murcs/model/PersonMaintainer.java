package sws.murcs.model;

import java.util.Collection;

public interface PersonMaintainer {

    boolean addPerson(Person person);
    boolean removePerson(Person person);
    Collection<Person> getPeople();
}
