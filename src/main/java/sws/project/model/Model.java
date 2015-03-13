package sws.project.model;

/**
 * Contains the basic model for each object type.
 */
public abstract class Model {
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
     * @throws java.lang.Exception if the shortName is invalid
     */
    public void setShortName(String shortName) throws Exception {
        if (shortName == null || shortName.trim().isEmpty()) throw new Exception("Short Name cannot be empty");
        this.shortName = shortName.trim();
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
