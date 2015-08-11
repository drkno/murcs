package sws.murcs.model.helpers;

/**
 * Stores information about a dependency tree.
 */
public class DependencyTreeInfo {
    /**
     * Maximum depth of the dependency tree.
     */
    private final int maxDepth;

    /**
     * Number of stories in the dependency tree.
     */
    private final int count;

    /**
     * Creates a new object to store information about a dependency tree.
     * @param maximumTreeDepth maximum depth of the dependency tree.
     * @param storiesCount number of stories in the dependency tree.
     */
    protected DependencyTreeInfo(final int maximumTreeDepth, final int storiesCount) {
        maxDepth = maximumTreeDepth;
        count = storiesCount;
    }

    /**
     * Gets the maximum depth of the dependency tree.
     * @return the maximum depth of the dependency tree.
     */
    public final int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Gets the number of stories in the dependency tree.
     * @return the number of stories in the dependency tree.
     */
    public final int getCount() {
        return count;
    }
}
