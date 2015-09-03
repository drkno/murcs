package sws.murcs.unit.model;

import org.junit.*;
import sws.murcs.debug.sampledata.ReleaseGenerator;
import sws.murcs.debug.sampledata.TeamGenerator;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.exceptions.MultipleSprintsException;
import sws.murcs.exceptions.NotReadyException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.*;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

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
        UndoRedoManager.get().setDisabled(true);
        PersistenceManager.setCurrent(new PersistenceManager(new FilePersistenceLoader()));
    }

    @AfterClass
    public static void afterClass() {
        PersistenceManager.setCurrent(null);
    }

    @Before
    public void setup() throws Exception{
        sprint = new Sprint();
        sprint.setShortName("name");
        sprint.setLongName("long name");
        sprint.setDescription("description");
        sprint.setStartDate(LocalDate.now());
        sprint.setEndDate(LocalDate.now().plus(10, ChronoUnit.DAYS));

        Team team = null;
        while (team == null) {
            team = (new TeamGenerator()).generate();
        }
        sprint.setTeam(team);

        Release release = null;
        while (release == null) {
             release = (new ReleaseGenerator()).generate();
        }
        release.setReleaseDate(sprint.getEndDate().plus(1, ChronoUnit.DAYS));
        sprint.setAssociatedRelease(release);

        Organisation organisation = new Organisation();
        PersistenceManager.getCurrent().setCurrentModel(organisation);
        organisation.add(sprint);
        organisation.add(release);
        organisation.add(team);
    }

    @After
    public void tearDown() throws Exception {
        PersistenceManager.getCurrent().setCurrentModel(null);
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
        assertTrue(sprint.getStories().size() == 1);
    }

    @Test(expected = MultipleSprintsException.class)
    public void testAddStoryTwice() throws Exception {
        Story story = new Story();
        story.setStoryState(Story.StoryState.Ready);
        sprint.addStory(story);
        sprint.addStory(story);
    }

    @Test
    public void testRemoveStory() throws Exception {
        Story story = new Story();
        story.setStoryState(Story.StoryState.Ready);
        sprint.addStory(story);
        sprint.removeStory(story);
        assertTrue(sprint.getStories().isEmpty());
    }

    @Test(expected = MultipleSprintsException.class)
    public void storyInMultipleSprints() throws Exception {
        Sprint sprint2 = new Sprint();
        sprint2.setShortName("name1");
        sprint2.setLongName("long name");
        sprint2.setDescription("description");
        sprint2.setStartDate(LocalDate.now());
        sprint2.setEndDate(LocalDate.now().plus(10, ChronoUnit.DAYS));

        Team team = null;
        while (team == null) {
            team = (new TeamGenerator()).generate();
        }
        sprint.setTeam(team);

        Release release = null;
        while (release == null) {
            release = (new ReleaseGenerator()).generate();
        }
        release.setReleaseDate(sprint.getEndDate().plus(1, ChronoUnit.DAYS));
        sprint.setAssociatedRelease(release);

        Story story = new Story();
        story.setShortName("blah");
        Task task = new Task();
        task.setName("hello world");
        task.setDescription("blah");
        task.setCurrentEstimate(10);
        task.setState(TaskState.InProgress);
        story.addTask(task);
        story.setStoryState(Story.StoryState.Ready);
        PersistenceManager.getCurrent().getCurrentModel().add(sprint2);

        sprint.addStory(story);
        sprint2.addStory(story);
    }
}
