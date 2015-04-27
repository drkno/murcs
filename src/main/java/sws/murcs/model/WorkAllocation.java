package sws.murcs.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A dumb information carrier used to represent a work schedule by a Team on a Project
 */
public class WorkAllocation implements Serializable {

    private final Project project;
    private final Team team;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public WorkAllocation(Project project, Team team, LocalDate startDate, LocalDate endDate) {
        this.project = project;
        this.team = team;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Project getProject() {
        return this.project;
    }

    public Team getTeam() {
        return this.team;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }
}
