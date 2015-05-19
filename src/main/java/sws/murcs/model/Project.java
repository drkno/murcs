package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * Model of a Project.
 */
public class Project extends Model {
    /**
     * The description of the project.
     */
    @TrackableValue
    private String description;

    /**
     * The releases associated with the project.
     */
    @TrackableValue
    @XmlElementWrapper(name = "releases")
    @XmlElement(name = "release")
    private List<Release> releases = new ArrayList<>();

    /**
     * Gets a description of the project.
     * @return a description of the project
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the current project.
     * @param newDescription The description of the project
     */
    public final void setDescription(final String newDescription) {
        this.description = newDescription;
        commit("edit project");
    }

    /**
     * Gets a list of releases associated with the project.
     * @return The releases for this project
     */
    public final List<Release> getReleases() {
        return releases;
    }

    /**
     * Adds a release to the project.
     * @param release The release to add
     */
    public final void addRelease(final Release release) {
        if (!getReleases().contains(release)) {
            releases.add(release);
        }
    }

    /**
     * Removes a release from the project.
     * @param release The release to remove
     */
    public final void removeRelease(final Release release) {
        if (releases.contains(release)) {
            releases.remove(release);
        }
    }

    /**
     * Returns the short name of the project.
     * @return Short name of the project
     */
    @Override
    public final String toString() {
        return getShortName();
    }

    @Override
    public final boolean equals(final Object object) {
        if (!(object instanceof Project)) {
            return false;
        }
        Project project = (Project) object;
        String shortName1 = project.getShortName();
        String shortName2 = getShortName();
        if (shortName1 == null || shortName2 == null) {
            return shortName1 == shortName2;
        }
        return shortName1.toLowerCase().equals(shortName2.toLowerCase());
    }

    @Override
    public final int hashCode() {
        int c = 0;
        if (getShortName() != null) {
            c = getShortName().hashCode();
        }
        return getHashCodePrime() + c;
    }
}
