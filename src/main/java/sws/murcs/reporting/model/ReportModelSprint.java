package sws.murcs.reporting.model;

import sws.murcs.model.Sprint;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
public class ReportModelSprint extends ReportModel {

    /**
     * The sprints in the report.
     */
    @XmlElementWrapper(name = "sprints")
    @XmlElement(name = "sprint")
    private List<Sprint> sprints;

    /**
     * Constructor.
     * @param pSprints sprints
     */
    public ReportModelSprint(final List<Sprint> pSprints) {
        this.sprints = pSprints;
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportModelSprint() {
    }
}
