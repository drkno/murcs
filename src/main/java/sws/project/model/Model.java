package sws.project.model;

import sws.project.magic.tracking.TrackValue;
import sws.project.magic.tracking.ValueTracker;
import sws.project.magic.easyedit.Editable;
import sws.project.magic.easyedit.fxml.FxmlPaneGenerator;

import java.io.Serializable;

/**
 * Contains the basic model for each object type.
 */
public abstract class Model extends ValueTracker implements Serializable{
    @TrackValue
    @Editable(editPaneGenerator = FxmlPaneGenerator.class, argument = "/sws/project/String.fxml", validatorName = "validateShortName")
    private String shortName;
    @TrackValue
    @Editable(editPaneGenerator = FxmlPaneGenerator.class, argument = "/sws/project/String.fxml")
    private String longName;

    /**
     * Gets the short name.
     * @return the short name.
     */
    public String getShortName() { return shortName; }

    /**
     * Sets the short name.
     * @param shortName the new short name.
     * @throws java.lang.Exception if the shortName is invalid
     */
    public void setShortName(String shortName) throws Exception {
        if (!validateShortName(shortName)) throw new Exception("Short Name cannot be empty");

        this.shortName = shortName.trim();
        saveCurrentState("Short Name change");
    }

    /**
     * Indicates whether a value is a valid value for 'shortName' to hold
     * @param value The value
     * @return Whether the value is valid for 'shortName'
     */
    private boolean validateShortName(String value){
        return value != null && !value.trim().isEmpty();
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
        saveCurrentState("Long Name change");
    }
}
