package sws.project.magic.fxml;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class BasicBooleanEditControllerTest {
    private BasicBooleanEditController controller;

    private Text titleText;
    private RadioButton trueButton;
    private RadioButton falseButton;
    /**
     * Note: this method will fail if the names of the title/editPaneGenerator texts change
     * @throws Exception
     */
    @Before
    public void setup() throws Exception{
        new JFXPanel();
        controller = new BasicBooleanEditController();

        titleText = new Text();

        trueButton = new RadioButton();
        falseButton = new RadioButton();

        inject("titleText", titleText);
        inject("trueButton", trueButton);
        inject("falseButton", falseButton);
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

        controller.setValue(true);
        Assert.assertTrue("The true button should be selected after setting the value to true", trueButton.isSelected());
        Assert.assertFalse("The false button should not be selected after setting the value to true", falseButton.isSelected());

        /*controller.setValue(false);
        Assert.assertFalse("The true button should not be selected after setting the editPaneGenerator to false", trueButton.isSelected());
        Assert.assertTrue("The false button should be selected after setting the editPaneGenerator to false", falseButton.isSelected());*/
    }

    @Test
    public void testSupportedTypes() throws Exception {
        Assert.assertEquals("The controller should support 2 types", 2, controller.supportedTypes().length);
        Assert.assertTrue("'Boolean' should be supported", containsClass(Boolean.class));
        Assert.assertTrue("'boolean' should be supported", containsClass(boolean.class));
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

        trueButton.setSelected(true);
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
        trueButton.setSelected(true);
        Assert.assertTrue("The change listener should have fired when the text of 'valueText' was changed", changed[0]);
        changed[0] = false;

        controller.addValidator(s -> false);

        falseButton.setSelected(true);
        Assert.assertFalse("The validator should have stopped the change event being fired!", changed[0]);
    }

    @Test
    public void testRadioButtons() throws Exception{
        controller.initialize(null, null);
        controller.setValue(true);

        Assert.assertTrue("True should be set", trueButton.isSelected());
        Assert.assertFalse("False should not be set", falseButton.isSelected());

        falseButton.setSelected(true);

        Assert.assertFalse("True should not be set", trueButton.isSelected());
        Assert.assertTrue("False should be set", falseButton.isSelected());
    }
}