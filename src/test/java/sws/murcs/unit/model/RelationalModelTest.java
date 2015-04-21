package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.debug.sampledata.*;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RelationalModelTest {

    private static Generator<Team> teamGenerator;
    private static Generator<Person> personGenerator;
    private static Generator<Skill> skillGenerator;
    private static Generator<Project> projectGenerator;
    private static Generator<Release> releaseGenerator;
    private Team teamGenerated;
    private Team team;
    private Person unassignedPerson;
    private Person unassignedPersonGenerated;
    private Skill skillGenerated;
    private Skill skill;
    private RelationalModel relationalModel;
    private Project projectGenerated;
    private Release releaseGenerated;

    @BeforeClass
    public static void oneTimeSetUp() {
        String[] skills = {"skill1", "skill2", "skill3"};
        String[] descriptions = {"description1", "description2", "description3"};
        String[] teamNames = {"name1", "name2", "name3"};
        String[] projectNames = {"A project", "I have no idea what I am doing :P"};

        skillGenerator = new SkillGenerator(skills, descriptions);
        personGenerator = new PersonGenerator(skillGenerator);
        teamGenerator = new TeamGenerator(personGenerator, teamNames, descriptions, 0.5f, 0.5f);
        projectGenerator = new ProjectGenerator();
        releaseGenerator = new ReleaseGenerator(projectGenerator, descriptions);
        UndoRedoManager.setDisabled(true);
    }

    @Before
    public void setUp() {
        try {
            teamGenerated = teamGenerator.generate();
            unassignedPersonGenerated = personGenerator.generate();
            skillGenerated = skillGenerator.generate();
            projectGenerated = projectGenerator.generate();
            releaseGenerated = releaseGenerator.generate();
            unassignedPerson = new Person();
            relationalModel = new RelationalModel();
            team = new Team();
            skill = new Skill();
            relationalModel.addProject(projectGenerated);
        }
        catch (DuplicateObjectException exception) {
            //
        }
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
        relationalModel.getSkills().clear();
        assertEquals(0, relationalModel.getSkills().size());
        testSkills.add(skillGenerated);

        relationalModel.addSkills(testSkills);
        assertTrue(relationalModel.getSkills().contains(skillGenerated));

        testSkills.add(skillGenerated);
        assertEquals(2, testSkills.size());
        assertEquals(1, relationalModel.getSkills().size());
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
        Project newProject = projectGenerated;

        assertEquals("If the project is not attached to the model it should not be in use", 0, relationalModel.findUsages(newProject).size());
        assertEquals("Projects should not ever have any usages", 0, relationalModel.findUsages(newProject).size());
    }

    @Test
    public void testFindUsagesTeam()throws Exception{
        relationalModel.getTeams().clear();
        relationalModel.getProjects().forEach(p -> p.getTeams().clear());

        Team newTeam = (new TeamGenerator()).generate();

        assertEquals("Teams not attached to the model should not have any usages", 0, relationalModel.findUsages(newTeam).size());

        relationalModel.add(newTeam);
        assertEquals("A team should have no usages when it is not used", 0, relationalModel.findUsages(newTeam).size());

        relationalModel.getProjects().get(0).addTeam(newTeam);
        assertEquals("The team should be used in one place", 1, relationalModel.findUsages(newTeam).size());
        assertEquals("The team should be used by the project", relationalModel.getProjects().get(0), relationalModel.findUsages(newTeam).get(0));

    }

    @Test
    public void testFindUsagesPerson() throws Exception{
        relationalModel.getPeople().clear();
        relationalModel.getTeams().clear();

        Team newTeam = new TeamGenerator().generate();
        newTeam.getMembers().clear();
        relationalModel.add(newTeam);

        Person newPerson = new PersonGenerator().generate();

        assertEquals("A person should have no usages before being added to the model", 0, relationalModel.findUsages(newPerson).size());

        relationalModel.add(newPerson);
        assertEquals("A person should have no usages before being added to team", 0, relationalModel.findUsages(newPerson).size());

        newTeam.addMember(newPerson);
        assertEquals("A person should have one usage upon being added to a team", 1, relationalModel.findUsages(newPerson).size());
        assertEquals("After a person has been added to a team that team should be in their usages", newTeam, relationalModel.findUsages(newPerson).get(0));
    }

    @Test
    public void testFindUsagesSkill() throws Exception{
        relationalModel.getPeople().clear();
        relationalModel.getSkills().clear();

        Person newPerson = new PersonGenerator().generate();
        newPerson.getSkills().clear();
        relationalModel.add(newPerson);

        Skill newSkill = new SkillGenerator().generate();

        assertEquals("A skill should have no usages before being added to the model", 0, relationalModel.findUsages(newSkill).size());

        relationalModel.add(newSkill);
        assertEquals("A skill should have no usages before being added to person", 0, relationalModel.findUsages(newSkill).size());

        newPerson.addSkill(newSkill);
        assertEquals("A skill should have one usage upon being added to a person", 1, relationalModel.findUsages(newSkill).size());
        assertEquals("After a skill has been added to a person that person should be in their usages", newPerson, relationalModel.findUsages(newSkill).get(0));
    }

    @Test
    public void testInUseProject() throws Exception{
        Project newProject = projectGenerated;

        assertFalse("If the project is not attached to the model it should not be in use", relationalModel.inUse(newProject));

        assertFalse("Projects should not be marked as in use even when they are attached to the model", relationalModel.inUse(newProject));
    }

    @Test
    public void testDeletionsCascadeTeam() throws Exception{
        //Make sure we're working on a clean state
        relationalModel.getTeams().clear();

        Project project = projectGenerated;
        project.getTeams().clear();

        for (int i = 0; i < 10; i++){
            Team team = teamGenerator.generate();
            team.setShortName(team.getLongName() + i);

            relationalModel.add(team);
            project.addTeam(team);
        }

        assertEquals("The project should now have ten teams", 10, project.getTeams().size());

        for (int i = 0; i < relationalModel.getTeams().size(); i++){
            relationalModel.remove(relationalModel.getTeams().get(i));
            i--;
        }

        assertEquals("The team should have been removed from the project", 0, project.getTeams().size());
    }

    @Test
    public void testDeletionsCascadePerson() throws Exception{
        //Make sure we're working from a clean slate
        relationalModel.getTeams().clear();
        relationalModel.getPeople().clear();

        //Create a few teams to add people to
        for (int i = 0; i < 10; i++) {
            Team team = teamGenerator.generate();
            team.setShortName(team.getShortName() + i);

            team.getMembers().clear();
            relationalModel.add(team);
        }

        //Add a few people to the model and to the team
        for (int i = 0; i < 100; i++) {
            Person p = personGenerator.generate();

            //Avoid duplicates
            p.setUserId(p.getUserId() + i);
            p.setShortName(p.getShortName() + i);

            relationalModel.add(p);
            //Add the person to a random team
            relationalModel.getTeams().get(NameGenerator.random(relationalModel.getTeams().size())).addMember(p);
        }

        //Remove all the people from the model. This should cascade, removing them from teams too
        for (int i = 0; i < relationalModel.getPeople().size(); ++i){
            relationalModel.remove(relationalModel.getPeople().get(i));
            i--;
        }

        //Check that there are no people in any team now
        for (int i = 0; i < relationalModel.getTeams().size(); i++){
            assertEquals("There should be no people in any team", 0 , relationalModel.getTeams().get(i).getMembers().size());
        }
    }

    @Test
    public void testDeletionsCascadeSkill() throws Exception{
        //Make sure we're working from a clean slate
        relationalModel.getSkills().clear();
        relationalModel.getPeople().clear();

        //Generate some random people
        for (int i = 0; i < 10; ++i) {
            Person person = personGenerator.generate();
            person.setUserId(person.getUserId() + i);
            person.setShortName(person.getShortName() + i);
            person.getSkills().clear();
            relationalModel.add(person);
        }

        //Add a few skills to the model and to a random person
        for (int i = 0; i < 10; ++i) {
            Skill skill = skillGenerator.generate();

            //Avoid duplicates
            skill.setShortName(skill.getShortName() + i);

            relationalModel.add(skill);
            relationalModel.getPeople().get(NameGenerator.random(relationalModel.getPeople().size())).addSkill(skill);
        }

        //Remove all the skills from the model. This should cascade to the people with the skills being removed
        for (int i = 0; i < relationalModel.getSkills().size(); ++i){
            relationalModel.remove(relationalModel.getSkills().get(i));
            i--;
        }

        //Check that all the skills have been removed from all the people
        for (Person p : relationalModel.getPeople()) {
            assertEquals("The person should now have no skills", 0, p.getSkills().size());
        }
    }

    @Test (expected = DuplicateObjectException.class)
    public void addReleaseTest() throws DuplicateObjectException {
        relationalModel.addRelease(releaseGenerated);
        relationalModel.addRelease(releaseGenerated);
    }

    @Test
    public void addRemoveReleaseTest() throws DuplicateObjectException {
        relationalModel.addRelease(releaseGenerated);
        assertTrue(relationalModel.getReleases().contains(releaseGenerated));
        relationalModel.removeRelease(releaseGenerated);
        assertFalse(relationalModel.getReleases().contains(releaseGenerated));
        relationalModel.add(releaseGenerated);
        assertTrue(relationalModel.getReleases().contains(releaseGenerated));
        relationalModel.remove(releaseGenerated);
        assertFalse(relationalModel.getReleases().contains(releaseGenerated));
    }
}
