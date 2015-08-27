package sws.murcs.unit.model;

import java.time.LocalDate;
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
        task.setEstimate(1, LocalDate.of(2001, 1, 1));
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
    public void setEstimateTest() throws Exception {
        task.setEstimate(5);
        assertEquals(5, task.getEstimate());
    }

    @Test
    public void setPreviousEstimate() {
        LocalDate date = LocalDate.of(2000, 1, 1);

        //The current estimate should be 1 (as the estimate in 2001 is 1 and today is after 2001)
        assertEquals(1, task.getEstimate(LocalDate.of(2001, 1, 1)), 0.1);
        assertEquals(1, task.getEstimate(), 0.1);

        //Should add ten to all estimates Jan 1, 2000
        task.setEstimate(10, date);

        assertEquals(10f, task.getEstimate(date), 0.1);
        assertEquals(11f, task.getEstimate(LocalDate.of(2001, 1, 1)), 0.1);
        assertEquals(11f, task.getEstimate(), 0.1);

        //Shouldn't change anything apart from our estimate for today
        task.setEstimate(1);
        assertEquals(10, task.getEstimate(date), 0.1);
        assertEquals(11, task.getEstimate(LocalDate.of(2001, 1, 1)), 0.1);
        assertEquals(1, task.getEstimate(), 0.1);

        task.setEstimate(6, LocalDate.of(2003, 1, 1));
        assertEquals(6, task.getEstimate(LocalDate.of(2003, 1, 1)), 0.1);

        //Set this to six, meaning we should subtract (6 - 11) from all estimates after this one
        task.setEstimate(6, LocalDate.of(2002, 1, 1));

        assertEquals(6, task.getEstimate(LocalDate.of(2002, 1, 1)), 0.1);
        assertEquals(1, task.getEstimate(LocalDate.of(2003, 1, 1)), 0.1);

        //This should be 0, as we should never have negative estimates
        assertEquals(0, task.getEstimate(), 0.1);
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
