package sws.project.model;

/**
 *
 */
public abstract class Model {
    private String shortName;
    private String longName;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        if (shortName == null) throw new IllegalArgumentException("shortname cannot be null!");

        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
}
