package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.project.magic.tracking.TrackableValue;
import sws.project.magic.tracking.TrackableObject;

public class TrackingStringTest {
    public class TestString extends TrackableObject {
        public TestString() {
            saveCurrentState("initial state", true);
        }

        @TrackableValue
        private String testString;

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
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
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        a.setTestString("string3");
        TrackableObject.undo();
        Assert.assertEquals("string2", a.getTestString());
        TrackableObject.undo();
        Assert.assertEquals("string1", a.getTestString());
        TrackableObject.undo();
        Assert.assertEquals(null, a.getTestString());
    }

    @Test
    public void redoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        a.setTestString("string3");
        TrackableObject.undo();
        TrackableObject.undo();
        TrackableObject.undo();
        Assert.assertEquals(null, a.getTestString());
        TrackableObject.redo();
        Assert.assertEquals("string1", a.getTestString());
        TrackableObject.redo();
        Assert.assertEquals("string2", a.getTestString());
        TrackableObject.redo();
        Assert.assertEquals("string3", a.getTestString());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        Assert.assertEquals("test desc.", TrackableObject.getUndoDescription());
        TrackableObject.undo();
        Assert.assertEquals("initial state", TrackableObject.getUndoDescription());
        Assert.assertEquals("test desc.", TrackableObject.getRedoDescription());
        TrackableObject.undo();
        Assert.assertEquals("initial state", TrackableObject.getRedoDescription());
    }

    @Test
    public void cannotUndoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
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
        TestString a = new TestString();
        a.setTestString("string1");
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
