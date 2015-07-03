package sws.murcs.reporting.model;

import sws.murcs.model.Project;

import javax.xml.bind.annotation.XmlElement;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
public class ReportModelProject extends ReportModel {
    /**
     * The project in the report.
     */
    @XmlElement(name = "project")
    private Project project;

    /**
     * Constructor.
     * @param pProject a project
     */
    public ReportModelProject(final Project pProject) {
        project = pProject;
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportModelProject() {
    }
}
