package sws.murcs.model.observable;

import javafx.beans.property.SimpleObjectProperty;

import java.lang.reflect.Field;

/**
 * Generic property used for notifying about changes in the value of a field.
 * @param <T> type of the field.
 */
public class ModelObjectProperty<T> extends SimpleObjectProperty<T> {

    /**
     * The field being watched.
     */
    private Field field;
    /**
     * The object the field exists in.
     */
    private Object object;

    /**
     * Instantiates a new ModelObjectProperty, used for notifying about a change in value of a field.
     * @param newObject Object that field exists in (can be a null pointer as long as refers to a valid memory location)
     * @param clazz Class type of object that the field exists in.
     * @param fieldName Name of the field to watch.
     * @throws NoSuchFieldException If/when the field does not exist.
     */
    public ModelObjectProperty(final Object newObject, final Class clazz, final String fieldName)
            throws NoSuchFieldException {
        field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        this.object = newObject;
    }

    @Override
    public final T get() {
        try {
            return (T) field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public final Object getBean() {
        return object;
    }

    @Override
    public final String getName() {
        return field.getName();
    }

    /**
     * Notifies listeners that this property has changed value.
     */
    public final void notifyChanged() {
        invalidated();
        fireValueChangedEvent();
    }
}
