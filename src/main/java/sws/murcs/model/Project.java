package sws.murcs.model;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.magic.easyedit.Editable;
import sws.murcs.magic.tracking.TrackableValue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Model of a Project.
 */
public class Project extends Model {

    @Editable()
    @TrackableValue
    private String description;

    /**
     * Gets a description of the project
     * @return a description of the project
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the current project
     * @param description The description of the project
     */
    public void setDescription(String description) {
        this.description = description;
        commit("edit project");
    }

    /**
     * Returns the short name of the project
     * @return Short name of the project
     */
    @Override
    public String toString() {
        return getShortName();
    }

    @Override
    public boolean equals(Object object) {
        return object != null && getShortName() != null && object instanceof Project && ((Project) object).getShortName().toLowerCase().equals(getShortName().toLowerCase());
    }
}
