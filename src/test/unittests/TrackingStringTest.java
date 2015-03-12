import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import sws.project.model.magic.tracking.TrackValue;
import sws.project.model.magic.tracking.ValueTracker;

public class TrackingStringTest {
    public class TestString extends ValueTracker {
        public TestString() {
            saveCurrentState("initial state", true);
        }

        @TrackValue
        private String testString;

        public String getTestString() {
            return testString;
        }

        public void setTestString(String testString) {
            this.testString = testString;
            saveCurrentState("test desc.");
        }
    }

    @After
    public void tearDown() throws Exception {
        ValueTracker.reset();
    }

    @Test
    public void undoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        a.setTestString("string3");
        ValueTracker.undo();
        Assert.assertEquals("string2", a.getTestString());
        ValueTracker.undo();
        Assert.assertEquals("string1", a.getTestString());
        ValueTracker.undo();
        Assert.assertEquals(null, a.getTestString());
    }

    @Test
    public void redoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        a.setTestString("string3");
        ValueTracker.undo();
        ValueTracker.undo();
        ValueTracker.undo();
        Assert.assertEquals(null, a.getTestString());
        ValueTracker.redo();
        Assert.assertEquals("string1", a.getTestString());
        ValueTracker.redo();
        Assert.assertEquals("string2", a.getTestString());
        ValueTracker.redo();
        Assert.assertEquals("string3", a.getTestString());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
        a.setTestString("string2");
        Assert.assertEquals("test desc.", ValueTracker.getUndoDescription());
        ValueTracker.undo();
        Assert.assertEquals("initial state", ValueTracker.getUndoDescription());
        Assert.assertEquals("test desc.", ValueTracker.getRedoDescription());
        ValueTracker.undo();
        Assert.assertEquals("initial state", ValueTracker.getRedoDescription());
    }

    @Test
    public void cannotUndoTest() throws Exception {
        TestString a = new TestString();
        a.setTestString("string1");
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
        TestString a = new TestString();
        a.setTestString("string1");
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
