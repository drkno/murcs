package sws.project.unit.magic.easyedit.fxml;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.easyedit.fxml.BasicNumberEditController;

import java.lang.reflect.Field;

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
}