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

    @TrackableValue
    private String description;
    @TrackableValue
    @XmlJavaTypeAdapter(type = LocalDate.class, value = LocalDateAdapter.class)
    private LocalDate releaseDate = LocalDate.now();

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

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof Release)) return false;
        Release release = (Release)object;
        if (release == this) return true;
        String rShortName = release.getShortName();
        String shortName = getShortName();
        if (shortName == null || rShortName == null) return shortName == rShortName;
        return rShortName.toLowerCase().equals(shortName.toLowerCase());
    }

    @Override
    public String toString() {
        return getShortName();
    }
}
