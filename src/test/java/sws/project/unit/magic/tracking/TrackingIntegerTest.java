package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import sws.project.magic.tracking.TrackableValue;
import sws.project.magic.tracking.TrackableObject;

public class TrackingIntegerTest {
    public class TestInteger extends TrackableObject {
        public TestInteger() {
            saveCurrentState("initial state", true);
        }

        @TrackableValue
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
        TrackableObject.reset();
    }

    @Test
    public void undoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        TrackableObject.undo();
        Assert.assertEquals(2, a.getTestInteger());
        TrackableObject.undo();
        Assert.assertEquals(1, a.getTestInteger());
        TrackableObject.undo();
        Assert.assertEquals(0, a.getTestInteger());
    }

    @Test
    public void redoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        TrackableObject.undo();
        TrackableObject.undo();
        TrackableObject.undo();
        Assert.assertEquals(0, a.getTestInteger());
        TrackableObject.redo();
        Assert.assertEquals(1, a.getTestInteger());
        TrackableObject.redo();
        Assert.assertEquals(2, a.getTestInteger());
        TrackableObject.redo();
        Assert.assertEquals(3, a.getTestInteger());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        Assert.assertEquals("test desc.", TrackableObject.getUndoDescription());
        TrackableObject.undo();
        Assert.assertEquals("initial state", TrackableObject.getUndoDescription());
        Assert.assertEquals("test desc.", TrackableObject.getRedoDescription());
        TrackableObject.undo();
        Assert.assertEquals("initial state", TrackableObject.getRedoDescription());
    }

    @Test
    public void cannotUndoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        TrackableObject.undo();
        Assert.assertFalse(TrackableObject.canUndo());
        try {
            TrackableObject.undo();
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
        Assert.assertFalse(TrackableObject.canRedo());
        try {
            TrackableObject.redo();
            Assert.fail();
        }
        catch (Exception e) {
            Assert.assertEquals("Redo is not possible as there are no saved redo states.", e.getMessage());
        }
    }

    @Test
    public void maximumUndoRedoStackSizeTest() throws Exception {
        TrackableObject.setMaximumTrackingSize(3);
        Assert.assertEquals(3, TrackableObject.getMaximumTrackingSize());
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        a.setTestInteger(4);
        a.setTestInteger(5);
        TrackableObject.undo();
        TrackableObject.undo();
        TrackableObject.undo();
        Assert.assertFalse(TrackableObject.canUndo());
        Assert.assertEquals(2, a.getTestInteger());
    }

    @Test
    public void impossibleRedoAfterActionPerformed() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        TrackableObject.undo();
        Assert.assertEquals(2, a.getTestInteger());
        Assert.assertTrue(TrackableObject.canRedo());
        a.setTestInteger(4);
        Assert.assertFalse(TrackableObject.canRedo());
        TrackableObject.undo();
        Assert.assertEquals(2, a.getTestInteger());
        TrackableObject.redo();
        Assert.assertEquals(4, a.getTestInteger());
    }
}
