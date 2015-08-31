package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaskTest {

    Task task;

    @Before
    public void setUp() throws Exception {
        task = new Task();
        task.setName("Need to do this");
        task.setEstimate(1);
        task.setState(TaskState.InProgress);
        task.setDescription("Something I need to do");
        UndoRedoManager.get().setDisabled(true);
    }

    @After
    public void tearDown() {
        task = null;
    }

    @Test
    public void setNameTest() throws Exception {
        task.setName("new name");
        assertEquals("new name", task.getName());
    }

    @Test
    public void setEstimateTest2() throws Exception {
        task.setEstimate(2);
        assertTrue(2.0f == task.getEstimate());
    }

    @Test
    public void setStateTest() throws Exception {
        task.setState(TaskState.Done);
        assertEquals(TaskState.Done, task.getState());
    }

    @Test
    public void setDescriptionTest() throws Exception {
        task.setDescription("new description");
        assertEquals("new description", task.getDescription());
    }
}
