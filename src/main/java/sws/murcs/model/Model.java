package sws.murcs.model;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

/**
 * Contains the basic model for each object type.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Model extends TrackableObject implements Serializable {
    @XmlAttribute
    @TrackableValue
    private String shortName;
    @TrackableValue
    private String longName;

    /**
     * Gets the short name.
     * @return the short name.
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the short name.
     * @param shortName the new short name.
     * @throws java.lang.Exception if the shortName is invalid
     */
    public void setShortName(String shortName) throws Exception {
        validateShortName(shortName);
        this.shortName = shortName.trim();
        commit("edit " + getClass().getSimpleName().toLowerCase());
    }

    /**
     * Indicates whether a value is a valid value for 'shortName' to hold
     * @param value The value.
     * @throws sws.murcs.exceptions.DuplicateObjectException if there is a duplicate object.
     */
    private void validateShortName(String value) throws Exception {
        DuplicateObjectException.CheckForDuplicates(this, value);
        InvalidParameterException.validate("Short Name", value);
    }

    /**
     * Gets the long name.
     * @return the long name.
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Sets the long name.
     * @param longName the new long name
     */
    public void setLongName(String longName) {
        this.longName = longName;
        commit("edit " + getClass().getSimpleName().toLowerCase());
    }
}
