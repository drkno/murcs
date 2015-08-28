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

public class TrackingIntegerTest {

    public class TestInteger extends TrackableObject {
        public TestInteger() throws Exception {
            UndoRedoManager.add(this);
            commit("initial state");
        }

        @TrackableValue
        private int testInteger = 0;

        public int getTestInteger() {
            return testInteger;
        }

        public void setTestInteger(int testInteger) throws Exception {
            this.testInteger = testInteger;
            commit("test desc.");
        }
    }

    private static Field listenersField;

    @BeforeClass
    public static void setupClass() throws Exception {
        listenersField = UndoRedoManager.class.getDeclaredField("changeListeners");
        listenersField.setAccessible(true);
        UndoRedoManager.setDisabled(false);
    }

    @Before
    public void setup() throws IllegalAccessException {
        UndoRedoManager.forget(true);
        listenersField.set(null, new ArrayList<ChangeListenerHandler>());
        UndoRedoManager.setMaximumCommits(-1);
    }

    @After
    public void tearDown() throws Exception {
        UndoRedoManager.forget(true);
    }

    @Test
    public void undoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        UndoRedoManager.revert();
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.revert();
        Assert.assertEquals(1, a.getTestInteger());
    }

    @Test
    public void redoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        UndoRedoManager.revert();
        UndoRedoManager.revert();
        Assert.assertEquals(1, a.getTestInteger());
        UndoRedoManager.remake();
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.remake();
        Assert.assertEquals(3, a.getTestInteger());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        Assert.assertEquals(null, UndoRedoManager.getRemakeMessage());
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
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        UndoRedoManager.revert();
        Assert.assertFalse(UndoRedoManager.canRevert());
        UndoRedoManager.revert();
    }

    @Test(expected = Exception.class)
    public void cannotRedoTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        Assert.assertFalse(UndoRedoManager.canRemake());

        UndoRedoManager.remake();
    }

    @Test
    public void maximumUndoRedoStackSizeTest() throws Exception {
        UndoRedoManager.setMaximumCommits(3);
        Assert.assertEquals(3, UndoRedoManager.getMaximumCommits());
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        a.setTestInteger(4);
        a.setTestInteger(5);
        UndoRedoManager.revert();
        UndoRedoManager.revert();
        UndoRedoManager.revert();
        Assert.assertEquals(2, a.getTestInteger());
        Assert.assertFalse(UndoRedoManager.canRevert());
        UndoRedoManager.setMaximumCommits(-1);
    }

    @Test
    public void impossibleRedoAfterActionPerformed() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        UndoRedoManager.revert();
        Assert.assertEquals(2, a.getTestInteger());
        Assert.assertTrue(UndoRedoManager.canRemake());
        a.setTestInteger(4);
        Assert.assertFalse(UndoRedoManager.canRemake());
        UndoRedoManager.revert();
        Assert.assertEquals(2, a.getTestInteger());
        UndoRedoManager.remake();
        Assert.assertEquals(4, a.getTestInteger());
    }

    @Test
    public void saveIgnoredIfValueDidNotChange() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        a.setTestInteger(3);
        UndoRedoManager.revert();
        Assert.assertEquals(2, a.getTestInteger());
    }

    @Test
    public void repetitiveRevertRemakeTest() throws Exception {
        TestInteger a = new TestInteger();
        a.setTestInteger(1);
        a.setTestInteger(2);
        a.setTestInteger(3);
        a.setTestInteger(4);
        a.setTestInteger(5);
        a.setTestInteger(6);
        a.setTestInteger(7);
        a.setTestInteger(8);
        UndoRedoManager.revert();  // 7
        UndoRedoManager.revert();  // 6
        UndoRedoManager.revert();  // 5
        UndoRedoManager.revert();  // 4
        UndoRedoManager.remake();  // 5
        UndoRedoManager.remake();  // 6
        UndoRedoManager.revert();  // 5
        UndoRedoManager.revert();  // 4
        UndoRedoManager.revert();  // 3
        UndoRedoManager.revert();  // 2
        UndoRedoManager.remake();  // 3
        UndoRedoManager.remake();  // 4
        UndoRedoManager.remake();  // 5
        UndoRedoManager.remake();  // 6
        UndoRedoManager.remake();  // 7
        Assert.assertEquals(7, a.getTestInteger());
    }

    @Test
    public void repetitiveRevertRemakeAddTest() throws Exception {
        TestInteger a = new TestInteger();  // 0
        a.setTestInteger(1);  // 1
        a.setTestInteger(2);  // 2
        a.setTestInteger(3);  // 3
        a.setTestInteger(4);  // 4
        a.setTestInteger(5);  // 5
        a.setTestInteger(6);  // 6
        a.setTestInteger(7);  // 7
        a.setTestInteger(8);  // 8
        UndoRedoManager.revert();  // 7
        UndoRedoManager.revert();  // 6
        UndoRedoManager.revert();  // 5
        UndoRedoManager.revert();  // 4
        UndoRedoManager.remake();  // 5
        UndoRedoManager.remake();  // 6
        a.setTestInteger(9);  // 9
        UndoRedoManager.revert();  // 6
        Assert.assertEquals(6, a.getTestInteger());
        UndoRedoManager.remake();  // 9
        Assert.assertEquals(9, a.getTestInteger());
        UndoRedoManager.revert();  // 6
        UndoRedoManager.revert();  // 5
        UndoRedoManager.revert();  // 4
        UndoRedoManager.revert();  // 3
        UndoRedoManager.revert();  // 2
        UndoRedoManager.revert();  // 1
        UndoRedoManager.revert();  // 0
        Assert.assertFalse(UndoRedoManager.canRevert());
        Assert.assertEquals(0, a.getTestInteger());
    }
}
