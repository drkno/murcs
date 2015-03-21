package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;

public class TrackingIntegerTest {
    public class TestInteger extends TrackableObject {
        public TestInteger() {
            commit("initial state", true);
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
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        UndoRedoManager.undo();
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.undo();
        Assert.assertEquals(1, a.getTestInteger());
        UndoRedoManager.undo();
        Assert.assertEquals(0, a.getTestInteger());
    }

    @Test
    public void redoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        Assert.assertEquals(0, a.getTestInteger());
        UndoRedoManager.redo();
        Assert.assertEquals(1, a.getTestInteger());
        UndoRedoManager.redo();
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.redo();
        Assert.assertEquals(3, a.getTestInteger());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        Assert.assertEquals("test desc.", UndoRedoManager.getUndoDescription());
        UndoRedoManager.undo();
        Assert.assertEquals("initial state", UndoRedoManager.getUndoDescription());
        Assert.assertEquals("test desc.", UndoRedoManager.getRedoDescription());
        UndoRedoManager.undo();
        Assert.assertEquals("initial state", UndoRedoManager.getRedoDescription());
    }

    @Test(expected = Exception.class)
    public void cannotUndoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        UndoRedoManager.undo();
        Assert.assertFalse(UndoRedoManager.canUndo());

        UndoRedoManager.undo();
    }

    @Test(expected = Exception.class)
    public void cannotRedoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        Assert.assertFalse(UndoRedoManager.canRedo());

        UndoRedoManager.redo();
    }

    @Test
    public void maximumUndoRedoStackSizeTest() throws Exception {
        UndoRedoManager.setMaximumTrackingSize(3);
        Assert.assertEquals(3, UndoRedoManager.getMaximumTrackingSize());
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        a.setTestInteger(4);
        a.setTestInteger(5);
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        Assert.assertEquals(2, a.getTestInteger());
        Assert.assertFalse(UndoRedoManager.canUndo());
        UndoRedoManager.setMaximumTrackingSize(-1);
    }

    @Test
    public void impossibleRedoAfterActionPerformed() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        UndoRedoManager.undo();
        Assert.assertEquals(2, a.getTestInteger());
        Assert.assertTrue(UndoRedoManager.canRedo());
        a.setTestInteger(4);
        Assert.assertFalse(UndoRedoManager.canRedo());
        UndoRedoManager.undo();
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.redo();
        Assert.assertEquals(4, a.getTestInteger());
    }

    @Test
    public void saveIgnoredIfValueDidNotChange() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        a.setTestInteger(3);
        UndoRedoManager.undo();
        Assert.assertEquals(2, a.getTestInteger());
    }

    @Test
    public void mergeChangesIfLessThanMergeTime() throws Exception {
        UndoRedoManager.setMergeWaitTime(1000000); // ~11 days. if a build is going that long we have a problem
        Assert.assertEquals(UndoRedoManager.getMergeWaitTime(), 1000000);
        TestInteger a = new TestInteger();
        a.setTestInteger(3);
        a.setTestInteger(2);
        a.setTestInteger(1);
        UndoRedoManager.setMergeWaitTime(0);
        a.setTestInteger(4);
        a.setTestInteger(5);
        UndoRedoManager.undo();
        Assert.assertEquals(a.getTestInteger(), 4);
        UndoRedoManager.undo();
        Assert.assertEquals(a.getTestInteger(), 1);
        UndoRedoManager.undo();
        Assert.assertEquals(a.getTestInteger(), 0);
        Assert.assertFalse(UndoRedoManager.canUndo());
    }
}
