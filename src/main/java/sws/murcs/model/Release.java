package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.reporting.LocalDateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

/**
 * A model that represents a release for a project.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Release extends Model {

    /**
     * Description of the release.
     */
    @TrackableValue
    private String description;
    /**
     * The date the release is due.
     */
    @TrackableValue
    @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class)
    private LocalDate releaseDate = LocalDate.now();

    /**
     * Gets the release date for the release.
     * @return The release date
     */
    public final LocalDate getReleaseDate() {
        return releaseDate;
    }

    /**
     * Set the release date for the release.
     * @param realease The release date
     */
    public final void setReleaseDate(final LocalDate realease) {
        this.releaseDate = realease;
        commit("edit release");
    }

    /**
     * Gets the description for the release.
     * @return The description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the description of the release.
     * @param newDescription The description
     */
    public final void setDescription(final String newDescription) {
        this.description = newDescription;
        commit("edit release");
    }

    @Override
    public final boolean equals(final Object object) {
        if (object == null || !(object instanceof Release)) {
            return false;
        }
        Release release = (Release) object;
        if (release == this) {
            return true;
        }
        String rShortName = release.getShortName();
        String shortName = getShortName();
        if (shortName == null || rShortName == null) {
            return shortName == rShortName;
        }
        return rShortName.toLowerCase().equals(shortName.toLowerCase());
    }

    @Override
    public final int hashCode() {
        int c = 0;
        if (getShortName() != null) {
            c = getShortName().hashCode();
        }
        return getHashCodePrime() + c;
    }

    /**
     * Adds the current release instance to a project.
     * @param project The project to add the release to.
     */
    public final void changeRelease(final Project project) {
        project.addRelease(this);
        commit("edit release");
    }
}
