package sws.project.magic;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 */
public class EditFormGenerator {
    public static Parent generatePane(Object from){
        VBox generated = new VBox(20);

        Class clazz = from.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields){
            if (!isEditable(field)) continue;

            //field --> getField or setField
            String getterName = "get" + (field.getName().charAt(0) + "").toUpperCase() + field.getName().substring(1);
            String setterName = "set" + (field.getName().charAt(0) + "").toUpperCase() + field.getName().substring(1);

            Editable editable = (Editable)getEditable(field);
            if (editable.value() == null) throw new UnsupportedOperationException("Can't create a new type of 'null.' Check you've assigned an EditPaneGenerator to " + field.getName());

            if (!editable.getterName().isEmpty())
                getterName = editable.getterName();
            if (!editable.setterName().isEmpty())
                setterName = editable.setterName();

            try {
                Method getter = clazz.getMethod(getterName);
                Method setter = clazz.getMethod(setterName, field.getType());

                Node child = generateFor(field, getter, setter, from, editable);
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

    private static Node generateFor(final Field field, final Method getter, final Method setter, final Object from, Editable editable){
        try {
            Constructor<?> constructor = editable.value().getConstructor();
            EditPaneGenerator generator = (EditPaneGenerator)constructor.newInstance();

            //If there was an argument set on the field, pass it on to the generator
            if (editable.argument() != null && !editable.argument().isEmpty())
                generator.setArgument(editable.argument());

            Class[] supportedClasses = generator.supportedTypes();
            boolean supported = false;

            for (Class clazz : supportedClasses)
            {
                if (field.getType().isAssignableFrom(clazz)) {
                    supported = true;
                    break;
                }
            }

            if (!supported)
                throw new UnsupportedOperationException("You've tried to generate a Form for the " + field.getName() + " property on the " + from.getClass().getName() + " object using a " + editable.value().getName() + " generator. Check and make sure that the converter is assigned and that it supports the field type.");

            return generator.generate(field, getter, setter, from);
        }catch (NoSuchMethodException e){
            throw new IllegalArgumentException("Unable to instantiate a new " + editable.value().getName() + ". You should check it has a default constructor.");
        }
        catch (Exception e){
            if (e instanceof UnsupportedOperationException)
                throw new UnsupportedOperationException(e.getMessage());
        }

        return new VBox();
    }
}
