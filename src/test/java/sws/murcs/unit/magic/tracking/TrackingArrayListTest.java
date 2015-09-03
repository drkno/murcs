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

public class TrackingArrayListTest {
    public class TestArrayList extends TrackableObject {
        public TestArrayList() throws Exception {
            testArrayList = new ArrayList<Integer>();
            testArrayList.add(0);
            UndoRedoManager.get().add(this);
            commit("initial state");
        }

        @TrackableValue
        private ArrayList<Integer> testArrayList;

        public int getLastValue() {
            return testArrayList.get(testArrayList.size() - 1);
        }

        public void addValue(int value) throws Exception {
            testArrayList.add(value);
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
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        a.addValue(3);
        UndoRedoManager.get().revert();
        Assert.assertEquals(2, a.getLastValue());
        UndoRedoManager.get().revert();
        Assert.assertEquals(1, a.getLastValue());
    }

    @Test
    public void redoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        a.addValue(3);
        UndoRedoManager.get().revert();
        UndoRedoManager.get().revert();
        Assert.assertEquals(1, a.getLastValue());
        UndoRedoManager.get().remake();
        Assert.assertEquals(2, a.getLastValue());
        UndoRedoManager.get().remake();
        Assert.assertEquals(3, a.getLastValue());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        a.addValue(2);
        Assert.assertEquals(null, UndoRedoManager.get().getRemakeMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRevertMessage());
        UndoRedoManager.get().revert();
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRemakeMessage());
        UndoRedoManager.get().revert();
        Assert.assertEquals("test desc.", UndoRedoManager.get().getRemakeMessage());
        Assert.assertEquals(null, UndoRedoManager.get().getRevertMessage());
    }

    @Test(expected = Exception.class)
    public void cannotUndoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        UndoRedoManager.get().revert();
        Assert.assertFalse(UndoRedoManager.get().canRevert());

        UndoRedoManager.get().revert();
    }

    @Test(expected = Exception.class)
    public void cannotRedoTest() throws Exception {
        TestArrayList a = new TestArrayList();
        a.addValue(1);
        Assert.assertFalse(UndoRedoManager.get().canRemake());

        UndoRedoManager.get().remake();
    }
}
