package sws.murcs.reporting.model;

import sws.murcs.model.Team;

import javax.xml.bind.annotation.XmlElement;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
public class ReportModelTeam extends ReportModel {
    /**
     * The project in the report.
     */
    @XmlElement(name = "team")
    private Team team;

    /**
     * Constructor.
     * @param pTeam a team
     */
    public ReportModelTeam(final Team pTeam) {
        team = pTeam;
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportModelTeam() {
    }
}
