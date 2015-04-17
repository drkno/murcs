package sws.murcs.magic.tracking;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * An object that is trackable by the UndoRedoManager.
 */
public abstract class TrackableObject {
    private ArrayList<Field> trackedFields;

    /**
     * Instantiates a new TrackableObject by getting annotated fields,
     * then adding this class for tracking.
     */
    protected TrackableObject() {
        initialiseTrackedFields();
        UndoRedoManager.add(this);
    }

    /**
     * Gets all the annotated fields and adds them to the tracked fields list.
     */
    private void initialiseTrackedFields() {
        trackedFields = new ArrayList<>();
        Class clazz = getClass();
        while (clazz != TrackableObject.class) {
            for (Field field : getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(TrackableValue.class)) {
                    trackedFields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Gets all of the fields in this class that can be tracked.
     * @return an ArrayList of trackable fields.
     */
    protected ArrayList<Field> getTrackedFields() {
        return trackedFields;
    }

    /**
     * Stops this object being tracked by the UndoRedoManager.
     */
    public void stopTracking() {
        UndoRedoManager.remove(this);
    }
}
