package sws.murcs.reporting.model;

import sws.murcs.model.Person;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * A model which matches the output of the status reports so it can be directly serialized.
 */
public class ReportModelPerson extends ReportModel {
    /**
     * The people in the report.
     */
    @XmlElementWrapper(name = "people")
    @XmlElement(name = "person")
    private List<Person> people;

    /**
     * Constructor.
     * @param pPeople people
     */
    public ReportModelPerson(final List<Person> pPeople) {
        people = pPeople;
    }

    /**
     * An unused constructor that is needed by Jaxb for some reason.
     */
    private ReportModelPerson() {
    }
}
