package sws.murcs.debug.sampledata;

import sws.murcs.model.Project;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generates random work allocations for teams on projects.
 */
public class WorkAllocationGenerator {

    /**
     * A pool of projects to choose from when
     * generating work allocations.
     */
    private ArrayList<Project> projectPool;
    /**
     * A pool of teams to choose from when
     * generating work allocations.
     */
    private ArrayList<Team> teamPool;

    /**
     * Sets the project pool.
     * @param projects The project pool
     */
    public final void setProjectPool(final ArrayList<Project> projects) {
        this.projectPool = projects;
    }

    /**
     * Sets the team pool.
     * @param teams The team pool
     */
    public final void setTeamPool(final ArrayList<Team> teams) {
        this.teamPool = teams;
    }

    /**
     * Creates work allocations to occupy a team for the next six months.
     * @param team The team to create work for
     * @return List of work allocations
     */
    private ArrayList<WorkAllocation> createWork(final Team team) {
        Random random = new Random();
        int numProjects = projectPool.size();
        ArrayList<WorkAllocation> allocations = new ArrayList<>();
        LocalDate currentDate = LocalDate.now().minus(6, ChronoUnit.MONTHS);
        LocalDate startDate;
        LocalDate endDate = currentDate;
        while (endDate.isBefore(LocalDate.now().plus(6, ChronoUnit.MONTHS))) {
            startDate = endDate.plus(random.nextInt(7) + 1, ChronoUnit.DAYS);
            endDate = startDate.plus(random.nextInt(21) + 1, ChronoUnit.DAYS);
            Project project = projectPool.get(random.nextInt(numProjects));
            allocations.add(new WorkAllocation(project, team, startDate, endDate));
        }
        return allocations;
    }

    /**
     * Creates a list of work allocations for all the given teams on any of the given projects.
     * @return The list of work allocations
     */
    public final ArrayList<WorkAllocation> generate() {
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
