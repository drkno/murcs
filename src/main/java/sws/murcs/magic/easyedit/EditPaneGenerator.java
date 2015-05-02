package sws.murcs.magic.easyedit;

import javafx.scene.Node;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A common interface for model pane generators
 */
public interface EditPaneGenerator {
    /**
     * Gets a list of the types that the EditPaneGenerator can generate for
     * @return A list of the supported types
     */
    Class[] supportedTypes();

    /**
     * Generates a JavaFX node to model the Specified Field
     * @param field The field to change
     * @param getter The getter for the field
     * @param setter The setter for the field
     * @param validator The validator for the field. This can quite realistically be null, if no validator is specified
     * @param from The object to change the field on
     * @return The node for editing
     * @throws java.lang.Exception when class is unsupported
     */
    Node generate(Field field, Method getter, Method setter, Method validator, Object from) throws Exception;

    /**
     * Sets the argument on the EditPaneGenerator
     * @param argument The argument
     */
    void setArgument(String argument);
}
