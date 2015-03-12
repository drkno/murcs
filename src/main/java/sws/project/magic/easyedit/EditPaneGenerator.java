package sws.project.magic.easyedit;

import javafx.scene.Node;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A common interface for edit pane generators
 */
public interface EditPaneGenerator {
    /**
     * Gets a list of the types that the EditPaneGenerator can generate for
     * @return A list of the supported types
     */
    Class[] supportedTypes();

    /**
     * Generates a JavaFX node to edit the Specified Field
     * @param field The field to change
     * @param getter The getter for the field
     * @param setter The setter for the field
     * @param from The object to change the field on
     * @return The node for editing
     */
    Node generate(Field field, Method getter, Method setter, Object from);

    /**
     * Sets the argument on the EditPaneGenerator
     * @param argument The argument
     */
    void setArgument(String argument);
}
