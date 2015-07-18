package sws.murcs.reporting.header;

import sws.murcs.model.Organisation;
import sws.murcs.reporting.model.ReportModelAll;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

/**
 * A class that gets the information needed for the header of the report, such as the product version and
 * date generated.
 */
@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportHeaderAll extends ReportHeader {

    /**
     * Creates a new Report Header from a given organisation.
     * @param organisation the organisation given.
     */
    public ReportHeaderAll(final Organisation organisation) {
        reportModel = new ReportModelAll(organisation);
        projectVersion = Organisation.getVersion();
        dateGenerated = LocalDate.now();
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportHeaderAll() {
    }
}
