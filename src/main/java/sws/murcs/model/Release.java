package sws.murcs.model;

import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.reporting.LocalDateAdapter;
import sws.murcs.search.Searchable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A model that represents a release for a project.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Release extends Model {

    /**
     * Serialisation ID for backwards compatible serialisation.
     */
    private static final long serialVersionUID = 0L;

    /**
     * The date the release is due.
     */
    @Searchable
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

    /**
     * Gets the pair programming groups that occurred in this story.
     * @return the groups that peer programmed together.
     */
    @XmlElementWrapper(name = "pairs")
    @XmlElement(name = "pair")
    public final List<PeerProgrammingGroup> getPairProgrammingGroups() {
        List<Sprint> sprints = UsageHelper.findAllBy(ModelType.Sprint, s -> s.getAssociatedRelease().equals(this));
        List<PeerProgrammingGroup> groups = sprints.stream()
                .map(Sprint::getPairProgrammingGroups).flatMap(Collection::stream)
                .collect(Collectors.toList());
        float total = sprints.stream().map(Sprint::getPairProgrammingGroups)
                .filter(g -> g.size() > 0)
                .map(g -> g.get(0).getTotalTime()).reduce((a, b) -> a + b).orElse(0f);

        Map<String, Float> map = new HashMap<>();
        groups.forEach(e -> {
            String name = e.getGroupMembers();
            if (map.containsKey(name)) {
                map.put(name, map.get(name) + e.getTimeSpent());
            } else {
                map.put(name, e.getTimeSpent());
            }
        });

        return map.entrySet().stream()
                .map(e -> new PeerProgrammingGroup(e.getKey(), e.getValue(),
                        total)).collect(Collectors.toList());
    }
}
