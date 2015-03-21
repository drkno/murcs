package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;

public class TrackingObjectTest {
    public class TestObject {
        public TestObject(int someField) {
            this.someField = someField;
        }
        private int someField;
        @Override
        public String toString() {
            return Integer.toString(someField);
        }
    }

    private class TestContainerObject extends TrackableObject {
        public TestContainerObject() {
            commit("initial state", true);
        }

        @TrackableValue
        private TestObject testObject;

        public TestObject getTestObject() {
            return testObject;
        }

        public void setTestObject(TestObject testObject) {
            this.testObject = testObject;
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
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
        a.setTestObject(new TestObject(3));
        UndoRedoManager.undo();
        Assert.assertEquals("2", a.getTestObject().toString());
        UndoRedoManager.undo();
        Assert.assertEquals("1", a.getTestObject().toString());
        UndoRedoManager.undo();
        Assert.assertEquals(null, a.getTestObject());
    }

    @Test
    public void redoTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
        a.setTestObject(new TestObject(3));
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        Assert.assertEquals(null, a.getTestObject());
        UndoRedoManager.redo();
        Assert.assertEquals("1", a.getTestObject().toString());
        UndoRedoManager.redo();
        Assert.assertEquals("2", a.getTestObject().toString());
        UndoRedoManager.redo();
        Assert.assertEquals("3", a.getTestObject().toString());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
        Assert.assertEquals("test desc.", UndoRedoManager.getUndoDescription());
        UndoRedoManager.undo();
        Assert.assertEquals("initial state", UndoRedoManager.getUndoDescription());
        Assert.assertEquals("test desc.", UndoRedoManager.getRedoDescription());
        UndoRedoManager.undo();
        Assert.assertEquals("initial state", UndoRedoManager.getRedoDescription());
    }

    @Test(expected = Exception.class)
    public void cannotUndoTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        UndoRedoManager.undo();
        Assert.assertFalse(UndoRedoManager.canUndo());

        UndoRedoManager.undo();
    }

    @Test(expected = Exception.class)
    public void cannotRedoTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        Assert.assertFalse(UndoRedoManager.canRedo());

        UndoRedoManager.redo();
    }
}
