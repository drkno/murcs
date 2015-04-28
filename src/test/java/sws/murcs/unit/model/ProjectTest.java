package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Project;
import sws.murcs.model.Team;
import sws.murcs.debug.sampledata.ProjectGenerator;
import sws.murcs.debug.sampledata.Generator;
import sws.murcs.debug.sampledata.TeamGenerator;
import sws.murcs.model.WorkAllocation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProjectTest {

    private static Generator<Team> teamGenerator;
    private static Generator<Project> projectGenerator;
    private WorkAllocation generatedAllocation;
    private Project generatedProject;
    private WorkAllocation constructedAllocation;
    private Project constructedProject;

    @BeforeClass
    public static void oneTimeSetUp() {
        teamGenerator = new TeamGenerator();
        projectGenerator = new ProjectGenerator();
        UndoRedoManager.setDisabled(true);
    }

    @Before
    public void setUp() {
        Team generatedTeam = teamGenerator.generate();
        generatedProject = projectGenerator.generate();
        Team constructedTeam = new Team();
        constructedProject = new Project();

        LocalDate startDate = LocalDate.now().minus(1, ChronoUnit.DAYS);
        LocalDate endDate = LocalDate.now().plus(1, ChronoUnit.DAYS);
        generatedAllocation = new WorkAllocation(generatedProject, generatedTeam, startDate, endDate);
        constructedAllocation = new WorkAllocation(constructedProject, constructedTeam, startDate, endDate);
    }

    @After
    public void tearDown() {
        generatedAllocation = null;
        generatedProject = null;
        constructedAllocation = null;
        constructedProject = null;
    }

    @Test(expected = Exception.class)
    public void setShortNameTest1() throws Exception {
        generatedProject.setShortName(null);
    }

    @Test(expected = Exception.class)
    public void setShortNameTest2() throws Exception {
        generatedProject.setShortName("");
    }

    @Test(expected = Exception.class)
    public void setShortNameTest3() throws Exception {
        generatedProject.setShortName("   \n\r\t");
    }

    @Test
    public void addTeamAllocationTest() throws Exception {
        assertFalse(constructedProject.getAllocations().contains(generatedAllocation));

        constructedProject.addAllocation(generatedAllocation);
        assertTrue(constructedProject.getAllocations().contains(generatedAllocation));
    }

    @Test (expected = DuplicateObjectException.class)
    public void addTeamAllocationExceptionTest1() throws Exception {
        constructedProject.addAllocation(generatedAllocation);
        constructedProject.addAllocation(generatedAllocation);
    }

    @Test (expected = DuplicateObjectException.class)
    public void addTeamAllocationExceptionTest2() throws Exception {
        constructedProject.addAllocation(generatedAllocation);
        constructedAllocation.getTeam().setShortName(generatedAllocation.getTeam().getShortName());
        constructedProject.addAllocation(generatedAllocation);
    }

    @Test
    public void addTeamAllocationsTest() throws Exception {
        List<WorkAllocation> testAllocations = new ArrayList<>();
        assertEquals(constructedProject.getAllocations().size(), 0);
        testAllocations.add(generatedAllocation);

        constructedProject.addAllocations(testAllocations);
        assertTrue(constructedProject.getAllocations().contains(generatedAllocation));

        testAllocations.add(generatedAllocation);
        assertEquals(testAllocations.size(), 2);
        assertEquals(constructedProject.getAllocations().size(), 1);
    }

    @Test
    public void removeTeamAllocationTest() throws Exception {
        constructedProject.addAllocation(generatedAllocation);
        assertTrue(constructedProject.getAllocations().contains(generatedAllocation));

        constructedProject.removeAllocation(generatedAllocation);
        assertFalse(constructedProject.getAllocations().contains(generatedAllocation));

        constructedProject.removeAllocation(generatedAllocation);
        assertFalse(constructedProject.getAllocations().contains(generatedAllocation));
    }
}
