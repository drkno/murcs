package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import sws.project.magic.tracking.TrackableValue;
import sws.project.magic.tracking.TrackableObject;

import java.util.ArrayList;

public class TrackingArrayListTest {
    public class TestArrayList extends TrackableObject {
        public TestArrayList() {
            testArrayList = new ArrayList<Integer>();
            testArrayList.add(0);
            saveCurrentState("initial state", true);
        }

        @TrackableValue
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
        TrackableObject.reset();
    }

    @Test
    public void undoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        a.addValue(3);
        TrackableObject.undo();
        Assert.assertEquals(2, a.getLastValue());
        TrackableObject.undo();
        Assert.assertEquals(1, a.getLastValue());
        TrackableObject.undo();
        Assert.assertEquals(0, a.getLastValue());
    }

    @Test
    public void redoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        a.addValue(3);
        TrackableObject.undo();
        TrackableObject.undo();
        TrackableObject.undo();
        Assert.assertEquals(0, a.getLastValue());
        TrackableObject.redo();
        Assert.assertEquals(1, a.getLastValue());
        TrackableObject.redo();
        Assert.assertEquals(2, a.getLastValue());
        TrackableObject.redo();
        Assert.assertEquals(3, a.getLastValue());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        Assert.assertEquals("test desc.", TrackableObject.getUndoDescription());
        TrackableObject.undo();
        Assert.assertEquals("initial state", TrackableObject.getUndoDescription());
        Assert.assertEquals("test desc.", TrackableObject.getRedoDescription());
        TrackableObject.undo();
        Assert.assertEquals("initial state", TrackableObject.getRedoDescription());
    }

    @Test
    public void cannotUndoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
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
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        Assert.assertFalse(TrackableObject.canRedo());
        try {
            TrackableObject.redo();
            Assert.fail();
        }
        catch (Exception e) {
            Assert.assertEquals("Redo is not possible as there are no saved redo states.", e.getMessage());
        }
    }
}
