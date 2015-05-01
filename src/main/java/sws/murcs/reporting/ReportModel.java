package sws.murcs.reporting;

import sws.murcs.model.*;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportModel {
    @XmlElementWrapper(name = "projects")
    @XmlElement(name = "project")
    private List<Project> projects= new ArrayList<>();
    @XmlElementWrapper(name = "teams")
    @XmlElement(name = "team")
    private List<Team> teams = new ArrayList<>();
    @XmlElementWrapper(name = "workAllocations")
    @XmlElement(name = "workAllocation")
    private List<WorkAllocation> workAllocations = new ArrayList<>();
    @XmlElementWrapper(name = "unassignedTeams")
    @XmlElement(name = "team")
    private List<Team> listUnassignedTeams = new ArrayList<>();
    @XmlElementWrapper(name = "unassignedPeople")
    @XmlElement(name = "person")
    private List<Person> listUnassignedPeople = new ArrayList<>();

    /**
     * Constructor.
     * @param relationalModel a relational model
     */
    public ReportModel(RelationalModel relationalModel) {
        projects.addAll(relationalModel.getProjects());
        teams.addAll(relationalModel.getTeams());
        workAllocations.addAll(relationalModel.getAllAllocations());
        listUnassignedTeams.addAll(relationalModel.getUnassignedTeams());
        listUnassignedPeople.addAll(relationalModel.getUnassignedPeople());
        Collections.sort(listUnassignedPeople, (Person p1, Person p2) -> p1.getShortName().compareTo(p2.getShortName()));
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    @SuppressWarnings("unused")
    private ReportModel() {
    }
}
