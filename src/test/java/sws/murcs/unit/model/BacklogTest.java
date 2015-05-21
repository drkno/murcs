package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Backlog;
import sws.murcs.model.Story;

import static org.junit.Assert.*;

public class BacklogTest {

    private Backlog backlog;
    private Backlog backlog2;
    private Backlog backlog3;

    private Story story1;
    private Story story2;
    private Story story3;
    private Story story4;
    private Story story5;


    @Before
    public void setUp() throws Exception {
        backlog = new Backlog();
        backlog2 = new Backlog();
        backlog3 = new Backlog();
        backlog.setShortName("OMG");
        backlog2.setShortName("A very good backlog to have");
        backlog3.setShortName("Something worth while");

        story1 = new Story();
        story2 = new Story();
        story3 = new Story();
        story4 = new Story();
        story5 = new Story();
        story1.setShortName("OMG");
        story2.setShortName("A very good story to have");
        story3.setShortName("Something worth while");
        story4.setShortName("another story");
        story5.setShortName("oh look");

        backlog.addStory(story1, null);
        backlog.addStory(story2, 0);
        UndoRedoManager.setDisabled(true);
    }

    @After
    public void tearDown() {
        backlog = null;
        backlog2 = null;
        backlog3 = null;

        story1 = null;
        story2 = null;
        story3 = null;
    }

    @Test (expected = Exception.class)
    public void setShortNameTest1() throws Exception{
        backlog.setShortName(null);
    }

    @Test(expected = Exception.class)
    public void setShortNameTest2() throws Exception{
        backlog.setShortName("");
    }

    @Test(expected = Exception.class)
    public void setShortNameTest3() throws Exception{
        backlog.setShortName("   \n\r\t");
    }

    @Test
    public void equalsTest() throws Exception {
        backlog2.setShortName("OMG");
        assertTrue(backlog.equals(backlog2));
        assertFalse(backlog.equals(backlog3));
    }

    @Test
    public void hashCodeTest() throws Exception {
        int actual = backlog.getHashCodePrime() + backlog.getShortName().hashCode();
        assertEquals(backlog.hashCode(), actual);
    }

    @Test
    public void testModifyStory() throws Exception {
        assertTrue(backlog.getUnPrioritisedStories().contains(story1));
        backlog.modifyStory(story1, 0);
        assertFalse(backlog.getUnPrioritisedStories().contains(story1));
        assertTrue(backlog.getStories().contains(story1));

        assertFalse(backlog.getAllStories().contains(story3));
        backlog.modifyStory(story3, 2);
        assertTrue(backlog.getStories().contains(story3));
        assertFalse(backlog.getUnPrioritisedStories().contains(story3));

        assertEquals(1, backlog.getStories().indexOf(story2));
        backlog.modifyStory(story2, 3);
        assertEquals(2, backlog.getStories().indexOf(story2));
    }

    @Test
    public void testAddStory() throws Exception {
        // try to add an existing story
        assertTrue(backlog.getAllStories().contains(story2));
        backlog.addStory(story2, null);
        assertFalse(backlog.getUnPrioritisedStories().contains(story2));

        // add a story without a priority
        assertFalse(backlog.getAllStories().contains(story3));
        backlog.addStory(story3, null);
        backlog.getUnPrioritisedStories().contains(story3);

        // add a story with a priority which is already taken by another story
        assertFalse(backlog.getAllStories().contains(story4));
        backlog.addStory(story4, 0);
        assertEquals(0, backlog.getStories().indexOf(story4));

        // add a story with a priority greater than the current number of prioritized stories
        int priority = 7;
        assertTrue(priority > backlog.getStories().size());
        backlog.addStory(story5, 7);
        assertEquals(backlog.getStories().size() - 1, backlog.getStories().indexOf(story5));

    }

    @Test(expected = Exception.class)
    public void testAddStoryException() throws Exception {
        // try add a story will a priority less than 0
        backlog.addStory(story3, -1);
    }

    @Test
    public void testModifyStoryPriority() throws Exception {
        // try to add a new story
        assertFalse(backlog.getAllStories().contains(story3));
        backlog.modifyStoryPriority(story3, 3);
        assertFalse(backlog.getAllStories().contains(story3));

        // set a prioritized stories priority to null
        assertEquals(0, backlog.getStories().indexOf(story2));
        backlog.modifyStoryPriority(story2, null);
        assertFalse(backlog.getStories().contains(story2));
        assertTrue(backlog.getUnPrioritisedStories().contains(story2));

        // set a stories priority to its current priority
        backlog.addStory(story4, 0);
        backlog.addStory(story3, 1);
        assertEquals(1, backlog.getStories().indexOf(story3));
        backlog.modifyStoryPriority(story3, 1);
        assertEquals(1, backlog.getStories().indexOf(story3));

        // set a stories priority which is already taken by another story
        assertEquals(1, backlog.getStories().indexOf(story3));
        backlog.modifyStoryPriority(story3, 0);
        assertEquals(0, backlog.getStories().indexOf(story3));
        assertEquals(1, backlog.getStories().indexOf(story4));

        // set a stories priority with a priority greater than the current number of prioritized stories
        int priority = 7;
        assertTrue(priority > backlog.getStories().size());
        backlog.modifyStoryPriority(story3, priority);
        assertEquals(1, backlog.getStories().indexOf(story3));

        // prioritises an unPrioritized story
        assertTrue(backlog.getUnPrioritisedStories().contains(story1));
        backlog.modifyStoryPriority(story1, 2);
        assertFalse(backlog.getUnPrioritisedStories().contains(story1));
        assertTrue(backlog.getStories().contains(story1));
    }

    @Test(expected = Exception.class)
    public void testModifyStoryPriorityException() throws Exception {
        // try add a story will a priority less than 0
        backlog.modifyStoryPriority(story2, -1);
    }

    @Test
    public void testGetStoryPriority() throws Exception {
        // get the priority of an un prioritised story
        assertTrue(backlog.getUnPrioritisedStories().contains(story1));
        assertFalse(backlog.getStories().contains(story1));
        assertTrue(backlog.getStoryPriority(story1) == null);

        // get the priority of a story not allocated to the backlog
        assertFalse(backlog.getUnPrioritisedStories().contains(story3));
        assertFalse(backlog.getStories().contains(story3));
        assertTrue(backlog.getStoryPriority(story3) == null);

        // get the priority of a prioritized story
        assertFalse(backlog.getUnPrioritisedStories().contains(story2));
        assertTrue(backlog.getStories().contains(story2));
        assertEquals(0, (int) backlog.getStoryPriority(story2));
    }

    @Test
    public void testRemoveStory() throws Exception {
        // remove a prioritized story
        assertTrue(backlog.getStories().contains(story2));
        backlog.removeStory(story2);
        assertFalse(backlog.getStories().contains(story2));

        // remove a unPrioritized story
        assertTrue(backlog.getUnPrioritisedStories().contains(story1));
        backlog.removeStory(story1);
        assertFalse(backlog.getUnPrioritisedStories().contains(story1));

        // remove a story which does not exist in the backlog
        assertFalse(backlog.getUnPrioritisedStories().contains(story3));
        assertFalse(backlog.getStories().contains(story3));
        backlog.removeStory(story3);
        assertFalse(backlog.getUnPrioritisedStories().contains(story3));
        assertFalse(backlog.getStories().contains(story3));
    }
}
