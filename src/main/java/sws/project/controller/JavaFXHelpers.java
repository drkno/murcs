package sws.project.controller;

import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.ArrayList;

/**
 * Created on 18/03/2015.
 */
public class JavaFXHelpers {

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    public static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }

    public static Node getByID(Parent root, String id) {
        ArrayList<Node> nodes = getAllNodes(root);
        for (Node node : nodes) {
            if (node.getId() != null && node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }
}
