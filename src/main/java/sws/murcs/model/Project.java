package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;

/**
 * Model of a Project.
 */
public class Project extends Model {
    @TrackableValue
    private String description;

    @TrackableValue
    @XmlElementWrapper(name = "releases")
    @XmlElement(name = "release")
    private ArrayList<Release> releases = new ArrayList<>();

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
     * Gets a list of releases associated with the project
     * @return The releases for this project
     */
    public ArrayList<Release> getReleases() {return releases;}

    /**
     * Adds a release to the project
     * @param release The release to add
     */
    public void addRelease(Release release){
        if (!getReleases().contains(release)) {
            releases.add(release);
        }
    }

    /**
     * Removes a release from the project
     * @param release The release to remove
     */
    public void removeRelease(Release release){
        if (releases.contains(release)) {
            releases.remove(release);
        }
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
