package sws.murcs.unit.model.organisation;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sws.murcs.debug.sampledata.OrganisationGenerator;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Backlog;
import sws.murcs.model.Model;
import sws.murcs.model.Organisation;
import sws.murcs.model.Story;
import sws.murcs.model.helpers.UsageHelper;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.model.persistence.loaders.FilePersistenceLoader;

import java.util.ArrayList;
import java.util.List;

public class OrganisationStoryTest {
    private static OrganisationGenerator generator;
    private Organisation model;

    @BeforeClass
    public static void classSetup() {
        generator = new OrganisationGenerator(OrganisationGenerator.Stress.Medium);
        UndoRedoManager.setDisabled(true);
        if (PersistenceManager.getCurrent() == null) {
            PersistenceManager.setCurrent(new PersistenceManager(new FilePersistenceLoader()));
        }
    }

    @AfterClass
    public static void classTearDown() {
        UndoRedoManager.setDisabled(false);
        PersistenceManager.getCurrent().setCurrentModel(null);
    }

    /**
     * Generates a organisation, and sets it to the currently in use
     * model in the current persistence manager instance.
     * @throws NullPointerException if no persistence manager exists.
     * @return a new organisation.
     */
    private static Organisation getNeworganisation() {
        PersistenceManager.getCurrent().setCurrentModel(null);
        Organisation model = generator.generate();
        PersistenceManager.getCurrent().setCurrentModel(model);
        return model;
    }

    @Before
    public void setup() throws Exception {
        model = getNeworganisation();
    }

    @Test
    public void testGetStoriesNotNullOrEmpty() throws Exception {
        Organisation model = getNeworganisation();
        Story story = new Story();
        story.setShortName("testing1234");
        model.add(story);
        List<Story> stories = model.getStories();

        Assert.assertNotNull("getPrioritisedStories() should return stories but is null.", stories);
        Assert.assertNotEquals("getPrioritisedStories() should return stories but is empty.", 0, stories.size());
    }

    @Test
    public void testGetStoriesStoryRemoved() throws Exception {
        Story removedStory = new Story();
        removedStory.setShortName("testing1234");
        model.add(removedStory);
        List<Story> stories = model.getStories();
        int size = stories.size();
        model.remove(removedStory);
        stories = model.getStories();

        Assert.assertFalse("Stories should not contain the removed story.", stories.contains(removedStory));
        Assert.assertNotEquals("Stories should not be the same size as before removing a story.", size, stories.size());
    }

    @Test
    public void testGetStoriesAdded() throws Exception {
        Story story = new Story();
        story.setShortName("testing1234");
        model.add(story);
        List<Story> stories = model.getStories();
        Story storyToAdd = stories.get(0);
        model.remove(storyToAdd);
        stories = model.getStories();
        int size = stories.size();

        model.add(storyToAdd);

        Assert.assertTrue("Stories should contain the added story.", stories.contains(storyToAdd));
        Assert.assertNotEquals("Stories should not be the same size as before adding a story.", size, stories.size());
    }

    @Test(expected = InvalidParameterException.class)
    public void testStoriesNoShortNameAdded() throws Exception {
        Story story = new Story();
        model.add(story);
    }

    @Test
    public void testStoriesNoShortNameNotAddedRemoved() throws Exception {
        List<Story> stories = model.getStories();
        int size = stories.size();
        Story story = new Story();
        model.remove(story);
        Assert.assertEquals("Removing story that isn't in the model should not change the stories collection.", size, stories.size());
    }

    @Test(expected = DuplicateObjectException.class)
    public void testStoriesDuplicateAdded() throws Exception {
        Story story = new Story();
        story.setShortName("testing1234");
        model.add(story);
        model.add(story);
    }

    @Test
    public void testGetStoriesNoDuplicates() throws Exception {
        List<Story> stories = model.getStories();

        List<Story> storyDuplicates = new ArrayList<>();
        stories.stream().filter(story -> !storyDuplicates.add(story)).forEach(story -> {
            Assert.fail("There cannot be duplicate stories returned by getPrioritisedStories().");
        });
    }

    @Test
    public void testStoryExists() throws Exception {
        Story story = new Story();
        story.setShortName("testing1234");
        model.add(story);
        List<Story> stories = model.getStories();
        Assert.assertTrue("Story exists but was not found.", UsageHelper.exists(stories.get(0)));
    }

    @Test
    public void testStoryDoesNotExist() throws Exception {
        Story story = new Story();
        story.setShortName("testing1234");
        Assert.assertFalse("Story exists when it should not.", UsageHelper.exists(story));
    }

    @Test
    public void testStoryFindUsagesDoesNotExist() throws Exception {
        Story story = new Story();
        story.setShortName("testing1234");
        List<Model> usages = UsageHelper.findUsages(story);

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertEquals("Usages were found for story not in model.", 0, usages.size());
    }

    @Test
    public void testStoryFindUsages() throws Exception {
        Story story = new Story();
        story.setShortName("testing1234");
        model.add(story);
        List<Story> stories = model.getStories();
        List<Backlog> backlogs = model.getBacklogs();
        try {
            backlogs.get(0).addStory(stories.get(0), 0);
        }
        catch (Exception e) {
            // ignore, we just want to ensure story is attached to a backlog
        }
        List<Model> usages = UsageHelper.findUsages(stories.get(0));

        Assert.assertNotNull("The returned usages was null.", usages);
        Assert.assertNotEquals("Usages were not found for story.", 0, usages.size());
        Assert.assertTrue("Item should be in use.", UsageHelper.inUse(stories.get(0)));
    }
}
