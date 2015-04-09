package sws.murcs.unit.magic.tracking;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;

public class TrackingStringTest {
    public class TestString extends TrackableObject {
        public TestString() throws Exception {
            UndoRedoManager.commit("initial state");
        }

        @TrackableValue
        private String testString;

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) throws Exception {
            this.testString = testString;
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
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        a.setTestString("string3");
        UndoRedoManager.revert();
        Assert.assertEquals("string2", a.getTestString());
        UndoRedoManager.revert();
        Assert.assertEquals("string1", a.getTestString());
        UndoRedoManager.revert();
        Assert.assertEquals(null, a.getTestString());
    }

    @Test
    public void redoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        a.setTestString("string3");
        UndoRedoManager.revert();
        UndoRedoManager.revert();
        UndoRedoManager.revert();
        Assert.assertEquals(null, a.getTestString());
        UndoRedoManager.remake();
        Assert.assertEquals("string1", a.getTestString());
        UndoRedoManager.remake();
        Assert.assertEquals("string2", a.getTestString());
        UndoRedoManager.remake();
        Assert.assertEquals("string3", a.getTestString());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        Assert.assertEquals("test desc.", UndoRedoManager.getRevertMessage());
        UndoRedoManager.revert();
        Assert.assertEquals("test desc.", UndoRedoManager.getRevertMessage());
        Assert.assertEquals("test desc.", UndoRedoManager.getRemakeMessage());
        UndoRedoManager.revert();
        Assert.assertEquals("test desc.", UndoRedoManager.getRemakeMessage());
        Assert.assertEquals(null, UndoRedoManager.getRevertMessage());
    }

    @Test(expected = Exception.class)
    public void cannotUndoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        UndoRedoManager.revert();
        Assert.assertFalse(UndoRedoManager.canRevert());

        UndoRedoManager.revert();
    }

    @Test(expected = Exception.class)
    public void cannotRedoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        Assert.assertFalse(UndoRedoManager.canRemake());

        UndoRedoManager.remake();
    }
}
