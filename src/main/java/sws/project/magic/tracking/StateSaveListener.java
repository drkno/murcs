package sws.project.magic.tracking;

/**
 * Listener for states that change.
 */
public interface StateSaveListener {
    /**
     * Run when a new change is saved.
     * @param change change that was made.
     */
    public void run(ValueChange change);
}
