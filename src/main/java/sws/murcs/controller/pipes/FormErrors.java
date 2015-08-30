package sws.murcs.controller.pipes;

import javafx.scene.Node;

/**
 * An interface that can be applied to anything that you want to display errors on.
 */
public interface FormErrors {

    /**
     * Clears all the errors on the form.
     */
    void clearErrors();

    /**
     * Clears all the errors in a specific section from the form.
     * @param sectionName the section to clear errors for.
     */
    void clearErrors(final String sectionName);

    /**
     * Adds a form error with a helpful message and the node that is causing the error.
     * @param invalidNode the node that is causing the error.
     * @param helpfulMessage the message explaining what is wrong.
     */
    void addFormError(final Node invalidNode, final String helpfulMessage);

    /**
     * Adds a form error in relation to a specific section with the same deatils as the previous addformerror.
     * @param sectionName the section the error belongs to.
     * @param invalidNode the node that is causing the error.
     * @param helpfulMessage the message explaining what is wrong.
     */
    void addFormError(final String sectionName, final Node invalidNode, final String helpfulMessage);

}
