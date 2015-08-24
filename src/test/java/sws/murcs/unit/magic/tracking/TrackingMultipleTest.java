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

public class TrackingMultipleTest {
    public class TestInteger extends TrackableObject {
        public TestInteger() throws Exception {
            UndoRedoManager.add(this);
            commit("initial state");
        }

        @TrackableValue
        private int testInteger = 0;

        public int getTestInteger() {
            return testInteger;
        }

        public void setTestInteger(int testInteger) throws Exception {
            this.testInteger = testInteger;
            commit("test desc.");
        }
    }

    public class TestString extends TrackableObject {
        public TestString() throws Exception {
            UndoRedoManager.add(this);
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
        UndoRedoManager.setDisabled(false);
    }

    @Before
    public void setup() throws IllegalAccessException {
        UndoRedoManager.forget(true);
        listenersField.set(null, new ArrayList<ChangeListenerHandler>());
        UndoRedoManager.setMaximumCommits(-1);
    }

    @After
    public void tearDown() throws Exception {
        UndoRedoManager.forget(true);
    }

    @Test
    public void undoTest() throws Exception {
        TestInteger a = new TestInteger();
        TestString b = new TestString();
        a.setTestInteger(1);
        b.setTestString("1");
        a.setTestInteger(2);
        b.setTestString("2");
        a.setTestInteger(3);
        b.setTestString("3");
        UndoRedoManager.revert();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(3, a.getTestInteger());
        UndoRedoManager.revert();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.revert();
        Assert.assertEquals("1", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
    }

    @Test
    public void redoTest() throws Exception {
        TestInteger a = new TestInteger();
        TestString b = new TestString();
        a.setTestInteger(1);
        b.setTestString("1");
        a.setTestInteger(2);
        b.setTestString("2");
        a.setTestInteger(3);
        b.setTestString("3");
        UndoRedoManager.revert();
        UndoRedoManager.revert();
        UndoRedoManager.revert();
        UndoRedoManager.revert();

        Assert.assertEquals("1", b.getTestString());
        Assert.assertEquals(1, a.getTestInteger());
        UndoRedoManager.remake();
        Assert.assertEquals("1", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.remake();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.remake();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(3, a.getTestInteger());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestInteger a = new TestInteger();
        TestString b = new TestString();
        a.setTestInteger(1);
        b.setTestString("1");
        a.setTestInteger(2);
        b.setTestString("2");
        Assert.assertEquals("test desc.", UndoRedoManager.getRevertMessage());
        UndoRedoManager.revert();
        Assert.assertEquals("test desc.", UndoRedoManager.getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.getRemakeMessage());
        UndoRedoManager.revert();
        Assert.assertEquals("test desc.", UndoRedoManager.getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.getRemakeMessage());
        UndoRedoManager.revert();
        Assert.assertEquals("test desc.", UndoRedoManager.getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.getRemakeMessage());
        UndoRedoManager.revert();
        Assert.assertEquals("initial state", UndoRedoManager.getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.getRemakeMessage());
        UndoRedoManager.revert();
        Assert.assertEquals(null, UndoRedoManager.getRevertMessage());
        Assert.assertEquals("initial state", UndoRedoManager.getRemakeMessage());
    }
}
