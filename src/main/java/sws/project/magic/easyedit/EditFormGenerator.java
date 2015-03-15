package sws.project.magic.easyedit;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Uses automagic (and reflection) to generate a form
 * for editing an object. Don't question the magic.
 */
public class EditFormGenerator {
    /**
     * Generates a pane for editing an object
     * @param from The object to generate a pane for
     * @return The edit pane
     */
    public static Parent generatePane(Object from) throws Exception{
        VBox generated = new VBox(20);

        Class clazz = from.getClass();
        Collection<Field> fields = getFieldsRecursive(from.getClass());

        ArrayList<Object[]> nodes = new ArrayList<>();

        for (Field field : fields) {
            if (!isEditable(field)) continue;

            //field --> getField or setField
            String getterName = "get" + capitalizeFieldName(field);
            String setterName = "set" + capitalizeFieldName(field);

            Editable editable = getEditable(field);

            if (!editable.getterName().isEmpty())
                getterName = editable.getterName();
            if (!editable.setterName().isEmpty())
                setterName = editable.setterName();

            Method getter = findMethodRecursive(clazz, getterName);
            getter.setAccessible(true);

            Method setter = null;
            try {
                setter = findMethodRecursive(clazz, setterName, field.getType());
                setter.setAccessible(true);
            }catch (NoSuchMethodException e){
                if (!editable.setterName().isEmpty())
                    throw e;
            }

            Method validator = null;

            if (!editable.validatorName().isEmpty()) {
                validator = findMethodRecursive(clazz, editable.validatorName(), field.getType());
                validator.setAccessible(true);
            }

            Node child = generateFor(field, getter, setter, validator, from, editable);
            int depth = editable.sort();

            insertInto(nodes, new Object[]{depth, child});
            //generated.getChildren().add(child);
        }

        for (Object[] nodePair : nodes){
            generated.getChildren().add((Node)nodePair[1]);
        }

        ScrollPane scroller = new ScrollPane(generated);
        scroller.setFitToWidth(true);
        return scroller;
    }

    private static void insertInto(ArrayList<Object[]> into, Object[] insert){
        int index = 0;
        while (index < into.size() && (Integer)into.get(index)[0] <= (Integer)insert[0]){
            index++;
        }
        if (index == into.size())
            into.add(insert);
        else into.add(index, insert);
    }

    /**
     * Gets all the fields on a class and its ancestors
     * @param clazz The type to search
     * @return
     */
    private static Collection<Field> getFieldsRecursive(Class clazz){
        Collection<Field> fields = new ArrayList<>();

        Collections.addAll(fields, clazz.getDeclaredFields());
        Collections.addAll(fields, clazz.getFields());

        if (clazz.getSuperclass() != null)
            fields.addAll(getFieldsRecursive(clazz.getSuperclass()));
        return fields;
    }

    /**
     * Finds a method on a class
     * @param clazz The class to find the method on
     * @param methodName The name of the method
     * @param parameters The types of parameters the class takes
     * @return
     */
    private static Method findMethodRecursive(Class clazz, String methodName, Class<?>... parameters) throws NoSuchMethodException{
        try{
            return clazz.getMethod(methodName, parameters);
        }catch (Exception e){

        }

        try{
            return clazz.getDeclaredMethod(methodName, parameters);
        }catch (Exception e){

        }

        if (clazz.getSuperclass() != null)
            return findMethodRecursive(clazz.getSuperclass(), methodName, parameters);

        throw new NoSuchMethodException("There is no " + methodName + " method on the " + clazz.getName() + " object taking " + parameters);
    }

    /**
     * Capitalizes the name of field fooBar goes to FooBar
     * @param field The field
     * @return The capitalized name of the field
     */
    private static String capitalizeFieldName(Field field){
        String text = field.getName();
        text = Character.toUpperCase(text.charAt(0)) + text.substring(1, text.length());
        return text;
    }

    /**
     * Indicates if a specified field is editable.
     * @param field the field to check for editableness.
     * @return Returns whether the field is editable
     */
    public static boolean isEditable(Field field){
        return getEditable(field) != null;
    }

    /**
     * Gets the friendly name of a field. For example, passing in a field named 'fooBar'
     * would result in 'Foo Bar'
     * @param field The field to get the friendly name of
     * @return The friendly name of the field
     */
    public static String getFriendlyName(Field field) {
        String raw = null;
        if (isEditable(field)){
            raw = getEditable(field).friendlyName();
        }

        if (raw == null || raw.isEmpty())
            raw = field.getName();

        String result = "";
        for (int i = 0; i < raw.length(); ++i){
            if (i == 0) {
                result += Character.toUpperCase(raw.charAt(i));
                continue;
            }

            if (Character.isUpperCase(raw.charAt(i)) && !Character.isUpperCase(raw.charAt(i - 1))) result += " ";

            result += raw.charAt(i);
        }

        return result;
    }

    /**
     * Gets the editable annotation for a specified field
     *
     * @param field The field to get the editable annotation for
     * @return The editable annotation. Null if there is not one
     */
    public static Editable getEditable(Field field){
        Annotation[] annotations = field.getDeclaredAnnotations();

        for (Annotation annotation : annotations){
            if (annotation instanceof Editable)
                return (Editable)annotation;
        }
        return null;
    }

    /**
     * Generates a node for a specific field automagically
     * @param field The field to generate an edit node for
     * @param getter The getter for the field
     * @param setter The setter for the field
     * @param from The object the field is from
     * @param editable The editable annotation of the
     * @return The node of the edit form
     */
    private static Node generateFor(final Field field, final Method getter, final Method setter, final Method validator, final Object from, Editable editable) throws Exception {
        Constructor<?> constructor;
        EditPaneGenerator generator;
        try {
            constructor = editable.editPaneGenerator().getConstructor();
            generator = (EditPaneGenerator) constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new Exception("\"Unable to instantiate a new \"" + editable.editPaneGenerator().getName() + "\". You should check it has a default constructor.\"");
        }

        //If there was an argument set on the field, pass it on to the generator
        if (editable.argument() != null && !editable.argument().isEmpty())
            generator.setArgument(editable.argument());

        Class[] supportedClasses = generator.supportedTypes();
        boolean supported = false;

        for (Class clazz : supportedClasses) {
            if (field.getType().isAssignableFrom(clazz)) {
                supported = true;
                break;
            }
        }

        if (!supported)
            throw new Exception("You've tried to generate a Form for the " + field.getName() + " property on the " + from.getClass().getName() + " object using a " + editable.editPaneGenerator().getName() + " generator. Check and make sure that the converter is assigned and that it supports the field type.");

        return generator.generate(field, getter, setter, validator, from);
    }
}
