package sws.murcs.reporting;

import sws.murcs.model.Organisation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

/**'
 * A class that gets the information needed for the header of the report, such as the product version and
 * date generated.
 */
@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReportHeader {

    /**
     * The version of the project.
     */
    private String projectVersion;

    /**
     * The date the report was generated.
     */
    @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class)
    private LocalDate dateGenerated;

    /**
     * The report model (model of all the project).
     */
    @XmlElement(name = "content")
    private ReportModel reportModel;

    /**
     * Creates a new Report Header from a given organisation.
     * @param organisation the organisation given.
     */
    public ReportHeader(final Organisation organisation) {
        reportModel = new ReportModel(organisation);
        projectVersion = Organisation.getVersion();
        dateGenerated = LocalDate.now();
    }

    /**
     * A constructor for the report header (used by the XML serialiser).
     */
    private ReportHeader() {
    }
}
