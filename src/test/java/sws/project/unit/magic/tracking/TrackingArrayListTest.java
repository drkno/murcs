package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

    @Before
    public void setup() {
        TrackableObject.setMergeWaitTime(0);
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

    @Test(expected = Exception.class)
    public void cannotUndoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        TrackableObject.undo();
        Assert.assertFalse(TrackableObject.canUndo());

        TrackableObject.undo();
    }

    @Test(expected = Exception.class)
    public void cannotRedoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        Assert.assertFalse(TrackableObject.canRedo());

        TrackableObject.redo();
    }
}
