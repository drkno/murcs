package sws.project.model.history;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Stack;

public class HistoryObject {
    private static final Stack<HistoryState> _revisionHistory = new Stack<>();;

    private HistoryValue _currentState;

    public HistoryObject() {
        try {
            ArrayList<Field> trackableFields = new ArrayList<>();
            for (Field field : getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(TrackState.class)) {
                    trackableFields.add(field);
                }
            }
            _currentState = new HistoryValue(trackableFields);
        }
        catch (Exception e) {
            System.err.println("Could not save current state as there is no state to save!");
            // continue anyway so the program isn't unstable.
        }
    }


    protected void saveCurrentState() {

    }

    public static String getLastestUndo() {
        return _revisionHistory.peek().getDescription();
    }
}
