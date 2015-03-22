package sws.murcs.unit.magic.easyedit;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.easyedit.EditFormGenerator;
import sws.murcs.magic.easyedit.Editable;

import java.lang.reflect.Field;

public class EditFormGeneratorTest {
    @Editable()
    private String editableString;

    @Editable(friendlyName = "A friendly name", getterName = "getEditableString", setterName = "setEditableString")
    private String editableWithName;

    private String notEditable;

    private Field editableWithNameField;
    private Field editableStringField;
    private Field notEditableField;

    public String getEditableString(){
        return editableString;
    }
    public void setEditableString(String value){
        editableString = value;
    }

    @Before
    public void setup() throws Exception{
        new JFXPanel();

        editableWithNameField = getClass().getDeclaredField("editableWithName");
        editableStringField = getClass().getDeclaredField("editableString");
        notEditableField = getClass().getDeclaredField("notEditable");
    }

    @Test
    public void testGeneratePane() throws Exception {
        ScrollPane root = (ScrollPane)EditFormGenerator.generatePane(this);
        Assert.assertNotNull("root should not be null");

        Assert.assertEquals("Root should have two children, one for each editable field on this class", 2, ((Parent)root.getContent()).getChildrenUnmodifiable().size());
    }

    @Test
    public void testIsEditable() throws Exception {
        Assert.assertTrue("Fields marked with the editableString flag should be editableString", EditFormGenerator.isEditable(editableStringField));
        Assert.assertFalse("Fields not marked with the editableString annotation should not be editableString", EditFormGenerator.isEditable(notEditableField));
    }

    @Test
    public void testGetFriendlyName() throws Exception {
        Assert.assertEquals("Default names should be cleaned up", "Editable String", EditFormGenerator.getFriendlyName(editableStringField));
        Assert.assertEquals("Fields given friendly names should use them", "A friendly name", EditFormGenerator.getFriendlyName(editableWithNameField));
    }

    @Test
    public void testGetEditable() throws Exception {
        Editable editable = EditFormGenerator.getEditable(editableStringField);
        Assert.assertNotNull("getEditable should get the editable on fields that have it", editable);

        editable = EditFormGenerator.getEditable(editableWithNameField);
        Assert.assertNotNull("getEditable should get the editable on fields that have it",editable);
        Assert.assertEquals("The returned editable should have the correct properties", "A friendly name", editable.friendlyName());
    }
}