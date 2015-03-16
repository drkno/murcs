package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import sws.project.magic.tracking.TrackableValue;
import sws.project.magic.tracking.TrackableObject;

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
            saveCurrentState("initial state", true);
        }

        @TrackableValue
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
        TrackableObject.reset();
    }

    @Test
    public void undoTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
        a.setTestObject(new TestObject(3));
        TrackableObject.undo();
        Assert.assertEquals("2", a.getTestObject().toString());
        TrackableObject.undo();
        Assert.assertEquals("1", a.getTestObject().toString());
        TrackableObject.undo();
        Assert.assertEquals(null, a.getTestObject());
    }

    @Test
    public void redoTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
        a.setTestObject(new TestObject(3));
        TrackableObject.undo();
        TrackableObject.undo();
        TrackableObject.undo();
        Assert.assertEquals(null, a.getTestObject());
        TrackableObject.redo();
        Assert.assertEquals("1", a.getTestObject().toString());
        TrackableObject.redo();
        Assert.assertEquals("2", a.getTestObject().toString());
        TrackableObject.redo();
        Assert.assertEquals("3", a.getTestObject().toString());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
        Assert.assertEquals("test desc.", TrackableObject.getUndoDescription());
        TrackableObject.undo();
        Assert.assertEquals("initial state", TrackableObject.getUndoDescription());
        Assert.assertEquals("test desc.", TrackableObject.getRedoDescription());
        TrackableObject.undo();
        Assert.assertEquals("initial state", TrackableObject.getRedoDescription());
    }

    @Test
    public void cannotUndoTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
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
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
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
