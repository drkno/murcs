package sws.murcs.reporting.model;

import sws.murcs.model.Team;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
public class ReportModelTeam extends ReportModel {

    /**
     * The teams in the report.
     */
    @XmlElementWrapper(name = "teams")
    @XmlElement(name = "team")
    private List<Team> teams;

    /**
     * Constructor.
     * @param pTeams teams
     */
    public ReportModelTeam(final List<Team> pTeams) {
        teams = pTeams;
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportModelTeam() {
    }
}
