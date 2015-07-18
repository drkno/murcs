package sws.murcs.reporting.header;

import sws.murcs.model.Organisation;
import sws.murcs.model.Project;
import sws.murcs.reporting.model.ReportModelProject;

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
public class ReportHeaderProject extends ReportHeader {

    /**
     * Creates a new Report Header from a given project.
     * @param projects the projects given.
     */
    public ReportHeaderProject(final List<Project> projects) {
        reportModel = new ReportModelProject(projects);
        projectVersion = Organisation.getVersion();
        dateGenerated = LocalDate.now();
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportHeaderProject() {
    }
}
