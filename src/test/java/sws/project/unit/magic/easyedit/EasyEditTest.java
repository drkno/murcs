package sws.project.unit.magic.easyedit;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sws.project.magic.easyedit.EditFormGenerator;
import sws.project.magic.easyedit.Editable;
import sws.project.magic.easyedit.fxml.FxmlPaneGenerator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Tests the functionallity of Easy Edit
 */
public class EasyEditTest extends Application {
    //@Editable(value = FxmlPaneGenerator.class, argument = "/sws/project/String.fxml")
    private String name;

    @Editable(value = FxmlPaneGenerator.class, argument = "/sws/project/Collection.fxml")
    private Collection<String> testStrings = new ArrayList<>();

    public Collection<String> getTestStrings() {
        return testStrings;
    }

    public void setTestStrings(Collection<String> testStrings) {
        this.testStrings = testStrings;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        testStrings.add("Item1");
        testStrings.add("Item2");
        testStrings.add("Item3");

        Parent root = EditFormGenerator.generatePane(this);
        Scene scene = new Scene(root, 800, 480);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
