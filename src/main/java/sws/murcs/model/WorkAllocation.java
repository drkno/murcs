package sws.murcs.model;

import sws.murcs.reporting.LocalDateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A dumb information carrier used to represent a work
 * schedule by a Team on a Project.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkAllocation implements Serializable {

    private final Project project;
    private final Team team;
    @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class)
    private final LocalDate startDate;
    @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class)
    private final LocalDate endDate;

    @SuppressWarnings("unused")
    public WorkAllocation() {
        project = null;
        team = null;
        startDate = null;
        endDate = null;
    }

    /**
     * Constructs a new work allocation. Which is a link between teams
     * and projects and specifies when a team is working on a project.
     * @param newProject The project the team is allocated to.
     * @param newTeam The team that is allocated to the project.
     * @param start The start date of the allocation.
     * @param end The end date of the allocation.
     */
    public WorkAllocation(final Project newProject, final Team newTeam, final LocalDate start, final LocalDate end) {
        this.project = newProject;
        this.team = newTeam;
        this.startDate = start;
        this.endDate = end;
    }

    /**
     * Gets the project of the work allocation.
     * @return The project.
     */
    public final Project getProject() {
        return this.project;
    }

    /**
     * Gets the team linked to the allocation.
     * @return The team.
     */
    public final Team getTeam() {
        return this.team;
    }

    /**
     * Gets the start date of the allocation.
     * @return The start date.
     */
    public final LocalDate getStartDate() {
        return this.startDate;
    }

    /**
     * The end date of the allocation.
     * @return The end date.
     */
    public final LocalDate getEndDate() {
        return this.endDate;
    }


}
