package sws.murcs.unit.model.relationalmodel;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.debug.sampledata.RelationalModelGenerator;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Model;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Release;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RelationalModelProjectTest {
    private static RelationalModelGenerator generator;
    private RelationalModel model;

    @BeforeClass
    public static void classSetup() {
        generator = new RelationalModelGenerator(RelationalModelGenerator.Stress.Medium);
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
     * Generates a relational model, and sets it to the currently in use
     * model in the current persistence manager instance.
     * @throws NullPointerException if no persistence manager exists.
     * @return a new relational model.
     */
    private static RelationalModel getNewRelationalModel() {
        PersistenceManager.getCurrent().setCurrentModel(null);
        RelationalModel model = generator.generate();
        PersistenceManager.getCurrent().setCurrentModel(model);
        return model;
    }

    @Before
    public void setup() throws Exception {
        model = getNewRelationalModel();
    }

    @Test
    public void testGetProjectsNotNullOrEmpty() throws Exception {
        RelationalModel model = getNewRelationalModel();
        List<Project> projects = model.getProjects();

        Assert.assertNotNull("getProjects() should return projects but is null.", projects);
        Assert.assertNotEquals("getProjects() should return projects but is empty.", 0, projects.size());
    }

    @Test
    public void testGetProjectsProjectRemoved() throws Exception {
        List<Project> projects = model.getProjects();
        int size = projects.size();
        Project removedProject = projects.get(0);
        model.remove(removedProject);
        projects = model.getProjects();

        Assert.assertFalse("Projects should not contain the removed project.", projects.contains(removedProject));
        Assert.assertNotEquals("Projects should not be the same size as before removing a project.", size, projects.size());
    }

    @Test
    public void testGetProjectsAdded() throws Exception {
        List<Project> projects = model.getProjects();
        Project projectToAdd = projects.get(0);
        model.remove(projectToAdd);
        projects = model.getProjects();
        int size = projects.size();

        model.add(projectToAdd);

        Assert.assertTrue("Projects should contain the added project.", projects.contains(projectToAdd));
        Assert.assertNotEquals("Projects should not be the same size as before adding a project.", size, projects.size());
    }

    @Test(expected = InvalidParameterException.class)
    public void testProjectsNoShortNameAdded() throws Exception {
        Project project = new Project();
        model.add(project);
    }

    @Test
    public void testProjectsNoShortNameNotAddedRemoved() throws Exception {
        List<Project> projects = model.getProjects();
        int size = projects.size();
        Project project = new Project();
        model.remove(project);
        Assert.assertEquals("Removing project that isn't in the model should not change the projects collection.", size, projects.size());
    }

    @Test(expected = DuplicateObjectException.class)
    public void testProjectsDuplicateAdded() throws Exception {
        List<Project> projects = model.getProjects();
        model.add(projects.get(0));
    }

    @Test
    public void testGetProjectsNoDuplicates() throws Exception {
        List<Project> projects = model.getProjects();

        List<Project> projectDuplicates = new ArrayList<>();
        projects.stream().filter(project -> !projectDuplicates.add(project)).forEach(project -> {
            Assert.fail("There cannot be duplicate projects returned by getProjects().");
        });
    }

    @Test
    public void testProjectExists() throws Exception {
        List<Project> projects = model.getProjects();
        Assert.assertTrue("Project exists but was not found.", model.exists(projects.get(0)));
    }

    @Test
    public void testProjectDoesNotExist() throws Exception {
        Project project = new Project();
        project.setShortName("testing1234");
        Assert.assertFalse("Project exists when it should not.", model.exists(project));
    }

    @Test
    public void testProjectFindUsagesDoesNotExist() throws Exception {
        Project project = new Project();
        project.setShortName("testing1234");
        List<Model> usages = model.findUsages(project);

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertEquals("Usages were found for project not in model.", 0, usages.size());
    }

    @Test
    public void testProjectFindUsages() throws Exception {
        List<Project> projects = model.getProjects();
        try {
            Release test = new Release();
            test.setReleaseDate(LocalDate.ofEpochDay(321));
            test.setDescription("");
            test.changeRelease(projects.get(0));
            model.add(test);
            projects.get(0).addRelease(test);
        }
        catch (Exception e) {
            // ignore, we just want to ensure a project is attached to a release
        }

        List<Model> usages = model.findUsages(projects.get(0));

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertNotEquals("Usages were not found for project.", 0, usages.size());
        Assert.assertTrue("Item should be in use.", model.inUse(projects.get(0)));
    }
}
