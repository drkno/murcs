package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.model.Project;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertTrue;

public class WorkAllocationTest {

    private Project project;
    private Team team;

    @Before
    public void setUp() {
        project = new Project();
        team = new Team();
    }

    @After
    public void tearDown() {
        project = null;
        team = null;
    }

    @Test
    public void createAllocationTest() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plus(7, ChronoUnit.DAYS);
        WorkAllocation allocation = new WorkAllocation(project, team, startDate, endDate);

        assertTrue(allocation.getProject() == project);
        assertTrue(allocation.getTeam() == team);
        assertTrue(allocation.getStartDate() == startDate);
        assertTrue(allocation.getEndDate() == endDate);
    }
}
