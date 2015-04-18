package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.debug.sampledata.Generator;
import sws.murcs.debug.sampledata.PersonGenerator;
import sws.murcs.debug.sampledata.SkillGenerator;
import sws.murcs.debug.sampledata.TeamGenerator;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.model.*;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

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
    private Project project;

    @BeforeClass
    public static void oneTimeSetUp() {
        String[] skills = {"skill1", "skill2", "skill3"};
        String[] descriptions = {"description1", "description2", "description3"};
        String[] teamNames = {"name1", "name2", "name3"};

        teamGenerator = new TeamGenerator(personGenerator, teamNames, descriptions, 0.5f, 0.5f);
        personGenerator = new PersonGenerator(skillGenerator);
        skillGenerator = new SkillGenerator(skills, descriptions);
    }

    @Before
    public void setUp() {
        teamGenerated = new TeamGenerator().generate();
        unassignedPersonGenerated = new PersonGenerator().generate();
        skillGenerated = new SkillGenerator().generate();
        unassignedPerson = new Person();
        relationalModel = new RelationalModel();
        team = new Team();
        skill = new Skill();
        project = new Project();
        relationalModel.setProject(project);
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

        relationalModel.addPerson(unassignedPersonGenerated);
        assertTrue(relationalModel.getUnassignedPeople().contains(unassignedPersonGenerated));
    }

    @Test (expected = DuplicateObjectException.class)
    public void addUnassignedPersonExceptionTest1() throws Exception {
        relationalModel.addPerson(unassignedPersonGenerated);
        relationalModel.addPerson(unassignedPersonGenerated);
    }

    @Test (expected = DuplicateObjectException.class)
    public void addUnassignedPersonExceptionTest2() throws Exception {
        relationalModel.addPerson(unassignedPersonGenerated);
        unassignedPerson.setShortName(unassignedPersonGenerated.getShortName());
        relationalModel.addPerson(unassignedPerson);
    }

    @Test
    public void addUnassignedPeopleTest() throws Exception {
        List<Person> testUnassignedPeople = new ArrayList<>();
        assertEquals(relationalModel.getUnassignedPeople().size(), 0);
        testUnassignedPeople.add(unassignedPersonGenerated);

        relationalModel.addPeople(testUnassignedPeople);
        assertTrue(relationalModel.getUnassignedPeople().contains(unassignedPersonGenerated));

        testUnassignedPeople.add(unassignedPersonGenerated);
        assertEquals(testUnassignedPeople.size(), 2);
        assertEquals(relationalModel.getUnassignedPeople().size(), 1);
    }

    @Test
    public void removeUnassignedPersonTest() throws Exception {
        relationalModel.addPerson(unassignedPersonGenerated);
        assertTrue(relationalModel.getUnassignedPeople().contains(unassignedPersonGenerated));

        relationalModel.removePerson(unassignedPersonGenerated);
        assertFalse(relationalModel.getUnassignedPeople().contains(unassignedPersonGenerated));

        relationalModel.removePerson(unassignedPersonGenerated);
        assertFalse(relationalModel.getUnassignedPeople().contains(unassignedPersonGenerated));
    }

    @Test
    public void addUnassignedTeamTest() throws Exception {
        assertFalse(relationalModel.getUnassignedTeams().contains(teamGenerated));

        relationalModel.addTeam(teamGenerated);
        assertTrue(relationalModel.getUnassignedTeams().contains(teamGenerated));
    }

    @Test (expected = DuplicateObjectException.class)
    public void addUnassignedTeamExceptionTest1() throws Exception {
        relationalModel.addTeam(teamGenerated);
        relationalModel.addTeam(teamGenerated);
    }

    @Test (expected = DuplicateObjectException.class)
    public void addUnassignedTeamExceptionTest2() throws Exception {
        relationalModel.addTeam(teamGenerated);
        team.setShortName(teamGenerated.getShortName());
        relationalModel.addTeam(teamGenerated);
    }

    @Test
    public void addUnassignedTeamsTest() throws Exception {
        List<Team> testUnassignedTeams = new ArrayList<>();
        assertEquals(relationalModel.getUnassignedTeams().size(), 0);
        testUnassignedTeams.add(teamGenerated);

        relationalModel.addTeams(testUnassignedTeams);
        assertTrue(relationalModel.getUnassignedTeams().contains(teamGenerated));

        testUnassignedTeams.add(teamGenerated);
        assertEquals(testUnassignedTeams.size(), 2);
        assertEquals(relationalModel.getUnassignedTeams().size(), 1);
    }

    @Test
    public void removeUnassignedTeamTest() throws Exception {
        relationalModel.addTeam(teamGenerated);
        assertTrue(relationalModel.getUnassignedTeams().contains(teamGenerated));

        relationalModel.removeTeam(teamGenerated);
        assertFalse(relationalModel.getUnassignedTeams().contains(teamGenerated));

        relationalModel.removeTeam(teamGenerated);
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
        assertEquals(relationalModel.getSkills().size(), 2);
        testSkills.add(skillGenerated);

        relationalModel.addSkills(testSkills);
        assertTrue(relationalModel.getSkills().contains(skillGenerated));

        testSkills.add(skillGenerated);
        assertEquals(testSkills.size(), 2);
        assertEquals(relationalModel.getSkills().size(), 3);
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

    @Test
    public void testFindUsagesProject() throws Exception{
        Project newProject = new Project();

        assertEquals("If the project is not attached to the model it should not be in use", 0, relationalModel.findUsages(newProject).size());

        relationalModel.setProject(newProject);
        assertEquals("Projects should not ever have any usages", 0, relationalModel.findUsages(newProject).size());
    }

    @Test
    public void testFindUsagesTeam()throws Exception{
        relationalModel.setProject(new Project());
        relationalModel.getTeams().clear();

        Team newTeam = (new TeamGenerator()).generate();

        assertEquals("Teams not attached to the model should not have any usages", 0, relationalModel.findUsages(newTeam).size());

        relationalModel.add(newTeam);
        assertEquals("A team should have no usages when it is not used", 0, relationalModel.findUsages(newTeam).size());

        relationalModel.getProject().addTeam(newTeam);
        assertEquals("The team should be used in one place", 1, relationalModel.findUsages(newTeam).size());
        assertEquals("The team should be used by the project", relationalModel.getProject(), relationalModel.findUsages(newTeam).get(0));

    }

    @Test
    public void testInUseProject() throws Exception{
        Project newProject = new Project();

        assertFalse("If the project is not attached to the model it should not be in use", relationalModel.inUse(newProject));

        relationalModel.setProject(newProject);
        assertFalse("Projects should not be marked as in use even when they are attached to the model", relationalModel.inUse(newProject));
    }
}
