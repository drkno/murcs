package sws.murcs.controller;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import sws.murcs.controller.controls.md.MaterialDesignButton;

import java.util.ArrayList;

/**
 * Helpers for Javafx.
 */
public final class JavaFXHelpers {

    /**
     * Empty constructor for utility class.
     */
    private JavaFXHelpers() {
        // Not called as this is a utility class.
    }

    /**
     * Gets all of the children of a parent node.
     * @param parent The parent to get the children of
     * @return The children of the parent
     */
    public static ArrayList<Node> getAllChildNodes(final Parent parent) {
        ArrayList<Node> nodes = new ArrayList<>();
        addAllDescendants(parent, nodes);
        return nodes;
    }

    /**
     * Adds all descendants of a parent to an array list of nodes.
     * @param parent The parent to get the descendants from
     * @param nodes The ArrayList of Nodes to add the descendants to
     */
    public static void addAllDescendants(final Parent parent, final ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent) {
                addAllDescendants((Parent) node, nodes);
            }
        }
    }

    /**
     * Gets a node based on its ID in the descendants of the given parent.
     * Returns null if there is no such node.
     * @param parent The parent who's descendants you search
     * @param id The id to find
     * @return The node with the given ID in the descendants of the parent.
     */
    public static Node getByID(final Parent parent, final String id) {
        ArrayList<Node> nodes = getAllChildNodes(parent);
        for (Node node : nodes) {
            if (node.getId() != null && node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Converts a hex colour into an Color type.
     * @param colourStr The hex value to convert
     * @return A Color
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public static Color hex2RGB(final String colourStr) {
        return Color.color(
                Double.valueOf(Integer.valueOf(colourStr.substring(1, 3), 16)) / 255,
                Double.valueOf(Integer.valueOf(colourStr.substring(3, 5), 16)) / 255,
                Double.valueOf(Integer.valueOf(colourStr.substring(5, 7), 16)) / 255);
    }

    /**
     * Finds and destroys the usefulness of controls.
     * Note: this is inefficient (there isn't really an efficient way to do it without
     * knowing about all controls beforehand) so should be used sparingly.
     * This is done by hiding buttons and disabling controls where appropriate.
     * @param currentNode the parent node to start from.
     */
    public static void findAndDestroyControls(final Parent currentNode) {
        if (currentNode == null) {
            return;
        }

        currentNode.getChildrenUnmodifiable().forEach(node -> {
            if (Button.class.isAssignableFrom(node.getClass()) || node instanceof MaterialDesignButton) {
                node.setVisible(false);
            } else if (node instanceof Hyperlink) {
                node.setDisable(true);
                node.getStyleClass().add("control-disabled");
            } else if (node instanceof TextField || node instanceof ComboBox || node instanceof TextArea
                    || node instanceof ChoiceBox || node instanceof ListView || node instanceof TableView
                    || node instanceof DatePicker || node instanceof CheckBox || node instanceof RadioButton) {
                node.setDisable(true);
                node.getStyleClass().add("control-disabled");
            } else if (node instanceof ScrollPane) {
                Node content = ((ScrollPane) node).getContent();
                if (content != null) {
                    findAndDestroyControls((Parent) content);
                }
            } else if (node instanceof TitledPane) {
                Node content = ((TitledPane) node).getContent();
                if (content != null) {
                    findAndDestroyControls((Parent) content);
                }
            }

            node.setFocusTraversable(false);
            if (!(node instanceof Parent)) {
                return;
            }

            findAndDestroyControls((Parent) node);
        });
    }
}
