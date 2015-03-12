package sws.project.magic;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;

/**
 *
 */
public class SimpleNumberPaneGenerator implements EditPaneGenerator {
    @Override
    public Class[] supportedTypes() {
        return new Class[] {int.class, Integer.class, float.class, Float.class, double.class, Double.class};
    }

    @Override
    public Node generate(Field field, Method getter, Method setter, Object from) {
        VBox container = new VBox();

        ArrayList<Node> children = new ArrayList<>();
        Text title = new Text(field.getName());
        children.add(title);

        try {
            final TextField textBox = new TextField();
            String currentText =  getter.invoke(from).toString();
            textBox.setText(currentText);

            textBox.textProperty().addListener(a -> {
                String newText = textBox.getText();
                Object number = convertToNumber(field, newText);
                try {
                    if (number != null)
                        setter.invoke(from, number);
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

    @Override
    public void setArgument(String argument) {
        //Don't do anything, there aren't any arguments we need
    }

    private Object convertToNumber(Field field, String text){
        if (field.getType().isAssignableFrom(int.class) || field.getType().isAssignableFrom(Integer.class)){
            try {
                return Integer.parseInt(text);
            }catch (Exception e){
                return null;
            }
        }
        else{
            try {
                double value = Double.parseDouble(text);
                if (field.getType().isAssignableFrom(Float.class) || field.getType().isAssignableFrom(float.class)){
                    return (float)value;
                }
                return value;
            }catch (Exception e){
                return null;
            }
        }
    }
}
