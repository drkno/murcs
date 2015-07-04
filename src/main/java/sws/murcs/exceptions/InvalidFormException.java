package sws.murcs.exceptions;

import javafx.scene.Node;

import java.util.Map;

/**
 * Thrown if a form has invalid information in it. Contains a HashMap of all the invalid parts of the form.
 */
public class InvalidFormException extends CustomException {

    /**
     * The sections of the form which are invalid and their error messages.
     */
    private Map<Node, String> invalidSections;

    /**
     * Creates a new InvalidFormException from a given piece of information about an invalid form.
     * @param errorMap The information.
     */
    public InvalidFormException(final Map<Node, String> errorMap) {
        invalidSections = errorMap;
    }

    /**
     * Gets the invalid sections.
     * @return The invalid sections.
     */
    public final Map<Node, String> getInvalidSections() {
        return invalidSections;
    }
}
