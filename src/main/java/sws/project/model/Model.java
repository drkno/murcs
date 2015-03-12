package sws.project.model;

import sws.project.model.magic.tracking.ValueTracker;

/**
 * Contains the basic model for each object type.
 */
public abstract class Model extends ValueTracker {
    private String shortName;
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
     * @throws java.lang.IllegalArgumentException if the shortName is invalid
     */
    public void setShortName(String shortName) {
        if (shortName == null) throw new IllegalArgumentException("shortname cannot be null!");
        this.shortName = shortName;
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
    }
}
