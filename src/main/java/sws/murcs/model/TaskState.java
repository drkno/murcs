package sws.murcs.model;

/**
 * The states that a Task can be in.
 */
public enum TaskState {

    /**
     * The task is not started.
     */
    NotStarted,

    /**
     * The task is in progress.
     */
    InProgress,

    /**
     * The task is completed.
     */
    Done;

    @Override
    public String toString() {
        return super.toString().replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }
}
