package sws.murcs.exceptions;

import sws.murcs.model.Story;

/**
 * Exception for when adding a dependency to a story would create
 * a dependency cycle.
 */
public final class CyclicDependencyException extends CustomException {

    /**
     * The story the dependency is being added to.
     */
    private Story storyNode;

    /**
     * The story that is being added as a dependency.
     */
    private Story dependencyNode;

    /**
     * Creates a new CyclicDependencyException, indicating that adding a dependency would create a cycle.
     * @param story story that the dependency is being added to.
     * @param dependency story that is being added as a dependency.
     */
    public CyclicDependencyException(final Story story, final Story dependency) {
        super("Cannot add story \"" + dependency + "\" as a dependency of \"" + story + "\" as a "
                + "dependency cycle would be created.");
        storyNode =  story;
        dependencyNode = dependency;
    }

    /**
     * Gets the Story that was having the dependency added to it.
     * @return the story.
     */
    public Story getStory() {
        return storyNode;
    }

    /**
     * Gets the Story that was the dependency being added.
     * @return the dependency.
     */
    public Story getDependency() {
        return dependencyNode;
    }
}
