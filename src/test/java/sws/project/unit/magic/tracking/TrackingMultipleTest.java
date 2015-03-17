package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.project.magic.tracking.TrackableObject;
import sws.project.magic.tracking.TrackableValue;

public class TrackingMultipleTest {
    public class TestInteger extends TrackableObject {
        public TestInteger() {
            saveCurrentState("initial state", true);
        }

        @TrackableValue
        private int testInteger = 0;

        public int getTestInteger() {
            return testInteger;
        }

        public void setTestInteger(int testInteger) {
            this.testInteger = testInteger;
            saveCurrentState("test desc.");
        }
    }

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
        TestInteger a = new TestInteger();
        TestString b = new TestString();
        a.setTestInteger(1);
        b.setTestString("1");
        a.setTestInteger(2);
        b.setTestString("2");
        a.setTestInteger(3);
        b.setTestString("3");
        TrackableObject.undo();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(3, a.getTestInteger());
        TrackableObject.undo();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
        TrackableObject.undo();
        Assert.assertEquals("1", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
    }

    @Test
    public void redoTest() throws Exception {
        TestInteger a = new TestInteger();
        TestString b = new TestString();
        a.setTestInteger(1);
        b.setTestString("1");
        a.setTestInteger(2);
        b.setTestString("2");
        a.setTestInteger(3);
        b.setTestString("3");
        TrackableObject.undo();
        TrackableObject.undo();
        TrackableObject.undo();
        TrackableObject.undo();

        Assert.assertEquals("1", b.getTestString());
        Assert.assertEquals(1, a.getTestInteger());
        TrackableObject.redo();
        Assert.assertEquals("1", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());
        TrackableObject.redo();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(2, a.getTestInteger());;
        TrackableObject.redo();
        Assert.assertEquals("2", b.getTestString());
        Assert.assertEquals(3, a.getTestInteger());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestInteger a = new TestInteger();
        TestString b = new TestString();
        a.setTestInteger(1);
        b.setTestString("1");
        a.setTestInteger(2);
        b.setTestString("2");
        Assert.assertEquals("test desc.", TrackableObject.getUndoDescription());
        TrackableObject.undo();
        Assert.assertEquals("test desc.", TrackableObject.getUndoDescription());
        Assert.assertEquals("test desc.", TrackableObject.getRedoDescription());
        TrackableObject.undo();
        Assert.assertEquals("initial state", TrackableObject.getUndoDescription());
        Assert.assertEquals("test desc.", TrackableObject.getRedoDescription());
        TrackableObject.undo();
        Assert.assertEquals("initial state", TrackableObject.getRedoDescription());
    }
}
