package sws.project.magic.easyedit;

import javafx.scene.Parent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.project.magic.easyedit.fxml.FxmlPaneGenerator;

import java.lang.reflect.Field;

public class EditFormGeneratorTest {
    @Editable(editPaneGenerator = FxmlPaneGenerator.class, argument = "sws/project/String.fxml")
    private String editableString;

    @Editable(editPaneGenerator = FxmlPaneGenerator.class, argument = "sws/project/String.fxml", friendlyName = "A friendly name", getterName = "getEditableString", setterName = "setEditableString")
    private String editableWithName;

    private String notEditable;

    public String getEditableString(){
        return editableString;
    }

    public void setEditableString(String value){
        editableString = value;
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGeneratePane() throws Exception {
        Parent root = EditFormGenerator.generatePane(this);
        Assert.assertNotNull("root should not be null");

        Assert.assertEquals("Root should have two children, one for each editable field on this class", 2, root.getChildrenUnmodifiable().size());
    }

    @Test
    public void testIsEditable() throws Exception {
        Field editableField = getClass().getDeclaredField("editableString");
        Field nonEditableField = getClass().getDeclaredField("notEditable");

        Assert.assertTrue("Fields marked with the editableString flag should be editableString", EditFormGenerator.isEditable(editableField));
        Assert.assertFalse("Fields not marked with the editableString annotation should not be editableString", EditFormGenerator.isEditable(nonEditableField));
    }

    @Test
    public void testGetFriendlyName() throws Exception {
        Field editableField = getClass().getDeclaredField("editableString");
        Field friendlyNamedField = getClass().getDeclaredField("editableWithName");

        Assert.assertEquals("Default names should be cleaned up", "Editable String", EditFormGenerator.getFriendlyName(editableField));
        Assert.assertEquals("Fields given friendly names should use them", "A friendly name", EditFormGenerator.getFriendlyName(friendlyNamedField));
    }

    @Test
    public void testGetEditable() throws Exception {
        Field editableField = getClass().getDeclaredField("editableString");
        Field friendlyNamedField = getClass().getDeclaredField("editableWithName");

        Editable editable = EditFormGenerator.getEditable(editableField);
        Assert.assertNotNull("getEditable should get the editable on fields that have it", editable);

        editable = EditFormGenerator.getEditable(friendlyNamedField);
        Assert.assertNotNull("getEditable should get the editable on fields that have it",editable);
        Assert.assertEquals("The returned editable should have the correct properties", "A friendly name", editable.friendlyName());
    }
}