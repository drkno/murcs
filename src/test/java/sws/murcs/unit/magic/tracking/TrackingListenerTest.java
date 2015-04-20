package sws.murcs.unit.magic.tracking;

import org.junit.*;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeListenerHandler;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TrackingListenerTest {
    public class TestInteger extends TrackableObject {
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

    public class TestListener implements UndoRedoChangeListener {
        private boolean listenerCalled;
        private ChangeState stateProvided;

        @Override
        public void undoRedoNotification(ChangeState param) {
            listenerCalled = true;
            stateProvided = param;
        }

        public ChangeState getState() {
            return stateProvided;
        }

        public boolean getCalled() {
            return listenerCalled;
        }

        public void reset() {
            listenerCalled = false;
            stateProvided = null;
        }

        public TestListener() {
            reset();
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
    public void setup() {
        UndoRedoManager.setMaximumCommits(-1);
    }

    @After
    public void tearDown() throws Exception {
        UndoRedoManager.forget(true);
        listenersField.set(null, new ArrayList<ChangeListenerHandler>());
    }

    @Test
    public void commitListenerTest() throws Exception {
        TestListener listener = new TestListener();
        TestInteger testInteger = new TestInteger();
        UndoRedoManager.addChangeListener(listener);
        UndoRedoManager.add(testInteger);
        Assert.assertFalse(listener.getCalled());
        UndoRedoManager.commit("test commit");
        Assert.assertTrue(listener.getCalled());
        Assert.assertEquals(ChangeState.Commit, listener.getState());
    }

    @Test
    public void revertListenerTest() throws Exception {
        TestListener listener = new TestListener();
        TestInteger testInteger = new TestInteger();
        UndoRedoManager.add(testInteger);
        UndoRedoManager.commit("test commit");
        testInteger.setTestInteger(42);
        UndoRedoManager.addChangeListener(listener);
        Assert.assertFalse(listener.getCalled());
        UndoRedoManager.revert();
        Assert.assertTrue(listener.getCalled());
        Assert.assertEquals(ChangeState.Revert, listener.getState());
    }

    @Test
    public void remakeListenerTest() throws Exception {
        TestListener listener = new TestListener();
        TestInteger testInteger = new TestInteger();
        UndoRedoManager.add(testInteger);
        UndoRedoManager.commit("test commit");
        testInteger.setTestInteger(42);
        UndoRedoManager.revert();
        UndoRedoManager.addChangeListener(listener);
        Assert.assertFalse(listener.getCalled());
        UndoRedoManager.remake();
        Assert.assertTrue(listener.getCalled());
        Assert.assertEquals(ChangeState.Remake, listener.getState());
    }

    @Test
    public void forgetListenerTest() throws Exception {
        TestListener listener = new TestListener();
        TestInteger testInteger = new TestInteger();
        UndoRedoManager.add(testInteger);
        UndoRedoManager.commit("test commit");
        testInteger.setTestInteger(42);
        testInteger.setTestInteger(41);
        UndoRedoManager.revert();
        UndoRedoManager.addChangeListener(listener);
        Assert.assertFalse(listener.getCalled());
        UndoRedoManager.forget();
        Assert.assertTrue(listener.getCalled());
        Assert.assertEquals(ChangeState.Forget, listener.getState());
    }

    @Test
    public void automaticRemoveListenerTest() throws Exception {
        TestListener listener = new TestListener();
        TestInteger testInteger = new TestInteger();
        UndoRedoManager.addChangeListener(listener);
        UndoRedoManager.add(testInteger);
        ArrayList<ChangeListenerHandler> handlers = (ArrayList<ChangeListenerHandler>) listenersField.get(null);
        Assert.assertTrue(handlers.get(0).equals(listener));
        Assert.assertFalse(listener.getCalled());
        listener = null;
        ChangeListenerHandler.performGC();
        Thread.sleep(1000);
        UndoRedoManager.commit("test commit");
        Assert.assertTrue(handlers.size() == 0);
    }

    @Test
    public void manualRemoveListenerTest() throws Exception {
        TestListener listener = new TestListener();
        TestInteger testInteger = new TestInteger();
        UndoRedoManager.addChangeListener(listener);
        UndoRedoManager.add(testInteger);
        ArrayList<ChangeListenerHandler> handlers = (ArrayList<ChangeListenerHandler>) listenersField.get(null);
        Assert.assertTrue(handlers.get(0).equals(listener));
        Assert.assertFalse(listener.getCalled());
        UndoRedoManager.removeChangeListener(listener);
        Assert.assertTrue(handlers.size() == 0);
        UndoRedoManager.commit("test commit");
        Assert.assertFalse(listener.getCalled());
    }
}
