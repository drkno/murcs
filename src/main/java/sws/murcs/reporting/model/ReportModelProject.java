package sws.murcs.reporting.model;

import sws.murcs.model.Project;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
public class ReportModelProject extends ReportModel {
    /**
     * The projects in the report.
     */
    @XmlElementWrapper(name = "projects")
    @XmlElement(name = "project")
    private List<Project> projects;

    /**
     * Constructor.
     * @param pProjects projects
     */
    public ReportModelProject(final List<Project> pProjects) {
        projects = pProjects;
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportModelProject() {
    }
}
