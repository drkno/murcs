package unit;

import dontclick.StudentList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jayha_000 on 3/2/2015.
 */
public class DontClickMeTest {
    private StudentList dontClickMe;

    @Before
    public void setup(){
        dontClickMe = new StudentList(false);
    }


    /**
     * Note: This test will fail if the number of messages in 'students.txt' is changed.
     */
    @Test
    public void testLoad(){
        Assert.assertEquals("There should be no messages before loading!", 0, dontClickMe.getStudents().size());

        dontClickMe.load();

        Assert.assertEquals("There should be 6 messages after loading!", 6, dontClickMe.getStudents().size());
    }

    /**
     * Note; This test will fail if the first two messages are the same :'(
     */
    @Test
    public void testClick(){
        dontClickMe.load();

        if (dontClickMe.getStudents().size() <= 1) return;

        String message = dontClickMe.currentStudentName();
        dontClickMe.click();

        String newMessage = dontClickMe.currentStudentName();

        Assert.assertNotEquals("The message should have changed!", message, newMessage);
    }

    /**
     * Note: This test will fail if the 'students.txt' file is changed..
     */
    @Test
    public void testGetMessage(){
        dontClickMe.load();

        Assert.assertEquals("The first message should be equal to 'James.'", "James.", dontClickMe.currentStudentName());
    }
}
