package sws.project.magic.fxml;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class BasicNumberEditControllerTest {
    private BasicNumberEditController controller;

    private Text titleText;
    private Text invalidText;
    private TextField numberText;

    /**
     * Note: this method will fail if the names of the title/value texts change
     * @throws Exception
     */
    @Before
    public void setup() throws Exception{
        new JFXPanel();
        controller = new BasicNumberEditController();

        titleText = new Text();
        invalidText = new Text();
        invalidText.setVisible(false);

        numberText = new TextField();

        inject("titleText", titleText);
        inject("invalidNode", invalidText);
        inject("numberText", numberText);
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
        Assert.assertEquals("The controller should support 6 types", 6, controller.supportedTypes().length);

        Assert.assertTrue("'Integer' should be supported", containsClass(Integer.class));
        Assert.assertTrue("'int' should be supported", containsClass(int.class));

        Assert.assertTrue("'Float' should be supported", containsClass(Integer.class));
        Assert.assertTrue("'float' should be supported", containsClass(int.class));

        Assert.assertTrue("'Double' should be supported", containsClass(Integer.class));
        Assert.assertTrue("'double' should be supported", containsClass(int.class));
    }

    private boolean containsClass(Class clazz){
        for (Class clasz : controller.supportedTypes())
            if (clasz.equals(clazz))
                return true;
        return false;
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