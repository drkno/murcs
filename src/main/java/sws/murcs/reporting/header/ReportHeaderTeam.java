package sws.murcs.reporting.header;

import sws.murcs.model.Organisation;
import sws.murcs.model.Team;
import sws.murcs.reporting.model.ReportModelTeam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.util.List;

/**
 * A class that gets the information needed for the header of the report, such as the product version and
 * date generated.
 */
@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportHeaderTeam extends ReportHeader {
    /**
     * Creates a new Report Header from a given team.
     * @param teams the teams given.
     */
    public ReportHeaderTeam(final List<Team> teams) {
        reportModel = new ReportModelTeam(teams);
        projectVersion = Organisation.getVersion();
        dateGenerated = LocalDate.now();
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportHeaderTeam() {
    }
}
