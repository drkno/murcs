package sws.murcs.reporting;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
@XmlSeeAlso({ReportModelAll.class, ReportModelProject.class, ReportModelTeam.class, ReportModelPerson.class})
public abstract class ReportModel {
}
