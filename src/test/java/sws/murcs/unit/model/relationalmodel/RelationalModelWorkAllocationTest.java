package sws.murcs.unit.model.relationalmodel;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.debug.sampledata.ProjectGenerator;
import sws.murcs.debug.sampledata.RelationalModelGenerator;
import sws.murcs.debug.sampledata.TeamGenerator;
import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.exceptions.OverlappedDatesException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class RelationalModelWorkAllocationTest {
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
    public void testGetWorkAllocationsNotNullOrEmpty() throws Exception {
        RelationalModel model = getNewRelationalModel();
        List<WorkAllocation> workAllocations = model.getAllocations();

        Assert.assertNotNull("getWorkAllocations() should return workAllocations but is null.", workAllocations);
        Assert.assertNotEquals("getWorkAllocations() should return workAllocations but is empty.", 0, workAllocations.size());
    }

    @Test
    public void testGetWorkAllocationsWorkAllocationRemoved() throws Exception {
        List<WorkAllocation> workAllocations = model.getAllocations();
        int size = workAllocations.size();
        WorkAllocation removedWorkAllocation = workAllocations.get(0);
        model.removeAllocation(removedWorkAllocation);
        workAllocations = model.getAllocations();

        Assert.assertFalse("WorkAllocations should not contain the removed workAllocation.", workAllocations.contains(removedWorkAllocation));
        Assert.assertNotEquals("WorkAllocations should not be the same size as before removing a workAllocation.", size, workAllocations.size());
    }

    @Test
    public void testGetWorkAllocationsAdded() throws Exception {
        List<WorkAllocation> workAllocations = model.getAllocations();
        WorkAllocation workAllocationToAdd = workAllocations.get(0);
        model.removeAllocation(workAllocationToAdd);
        workAllocations = model.getAllocations();
        int size = workAllocations.size();

        model.addAllocation(workAllocationToAdd);

        Assert.assertTrue("WorkAllocations should contain the added workAllocation.", workAllocations.contains(workAllocationToAdd));
        Assert.assertNotEquals("WorkAllocations should not be the same size as before adding a workAllocation.", size, workAllocations.size());
    }

    @Test(expected = InvalidParameterException.class)
    public void testWorkAllocationAddNull() throws Exception {
        model.addAllocation(null);
    }

    @Test
    public void testWorkAllocationRemoveNull() throws Exception {
        List<WorkAllocation> workAllocations = model.getAllocations();
        int size = workAllocations.size();
        model.removeAllocation(null);
        workAllocations = model.getAllocations();
        Assert.assertEquals("Removing null from work allocations should not change the amount of allocations.", size, workAllocations.size());
    }

    @Test
    public void testWorkAllocationsNoShortNameNotAddedRemoved() throws Exception {
        List<WorkAllocation> workAllocations = model.getAllocations();
        int size = workAllocations.size();
        WorkAllocation workAllocation = new WorkAllocation();
        model.removeAllocation(workAllocation);
        Assert.assertEquals("Removing workAllocation that isn't in the model should not change the workAllocations collection.", size, workAllocations.size());
    }

    @Test(expected = OverlappedDatesException.class)
    public void testWorkAllocationsDuplicateAdded() throws Exception {
        List<WorkAllocation> workAllocations = model.getAllocations();
        model.addAllocation(workAllocations.get(0));
    }

    @Test
    public void testGetWorkAllocationsNoDuplicates() throws Exception {
        List<WorkAllocation> workAllocations = model.getAllocations();

        List<WorkAllocation> workAllocationDuplicates = new ArrayList<>();
        workAllocations.stream().filter(workAllocation -> !workAllocationDuplicates.add(workAllocation)).forEach(workAllocation -> {
            Assert.fail("There cannot be duplicate workAllocations returned by getWorkAllocations().");
        });
    }

    @Test
    public void addWorkAllocationTest() throws Exception {
        PersistenceManager.getCurrent().setCurrentModel(null);
        TeamGenerator teamGenerator = new TeamGenerator();
        ProjectGenerator projectGenerator = new ProjectGenerator();
        Project projectGenerated = projectGenerator.generate();
        Team teamGenerated = teamGenerator.generate();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plus(7, ChronoUnit.DAYS);
        WorkAllocation allocation1 = new WorkAllocation(projectGenerated, teamGenerated, startDate, endDate);
        WorkAllocation allocation2 = new WorkAllocation(projectGenerated, teamGenerated, endDate.plus(1, ChronoUnit.DAYS), null);
        model.addAllocation(allocation1);
        model.addAllocation(allocation2);
        assertTrue(model.getAllocations().contains(allocation1));
        assertTrue(model.getAllocations().contains(allocation2));
    }

    @Test (expected = CustomException.class)
    public void overlappedWorkTest1() throws Exception {
        PersistenceManager.getCurrent().setCurrentModel(null);
        TeamGenerator teamGenerator = new TeamGenerator();
        ProjectGenerator projectGenerator = new ProjectGenerator();
        Project projectGenerated = projectGenerator.generate();
        Team teamGenerated = teamGenerator.generate();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plus(7, ChronoUnit.DAYS);
        WorkAllocation allocation1 = new WorkAllocation(projectGenerated, teamGenerated, startDate, endDate);
        WorkAllocation allocation2 = new WorkAllocation(projectGenerated, teamGenerated, startDate, endDate);
        model.addAllocation(allocation1);
        model.addAllocation(allocation2);
    }

    @Test (expected = CustomException.class)
    public void overlappedWorkTest2() throws Exception {
        PersistenceManager.getCurrent().setCurrentModel(null);
        TeamGenerator teamGenerator = new TeamGenerator();
        ProjectGenerator projectGenerator = new ProjectGenerator();
        Project projectGenerated = projectGenerator.generate();
        Team teamGenerated = teamGenerator.generate();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plus(7, ChronoUnit.DAYS);
        WorkAllocation allocation1 = new WorkAllocation(projectGenerated, teamGenerated, startDate, endDate);
        WorkAllocation allocation2 = new WorkAllocation(projectGenerated, teamGenerated, startDate, null);
        model.addAllocation(allocation1);
        model.addAllocation(allocation2);
    }
}
