package sws.murcs.reporting.header;

import sws.murcs.model.Organisation;
import sws.murcs.model.Sprint;
import sws.murcs.reporting.model.ReportModelSprint;

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
public class ReportHeaderSprint extends ReportHeader {

    /**
     * Creates a new Report Header from given sprints.
     * @param sprints the sprints given.
     */
    public ReportHeaderSprint(final List<Sprint> sprints) {
        reportModel = new ReportModelSprint(sprints);
        projectVersion = Organisation.getVersion();
        dateGenerated = LocalDate.now();
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportHeaderSprint() {
    }
}
