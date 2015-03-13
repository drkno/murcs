package sws.project.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.project.exceptions.DuplicateObjectException;
import sws.project.model.Project;
import sws.project.model.Team;
import sws.project.sampledata.Generator;
import sws.project.sampledata.ProjectGenerator;
import sws.project.sampledata.TeamGenerator;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * 13/03/2015
 */
public class ProjectTest {

    private static Generator<Team> teamGenerator;
    private static Generator<Project> projectGenerator;
    private Team teamGenerated;
    private Project projectGenerated;
    private Team team;
    private Project project;

    @BeforeClass
    public static void oneTimeSetUp() {
        teamGenerator = new TeamGenerator();
        projectGenerator = new ProjectGenerator();
    }

    @Before
    public void setUp() {
        teamGenerated = teamGenerator.generate();
        projectGenerated = projectGenerator.generate();
        team = new Team();
        project = new Project();
    }

    @After
    public void tearDown() {
        teamGenerated = null;
        projectGenerated = null;
        team = null;
        project = null;
    }

    @Test(expected = Exception.class)
    public void setShortNameTest1() throws Exception {
        projectGenerated.setShortName(null);
    }

    @Test(expected = Exception.class)
    public void setShortNameTest2() throws Exception {
        projectGenerated.setShortName("");
    }

    @Test(expected = Exception.class)
    public void setShortNameTest3() throws Exception {
        projectGenerated.setShortName("   \n\r\t");
    }

    @Test
    public void addTeamTest() throws Exception {
        assertFalse(project.getTeams().contains(teamGenerated));

        project.addTeam(teamGenerated);
        assertTrue(project.getTeams().contains(teamGenerated));
    }

    @Test (expected = DuplicateObjectException.class)
    public void addTeamExceptionTest1() throws Exception {
        project.addTeam(teamGenerated);
        project.addTeam(teamGenerated);
    }

    @Test (expected = DuplicateObjectException.class)
    public void addTeamExceptionTest2() throws Exception {
        project.addTeam(teamGenerated);
        team.setShortName(teamGenerated.getShortName());
        project.addTeam(teamGenerated);
    }

    @Test
    public void addTeamsTest() throws Exception {
        List<Team> testTeams = new ArrayList<>();
        assertEquals(project.getTeams().size(), 0);
        testTeams.add(teamGenerated);

        project.addTeams(testTeams);
        assertTrue(project.getTeams().contains(teamGenerated));

        testTeams.add(teamGenerated);
        assertEquals(testTeams.size(), 2);
        assertEquals(project.getTeams().size(), 1);
    }

    @Test
    public void removeTeamTest() throws Exception {
        project.addTeam(teamGenerated);
        assertTrue(project.getTeams().contains(teamGenerated));

        project.removeTeam(teamGenerated);
        assertFalse(project.getTeams().contains(teamGenerated));

        project.removeTeam(teamGenerated);
        assertFalse(project.getTeams().contains(teamGenerated));
    }
}
