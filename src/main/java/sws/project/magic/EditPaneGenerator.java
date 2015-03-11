package sws.project.magic;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 */
public class EditPaneGenerator {
    public static Parent generatePane(Object from){
        VBox generated = new VBox(20);

        Class clazz = from.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields){
            if (!isEditable(field)) continue;

            //field --> getField or setField
            String getterName = "get" + (field.getName().charAt(0) + "").toUpperCase() + field.getName().substring(1);
            String setterName = "set" + (field.getName().charAt(0) + "").toUpperCase() + field.getName().substring(1);

            try {
                Method getter = clazz.getMethod(getterName);
                Method setter = clazz.getMethod(setterName, field.getType());

                Node child = generateFor(field, getter, setter, from);
                generated.getChildren().add(child);

            }catch (NoSuchMethodException e){
                continue;
            }
        }

        return generated;
    }

    private static boolean isEditable(Field field){
        return getEditable(field) != null;
    }

    private static Annotation getEditable(Field field){
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations){
            if (annotation instanceof Editable)
                return annotation;
        }
        return null;
    }

    private static Node generateFor(final Field field, final Method getter, final Method setter, final Object from){
        Editable editable = (Editable)getEditable(field);

        Class<?> supported = editable.value();
        if (!field.getType().isAssignableFrom(supported))
            throw new UnsupportedOperationException("You've tried to generate a Form for the " + field.getName() + " property on the " + from.getClass().getName() + " object using a " + (supported == null ? "null" : supported.getName()) + " generator. Check and make sure that the converter is assigned and that it supports the field type.");


    }
}
