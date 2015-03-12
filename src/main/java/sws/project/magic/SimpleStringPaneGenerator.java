package sws.project.magic;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *
 */
public class SimpleStringPaneGenerator implements EditPaneGenerator {
    @Override
    public Class[] supportedTypes() {
        return new Class[] {String.class};
    }

    @Override
    public Node generate(Field field, Method getter, Method setter, Object from) {
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
