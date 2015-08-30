package sws.murcs.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeListenerHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TrackingStringTest {
    public class TestString extends TrackableObject {
        public TestString() throws Exception {
            UndoRedoManager.get().add(this);
            commit("initial state");
        }

        @TrackableValue
        private String testString;

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) throws Exception {
            this.testString = testString;
            commit("test desc.");
        }
    }

    private static Field listenersField;

    @BeforeClass
    public static void setupClass() throws Exception {
        listenersField = UndoRedoManager.class.getDeclaredField("changeListeners");
        listenersField.setAccessible(true);
        UndoRedoManager.get().setDisabled(false);
    }

    @Before
    public void setup() throws IllegalAccessException {
        UndoRedoManager.get().forget(true);
        listenersField.set(UndoRedoManager.get(), new ArrayList<ChangeListenerHandler>());
        UndoRedoManager.get().setMaximumCommits(-1);
    }

    @After
    public void tearDown() throws Exception {
        UndoRedoManager.get().forget(true);
    }

    @Test
    public void undoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        a.setTestString("string3");
        UndoRedoManager.get().revert();
        Assert.assertEquals("string2", a.getTestString());
        UndoRedoManager.get().revert();
        Assert.assertEquals("string1", a.getTestString());
    }

    @Test
    public void redoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        a.setTestString("string3");
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        Assert.assertEquals("string1", a.getTestString());
        UndoRedoManager.get().remake();
        Assert.assertEquals("string2", a.getTestString());
        UndoRedoManager.get().remake();
        Assert.assertEquals("string3", a.getTestString());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRevertMessage());
        UndoRedoManager.get().revert();
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRemakeMessage());
        UndoRedoManager.get().revert();
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRemakeMessage());
        Assert.assertEquals(null, UndoRedoManager.get().getRevertMessage());
    }

    @Test(expected = Exception.class)
    public void cannotUndoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        UndoRedoManager.get().revert();
        Assert.assertFalse(UndoRedoManager.get().canRevert());

        UndoRedoManager.get().revert();
    }

    @Test(expected = Exception.class)
    public void cannotRedoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        Assert.assertFalse(UndoRedoManager.get().canRemake());

        UndoRedoManager.get().remake();
    }

    @Test
    public void repetitiveRevertRemakeTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("1");
        a.setTestString("2");
        a.setTestString("3");
        a.setTestString("4");
        a.setTestString("5");
        a.setTestString("6");
        a.setTestString("7");
        a.setTestString("8");
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().remake();
        UndoRedoManager.get().remake();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().remake();
        UndoRedoManager.get().remake();
        UndoRedoManager.get().remake();
        UndoRedoManager.get().remake();
        UndoRedoManager.get().remake();
        Assert.assertEquals("7", a.getTestString());
    }

    @Test
    public void repetitiveRevertRemakeAddTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("1");
        a.setTestString("2");
        a.setTestString("3");
        a.setTestString("4");
        a.setTestString("5");
        a.setTestString("6");
        a.setTestString("7");
        a.setTestString("8");
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().remake();
        UndoRedoManager.get().remake();
        a.setTestString("9");
        UndoRedoManager.get().revert();
        Assert.assertEquals("6", a.getTestString());
        UndoRedoManager.get().remake();
        Assert.assertEquals("9", a.getTestString());
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        Assert.assertFalse(UndoRedoManager.get().canRevert());
        Assert.assertEquals(null, a.getTestString());
    }
}
