package sws.murcs.model;

import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.TrackableValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

/**
 * A model that represents a release for a project.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Release extends Model {

    @TrackableValue
    private String description;
    @TrackableValue
    private LocalDate releaseDate;
    @TrackableValue
    private Project associatedProject;

    /**
     * Gets the release date for the release.
     * @return The release date
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    /**
     * Set the release date for the release
     * @param releaseDate The release date
     */
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
        commit("edit release");
    }

    /**
     * Gets the description for the release.
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the release.
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
        commit("edit release");
    }

    /**
     * Gets the Project that the release is associated with.
     * @return The associated project
     */
    public Project getAssociatedProject() {
        return associatedProject;
    }

    /**
     * Sets the project that the release is associated with.
     * @param associatedProject The associated project
     */
    public void setAssociatedProject(Project associatedProject) throws Exception{
        InvalidParameterException.validate("Associated Project", associatedProject);
        this.associatedProject = associatedProject;
        commit("edit release");
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Release && ((Release) object).getShortName().toLowerCase().equals(getShortName().toLowerCase());
    }

    @Override
    public String toString() {
        return getShortName();
    }
}
