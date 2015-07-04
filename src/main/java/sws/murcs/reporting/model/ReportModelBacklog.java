package sws.murcs.reporting.model;

import sws.murcs.model.Backlog;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
public class ReportModelBacklog extends ReportModel {
    /**
     * The backlogs in the report.
     */
    @XmlElementWrapper(name = "backlogs")
    @XmlElement(name = "backlog")
    private List<Backlog> backlogs;

    /**
     * Constructor.
     * @param pBacklogs backlogs
     */
    public ReportModelBacklog(final List<Backlog> pBacklogs) {
        backlogs = pBacklogs;
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportModelBacklog() {
    }
}
