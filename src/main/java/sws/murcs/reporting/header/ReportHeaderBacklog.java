package sws.murcs.reporting.header;

import sws.murcs.model.Backlog;
import sws.murcs.model.Organisation;
import sws.murcs.reporting.model.ReportModelBacklog;

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
public class ReportHeaderBacklog extends ReportHeader {

    /**
     * Creates a new Report Header from a given backlog.
     * @param backlogs the projects given.
     */
    public ReportHeaderBacklog(final List<Backlog> backlogs) {
        reportModel = new ReportModelBacklog(backlogs);
        projectVersion = Organisation.getVersion();
        dateGenerated = LocalDate.now();
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportHeaderBacklog() {
    }
}
