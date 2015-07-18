package sws.murcs.unit.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sws.murcs.exceptions.CyclicDependencyException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.AcceptanceCondition;
import sws.murcs.model.EstimateType;
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
    public void testManipulateAcceptanceCriteria() throws Exception{
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

        story.removeAcceptanceCondition(first);
        assertEquals("The first condition should be \"I'm the second\"",
                story.getAcceptanceCriteria().get(0),
                second);

        story.removeAcceptanceCondition(second);
        assertTrue("There should be no acceptance criteria", story.getAcceptanceCriteria().size() == 0);
    }

    @Test
    public void testResetEstimationResetsState() {
        story.setEstimate("Foo");
        story.setStoryState(Story.StoryState.Ready);

        story.setEstimate(EstimateType.NOT_ESTIMATED);
        assertEquals("Story state should have reset to 'None'", Story.StoryState.None, story.getStoryState());
    }

    @Test
    public void testRemoveLastACClearsEstimateAndStoryState() throws Exception{
        AcceptanceCondition condition = new AcceptanceCondition();
        condition.setCondition("I'm not a frog");

        story.addAcceptanceCondition(condition);

        story.setEstimate("Foo");
        story.removeAcceptanceCondition(condition);
        assertEquals("The story should have no estimates", EstimateType.NOT_ESTIMATED, story.getEstimate());

        story.addAcceptanceCondition(condition);
        story.setStoryState(Story.StoryState.Ready);
        story.removeAcceptanceCondition(condition);
        assertEquals("The story should have state 'None'", Story.StoryState.None, story.getStoryState());

        story.addAcceptanceCondition(condition);
        story.setEstimate("Foo");
        story.setStoryState(Story.StoryState.Ready);
        story.removeAcceptanceCondition(condition);
        assertEquals("The story should have no estimates", EstimateType.NOT_ESTIMATED, story.getEstimate());
        assertEquals("The story should have state 'None'", Story.StoryState.None, story.getStoryState());

    }

    @Test
    public void testReorderAcceptanceConditions() throws Exception{
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
    public void testChangeStoryState(){
        //Test that the story initially has "None" as its state
        assertEquals("Initially a story should have no state", Story.StoryState.None, story.getStoryState());

        //Ideally, you would not be able to change the story state to ready unless a number of conditions
        //are met, such as the story has ACs and is in a backlog. However, a lot of these conditions depend on
        //the state of the model as a whole, so it is not possible to test all these conditions are met here.
        //Instead, this is done on the GUI side of things, which makes testing a lot more difficult.
        story.setStoryState(Story.StoryState.Ready);

        assertEquals("Story state should be ready!", Story.StoryState.Ready, story.getStoryState());
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

    @Test(expected = CyclicDependencyException.class)
    public void immediateCyclicDependencyExceptionTest() throws Exception {
        story.addDependency(story);
    }

    @Test(expected = CyclicDependencyException.class)
    public void intermediateCyclicDependencyExceptionTest() throws Exception {
        story2.addDependency(story);
        story.addDependency(story2);
    }

    @Test(expected = CyclicDependencyException.class)
    public void longCyclicDependencyExceptionTest() throws Exception {
        story3.addDependency(story2);
        story2.addDependency(story);
        story.addDependency(story3);
    }

    @Test
    public void immediateDependencyTest() throws Exception {
        story.addDependency(story2);
        assertTrue("Story should have been added as a dependency but was not.", story.getDependencies().contains(story2));
    }

    @Test
    public void immediateMultipleDependencyTest() throws Exception {
        story.addDependency(story2);
        story.addDependency(story3);
        assertTrue("Story should have been added as a dependency but was not.", story.getDependencies().contains(story2));
        assertTrue("Story should have been added as a dependency but was not.", story.getDependencies().contains(story3));
    }

    @Test
    public void immediateAddDuplicateDependencyTest() throws Exception {
        story.addDependency(story2);
        story.addDependency(story2);
        assertTrue("Story should have been added as a dependency but was not.", story.getDependencies().contains(story2));
        assertEquals("Story was added multiple times when it should not have been.", story.getDependencies().size(), 1);
    }

    @Test
    public void transitiveDependencyTest() throws Exception {
        story2.addDependency(story3);
        story.addDependency(story2);
    }

    @Test
    public void removeDependencyTest() throws Exception {
        story.addDependency(story2);
        story.addDependency(story3);

        story.removeDependency(story2);
        assertFalse("Story was not removed when it should have been.", story.getDependencies().contains(story2));
        story.removeDependency(story3);
        assertFalse("Story was not removed when it should have been.", story.getDependencies().contains(story3));
    }
}
