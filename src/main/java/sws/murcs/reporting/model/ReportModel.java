package sws.murcs.reporting.model;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
@XmlSeeAlso({ReportModelAll.class,
        ReportModelProject.class,
        ReportModelTeam.class,
        ReportModelPerson.class,
        ReportModelBacklog.class,
        ReportModelStory.class,
        ReportModelSprint.class})
public abstract class ReportModel {
}
