package sws.murcs.reporting;

import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Team;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportModel {
    @XmlElementWrapper(name = "projects")
    @XmlElement(name = "project")
    private ArrayList<Project> projects = new ArrayList<>();
    @XmlElementWrapper(name = "unassignedTeams")
    @XmlElement(name = "team")
    private ArrayList<Team> listUnassignedTeams = new ArrayList<>();
    @XmlElementWrapper(name = "unassignedPeople")
    @XmlElement(name = "person")
    private ArrayList<Person> listUnassignedPeople = new ArrayList<>();

    /**
     * Constructor.
     * @param relationalModel a relational model
     */
    public ReportModel(RelationalModel relationalModel) {
        projects.addAll(relationalModel.getProjects());
        listUnassignedTeams.addAll(relationalModel.getUnassignedTeams());
        Collections.sort(listUnassignedTeams, (Team t1, Team t2) -> t1.getShortName().compareTo(t2.getShortName()));
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
