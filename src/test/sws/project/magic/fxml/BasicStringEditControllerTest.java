package sws.project.magic.fxml;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class BasicStringEditControllerTest {
    private BasicStringEditController controller;

    private Text titleText;
    private Text invalidText;
    private TextField valueText;

    /**
     * Note: this method will fail if the names of the title/value texts change
     * @throws Exception
     */
    @Before
    public void setup() throws Exception{
        new JFXPanel();
        controller = new BasicStringEditController();

        titleText = new Text();
        invalidText = new Text();
        invalidText.setVisible(false);

        valueText = new TextField();

        inject("titleText", titleText);
        inject("invalidNode", invalidText);
        inject("valueText", valueText);
    }

    /**
     * Injects an object into the controller
     * @param name The name of the value to inject
     * @param value The value to inject
     * @throws Exception An exception will be thrown if the object is of the wrong type or the field doesn't exist
     */
    private void inject(String name, Object value) throws Exception{
        Field field = controller.getClass().getDeclaredField(name);
        field.setAccessible(true);

        field.set(controller, value);

        field.setAccessible(false);
    }

    @Test
    public void testSupportedTypes() throws Exception {
        Class[] returned = controller.supportedTypes();

        Assert.assertEquals("The BasicStringEditController should only have one supported type", 1,returned.length);

        boolean supported = false;
        for (Class clazz : returned)
            if (String.class.equals(clazz))
                supported = true;

        Assert.assertEquals("The BasicStringEditController should support 'Strings'", true, supported);
    }

    @Test
    public void testChangeListening() throws Exception {
        controller.initialize(null, null);

        final boolean[] changed = {false};
        controller.addChangeListener((p, o, n) -> {
            changed[0] = true;
        });

        valueText.setText("foo");
        Assert.assertTrue("The change listener should have fired when the text of 'valueText' was changed", changed[0]);
    }

    @Test
    public void testValidators() throws Exception{
        controller.initialize(null, null);

        final boolean[] changed = {false};
        controller.addChangeListener((p, o, n) -> {
            changed[0] = true;
        });

        controller.addValidator(s -> true);
        valueText.setText("foo");
        Assert.assertTrue("The change listener should have fired when the text of 'valueText' was changed", changed[0]);
        changed[0] = false;

        controller.addValidator(s -> false);

        valueText.setText("foo");
        Assert.assertFalse("The validator should have stopped the change event being fired!", changed[0]);
    }
}