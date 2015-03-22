package sws.murcs.controller;

import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.ArrayList;

/**
 * Helpers for Javafx
 */
public class JavaFXHelpers {

    /**
     * Gets all of the children of a parent node
     * @param parent The parent to get the children of
     * @return The children of the parent
     */
    public static ArrayList<Node> getAllChildNodes(Parent parent) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendants(parent, nodes);
        return nodes;
    }

    /**
     * Adds all descendants of a parent to an array list of nodes
     * @param parent The parent to get the descendants from
     * @param nodes The ArrayList of Nodes to add the descendants to
     */
    public static void addAllDescendants(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendants((Parent) node, nodes);
        }
    }

    /**
     * Gets a node based on its ID in the descendants of the given parent. Returns null if there is no such node
     * @param parent The parent who's descendants you search
     * @param id The id to find
     * @return The node with the given ID in the descendants of the parent.
     */
    public static Node getByID(Parent parent, String id) {
        ArrayList<Node> nodes = getAllChildNodes(parent);
        for (Node node : nodes) {
            if (node.getId() != null && node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }
}
