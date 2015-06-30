package sws.murcs.model.helpers;

import sws.murcs.model.Story;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Helps manage dependencies by providing helper methods to perform tasks such as
 * searching for graph cycles and collecting the entire dependency tree into a flat
 * collection.
 */
public final class DependenciesHelper {

    /**
     * Utilities class, private constructor.
     */
    private DependenciesHelper() {
    }

    /**
     * Determines if it is possible to navigate from one node to another using
     * dependencies as graph edges (is a node reachable from another?).
     * @param searchNode node to search from.
     * @param reachableNode node to search for.
     * @return true if node is found, false otherwise.
     */
    public static boolean isReachable(final Story searchNode, final Story reachableNode) {
        if (searchNode == reachableNode) {
            return true;    // special case
        }

        Set<Story> visitedSet = new HashSet<>();
        Queue<Story> queue = new ArrayDeque<>();
        queue.offer(searchNode);
        visitedSet.add(searchNode);
        while (!queue.isEmpty()) {
            if (isReachable(visitedSet, queue, queue.poll(), reachableNode)) return true;
        }

        return false;
    }

    /**
     * Determines if a node is immediately reachable from the given search node, otherwise
     * adding all immediately reachable nodes to the visited set and the queue (provided
     * they have not already been visited).
     * @param visitedSet set of previously visited nodes.
     * @param queue queue of nodes to search in future.
     * @param searchNode node to search from.
     * @param reachableNode node to search for.
     * @return true if node is found, false otherwise.
     */
    private static boolean isReachable(final Set<Story> visitedSet, final Queue<Story> queue,
                                       final Story searchNode, final Story reachableNode) {
        for (Story story : searchNode.getImmediateDependencies()) {
            if (visitedSet.contains(story)) continue;
            if (story == reachableNode) return true;
            queue.offer(story);
            visitedSet.add(story);
        }
        return false;
    }
}
