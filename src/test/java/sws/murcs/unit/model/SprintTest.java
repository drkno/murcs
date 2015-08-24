package sws.murcs.unit.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.debug.sampledata.ReleaseGenerator;
import sws.murcs.debug.sampledata.TeamGenerator;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.exceptions.NotReadyException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Release;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Team;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the sprint class lol
 */
public class SprintTest {
    private Sprint sprint;

    @BeforeClass
    public static void beforeClass() {
        UndoRedoManager.setDisabled(true);
    }

    @Before
    public void setup() throws Exception{
        sprint = new Sprint();
        sprint.setShortName("name");
        sprint.setLongName("long name");
        sprint.setDescription("description");
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plus(10, ChronoUnit.DAYS));
        sprint.setTeam((new TeamGenerator()).generate());

        Release release = (new ReleaseGenerator()).generate();
        release.setReleaseDate(sprint.getEndDate().plus(1, ChronoUnit.DAYS));
        sprint.setAssociatedRelease(release);
    }


    @Test
    public void testModifyName() throws Exception{
        sprint.setShortName("new name");
        assertEquals("Short name should be new name!", "new name", sprint.getShortName());
    }

    @Test
    public void testModifyLongName() throws Exception{
        sprint.setLongName("new long name");
        assertEquals("Long name should be new long name!", "new long name", sprint.getLongName());
    }

    @Test
    public void testModifyDescription() throws Exception {
        sprint.setDescription("new description");
        assertEquals("Description should be new description!", "new description", sprint.getDescription());
    }

    @Test
    public void testModifyTeam() throws Exception {
        Team team = (new TeamGenerator()).generate();
        sprint.setTeam(team);

        assertEquals("Team should be team", team, sprint.getTeam());
    }

    @Test()
    public void testModifyRelease() throws Exception {
        Release release = (new ReleaseGenerator()).generate();
        release.setReleaseDate(sprint.getEndDate().plus(10, ChronoUnit.DAYS));
        sprint.setAssociatedRelease(release);

        assertEquals("Release should be release", release, sprint.getAssociatedRelease());
    }

    @Test(expected = InvalidParameterException.class)
    public void testInvalidRelease() throws Exception {
        Release release = (new ReleaseGenerator()).generate();
        release.setReleaseDate(sprint.getStartDate().plus(1, ChronoUnit.DAYS));

        sprint.setAssociatedRelease(release);
    }

    @Test(expected = InvalidParameterException.class)
    public void testInvalidStartDate() throws Exception {
        LocalDate start = sprint.getEndDate().plus(1, ChronoUnit.DAYS);
        sprint.setStartDate(start);
    }

    @Test(expected = InvalidParameterException.class)
    public void testInvalidEndDate1() throws Exception {
        LocalDate end = sprint.getStartDate().minus(1, ChronoUnit.DAYS);
        sprint.setEndDate(end);
    }

    @Test(expected = InvalidParameterException.class)
    public void testInvalidEndDate2() throws Exception {
        LocalDate end = sprint.getAssociatedRelease().getReleaseDate().plus(10, ChronoUnit.DAYS);
        sprint.setEndDate(end);
    }

    @Test(expected = NotReadyException.class)
    public void testAddStoryNotReady() throws Exception {
        Story story = new Story();
        story.setStoryState(Story.StoryState.None);
        sprint.addStory(story);
    }

    @Test
    public void testAddStoryReady() throws Exception {
        Story story = new Story();
        story.setStoryState(Story.StoryState.Ready);
        sprint.addStory(story);
        // Check that you cannot add the same story twice.
        sprint.addStory(story);
        assertTrue(sprint.getStories().size() == 1);
    }

    @Test
    public void testRemoveStory() throws Exception {
        Story story = new Story();
        story.setStoryState(Story.StoryState.Ready);
        sprint.addStory(story);
        sprint.removeStory(story);
        assertTrue(sprint.getStories().isEmpty());
    }
}
