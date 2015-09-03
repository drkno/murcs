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
import sws.murcs.model.Model;
import sws.murcs.model.observable.ModelObservableArrayList;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TrackingObservableListTest {
    public class TestArrayList extends TrackableObject {
        public TestArrayList() throws Exception {
            testArrayList = new ModelObservableArrayList<TestClass>();
            testArrayList.add(new TestClass(0));
            UndoRedoManager.get().add(this);
            commit("initial state");
        }

        @TrackableValue
        private ModelObservableArrayList<TestClass> testArrayList;

        public int getLastValue() {
            return testArrayList.get(testArrayList.size() - 1).getA();
        }

        public void addValue(int value) throws Exception {
            testArrayList.add(new TestClass(value));
            commit("test desc.");
        }

        private class TestClass extends Model {
            private int a;
            public TestClass(int newA) { setA(newA); }
            public int getA() { return a; }
            public void setA(int newA) { a = newA; }
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
