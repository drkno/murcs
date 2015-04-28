package sws.murcs.magic.tracking.listener;

/**
 * Possible state notifications of the UndoRedoManager.
 */
public enum ChangeState {
    Forget,
    Revert,
    Commit,
    Remake,
    Assimilate
}
