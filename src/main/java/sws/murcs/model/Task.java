package sws.murcs.model;

/**
 * A class for keeping track of a Task within a story.
 */
public class Task extends Model {

    /**
     * The estimate in hours for the time it will take to complete the task.
     */
    private float estimate;

    /**
     * The state that the task is currently in.
     */
    private TaskState state;

    /**
     * Gets the current state of the task.
     * @return The current state.
     */
    public final TaskState getState() {
        return state;
    }

    /**
     * Sets the state for the task.
     * @param newState The state that it's being changed to.
     */
    public final void setState(final TaskState newState) {
        this.state = newState;
    }

    /**
     * Gets the current estimate for the task. This is given in hours.
     * @return The current estimate for the task in hours.
     */
    public final float getEstimate() {
        return estimate;
    }

    /**
     * Sets the estimate for the task in hours.
     * @param newEstimate The new estimate for the task.
     */
    public final void setEstimate(final float newEstimate) {
        this.estimate = newEstimate;
    }

    @Override
    public final boolean equals(final Object object) {
        if (object == null || !(object instanceof Task)) {
            return false;
        }
        String shortName = getShortName();
        String shortNameO = ((Task) object).getShortName();
        if (shortName == null || shortNameO == null) {
            return shortName == shortNameO;
        }
        return shortName.toLowerCase().equals(shortNameO.toLowerCase());
    }

    @Override
    public final int hashCode() {
        int c = 0;
        if (getShortName() != null) {
            c = getShortName().hashCode();
        }
        return getHashCodePrime() + c;
    }
}
