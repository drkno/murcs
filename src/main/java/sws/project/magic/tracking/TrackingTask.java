package sws.project.magic.tracking;

import java.util.TimerTask;

/**
 * Actions to perform when a tracking adds a new history state.
 */
public abstract class TrackingTask extends TimerTask {
    private ValueChange change;

    @Override
    public void run() {
        onChange(change);
        cancel();
    }

    /**
     * Sets the change that occurred.
     * @param change change that occurred.
     */
    protected void setChange(ValueChange change) {
        this.change = change;
    }

    /**
     * Method that occurs on a change.
     * @param change change that occurred.
     */
    protected abstract void onChange(ValueChange change);
}
