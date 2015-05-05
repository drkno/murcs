package sws.murcs.magic.tracking.listener;

/**
 * Possible state notifications of the UndoRedoManager.
 */
public enum ChangeState {
    /**
     * A forget operation occurred.
     */
    Forget,
    /**
     * A revert operation occurred.
     */
    Revert,
    /**
     * A commit operation occurred.
     */
    Commit,
    /**
     * A remake operation occurred.
     */
    Remake,
    /**
     * An assimilate operation occurred.
     */
    Assimilate
}
