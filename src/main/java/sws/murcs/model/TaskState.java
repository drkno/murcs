package sws.murcs.model;

import sws.murcs.internationalization.InternationalizationHelper;

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
        return InternationalizationHelper.tryGet(super.toString());
    }
}
