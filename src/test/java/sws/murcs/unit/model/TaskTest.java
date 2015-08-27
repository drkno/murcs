package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;

import static org.junit.Assert.assertEquals;

public class TaskTest {

    Task task;

    @Before
    public void setUp() throws Exception {
        task = new Task();
        task.setName("Need to do this");
        task.setCurrentEstimate(1);
        task.setState(TaskState.InProgress);
        task.setDescription("Something I need to do");
        UndoRedoManager.setDisabled(true);
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
    public void setStateTest() throws Exception {
        task.setState(TaskState.Done);
        assertEquals(TaskState.Done, task.getState());
    }

    @Test
    public void estimateTest() {
        assertEquals(1, task.getCurrentEstimate(), 0.1);
    }

    @Test
    public void setDescriptionTest() throws Exception {
        task.setDescription("new description");
        assertEquals("new description", task.getDescription());
    }
}
