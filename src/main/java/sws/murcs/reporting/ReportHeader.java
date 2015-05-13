package sws.murcs.reporting;

import sws.murcs.model.RelationalModel;

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
     * Creates a new Report Header from a given relational model.
     * @param relationalModel the relational model given.
     */
    public ReportHeader(final RelationalModel relationalModel) {
        reportModel = new ReportModel(relationalModel);
        projectVersion = RelationalModel.getVersion();
        dateGenerated = LocalDate.now();
    }

    /**
     * A constructor for the report header (used by the XML serialiser).
     */
    @SuppressWarnings("unused")
    private ReportHeader() {
    }
}
