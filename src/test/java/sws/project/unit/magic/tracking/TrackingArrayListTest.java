package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.project.magic.tracking.TrackableObject;
import sws.project.magic.tracking.TrackableValue;
import sws.project.magic.tracking.UndoRedoManager;

import java.util.ArrayList;

public class TrackingArrayListTest {
    public class TestArrayList extends TrackableObject {
        public TestArrayList() {
            testArrayList = new ArrayList<Integer>();
            testArrayList.add(0);
            commit("initial state", true);
        }

        @TrackableValue
        private ArrayList<Integer> testArrayList;

        public int getLastValue() {
            return testArrayList.get(testArrayList.size() - 1);
        }

        public void addValue(int value) {
            testArrayList.add(value);
            commit("test desc.");
        }
    }

    @Before
    public void setup() {
        UndoRedoManager.setMergeWaitTime(0);
    }

    @After
    public void tearDown() throws Exception {
        UndoRedoManager.reset();
    }

    @Test
    public void undoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        a.addValue(3);
        UndoRedoManager.undo();
        Assert.assertEquals(2, a.getLastValue());
        UndoRedoManager.undo();
        Assert.assertEquals(1, a.getLastValue());
        UndoRedoManager.undo();
        Assert.assertEquals(0, a.getLastValue());
    }

    @Test
    public void redoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        a.addValue(3);
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        Assert.assertEquals(0, a.getLastValue());
        UndoRedoManager.redo();
        Assert.assertEquals(1, a.getLastValue());
        UndoRedoManager.redo();
        Assert.assertEquals(2, a.getLastValue());
        UndoRedoManager.redo();
        Assert.assertEquals(3, a.getLastValue());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        Assert.assertEquals("test desc.", UndoRedoManager.getUndoDescription());
        UndoRedoManager.undo();
        Assert.assertEquals("initial state", UndoRedoManager.getUndoDescription());
        Assert.assertEquals("test desc.", UndoRedoManager.getRedoDescription());
        UndoRedoManager.undo();
        Assert.assertEquals("initial state", UndoRedoManager.getRedoDescription());
    }

    @Test(expected = Exception.class)
    public void cannotUndoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        UndoRedoManager.undo();
        Assert.assertFalse(UndoRedoManager.canUndo());

        UndoRedoManager.undo();
    }

    @Test(expected = Exception.class)
    public void cannotRedoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        Assert.assertFalse(UndoRedoManager.canRedo());

        UndoRedoManager.redo();
    }
}
