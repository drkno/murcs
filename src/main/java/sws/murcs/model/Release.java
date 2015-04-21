package sws.murcs.model;

import sws.murcs.exceptions.InvalidParameterException;

import java.time.LocalDate;

/**
 * A model that represents a release for a project.
 */
public class Release extends Model {

    private String description;
    private LocalDate releaseDate;
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
