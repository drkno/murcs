package sws.project.magic.tracking;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Actions to perform when a tracking adds a new history state.
 */
public class TrackingTask extends TimerTask {
    private ValueChange _change;
    private ArrayList<StateSaveListener> _listeners;

    /**
     * Creates a new TrackingTask
     * @param change change that the task is for
     * @param listeners listeners to run
     */
    public TrackingTask(ValueChange change, ArrayList<StateSaveListener> listeners) {
        _change = change;
        _listeners = listeners;
    }

    @Override
    public void run() {
        _listeners.forEach(l -> l.run(_change));
        cancel();
    }
}
