package sws.murcs.reporting.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

/**
 * Adapter for converting local dates to strings so that they appear correctly in the report.
 */
public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    /**
     * Converts the local date from a string to a local date.
     * @param v the local date
     * @return A new local date with the date given in the string
     * @throws Exception An exception from parsing the string
     */
    public final LocalDate unmarshal(final String v) throws Exception {
        return LocalDate.parse(v);
    }

    /**
     * Converts the local date given to a string so it can be displayed in the report.
     * @param v The local date being converted to a string
     * @return the local date as a string
     * @throws Exception An exception from converting the local date to a string
     */
    public final String marshal(final LocalDate v) throws Exception {
        return v.toString();
    }
}
