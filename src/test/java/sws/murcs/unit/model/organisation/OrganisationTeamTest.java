package sws.murcs.unit.model.organisation;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.debug.sampledata.OrganisationGenerator;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.*;
import sws.murcs.model.Organisation;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrganisationTeamTest {
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
        PersistenceManager.getCurrent().setCurrentModel(null);
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
    public void testGetTeamsNotNullOrEmpty() throws Exception {
        Organisation model = getNeworganisation();
        List<Team> teams = model.getTeams();

        Assert.assertNotNull("getTeams() should return teams but is null.", teams);
        Assert.assertNotEquals("getTeams() should return teams but is empty.", 0, teams.size());
    }

    @Test
    public void testGetTeamsTeamRemoved() throws Exception {
        List<Team> teams = model.getTeams();
        int size = teams.size();
        Team removedTeam = teams.get(0);
        model.remove(removedTeam);
        teams = model.getTeams();

        Assert.assertFalse("Teams should not contain the removed team.", teams.contains(removedTeam));
        Assert.assertNotEquals("Teams should not be the same size as before removing a team.", size, teams.size());
    }

    @Test
    public void testGetTeamsAdded() throws Exception {
        List<Team> teams = model.getTeams();
        Team teamToAdd = teams.get(0);
        model.remove(teamToAdd);
        teams = model.getTeams();
        int size = teams.size();

        model.add(teamToAdd);

        Assert.assertTrue("Teams should contain the added team.", teams.contains(teamToAdd));
        Assert.assertNotEquals("Teams should not be the same size as before adding a team.", size, teams.size());
    }

    @Test(expected = InvalidParameterException.class)
    public void testTeamsNoShortNameAdded() throws Exception {
        Team team = new Team();
        model.add(team);
    }

    @Test
    public void testTeamsNoShortNameNotAddedRemoved() throws Exception {
        List<Team> teams = model.getTeams();
        int size = teams.size();
        Team team = new Team();
        model.remove(team);
        Assert.assertEquals("Removing team that isn't in the model should not change the teams collection.", size, teams.size());
    }

    @Test(expected = DuplicateObjectException.class)
    public void testTeamsDuplicateAdded() throws Exception {
        List<Team> teams = model.getTeams();
        model.add(teams.get(0));
    }

    @Test
    public void testGetTeamsNoDuplicates() throws Exception {
        List<Team> teams = model.getTeams();

        List<Team> teamDuplicates = new ArrayList<>();
        teams.stream().filter(team -> !teamDuplicates.add(team)).forEach(team -> {
            Assert.fail("There cannot be duplicate teams returned by getTeams().");
        });
    }

    @Test
    public void testTeamExists() throws Exception {
        List<Team> teams = model.getTeams();
        Assert.assertTrue("Team exists but was not found.", UsageHelper.exists(teams.get(0)));
    }

    @Test
    public void testTeamDoesNotExist() throws Exception {
        Team team = new Team();
        team.setShortName("testing1234");
        Assert.assertFalse("Team exists when it should not.", UsageHelper.exists(team));
    }

    @Test
    public void testTeamFindUsagesDoesNotExist() throws Exception {
        Team team = new Team();
        team.setShortName("testing1234");
        List<Model> usages = UsageHelper.findUsages(team);

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertEquals("Usages were found for team not in model.", 0, usages.size());
    }

    @Test
    public void testTeamFindUsages() throws Exception {
        List<Team> teams = model.getTeams();
        List<Project> projects = model.getProjects();
        try {
            WorkAllocation allocation = new WorkAllocation(projects.get(0), teams.get(0), LocalDate.ofEpochDay(0), LocalDate.ofEpochDay(30));
            model.addAllocation(allocation);
        }
        catch (Exception e) {
            // ignore, we just want to ensure team is attached to a work allocation
        }
        List<Model> usages = UsageHelper.findUsages(teams.get(0));

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertNotEquals("Usages were not found for team.", 0, usages.size());
        Assert.assertTrue("Item should be in use.", UsageHelper.inUse(teams.get(0)));
    }

    @Test
    public void testUnassignedTeam() throws Exception {
        Team testTeam = new Team();
        testTeam.setShortName("testing12345");
        model.add(testTeam);
        Collection<Team> people = model.getUnassignedTeams();
        Assert.assertFalse("Item should not be in use.", UsageHelper.inUse(people.iterator().next()));
    }
}
