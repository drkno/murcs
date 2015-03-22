package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.model.Person;
import sws.murcs.model.Team;
import sws.murcs.sampledata.Generator;
import sws.murcs.sampledata.PersonGenerator;
import sws.murcs.sampledata.TeamGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TeamTest {


    private static Generator<Team> teamGenerator;
    private static Generator<Person> personGenerator;
    private Team teamGenerated;
    private Team team;
    private Person personGenerated;
    private Person person;

    @BeforeClass
    public static void oneTimeSetUp() {
        teamGenerator = new TeamGenerator();
        personGenerator = new PersonGenerator();
    }

    @Before
    public void setUp() {
        teamGenerated = teamGenerator.generate();
        personGenerated = personGenerator.generate();
        team = new Team();
        person = new Person();
    }

    @After
    public void tearDown() {
        teamGenerated = null;
        personGenerated = null;
        team = null;
        person = null;
    }

    @Test(expected = Exception.class)
    public void setShortNameTest1() throws Exception{
        teamGenerated.setShortName(null);
    }

    @Test(expected = Exception.class)
    public void setShortNameTest2() throws Exception{
        teamGenerated.setShortName("");
    }

    @Test(expected = Exception.class)
    public void setShortNameTest3() throws Exception{
        teamGenerated.setShortName("   \n\r\t");
    }

    @Test
    public void addMemberTest() throws Exception {
        assertFalse(team.getMembers().contains(personGenerated));

        team.addMember(personGenerated);
        assertTrue(team.getMembers().contains(personGenerated));
    }

    @Test (expected = DuplicateObjectException.class)
    public void addMemberExceptionTest1() throws Exception {
        team.addMember(personGenerated);
        team.addMember(personGenerated);
    }

    @Test (expected = DuplicateObjectException.class)
    public void addMemberExceptionTest2() throws Exception {
        team.addMember(personGenerated);
        person.setUserId(personGenerated.getUserId());
        team.addMember(person);
    }

    @Test
    public void addMembersTest() throws Exception {
        List<Person> testPeople = new ArrayList<>();
        assertEquals(team.getMembers().size(), 0);
        testPeople.add(personGenerated);

        team.addMembers(testPeople);
        assertTrue(team.getMembers().contains(personGenerated));

        testPeople.add(personGenerated);
        assertEquals(testPeople.size(), 2);
        assertEquals(team.getMembers().size(), 1);
    }

    @Test
    public void removeMemberTest() throws Exception {
        team.addMember(personGenerated);
        assertTrue(team.getMembers().contains(personGenerated));

        team.removeMember(personGenerated);
        assertFalse(team.getMembers().contains(personGenerated));

        team.removeMember(personGenerated);
        assertFalse(team.getMembers().contains(personGenerated));
    }
}
