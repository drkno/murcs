package sws.murcs.reporting.model;

import sws.murcs.model.Backlog;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.Skill;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportModelAll extends ReportModel {

    /**
     * The projects in the report.
     */
    @XmlElementWrapper(name = "projects")
    @XmlElement(name = "project")
    private List<Project> projects;

    /**
     * The backlogs in the report.
     */
    @XmlElementWrapper(name = "backlogs")
    @XmlElement(name = "backlog")
    private List<Backlog> backlogs;

    /**
     * The backlogs in the report.
     */
    @XmlElementWrapper(name = "sprints")
    @XmlElement(name = "sprint")
    private List<Sprint> sprints;

    /**
     * The work allocations in the report.
     */
    @XmlElementWrapper(name = "workAllocations")
    @XmlElement(name = "workAllocation")
    private List<WorkAllocation> workAllocations;

    /**
     * The unassigned people in the report.
     */
    @XmlElementWrapper(name = "unassignedStories")
    @XmlElement(name = "story")
    private List<Story> listUnassignedStories;

    /**
     * The unassigned teams in the report.
     */
    @XmlElementWrapper(name = "unassignedTeams")
    @XmlElement(name = "team")
    @XmlIDREF
    private List<Team> listUnassignedTeams;

    /**
     * The unassigned people in the report.
     */
    @XmlElementWrapper(name = "unassignedPeople")
    @XmlElement(name = "person")
    @XmlIDREF
    private List<Person> listUnassignedPeople;

    /**
     * The list of Teams and their details.
     */
    @XmlElementWrapper(name = "teams")
    @XmlElement(name = "team")
    private List<Team> listTeams;

    /**
     * The list of people and their details.
     */
    @XmlElementWrapper(name = "people")
    @XmlElement(name = "person")
    private List<Person> listPeople;

    /**
     * The list of skills and their details.
     */
    @XmlElementWrapper(name = "skills")
    @XmlElement(name = "skill")
    private List<Skill> listSkills;

    /**
     * Constructor.
     * @param organisation a organisation
     */
    public ReportModelAll(final Organisation organisation) {
        projects = new ArrayList<>(organisation.getProjects());
        workAllocations = new ArrayList<>(organisation.getAllocations());
        backlogs = new ArrayList<>(organisation.getBacklogs());
        sprints = new ArrayList<>(organisation.getSprints());
        listUnassignedStories = new ArrayList<>(organisation.getUnassignedStories());
        listUnassignedTeams = new ArrayList<>(organisation.getUnassignedTeams());
        listUnassignedPeople = new ArrayList<>(organisation.getUnassignedPeople());
        Collections.sort(listUnassignedStories, (Story s1, Story s2) -> s1.getShortName()
                .toLowerCase().compareTo(s2.getShortName().toLowerCase()));
        Collections.sort(listUnassignedPeople, (Person p1, Person p2) -> p1.getShortName()
                .toLowerCase().compareTo(p2.getShortName().toLowerCase()));
        Collections.sort(listUnassignedTeams, (Team t1, Team t2) -> t1.getShortName()
                .toLowerCase().compareTo(t2.getShortName().toLowerCase()));
        listTeams = new ArrayList<>(organisation.getTeams());
        listPeople = new ArrayList<>(organisation.getPeople());
        listSkills = new ArrayList<>(organisation.getSkills());
        Collections.sort(listTeams, (Team t1, Team t2) -> t1.getShortName()
                .toLowerCase().compareTo(t2.getShortName().toLowerCase()));
        Collections.sort(listPeople, (Person p1, Person p2) -> p1.getShortName()
                .toLowerCase().compareTo(p2.getShortName().toLowerCase()));
        Collections.sort(listSkills, (Skill s1, Skill s2) -> s1.getShortName()
                .toLowerCase().compareTo(s2.getShortName().toLowerCase()));
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportModelAll() {
    }
}
