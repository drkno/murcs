package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Story;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StoryTest {

    private Story story;
    private Story story2;
    private Story story3;


    @Before
    public void setUp() throws Exception {
        story = new Story();
        story2 = new Story();
        story3 = new Story();
        story.setShortName("OMG");
        story2.setShortName("A very good story to have");
        story3.setShortName("Something worth while");
        UndoRedoManager.setDisabled(true);
    }

    @After
    public void tearDown() {
        story = null;
        story2 = null;
        story3 = null;
    }

    @Test (expected = Exception.class)
    public void setShortNameTest1() throws Exception{
        story.setShortName(null);
    }

    @Test(expected = Exception.class)
    public void setShortNameTest2() throws Exception{
        story.setShortName("");
    }

    @Test(expected = Exception.class)
    public void setShortNameTest3() throws Exception{
        story.setShortName("   \n\r\t");
    }

    @Test
    public void equalsTest() throws Exception {
        story2.setShortName("OMG");
        assertTrue(story.equals(story2));
        assertFalse(story.equals(story3));
    }

    @Test
    public void hashCodeTest() throws Exception {
        int actual = story.getHashCodePrime() + story.getShortName().hashCode();
        assertEquals(story.hashCode(), actual);
    }
}
