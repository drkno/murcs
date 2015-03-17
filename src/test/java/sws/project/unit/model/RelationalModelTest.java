package sws.project.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.project.exceptions.DuplicateObjectException;
import sws.project.model.Person;
import sws.project.model.RelationalModel;
import sws.project.model.Skill;
import sws.project.model.Team;
import sws.project.sampledata.*;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class RelationalModelTest {

    private static Generator<Team> teamGenerator;
    private static Generator<Person> personGenerator;
    private static Generator<Skill> skillGenerator;
    private Team teamGenerated;
    private Team team;
    private Person unassignedPerson;
    private Person unassignedPersonGenerated;
    private Skill skillGenerated;
    private Skill skill;
    private RelationalModel relationalModel;

    @BeforeClass
    public static void oneTimeSetUp() {
        teamGenerator = new TeamGenerator();
        personGenerator = new PersonGenerator();
        skillGenerator = new SkillGenerator();
    }

    @Before
    public void setUp() {
        teamGenerated = teamGenerator.generate();
        unassignedPersonGenerated = personGenerator.generate();
        skillGenerated = skillGenerator.generate();
        unassignedPerson = new Person();
        relationalModel = new RelationalModel();
        team = new Team();
        skill = new Skill();
    }

    @After
    public void tearDown() {
        teamGenerated = null;
        relationalModel = null;
        unassignedPerson = null;
        unassignedPersonGenerated = null;
        team = null;
        skill = null;
        skillGenerated = null;
    }

    @Test
    public void addUnassignedPersonTest() throws Exception {
        assertFalse(relationalModel.getUnassignedPeople().contains(unassignedPersonGenerated));

        relationalModel.addUnassignedPerson(unassignedPersonGenerated);
        assertTrue(relationalModel.getUnassignedPeople().contains(unassignedPersonGenerated));
    }

    @Test (expected = DuplicateObjectException.class)
    public void addUnassignedPersonExceptionTest1() throws Exception {
        relationalModel.addUnassignedPerson(unassignedPersonGenerated);
        relationalModel.addUnassignedPerson(unassignedPersonGenerated);
    }

    @Test (expected = DuplicateObjectException.class)
    public void addUnassignedPersonExceptionTest2() throws Exception {
        relationalModel.addUnassignedPerson(unassignedPersonGenerated);
        unassignedPerson.setShortName(unassignedPersonGenerated.getShortName());
        relationalModel.addUnassignedPerson(unassignedPerson);
    }

    @Test
    public void addUnassignedPeopleTest() throws Exception {
        List<Person> testUnassignedPeople = new ArrayList<>();
        assertEquals(relationalModel.getUnassignedPeople().size(), 0);
        testUnassignedPeople.add(unassignedPersonGenerated);

        relationalModel.addUnassignedPeople(testUnassignedPeople);
        assertTrue(relationalModel.getUnassignedPeople().contains(unassignedPersonGenerated));

        testUnassignedPeople.add(unassignedPersonGenerated);
        assertEquals(testUnassignedPeople.size(), 2);
        assertEquals(relationalModel.getUnassignedPeople().size(), 1);
    }

    @Test
    public void removeUnassignedPersonTest() throws Exception {
        relationalModel.addUnassignedPerson(unassignedPersonGenerated);
        assertTrue(relationalModel.getUnassignedPeople().contains(unassignedPersonGenerated));

        relationalModel.removeUnassignedPerson(unassignedPersonGenerated);
        assertFalse(relationalModel.getUnassignedPeople().contains(unassignedPersonGenerated));

        relationalModel.removeUnassignedPerson(unassignedPersonGenerated);
        assertFalse(relationalModel.getUnassignedPeople().contains(unassignedPersonGenerated));
    }

    @Test
    public void addUnassignedTeamTest() throws Exception {
        assertFalse(relationalModel.getUnassignedTeams().contains(teamGenerated));

        relationalModel.addUnassignedTeam(teamGenerated);
        assertTrue(relationalModel.getUnassignedTeams().contains(teamGenerated));
    }

    @Test (expected = DuplicateObjectException.class)
    public void addUnassignedTeamExceptionTest1() throws Exception {
        relationalModel.addUnassignedTeam(teamGenerated);
        relationalModel.addUnassignedTeam(teamGenerated);
    }

    @Test (expected = DuplicateObjectException.class)
    public void addUnassignedTeamExceptionTest2() throws Exception {
        relationalModel.addUnassignedTeam(teamGenerated);
        team.setShortName(teamGenerated.getShortName());
        relationalModel.addUnassignedTeam(teamGenerated);
    }

    @Test
    public void addUnassignedTeamsTest() throws Exception {
        List<Team> testUnassignedTeams = new ArrayList<>();
        assertEquals(relationalModel.getUnassignedTeams().size(), 0);
        testUnassignedTeams.add(teamGenerated);

        relationalModel.addUnassignedTeams(testUnassignedTeams);
        assertTrue(relationalModel.getUnassignedTeams().contains(teamGenerated));

        testUnassignedTeams.add(teamGenerated);
        assertEquals(testUnassignedTeams.size(), 2);
        assertEquals(relationalModel.getUnassignedTeams().size(), 1);
    }

    @Test
    public void removeUnassignedTeamTest() throws Exception {
        relationalModel.addUnassignedTeam(teamGenerated);
        assertTrue(relationalModel.getUnassignedTeams().contains(teamGenerated));

        relationalModel.removeUnassignedTeam(teamGenerated);
        assertFalse(relationalModel.getUnassignedTeams().contains(teamGenerated));

        relationalModel.removeUnassignedTeam(teamGenerated);
        assertFalse(relationalModel.getUnassignedTeams().contains(teamGenerated));
    }

    @Test
    public void addSkillTest() throws Exception {
        assertFalse(relationalModel.getSkills().contains(skillGenerated));

        relationalModel.addSkill(skillGenerated);
        assertTrue(relationalModel.getSkills().contains(skillGenerated));
    }

    @Test (expected = DuplicateObjectException.class)
    public void addSkillExceptionTest1() throws Exception {
        relationalModel.addSkill(skillGenerated);
        relationalModel.addSkill(skillGenerated);
    }

    @Test (expected = DuplicateObjectException.class)
    public void addSkillExceptionTest2() throws Exception {
        relationalModel.addSkill(skillGenerated);
        skill.setShortName(skillGenerated.getShortName());
        relationalModel.addSkill(skill);
    }

    @Test
    public void addSkillsTest() throws Exception {
        List<Skill> testSkills = new ArrayList<>();
        assertEquals(relationalModel.getSkills().size(), 0);
        testSkills.add(skillGenerated);

        relationalModel.addSkills(testSkills);
        assertTrue(relationalModel.getSkills().contains(skillGenerated));

        testSkills.add(skillGenerated);
        assertEquals(testSkills.size(), 2);
        assertEquals(relationalModel.getSkills().size(), 1);
    }

    @Test
    public void removeSkillTest() throws Exception {
        relationalModel.addSkill(skillGenerated);
        assertTrue(relationalModel.getSkills().contains(skillGenerated));

        relationalModel.removeSkill(skillGenerated);
        assertFalse(relationalModel.getSkills().contains(skillGenerated));

        relationalModel.removeSkill(skillGenerated);
        assertFalse(relationalModel.getSkills().contains(skillGenerated));
    }
}
