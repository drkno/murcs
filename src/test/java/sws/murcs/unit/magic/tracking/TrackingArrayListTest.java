package sws.murcs.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;

import java.util.ArrayList;

public class TrackingArrayListTest {
    public class TestArrayList extends TrackableObject {
        public TestArrayList() throws Exception {
            testArrayList = new ArrayList<Integer>();
            testArrayList.add(0);
            UndoRedoManager.commit("initial state");
        }

        @TrackableValue
        private ArrayList<Integer> testArrayList;

        public int getLastValue() {
            return testArrayList.get(testArrayList.size() - 1);
        }

        public void addValue(int value) throws Exception {
            testArrayList.add(value);
            UndoRedoManager.commit("test desc.");
        }
    }

    @Before
    public void setup() {
        UndoRedoManager.setMaximumCommits(-1);
    }

    @After
    public void tearDown() throws Exception {
        UndoRedoManager.forget(true);
    }

    @Test
    public void undoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        a.addValue(3);
        UndoRedoManager.revert();
        Assert.assertEquals(2, a.getLastValue());
        UndoRedoManager.revert();
        Assert.assertEquals(1, a.getLastValue());
        UndoRedoManager.revert();
        Assert.assertEquals(0, a.getLastValue());
    }

    @Test
    public void redoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        a.addValue(3);
        UndoRedoManager.revert();
        UndoRedoManager.revert();
        UndoRedoManager.revert();
        Assert.assertEquals(0, a.getLastValue());
        UndoRedoManager.remake();
        Assert.assertEquals(1, a.getLastValue());
        UndoRedoManager.remake();
        Assert.assertEquals(2, a.getLastValue());
        UndoRedoManager.remake();
        Assert.assertEquals(3, a.getLastValue());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        Assert.assertEquals("test desc.", UndoRedoManager.getRevertMessage());
        UndoRedoManager.revert();
        Assert.assertEquals("initial state", UndoRedoManager.getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.getRemakeMessage());
        UndoRedoManager.revert();
        Assert.assertEquals("test desc.", UndoRedoManager.getRemakeMessage());
    }

    @Test(expected = Exception.class)
    public void cannotUndoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        UndoRedoManager.revert();
        Assert.assertFalse(UndoRedoManager.canRevert());

        UndoRedoManager.revert();
    }

    @Test(expected = Exception.class)
    public void cannotRedoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        Assert.assertFalse(UndoRedoManager.canRemake());

        UndoRedoManager.remake();
    }
}
