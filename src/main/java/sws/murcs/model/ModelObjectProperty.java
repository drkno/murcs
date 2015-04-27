package sws.murcs.model;

import javafx.beans.property.SimpleObjectProperty;

import java.lang.reflect.Field;

public class ModelObjectProperty<T> extends SimpleObjectProperty<T> {
    private Field field;
    private Object object;

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

    public void notifyChanged() {
        invalidated();
        fireValueChangedEvent();
    }
}
