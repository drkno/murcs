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
     * The task is pending.
     */
    Pending,

    /**
     * The task is blocked by another task.
     */
    Blocked,

    /**
     * The task is ready to be started.
     */
    Ready,

    /**
     * The task is completed.
     */
    Done,

    /**
     * The task is deferred to a later time.
     */
    Deferred
}
