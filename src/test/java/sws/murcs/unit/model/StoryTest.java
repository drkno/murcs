package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.AcceptanceCondition;
import sws.murcs.model.Story;

import static org.junit.Assert.*;

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
    public void testManipulateAcceptanceCriteria(){
        assertTrue("A new story should have no conditions", story.getAcceptanceCriteria().size() == 0);

        AcceptanceCondition first = new AcceptanceCondition();
        first.setCondition("I should be completed!");
        story.addAcceptanceCondition(first);
        assertEquals("The first condition on the story should be 'I should be completed'",
                story.getAcceptanceCriteria().get(0),
                first);

        AcceptanceCondition second = new AcceptanceCondition();
        second.setCondition("I'm the second");
        story.addAcceptanceCondition(second);
        assertEquals("The second condition on the story should be 'I'm the second'",
                story.getAcceptanceCriteria().get(1),
                second);

        story.removeAcceptanceCriteria(first);
        assertEquals("The first condition should be \"I'm the second\"",
                story.getAcceptanceCriteria().get(0),
                second);

        story.removeAcceptanceCriteria(second);
        assertTrue("There should be no acceptance criteria", story.getAcceptanceCriteria().size() == 0);
    }

    @Test
    public void testReorderAcceptanceConditions(){
        for (int i = 0; i < 10; i++){
            AcceptanceCondition condition = new AcceptanceCondition();
            condition.setCondition("Condition " + i);
            story.addAcceptanceCondition(condition);
        }

        AcceptanceCondition condition = story.getAcceptanceCriteria().get(0);

        for (int i = 0; i < 10; i++){
            story.repositionCondition(condition, i);
            int actualIndex = story.getAcceptanceCriteria().indexOf(condition);

            assertEquals("The story should be at position " + i, i, actualIndex);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAcceptanceCriteriaUnmodifiable(){
        story.getAcceptanceCriteria().add(new AcceptanceCondition());
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
