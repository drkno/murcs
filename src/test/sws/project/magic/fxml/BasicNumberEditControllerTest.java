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
     * Note: this method will fail if the names of the title/editPaneGenerator texts change
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
        inject("numberText", numberText);
    }

    /**
     * Injects an object into the controller
     * @param name The name of the editPaneGenerator to inject
     * @param value The editPaneGenerator to inject
     * @throws Exception An exception will be thrown if the object is of the wrong type or the field doesn't exist
     */
    private void inject(String name, Object value) throws Exception{
        Field field = controller.getClass().getDeclaredField(name);
        field.setAccessible(true);

        field.set(controller, value);

        field.setAccessible(false);
    }

    @Test
    public void testSetTitle() throws Exception {
        controller.setTitle("foo");
        Assert.assertEquals("The titleText should change when the title is set", "foo", titleText.getText());
    }

    @Test
    public void testSetValue() throws Exception {
        controller.initialize(null, null);

        controller.setValue(1);
        Assert.assertEquals("the text of 'numberText' should have changed to 1", "1", numberText.getText());
    }

    @Test
    public void testSupportedTypes() throws Exception {
        Assert.assertEquals("The controller should support 7 types", 7, controller.supportedTypes().length);

        Assert.assertTrue("'Number' should be supported", containsClass(Number.class));

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

        final boolean[] changed = {false, false, false};
        controller.addChangeListener((p, o, n) -> {
            setFalse(changed);

            if (n instanceof Integer)
                changed[0] = true;
            else if (n instanceof Float)
                changed[1] = true;
            else if (n instanceof Double)
                changed[2] = true;
        });

        numberText.setText("foo");
        Assert.assertTrue("The change listener should not have fired when the text of 'numberText' was changed to something that is not a number", allFalse(changed));

        numberText.setText("1");
        Assert.assertTrue("The change listener should have fired when the text of 'numberText' was changed and passed an integer for a whole number (e.g. no decimal places)", changed[0]);

        numberText.setText("1.0");
        Assert.assertTrue("The change listener should have fired and been passed a float", changed[1]);
    }

    private void setFalse(boolean[] changes){
        for (int i = 0; i < changes.length; ++i)
            changes[i] = false;
    }

    private boolean allFalse(boolean[] changes){
        for (boolean b : changes)
            if (b) return false;
        return true;
    }

    @Test
    public void testValidators() throws Exception{
        controller.initialize(null, null);

        final boolean[] changed = {false};
        controller.addChangeListener((p, o, n) -> {
            changed[0] = true;
        });

        controller.addValidator(s -> true);
        numberText.setText("1");
        Assert.assertTrue("The change listener should have fired when the text of 'numberText' was changed", changed[0]);
        changed[0] = false;

        controller.addValidator(s -> false);

        numberText.setText("1");
        Assert.assertFalse("The validator should have stopped the change event being fired!", changed[0]);
    }
}