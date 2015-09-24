package sws.murcs.reporting.adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

/**
 * Representation of the estimate info for XML report generation.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EstimateXMLRepresentation {

    /**
     * Date of estimate.
     */
    @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class)
    protected LocalDate date;

    /**
     * Estimate for a date.
     */
    protected Float estimateValue;

    /**
     * Required by JAXB.
     */
    private EstimateXMLRepresentation() {
    }

    /**
     * Base constructor for storing.
     * @param date Date of estimate.
     * @param estimateValue Estimate value the for date.
     */
    public EstimateXMLRepresentation(final LocalDate date, final Float estimateValue) {
        this.date = date;
        this.estimateValue = estimateValue;
    }
}
