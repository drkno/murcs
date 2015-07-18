package sws.murcs.controller;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;

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
        ArrayList<Node> nodes = new ArrayList<Node>();
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
}
