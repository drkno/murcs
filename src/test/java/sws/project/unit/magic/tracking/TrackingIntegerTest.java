package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import sws.project.magic.tracking.TrackValue;
import sws.project.magic.tracking.ValueTracker;

public class TrackingIntegerTest {
    public class TestInteger extends ValueTracker {
        public TestInteger() {
            saveCurrentState("initial state", true);
        }

        @TrackValue
        private int testInteger = 0;

        public int getTestInteger() {
            return testInteger;
        }

        public void setTestInteger(int testInteger) {
            this.testInteger = testInteger;
            saveCurrentState("test desc.");
        }
    }

    @After
    public void tearDown() throws Exception {
        ValueTracker.reset();
    }

    @Test
    public void undoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        ValueTracker.undo();
        Assert.assertEquals(2, a.getTestInteger());
        ValueTracker.undo();
        Assert.assertEquals(1, a.getTestInteger());
        ValueTracker.undo();
        Assert.assertEquals(0, a.getTestInteger());
    }

    @Test
    public void redoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        ValueTracker.undo();
        ValueTracker.undo();
        ValueTracker.undo();
        Assert.assertEquals(0, a.getTestInteger());
        ValueTracker.redo();
        Assert.assertEquals(1, a.getTestInteger());
        ValueTracker.redo();
        Assert.assertEquals(2, a.getTestInteger());
        ValueTracker.redo();
        Assert.assertEquals(3, a.getTestInteger());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        Assert.assertEquals("test desc.", ValueTracker.getUndoDescription());
        ValueTracker.undo();
        Assert.assertEquals("initial state", ValueTracker.getUndoDescription());
        Assert.assertEquals("test desc.", ValueTracker.getRedoDescription());
        ValueTracker.undo();
        Assert.assertEquals("initial state", ValueTracker.getRedoDescription());
    }

    @Test
    public void cannotUndoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        ValueTracker.undo();
        Assert.assertFalse(ValueTracker.canUndo());
        try {
            ValueTracker.undo();
            Assert.fail();
        }
        catch (Exception e) {
            Assert.assertEquals("Undo is not possible as there are no saved undo states.", e.getMessage());
        }
    }

    @Test
    public void cannotRedoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        Assert.assertFalse(ValueTracker.canRedo());
        try {
            ValueTracker.redo();
            Assert.fail();
        }
        catch (Exception e) {
            Assert.assertEquals("Redo is not possible as there are no saved redo states.", e.getMessage());
        }
    }

    @Test
    public void maximumUndoRedoStackSizeTest() throws Exception {
        ValueTracker.setMaximumTrackingSize(3);
        Assert.assertEquals(3, ValueTracker.getMaximumTrackingSize());
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        a.setTestInteger(4);
        a.setTestInteger(5);
        ValueTracker.undo();
        ValueTracker.undo();
        ValueTracker.undo();
        Assert.assertFalse(ValueTracker.canUndo());
        Assert.assertEquals(2, a.getTestInteger());
    }
}
