package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import sws.project.magic.tracking.TrackValue;
import sws.project.magic.tracking.ValueTracker;

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

    private class TestContainerObject extends ValueTracker {
        public TestContainerObject() {
            saveCurrentState("initial state", true);
        }

        @TrackValue
        private TestObject testObject;

        public TestObject getTestObject() {
            return testObject;
        }

        public void setTestObject(TestObject testObject) {
            this.testObject = testObject;
            saveCurrentState("test desc.");
        }
    }

    @After
    public void tearDown() throws Exception {
        ValueTracker.reset();
    }

    @Test
    public void undoTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
        a.setTestObject(new TestObject(3));
        ValueTracker.undo();
        Assert.assertEquals("2", a.getTestObject().toString());
        ValueTracker.undo();
        Assert.assertEquals("1", a.getTestObject().toString());
        ValueTracker.undo();
        Assert.assertEquals(null, a.getTestObject());
    }

    @Test
    public void redoTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
        a.setTestObject(new TestObject(3));
        ValueTracker.undo();
        ValueTracker.undo();
        ValueTracker.undo();
        Assert.assertEquals(null, a.getTestObject());
        ValueTracker.redo();
        Assert.assertEquals("1", a.getTestObject().toString());
        ValueTracker.redo();
        Assert.assertEquals("2", a.getTestObject().toString());
        ValueTracker.redo();
        Assert.assertEquals("3", a.getTestObject().toString());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
        Assert.assertEquals("test desc.", ValueTracker.getUndoDescription());
        ValueTracker.undo();
        Assert.assertEquals("initial state", ValueTracker.getUndoDescription());
        Assert.assertEquals("test desc.", ValueTracker.getRedoDescription());
        ValueTracker.undo();
        Assert.assertEquals("initial state", ValueTracker.getRedoDescription());
    }

    @Test
    public void cannotUndoTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
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
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
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
