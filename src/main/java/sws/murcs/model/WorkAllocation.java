package sws.murcs.model;

import sws.murcs.reporting.LocalDateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A dumb information carrier used to represent a work schedule by a Team on a Project
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
