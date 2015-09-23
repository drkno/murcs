package sws.murcs.exceptions;

import sws.murcs.model.Sprint;
import sws.murcs.model.Story;

/**
 * Exception thrown when a story is in multiple sprints.
 */
public class MultipleSprintsException extends CustomException {

    /**
     * Previous sprint the story was used in.
     */
    private Sprint previousUsage;

    /**
     * The story that was already used.
     */
    private Story theStory;

    /**
     * Creates an exception for dealing with a story being in multiple sprints.
     * @param usage the sprint the story was previously used in.
     * @param story the story that was previously used.
     */
    public MultipleSprintsException(final Sprint usage, final Story story) {
        super(usage.getShortName() + " is already in sprint " + usage.getShortName());
        previousUsage = usage;
        theStory = story;
    }

    /**
     * Gets where the story was previously used.
     * @return the sprint where the story was previously used.
     */
    public Sprint getPreviousUsage() {
        return previousUsage;
    }

    /**
     * Gets the story that was previously used.
     * @return the story.
     */
    public Story getStory() {
        return theStory;
    }
}
