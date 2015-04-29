package sws.murcs.model.observable;

import javafx.beans.property.SimpleObjectProperty;

import java.lang.reflect.Field;

/**
 * Generic property used for notifying about changes in the value of a field.
 * @param <T> type of the field.
 */
public class ModelObjectProperty<T> extends SimpleObjectProperty<T> {
    private Field field;
    private Object object;

    /**
     * Instantiates a new ModelObjectProperty, used for notifying about a change in value of a field.
     * @param object Object that field exists in (can be a null pointer as long as refers to a valid memory location).
     * @param clazz Class type of object that the field exists in.
     * @param fieldName Name of the field to watch.
     * @throws NoSuchFieldException If/when the field does not exist.
     */
    public ModelObjectProperty(Object object, Class clazz, String fieldName) throws NoSuchFieldException {
        field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        this.object = object;
    }

    @Override
    public T get() {
        try {
            return (T)field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getBean() {
        return object;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    /**
     * Notifies listeners that this property has changed value.
     */
    public void notifyChanged() {
        invalidated();
        fireValueChangedEvent();
    }
}
