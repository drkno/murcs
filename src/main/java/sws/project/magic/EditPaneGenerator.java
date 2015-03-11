package sws.project.magic;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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

    private static Node generateFor(final Field field, final Method getter, final Method setter, final Object from){
        if (field.getType().isAssignableFrom(String.class))
            return generateForString(field, getter,setter,from);
        if (field.getType().isAssignableFrom(Boolean.class))
            generateForBoolean(field, getter, setter, from);

        return new VBox(20);
    }

    private static Node generateForBoolean(Field field, Method getter, Method setter, Object from) {
        return new VBox(20);
    }

    private static Node generateForString(Field field, Method getter, Method setter, Object from) {
        VBox container = new VBox();

        ArrayList<Node> children = new ArrayList<>();
        Text title = new Text(field.getName());
        children.add(title);

        try {
            final TextField textBox = new TextField();
            String currentText = (String) getter.invoke(from);
            textBox.setText(currentText);

            textBox.textProperty().addListener(a -> {
                String newText = textBox.getText();
                try {
                    setter.invoke(from, newText);
                } catch (Exception e) {
                    return;
                }
            });

            children.add(textBox);
        } catch (Exception e) {
            return container;
        }

        container.getChildren().addAll(children);
        return container;
    }
}
