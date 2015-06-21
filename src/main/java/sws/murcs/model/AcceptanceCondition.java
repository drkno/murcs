package sws.murcs.model;

/**
 * A class representing a single
 * acceptance condition on a story.
 */
public class AcceptanceCondition {
    /**
     * The text representing the condition
     */
    private String condition;

    /**
     * Gets a string describing this condition
     * @return The condition
     */
    public final String getCondition() {
        return condition;
    }

    /**
     * Sets the condition that this acceptance condition
     * describes
     * @param condition The new condition
     */
    public final void setCondition(final String condition) {
        this.condition = condition;
    }
}
