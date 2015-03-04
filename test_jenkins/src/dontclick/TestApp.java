package dontclick;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Created by Dion on 3/5/2015.
 */
public class TestApp extends VBox {
    public TestApp(){

        DontClickMe model = new DontClickMe();
        Label label = new Label();
        label.setId("label");
        label.setText("This is not working");

        Button button = new Button("Click me");
        button.setId("button");

        button.setOnAction(e -> {
            model.click();
            label.setText(model.currentText());
        });

        setSpacing(12);
        setPadding(new Insets(12));
        getChildren().addAll(label, button);
    }
}
