package sws.project.model.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import sws.project.model.magic.tracking.TrackValue;
import sws.project.model.magic.tracking.ValueTracker;

import java.util.ArrayList;

public class TrackingArrayListTest {
    public class TestArrayList extends ValueTracker {
        public TestArrayList() {
            testArrayList = new ArrayList<Integer>();
            testArrayList.add(0);
            saveCurrentState("initial state", true);
        }

        @TrackValue
        private ArrayList<Integer> testArrayList;

        public int getLastValue() {
            return testArrayList.get(testArrayList.size() - 1);
        }

        public void addValue(int value) {
            testArrayList.add(value);
            saveCurrentState("test desc.");
        }
    }

    @After
    public void tearDown() throws Exception {
        ValueTracker.reset();
    }

    @Test
    public void undoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        a.addValue(3);
        ValueTracker.undo();
        Assert.assertEquals(2, a.getLastValue());
        ValueTracker.undo();
        Assert.assertEquals(1, a.getLastValue());
        ValueTracker.undo();
        Assert.assertEquals(0, a.getLastValue());
    }

    @Test
    public void redoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        a.addValue(3);
        ValueTracker.undo();
        ValueTracker.undo();
        ValueTracker.undo();
        Assert.assertEquals(0, a.getLastValue());
        ValueTracker.redo();
        Assert.assertEquals(1, a.getLastValue());
        ValueTracker.redo();
        Assert.assertEquals(2, a.getLastValue());
        ValueTracker.redo();
        Assert.assertEquals(3, a.getLastValue());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        Assert.assertEquals("test desc.", ValueTracker.getUndoDescription());
        ValueTracker.undo();
        Assert.assertEquals("initial state", ValueTracker.getUndoDescription());
        Assert.assertEquals("test desc.", ValueTracker.getRedoDescription());
        ValueTracker.undo();
        Assert.assertEquals("initial state", ValueTracker.getRedoDescription());
    }

    @Test
    public void cannotUndoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
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
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        Assert.assertFalse(ValueTracker.canRedo());
        try {
            ValueTracker.redo();
            Assert.fail();
        }
        catch (Exception e) {
            Assert.assertEquals("Redo is not possible as there are no saved redo states.", e.getMessage());
        }
    }
}
