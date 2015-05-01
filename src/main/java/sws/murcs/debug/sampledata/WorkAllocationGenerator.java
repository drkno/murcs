package sws.murcs.debug.sampledata;

import sws.murcs.model.Project;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generates random work allocations for teams on projects
 */
public class WorkAllocationGenerator {

    private ArrayList<Project> projectPool;
    private ArrayList<Team> teamPool;

    /**
     * Sets the project pool
     * @param projectPool The project pool
     */
    public void setProjectPool(ArrayList<Project> projectPool) {
        this.projectPool = projectPool;
    }

    /**
     * Sets the team pool
     * @param teamPool The team pool
     */
    public void setTeamPool(ArrayList<Team> teamPool) {
        this.teamPool = teamPool;
    }

    /**
     * Creates work allocations to occupy a team for the next six months
     * @param team The team to create work for
     * @return List of work allocations
     */
    private ArrayList<WorkAllocation> createWork(Team team) {
        Random random = new Random();
        int numProjects = projectPool.size();
        ArrayList<WorkAllocation> allocations = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().minus(6, ChronoUnit.MONTHS);
        LocalDate startDate;
        LocalDate endDate = currentDate;
        while (endDate.isBefore(LocalDate.now().plus(6, ChronoUnit.MONTHS))) {
            startDate = endDate.plus(random.nextInt(7)+1, ChronoUnit.DAYS);
            endDate = startDate.plus(random.nextInt(21)+1, ChronoUnit.DAYS);
            Project project = projectPool.get(random.nextInt(numProjects));
            allocations.add(new WorkAllocation(project, team, startDate, endDate));
        }
        return allocations;
    }

    /**
     * Creates a list of work allocations for all the given teams on any of the given projects
     * @return The list of work allocations
     */
    public ArrayList<WorkAllocation> generate() {
        if (projectPool == null || teamPool == null || projectPool.isEmpty() || teamPool.isEmpty()) {
            return null;
        }

        ArrayList<WorkAllocation> allocations = new ArrayList<>();
        for (Team team : teamPool) {
            allocations.addAll(createWork(team));
        }
        return allocations;
    }
}
