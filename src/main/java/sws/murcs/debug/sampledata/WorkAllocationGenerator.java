package sws.murcs.debug.sampledata;

import sws.murcs.model.Project;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates random work allocations for teams on projects
 */
public class WorkAllocationGenerator {

    private Generator<Project> projectGenerator;
    private Generator<Team> teamGenerator;

    private List<Project> projectPool;
    private List<Team> teamPool;

    /**
     * Instantiates a new Project and Team generator
     */
    public WorkAllocationGenerator() {
        this.projectGenerator = new ProjectGenerator();
        this.teamGenerator = new TeamGenerator();
    }

    /**
     * Instantiates a new Project and Team generator
     * @param projectGenerator The project generator to use
     * @param teamGenerator The team generator to use
     */
    public WorkAllocationGenerator(Generator<Project> projectGenerator, Generator<Team> teamGenerator) {
        this.projectGenerator = projectGenerator;
        this.teamGenerator = teamGenerator;
    }

    /**
     * Sets the project generator
     * @param projectGenerator The project generator
     */
    public void setProjectGenerator(Generator<Project> projectGenerator) {
        this.projectGenerator = projectGenerator;
    }

    /**
     * Sets the team generator
     * @param teamGenerator The team generator
     */
    public void setTeamGenerator(Generator<Team> teamGenerator) {
        this.teamGenerator = teamGenerator;
    }

    /**
     * Sets the project pool. If null, projects will be randomly generated
     * @param projectPool The project pool
     */
    public void setProjectPool(List<Project> projectPool) {
        this.projectPool = projectPool;
    }

    /**
     * Sets the team pool. If null, teams will be randomly generated
     * @param teamPool The team pool
     */
    public void setTeamPool(List<Team> teamPool) {
        this.teamPool = teamPool;
    }

    public List<WorkAllocation> generate() {
        List<WorkAllocation> allocations = new ArrayList<>();

        return allocations;
    }
}
