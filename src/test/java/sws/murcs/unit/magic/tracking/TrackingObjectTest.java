package sws.murcs.unit.magic.tracking;

import org.junit.*;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeListenerHandler;

import java.lang.reflect.Field;
import java.util.ArrayList;

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
        public TestContainerObject() throws Exception {
            UndoRedoManager.add(this);
            commit("initial state");
        }

        @TrackableValue
        private TestObject testObject;

        public TestObject getTestObject() {
            return testObject;
        }

        public void setTestObject(TestObject testObject) throws Exception {
            this.testObject = testObject;
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
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
        a.setTestObject(new TestObject(3));
        UndoRedoManager.revert();
        Assert.assertEquals("2", a.getTestObject().toString());
        UndoRedoManager.revert();
        Assert.assertEquals("1", a.getTestObject().toString());
    }

    @Test
    public void redoTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
        a.setTestObject(new TestObject(3));
        UndoRedoManager.revert();
        UndoRedoManager.revert();
        Assert.assertEquals("1", a.getTestObject().toString());
        UndoRedoManager.remake();
        Assert.assertEquals("2", a.getTestObject().toString());
        UndoRedoManager.remake();
        Assert.assertEquals("3", a.getTestObject().toString());
    }

    @Test
    public void descriptionTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        a.setTestObject(new TestObject(2));
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
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        UndoRedoManager.revert();
        Assert.assertFalse(UndoRedoManager.canRevert());

        UndoRedoManager.revert();
    }

    @Test(expected = Exception.class)
    public void cannotRedoTest() throws Exception {
        TestContainerObject a = new TestContainerObject();
        a.setTestObject(new TestObject(1));
        Assert.assertFalse(UndoRedoManager.canRemake());

        UndoRedoManager.remake();
    }
}
