package sws.murcs.reporting;

import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Team;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author dpv11@uclive.ac.nz (Daniel van Wichen)
 */
@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportModel {
    private Project project;
    @XmlElementWrapper(name = "unassignedTeams")
    @XmlElement(name = "team")
    private ArrayList<Team> listUnassignedTeams = new ArrayList<>();
    @XmlElementWrapper(name = "unassignedPeople")
    @XmlElement(name = "person")
    private ArrayList<Person> listUnassignedPeople = new ArrayList<>();

    public ReportModel(RelationalModel relationalModel) {
        project = relationalModel.getProject();
        listUnassignedTeams.addAll(relationalModel.getUnassignedTeams());
        Collections.sort(listUnassignedTeams, (Team t1, Team t2) -> t1.getShortName().compareTo(t2.getShortName()));
        listUnassignedPeople.addAll(relationalModel.getUnassignedPeople());
        Collections.sort(listUnassignedPeople, (Person p1, Person p2) -> p1.getShortName().compareTo(p2.getShortName()));
    }

    @SuppressWarnings("unused")
    private ReportModel() {
    }
}
