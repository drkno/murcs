package sws.murcs.reporting.model;

import sws.murcs.model.Person;

import javax.xml.bind.annotation.XmlElement;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
public class ReportModelPerson extends ReportModel {
    /**
     * The person in the report.
     */
    @XmlElement(name = "person")
    private Person person;

    /**
     * Constructor.
     * @param pPerson a person
     */
    public ReportModelPerson(final Person pPerson) {
        person = pPerson;
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportModelPerson() {
    }
}
