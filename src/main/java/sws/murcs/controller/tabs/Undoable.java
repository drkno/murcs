package sws.murcs.controller.tabs;

/**
 * Provides methods of linking undo commands to a controller
 */
public interface Undoable {
    public void undo();
    public void redo();
    public void revert();
}
