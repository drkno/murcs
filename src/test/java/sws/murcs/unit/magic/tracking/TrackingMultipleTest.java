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
            UndoRedoManager.get().add(this);
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
        TestInteger a = new TestInteger();
        TestString b = new TestString();
        a.setTestInteger(1);
        b.setTestString("1");
        a.setTestInteger(2);
        b.setTestString("2");
        a.setTestInteger(3);
        b.setTestString("3");
        UndoRedoManager.get().revert();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(3, a.getTestInteger());
        UndoRedoManager.get().revert();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.get().revert();
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
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();

        Assert.assertEquals("1", b.getTestString());
        Assert.assertEquals(1, a.getTestInteger());
        UndoRedoManager.get().remake();
        Assert.assertEquals("1", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.get().remake();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.get().remake();
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
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRevertMessage());
        UndoRedoManager.get().revert();
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRemakeMessage());
        UndoRedoManager.get().revert();
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRemakeMessage());
        UndoRedoManager.get().revert();
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRemakeMessage());
        UndoRedoManager.get().revert();
        Assert.assertEquals("initial state", UndoRedoManager.get().getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRemakeMessage());
        UndoRedoManager.get().revert();
        Assert.assertEquals(null, UndoRedoManager.get().getRevertMessage());
        Assert.assertEquals("initial state", UndoRedoManager.get().getRemakeMessage());
    }

    @Test
    public void repetitiveRevertRemakeTest() throws Exception {
        TestInteger a = new TestInteger();
        TestString b = new TestString();
        a.setTestInteger(1);
        b.setTestString("1");
        a.setTestInteger(2);
        b.setTestString("2");
        a.setTestInteger(3);
        b.setTestString("3");
        a.setTestInteger(4);
        b.setTestString("4");
        a.setTestInteger(5);
        b.setTestString("5");
        a.setTestInteger(6);
        b.setTestString("6");
        a.setTestInteger(7);
        b.setTestString("7");
        a.setTestInteger(8);
        b.setTestString("8");

        UndoRedoManager.get().revert(); // "7"
        UndoRedoManager.get().revert(); //  7
        UndoRedoManager.get().revert(); // "6"
        UndoRedoManager.get().revert(); //  6
        UndoRedoManager.get().revert(); // "5"
        UndoRedoManager.get().revert(); //  5
        UndoRedoManager.get().revert(); // "4"
        UndoRedoManager.get().revert(); //  4
        UndoRedoManager.get().revert(); // "3"

        Assert.assertEquals(4, a.getTestInteger());
        Assert.assertEquals("3", b.getTestString());

        UndoRedoManager.get().remake(); // "4"
        UndoRedoManager.get().remake(); //  5
        UndoRedoManager.get().remake(); // "5"
        UndoRedoManager.get().revert(); // "4"
        UndoRedoManager.get().revert(); //  4
        UndoRedoManager.get().revert(); // "3"
        UndoRedoManager.get().revert(); //  3
        UndoRedoManager.get().remake(); //  4
        UndoRedoManager.get().remake(); // "4"
        UndoRedoManager.get().remake(); //  5
        UndoRedoManager.get().remake(); // "5"
        UndoRedoManager.get().revert(); // "4"
        UndoRedoManager.get().remake(); // "5"

        Assert.assertEquals(5, a.getTestInteger());
        Assert.assertEquals("5", b.getTestString());

        UndoRedoManager.get().remake(); //  6
        UndoRedoManager.get().remake(); // "6"
        UndoRedoManager.get().remake(); //  7
        UndoRedoManager.get().remake(); // "7"
        UndoRedoManager.get().remake(); //  8
        UndoRedoManager.get().remake(); // "8"

        Assert.assertEquals(8, a.getTestInteger());
        Assert.assertEquals("8", b.getTestString());
        Assert.assertFalse(UndoRedoManager.get().canRemake());
    }

    @Test
    public void assimilateTest() throws Exception {
        TestInteger a = new TestInteger();
        TestString b = new TestString();
        a.setTestInteger(1);
        b.setTestString("1");
        a.setTestInteger(2);
        b.setTestString("2");
        long commitNumber = UndoRedoManager.get().getHead().getCommitNumber();
        a.setTestInteger(3);
        b.setTestString("3");
        a.setTestInteger(4);
        b.setTestString("4");
        UndoRedoManager.get().assimilate(commitNumber);
        Assert.assertEquals("Value lost after assimilate", 4, a.getTestInteger());
        Assert.assertEquals("Value lost after assimilate", "4", b.getTestString());
        UndoRedoManager.get().revert();
        Assert.assertEquals("Value lost after assimilate", 2, a.getTestInteger());
        Assert.assertEquals("Value lost after assimilate", "2", b.getTestString());
        UndoRedoManager.get().remake();
        Assert.assertEquals("Value lost after assimilate", 4, a.getTestInteger());
        Assert.assertEquals("Value lost after assimilate", "4", b.getTestString());
    }

    @Test
    public void assimilateRevertUnevenTest() throws Exception {
        TestInteger a = new TestInteger();
        TestString b = new TestString();
        a.setTestInteger(0);
        b.setTestString("0");
        a.setTestInteger(1);
        a.setTestInteger(2);
        long commitNumber = UndoRedoManager.get().getHead().getCommitNumber();
        a.setTestInteger(3);
        a.setTestInteger(4);
        a.setTestInteger(5);
        b.setTestString("1");
        UndoRedoManager.get().assimilate(commitNumber);
        Assert.assertEquals("Incorrect value for test.", 5, a.getTestInteger());
        Assert.assertEquals("Incorrect value for test.", "1", b.getTestString());
        UndoRedoManager.get().revert();
        Assert.assertEquals("Assimilate failed", "0", b.getTestString());
        Assert.assertEquals("Assimilate failed", 2, a.getTestInteger());
    }
}
