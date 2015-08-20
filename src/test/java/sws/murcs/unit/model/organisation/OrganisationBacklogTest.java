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
import sws.murcs.model.Backlog;
import sws.murcs.model.Model;
import sws.murcs.model.Organisation;
import sws.murcs.model.Project;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.util.ArrayList;
import java.util.List;

public class OrganisationBacklogTest {
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
    public void testGetBacklogsNotNullOrEmpty() throws Exception {
        Organisation model = getNeworganisation();
        List<Backlog> backlogs = model.getBacklogs();

        Assert.assertNotNull("getBacklogs() should return backlogs but is null.", backlogs);
        Assert.assertNotEquals("getBacklogs() should return backlogs but is empty.", 0, backlogs.size());
    }

    @Test
    public void testGetBacklogsBacklogRemoved() throws Exception {
        List<Backlog> backlogs = model.getBacklogs();
        int size = backlogs.size();
        Backlog removedBacklog = backlogs.get(0);
        model.remove(removedBacklog);
        backlogs = model.getBacklogs();

        Assert.assertFalse("Backlogs should not contain the removed backlog.", backlogs.contains(removedBacklog));
        Assert.assertNotEquals("Backlogs should not be the same size as before removing a backlog.", size, backlogs.size());
    }

    @Test
    public void testGetBacklogsAdded() throws Exception {
        List<Backlog> backlogs = model.getBacklogs();
        Backlog backlogToAdd = backlogs.get(0);
        model.remove(backlogToAdd);
        backlogs = model.getBacklogs();
        int size = backlogs.size();

        model.add(backlogToAdd);

        Assert.assertTrue("Backlogs should contain the added backlog.", backlogs.contains(backlogToAdd));
        Assert.assertNotEquals("Backlogs should not be the same size as before adding a backlog.", size, backlogs.size());
    }

    @Test(expected = InvalidParameterException.class)
    public void testBacklogsNoShortNameAdded() throws Exception {
        Backlog backlog = new Backlog();
        model.add(backlog);
    }

    @Test
    public void testBacklogsNoShortNameNotAddedRemoved() throws Exception {
        List<Backlog> backlogs = model.getBacklogs();
        int size = backlogs.size();
        Backlog backlog = new Backlog();
        model.remove(backlog);
        Assert.assertEquals("Removing backlog that isn't in the model should not change the backlogs collection.", size, backlogs.size());
    }

    @Test(expected = DuplicateObjectException.class)
    public void testBacklogsDuplicateAdded() throws Exception {
        List<Backlog> backlogs = model.getBacklogs();
        model.add(backlogs.get(0));
    }

    @Test
    public void testGetBacklogsNoDuplicates() throws Exception {
        List<Backlog> backlogs = model.getBacklogs();

        List<Backlog> backlogDuplicates = new ArrayList<>();
        backlogs.stream().filter(backlog -> !backlogDuplicates.add(backlog)).forEach(backlog -> {
            Assert.fail("There cannot be duplicate backlogs returned by getBacklogs().");
        });
    }

    @Test
    public void testBacklogExists() throws Exception {
        List<Backlog> backlogs = model.getBacklogs();
        Assert.assertTrue("Backlog exists but was not found.", UsageHelper.exists(backlogs.get(0)));
    }

    @Test
    public void testBacklogDoesNotExist() throws Exception {
        Backlog backlog = new Backlog();
        backlog.setShortName("testing1234");
        Assert.assertFalse("Backlog exists when it should not.", UsageHelper.exists(backlog));
    }

    @Test
    public void testBacklogFindUsagesDoesNotExist() throws Exception {
        Backlog backlog = new Backlog();
        backlog.setShortName("testing1234");
        List<Model> usages = UsageHelper.findUsages(backlog);

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertEquals("Usages were found for backlog not in model.", 0, usages.size());
    }

    @Test
    public void testBacklogFindUsages() throws Exception {
        List<Backlog> backlogs = model.getBacklogs();
        List<Project> projects = model.getProjects();
        try {
            projects.get(0).addBacklog(backlogs.get(0));
        }
        catch (Exception e) {
            // ignore, we just want to ensure backlog is attached to a project
        }
        List<Model> usages = UsageHelper.findUsages(backlogs.get(0));

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertNotEquals("Usages were not found for backlog.", 0, usages.size());
        Assert.assertTrue("Item should be in use.", UsageHelper.inUse(backlogs.get(0)));
    }
}
