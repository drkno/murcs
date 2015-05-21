package sws.murcs.reporting;

import sws.murcs.model.Backlog;
import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Story;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportModel {
    /**
     * The projects in the report.
     */
    @XmlElementWrapper(name = "projects")
    @XmlElement(name = "project")
    private List<Project> projects = new ArrayList<>();
    /**
     * The backlogs in the report.
     */
    @XmlElementWrapper(name = "backlogs")
    @XmlElement(name = "backlog")
    private List<Backlog> backlogs = new ArrayList<>();
    /**
     * The work allocations in the report.
     */
    @XmlElementWrapper(name = "workAllocations")
    @XmlElement(name = "workAllocation")
    private List<WorkAllocation> workAllocations = new ArrayList<>();
    /**
     * The unassigned people in the report.
     */
    @XmlElementWrapper(name = "unassignedStories")
    @XmlElement(name = "story")
    private List<Story> listUnassignedStories = new ArrayList<>();
    /**
     * The unassigned teams in the report.
     */
    @XmlElementWrapper(name = "unassignedTeams")
    @XmlElement(name = "team")
    private List<Team> listUnassignedTeams = new ArrayList<>();
    /**
     * The unassigned people in the report.
     */
    @XmlElementWrapper(name = "unassignedPeople")
    @XmlElement(name = "person")
    private List<Person> listUnassignedPeople = new ArrayList<>();

    /**
     * Constructor.
     * @param relationalModel a relational model
     */
    public ReportModel(final RelationalModel relationalModel) {
        projects.addAll(relationalModel.getProjects());
        workAllocations.addAll(relationalModel.getAllocations());
        backlogs.addAll(relationalModel.getBacklogs());
        listUnassignedStories.addAll(relationalModel.getUnassignedStories());
        listUnassignedTeams.addAll(relationalModel.getUnassignedTeams());
        listUnassignedPeople.addAll(relationalModel.getUnassignedPeople());
        Collections.sort(listUnassignedStories, (Story s1, Story s2) -> s1.getShortName()
                .toLowerCase().compareTo(s2.getShortName().toLowerCase()));
        Collections.sort(listUnassignedPeople, (Person p1, Person p2) -> p1.getShortName()
                .toLowerCase().compareTo(p2.getShortName().toLowerCase()));
        Collections.sort(listUnassignedTeams, (Team t1, Team t2) -> t1.getShortName()
                .toLowerCase().compareTo(t2.getShortName().toLowerCase()));
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    @SuppressWarnings("unused")
    private ReportModel() {
    }
}
