package sws.murcs.model;

import sws.murcs.exceptions.CyclicDependencyException;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.model.helpers.DependenciesHelper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * A class representing a story in the backlog for a project.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Story extends Model {
    /**
     * A description of the story.
     */
    @TrackableValue
    private String description;

    /**
     * The person who created this story. This should not be changed after
     * initial creation.
     */
    @TrackableValue
    private Person creator;

    /**
     * Stories that must be complete before this story can be worked on.
     */
    @TrackableValue
    private Collection<Story> dependencies;

    /**
     * Instantiates a new Story.
     */
    public Story() {
        dependencies = new LinkedHashSet<>();
    }

    /**
     * Gets the stories that this story immediately (not transitively)
     * requires to be complete before work can begin.
     * @return a collection of the immediate dependencies.
     */
    public final Collection<Story> getImmediateDependencies() {
        return Collections.unmodifiableCollection(dependencies);
    }

    /**
     * Adds a new Story that this Story requires to be complete before
     * work can begin.
     * @param dependentStory the new dependency.
     * @throws CyclicDependencyException when adding the new dependency would create a
     * dependency cycle.
     */
    public final void addDependency(final Story dependentStory) throws CyclicDependencyException {
        if (dependencies.contains(dependentStory)) return;
        if (DependenciesHelper.isReachable(dependentStory, this)) {
            throw new CyclicDependencyException(this, dependentStory);
        }
        dependencies.add(dependentStory);
        commit("edit story");
    }

    /**
     * Removes a Story that this Story requires to be complete before
     * work can begin.
     * @param dependentStory the dependency to remove.
     */
    public final void removeDependency(final Story dependentStory) {
        dependencies.remove(dependentStory);
        commit("edit story");
    }

    /**
     * Gets a description for the current story.
     * @return The description.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Sets the description of the story.
     * @param newDescription The new description.
     */
    public final void setDescription(final String newDescription) {
        this.description = newDescription;
        commit("edit story");
    }

    /**
     * Gets the creator of this story.
     * @return The creator
     */
    public final Person getCreator() {
        return creator;
    }

    /**
     * Sets the creator of the story.
     * This should not be changed after initially being set.
     * @param person The creator
     */
    public final void setCreator(final Person person) {
        this.creator = person;
        commit("edit story");
    }

    @Override
    public final int hashCode() {
        int c = 0;
        if (getShortName() != null) {
            c = getShortName().hashCode();
        }
        return getHashCodePrime() + c;
    }

    @Override
    public final boolean equals(final Object object) {
        if (object == null || !(object instanceof Story)) {
            return false;
        }
        String shortNameO = ((Story) object).getShortName();
        String shortName = getShortName();
        if (shortName == null || shortNameO == null) {
            return shortName == shortNameO;
        }
        return shortName.toLowerCase().equals(shortNameO.toLowerCase());
    }
}
