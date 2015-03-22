package sws.murcs.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;

public class TrackingMultipleTest {
    public class TestInteger extends TrackableObject {
        public TestInteger() {
            commit("initial state");
        }

        @TrackableValue
        private int testInteger = 0;

        public int getTestInteger() {
            return testInteger;
        }

        public void setTestInteger(int testInteger) {
            this.testInteger = testInteger;
            commit("test desc.");
        }
    }

    public class TestString extends TrackableObject {
        public TestString() {
            commit("initial state");
        }

        @TrackableValue
        private String testString;

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
            commit("test desc.");
        }
    }

    @Before
    public void setup() {
        UndoRedoManager.setMergeWaitTime(0);
        UndoRedoManager.setMaximumTrackingSize(-1);
    }

    @After
    public void tearDown() throws Exception {
        UndoRedoManager.reset();
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
        UndoRedoManager.undo();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(3, a.getTestInteger());
        UndoRedoManager.undo();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.undo();
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
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        UndoRedoManager.undo();

        Assert.assertEquals("1", b.getTestString());
        Assert.assertEquals(1, a.getTestInteger());
        UndoRedoManager.redo();
        Assert.assertEquals("1", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.redo();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());;
        UndoRedoManager.redo();
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
        Assert.assertEquals("test desc.", UndoRedoManager.getUndoDescription());
        UndoRedoManager.undo();
        Assert.assertEquals("test desc.", UndoRedoManager.getUndoDescription());
        Assert.assertEquals("test desc.", UndoRedoManager.getRedoDescription());
        UndoRedoManager.undo();
        Assert.assertEquals("initial state", UndoRedoManager.getUndoDescription());
        Assert.assertEquals("test desc.", UndoRedoManager.getRedoDescription());
        UndoRedoManager.undo();
        Assert.assertEquals("initial state", UndoRedoManager.getRedoDescription());
    }
}
