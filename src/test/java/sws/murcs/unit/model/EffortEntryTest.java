package sws.murcs.unit.model;

import org.junit.*;
import sws.murcs.debug.sampledata.PersonGenerator;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.EffortEntry;
import sws.murcs.model.Person;

import java.util.Collection;

public class EffortEntryTest {

    private static PersonGenerator personGenerator;

    @BeforeClass
    public static void setupClass() throws Exception {
        personGenerator = new PersonGenerator();
    }

    @Before
    public void setUp() throws Exception {
        UndoRedoManager.get().setDisabled(true);
    }

    @After
    public void tearDown() throws Exception {
        UndoRedoManager.get().setDisabled(false);
    }

    @Test
    public void testAddAndGetPeople() throws Exception {
        EffortEntry effortEntry = new EffortEntry();
        Person person1 = personGenerator.generate(), person2 = personGenerator.generate();
        effortEntry.addPerson(person1);
        effortEntry.addPerson(person2);

        Collection<Person> people = effortEntry.getPeople();
        Assert.assertEquals("Wrong number of people.", 2, people.size());
        Assert.assertTrue("Person could not be found.", people.contains(person1));
        Assert.assertTrue("Person could not be found.", people.contains(person2));
    }

    @Test
    public void testAddDuplicatePerson() throws Exception {
        EffortEntry effortEntry = new EffortEntry();
        Person person = personGenerator.generate();
        Assert.assertTrue(effortEntry.addPerson(person));
        Assert.assertFalse(effortEntry.addPerson(person));

        Collection<Person> people = effortEntry.getPeople();
        Assert.assertEquals("Wrong number of people.", 1, people.size());
        Assert.assertTrue("Person could not be found.", people.contains(person));
    }

    @Test
    public void testRemovePerson() throws Exception {
        EffortEntry effortEntry = new EffortEntry();
        Person person1 = personGenerator.generate(), person2 = personGenerator.generate();
        effortEntry.addPerson(person1);
        effortEntry.addPerson(person2);

        Collection<Person> people = effortEntry.getPeople();
        Assert.assertEquals("Wrong number of people.", 2, people.size());
        effortEntry.removePerson(person1);
        people = effortEntry.getPeople();
        Assert.assertFalse("Person found who shouldn't have been.", people.contains(person1));
        Assert.assertTrue("Person could not be found.", people.contains(person2));
    }

    @Test
    public void testEffort() throws Exception {
        EffortEntry entry = new EffortEntry();
        entry.addPerson(personGenerator.generate());
        entry.addPerson(personGenerator.generate());
        entry.setEffort(10f);
        Assert.assertEquals("Effort was stored incorrectly.", 10, entry.getSetEffort(), 0);
        Assert.assertEquals("Effort was returned incorrectly.", 20, entry.getEffort(), 0);
    }

    @Test
    public void testDescription() throws Exception {
        EffortEntry entry = new EffortEntry();
        entry.setDescription("Hello World");
        Assert.assertEquals("Description was wrong", "Hello World", entry.getDescription());
        entry.setDescription("Hello World1");
        Assert.assertEquals("Description was wrong", "Hello World1", entry.getDescription());
    }

    @Test
    public void testGetDate() throws Exception {

    }

    @Test
    public void testSetDate() throws Exception {

    }
}