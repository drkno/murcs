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
     * Layer 1 depth of the tree.
     */
    private final int immediateCount;

    /**
     * Creates a new object to store information about a dependency tree.
     * @param maximumTreeDepth maximum depth of the dependency tree.
     * @param storiesCount number of stories in the dependency tree.
     * @param immediateDepth the depth at layer 1.
     */
    protected DependencyTreeInfo(final int maximumTreeDepth, final int storiesCount, final int immediateDepth) {
        maxDepth = maximumTreeDepth;
        count = storiesCount;
        immediateCount = immediateDepth;
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

    /**
     * Gets the depth one layer deep.
     * @return the immediate depth.
     */
    public final int getImmediateDepth() {
        return immediateCount;
    }
}
