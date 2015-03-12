package sws.project.magic;

import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 */
public class SimpleBooleanPaneGenerator implements EditPaneGenerator {
    @Override
    public Class[] supportedTypes() {
        return new Class[] {Boolean.class, boolean.class};
    }

    @Override
    public Node generate(Field field, Method getter, Method setter, Object from) {
        VBox container = new VBox();

        ArrayList<Node> children = new ArrayList<>();
        Text title = new Text(field.getName());
        children.add(title);

        try {
            final HBox wrapper = new HBox(10);

            final RadioButton trueButton = new RadioButton("True");
            final RadioButton falseButton = new RadioButton("False");

            ToggleGroup group = new ToggleGroup();
            trueButton.setToggleGroup(group);
            falseButton.setToggleGroup(group);

            wrapper.getChildren().add(trueButton);
            wrapper.getChildren().add(falseButton);

            boolean currentValue = (boolean) getter.invoke(from);
            trueButton.setSelected(currentValue);
            falseButton.setSelected(!currentValue);

            group.selectedToggleProperty().addListener(a -> {
                boolean value = trueButton.isSelected();
                try {
                    setter.invoke(from, value);
                } catch (Exception e) {
                    return;
                }
            });

            children.add(wrapper);
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
}
