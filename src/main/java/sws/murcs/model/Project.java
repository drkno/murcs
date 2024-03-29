package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.search.Searchable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import java.util.ArrayList;
import java.util.List;

/**
 * Model of a Project.
 */
public class Project extends Model {

    /**
     * Serialisation ID for backwards compatible serialisation.
     */
    private static final long serialVersionUID = 0L;

    /**
     * The releases associated with the project.
     */
    @Searchable
    @TrackableValue
    @XmlElementWrapper(name = "releases")
    @XmlElement(name = "release")
    @XmlIDREF
    private List<Release> releases = new ArrayList<>();

    /**
     * The backlogs for a project.
     */
    @Searchable
    @TrackableValue
    @XmlElementWrapper(name = "backlogs")
    @XmlElement(name = "backlog")
    @XmlIDREF
    private List<Backlog> backlogs = new ArrayList<>();

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

    /**
     * Get the backlogs for a project.
     * @return The list of backlogs
     */
    public final List<Backlog> getBacklogs() {
        return backlogs;
    }

    /**
     * Add a backlog to the project.
     * @param backlog The backlog to add
     */
    public final void addBacklog(final Backlog backlog) {
        if (!backlogs.contains(backlog)) {
            backlogs.add(backlog);
        }
    }

    /**
     * Remove a backlog from the project.
     * @param backlog The backlog to remove
     */
    public final void removeBacklog(final Backlog backlog) {
        if (!backlogs.contains(backlog)) {
            backlogs.remove(backlog);
        }
    }
}
