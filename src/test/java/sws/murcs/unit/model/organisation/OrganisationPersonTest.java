package sws.murcs.unit.model.organisation;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.debug.sampledata.NameGenerator;
import sws.murcs.debug.sampledata.PersonGenerator;
import sws.murcs.debug.sampledata.OrganisationGenerator;
import sws.murcs.debug.sampledata.TeamGenerator;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Model;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Team;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OrganisationPersonTest {
    private static OrganisationGenerator generator;
    private Organisation model;

    @BeforeClass
    public static void classSetup() {
        generator = new OrganisationGenerator(OrganisationGenerator.Stress.Medium);
        UndoRedoManager.setDisabled(true);
        if (PersistenceManager.getCurrent() == null) {
            PersistenceManager.setCurrent(new PersistenceManager(new FilePersistenceLoader()));
        }
    }

    @AfterClass
    public static void classTearDown() {
        UndoRedoManager.setDisabled(false);
    }

    /**
     * Generates a organisation, and sets it to the currently in use
     * model in the current persistence manager instance.
     * @throws NullPointerException if no persistence manager exists.
     * @return a new organisation.
     */
    private static Organisation getNeworganisation() {
        PersistenceManager.getCurrent().setCurrentModel(null);
        Organisation model = generator.generate();
        PersistenceManager.getCurrent().setCurrentModel(model);
        return model;
    }

    @Before
    public void setup() throws Exception {
        model = getNeworganisation();
    }

    @Test
    public void testGetPeopleNotNullOrEmpty() throws Exception {
        Organisation model = getNeworganisation();
        List<Person> people = model.getPeople();

        Assert.assertNotNull("getPeople() should return people but is null.", people);
        Assert.assertNotEquals("getPeople() should return people but is empty.", 0, people.size());
    }

    @Test
    public void testGetPeoplePersonRemoved() throws Exception {
        List<Person> people = model.getPeople();
        int size = people.size();
        Person removedPerson = people.get(0);
        model.remove(removedPerson);
        people = model.getPeople();

        Assert.assertFalse("People should not contain the removed person.", people.contains(removedPerson));
        Assert.assertNotEquals("People should not be the same size as before removing a person.", size, people.size());
    }

    @Test
    public void testGetPeopleAdded() throws Exception {
        List<Person> people = model.getPeople();
        Person personToAdd = people.get(0);
        model.remove(personToAdd);
        people = model.getPeople();
        int size = people.size();

        model.add(personToAdd);

        Assert.assertTrue("People should contain the added person.", people.contains(personToAdd));
        Assert.assertNotEquals("People should not be the same size as before adding a person.", size, people.size());
    }

    @Test(expected = InvalidParameterException.class)
    public void testPeopleNoShortNameAdded() throws Exception {
        Person person = new Person();
        model.add(person);
    }

    @Test
    public void testPeopleNoShortNameNotAddedRemoved() throws Exception {
        List<Person> people = model.getPeople();
        int size = people.size();
        Person person = new Person();
        model.remove(person);
        Assert.assertEquals("Removing person that isn't in the model should not change the people collection.", size, people.size());
    }

    @Test(expected = DuplicateObjectException.class)
    public void testPeopleDuplicateAdded() throws Exception {
        List<Person> people = model.getPeople();
        model.add(people.get(0));
    }

    @Test
    public void testGetPeopleNoDuplicates() throws Exception {
        List<Person> people = model.getPeople();

        List<Person> personDuplicates = new ArrayList<>();
        people.stream().filter(person -> !personDuplicates.add(person)).forEach(person -> {
            Assert.fail("There cannot be duplicate people returned by getPeople().");
        });
    }

    @Test
    public void testPersonExists() throws Exception {
        List<Person> people = model.getPeople();
        Assert.assertTrue("Person exists but was not found.", UsageHelper.exists(people.get(0)));
    }

    @Test
    public void testPersonDoesNotExist() throws Exception {
        Person person = new Person();
        person.setShortName("testing1234");
        Assert.assertFalse("Person exists when it should not.", UsageHelper.exists(person));
    }

    @Test
    public void testPersonFindUsagesDoesNotExist() throws Exception {
        Person person = new Person();
        person.setShortName("testing1234");
        List<Model> usages = UsageHelper.findUsages(person);

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertEquals("Usages were found for person not in model.", 0, usages.size());
    }

    @Test
    public void testPersonFindUsages() throws Exception {
        List<Person> people = model.getPeople();
        List<Team> teams = model.getTeams();
        try {
            teams.get(0).addMember(people.get(0));
        }
        catch (DuplicateObjectException e) {
            // ignore, we just want to ensure person is in a team
        }
        List<Model> usages = UsageHelper.findUsages(people.get(0));

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertNotEquals("Usages were not found for person.", 0, usages.size());
        Assert.assertTrue("Item should be in use.", UsageHelper.inUse(people.get(0)));
    }

    @Test
    public void testUnassignedPeople() throws Exception {
        Person testPerson = new Person();
        testPerson.setShortName("testing12345");
        testPerson.setUserId("testing12345");
        model.add(testPerson);
        Collection<Person> people = model.getUnassignedPeople();
        Assert.assertFalse("Item should not be in use.", UsageHelper.inUse(people.iterator().next()));
    }

    @Test
    public void testDeletionsCascadePerson() throws Exception{
        PersistenceManager.getCurrent().setCurrentModel(null);
        PersonGenerator personGenerator = new PersonGenerator();
        TeamGenerator teamGenerator = new TeamGenerator();

        //Make sure we're working from a clean slate
        model.getTeams().clear();
        model.getPeople().clear();

        //Create a few teams to add people to
        for (int i = 0; i < 10; i++) {
            Team team = teamGenerator.generate();
            team.setShortName(team.getShortName() + i);

            team.getMembers().clear();
            model.add(team);
        }

        //Add a few people to the model and to the team
        for (int i = 0; i < 100; i++) {
            Person p = personGenerator.generate();

            //Avoid duplicates
            p.setUserId(p.getUserId() + i);
            p.setShortName(p.getShortName() + i);

            model.add(p);
            //Add the person to a random team
            model.getTeams().get(NameGenerator.random(model.getTeams().size())).addMember(p);
        }

        //Remove all the people from the model. This should cascade, removing them from teams too
        for (int i = 0; i < model.getPeople().size(); ++i){
            model.remove(model.getPeople().get(i));
            i--;
        }

        //Check that there are no people in any team now
        for (int i = 0; i < model.getTeams().size(); i++){
            assertEquals("There should be no people in any team", 0 , model.getTeams().get(i).getMembers().size());
        }
    }
}
