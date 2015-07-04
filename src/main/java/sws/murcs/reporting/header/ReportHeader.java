package sws.murcs.reporting.header;

import sws.murcs.reporting.LocalDateAdapter;
import sws.murcs.reporting.model.ReportModel;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

/**
 * A class that gets the information needed for the header of the report, such as the product version and
 * date generated.
 */
@XmlSeeAlso({ReportHeaderAll.class,
        ReportHeaderPerson.class,
        ReportHeaderProject.class,
        ReportHeaderTeam.class,
        ReportHeaderBacklog.class,
        ReportHeaderStory.class})
public abstract class ReportHeader {
    /**
     * The version of the project.
     */
    @XmlElement(name = "projectVersion")
    String projectVersion;
    /**
     * The date the report was generated.
     */
    @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class)
    LocalDate dateGenerated;
    /**
     * The report model (model of all the project).
     */
    @XmlElement(name = "content", type = ReportModel.class)
    ReportModel reportModel;
}
