package unit;

import dontclick.DontClickMe;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jayha_000 on 3/2/2015.
 */
public class DontClickMeTest {
    private DontClickMe dontClickMe;

    @Before
    public void setup(){
        dontClickMe = new DontClickMe();
    }


    /**
     * Note: This test will fail if the number of messages in 'messages.txt' is changed.
     */
    @Test
    public void testLoad(){
        Assert.assertEquals("There should be 6 messages after loading!", 6, dontClickMe.getMessages().size());
    }

    /**
     * Note; This test will fail if the first two messages are the same :'(
     */
    @Test
    public void testClick(){
        dontClickMe.load();

        if (dontClickMe.getMessages().size() <= 1) return;

        String message = dontClickMe.currentText();
        dontClickMe.click();

        String newMessage = dontClickMe.currentText();

        Assert.assertNotEquals("The message should have changed!", message, newMessage);
    }

    /**
     * Note: This test will fail if the 'messages.txt' file is changed..
     */
    @Test
    public void testGetMessage(){
        dontClickMe.load();

        Assert.assertEquals("The first message should be equal to 'James.'", "James.", dontClickMe.currentText());
    }
}
