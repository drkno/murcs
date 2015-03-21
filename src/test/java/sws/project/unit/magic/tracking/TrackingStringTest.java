package sws.project.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;

public class TrackingStringTest {
    public class TestString extends TrackableObject {
        public TestString() {
            commit("initial state", true);
        }

        @TrackableValue
        private String testString;

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
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
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        a.setTestString("string3");
        UndoRedoManager.undo();
        Assert.assertEquals("string2", a.getTestString());
        UndoRedoManager.undo();
        Assert.assertEquals("string1", a.getTestString());
        UndoRedoManager.undo();
        Assert.assertEquals(null, a.getTestString());
    }

    @Test
    public void redoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        a.setTestString("string3");
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        UndoRedoManager.undo();
        Assert.assertEquals(null, a.getTestString());
        UndoRedoManager.redo();
        Assert.assertEquals("string1", a.getTestString());
        UndoRedoManager.redo();
        Assert.assertEquals("string2", a.getTestString());
        UndoRedoManager.redo();
        Assert.assertEquals("string3", a.getTestString());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        Assert.assertEquals("test desc.", UndoRedoManager.getUndoDescription());
        UndoRedoManager.undo();
        Assert.assertEquals("initial state", UndoRedoManager.getUndoDescription());
        Assert.assertEquals("test desc.", UndoRedoManager.getRedoDescription());
        UndoRedoManager.undo();
        Assert.assertEquals("initial state", UndoRedoManager.getRedoDescription());
    }

    @Test(expected = Exception.class)
    public void cannotUndoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        UndoRedoManager.undo();
        Assert.assertFalse(UndoRedoManager.canUndo());

        UndoRedoManager.undo();
    }

    @Test(expected = Exception.class)
    public void cannotRedoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        Assert.assertFalse(UndoRedoManager.canRedo());

        UndoRedoManager.redo();
    }
}
