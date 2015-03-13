package sws.project.magic.easyedit.fxml;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.project.magic.easyedit.Editable;
import sws.project.magic.easyedit.fxml.FxmlPaneGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class FxmlPaneGeneratorTest {
    private FxmlPaneGenerator generator;

    //A string we test the changes for
    @Editable(editPaneGenerator = FxmlPaneGenerator.class)
    private String testString;

    @Editable(editPaneGenerator = FxmlPaneGenerator.class, friendlyName = "A test string")
    private String testString2;

    @Before
    public void setup(){
        new JFXPanel();
        generator = new FxmlPaneGenerator();
    }

    @Test
    public void testSupportedTypes() throws Exception {
        Assert.assertEquals("There should be 9 supported types!", 9, generator.supportedTypes().length);
    }

    @Test
    public void testGenerate() throws Exception {
        generator.setArgument("/sws/project/String.fxml");

        Field field = this.getClass().getDeclaredField("testString");
        Method getter = this.getClass().getMethod("getter");
        Method setter = this.getClass().getMethod("setter", String.class);

        Node result = generator.generate(field, getter, setter, this);
        Assert.assertNotNull("Result should not be null having been loaded! Note: This could fail if you don't point it at a valid FXML file", result);

        field = this.getClass().getDeclaredField("testString2");
        result = generator.generate(field, getter, setter, this);

        Assert.assertNotNull("Result should not be null for field2 either", result);
    }

    public String getter(){
        return testString;
    }

    public void setter(String newValue){
        this.testString = newValue;
    }
}