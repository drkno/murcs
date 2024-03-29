package sws.murcs.debug.sampledata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.AcceptanceCondition;
import sws.murcs.model.Backlog;
import sws.murcs.model.EstimateType;
import sws.murcs.model.Person;
import sws.murcs.model.Story;

/**
 * Generates random Backlogs with stories.
 */
public class BacklogGenerator implements Generator<Backlog> {

    /**
     * The max number of projects generated when stress level is low.
     */
    public static final int LOW_STRESS_MAX = 3;

    /**
     * The min number of projects generated when stress level is low.
     */
    public static final int LOW_STRESS_MIN = 1;


    /**
     * The max number of projects generated when stress level is medium.
     */
    public static final int MEDIUM_STRESS_MAX = 6;

    /**
     * The min number of projects generated when stress level is medium.
     */
    public static final int MEDIUM_STRESS_MIN = 3;


    /**
     * The max number of projects generated when stress level is high.
     */
    public static final int HIGH_STRESS_MAX = 12;

    /**
     * The min number of projects generated when stress level is high.
     */
    public static final int HIGH_STRESS_MIN = 6;

    /**
     * Indicator used for stories that arise with duplicate names.
     */
    private static int indicator = 0;


    /**
     * A list of backlog names.
     */
    private String[] backlogNames = {
            "A backlog",
            "The Important Things",
            "The Boring Things",
            "The Fun Things",
            "Random Stuff",
            "Top Secret",
            "Confidential",
            "Abandoned Features",
            "Cool Ideas",
            "Dion's Recommendations"
    };

    /**
     * The story generator for this backlog generator.
     */
    private Generator<Story> storyGenerator;

    /**
     * A list of stories to use in this team.
     */
    private List<Story> storyPool;

    /**
     * A list of stories to use in this backlog, not copied.
     */
    private Collection<Story> unsafeStoryPool;

    /**
     * A list of people to use in this team.
     */
    private Collection<Person> personsPool;

    /**
     * Instantiates a new story generator.
     */
    public BacklogGenerator() {
        this(new StoryGenerator());
    }

    /**
     * Instantiates a new story generator.
     * @param generator story generator to use.
     */
    public BacklogGenerator(final Generator<Story> generator) {
        this.storyGenerator = generator;
        storyPool = new ArrayList<>();
        unsafeStoryPool = new ArrayList<>();
    }

    /**
     * Sets the story generator.
     * @param generator The story generator
     */
    public final void setStoryGenerator(final Generator<Story> generator) {
        this.storyGenerator = generator;
    }

    /**
     * Sets the person pool.
     * @param newPersonsPool The pool of persons.
     */
    public final void setPersonsPool(final List<Person> newPersonsPool) {
        personsPool = newPersonsPool;
    }

    /**
     * Generates the stories of a backlog.
     * @param min The min stories
     * @param max The max stories
     * @return The stories
     */
    private List<Story> generateStories(final int min, final int max) {
        List<Story> generated = new ArrayList<>();
        int storyCount = GenerationHelper.random(min, max);

        //If there are less stories than the story count, then add a bunch more stories
        if (storyCount > storyPool.size()) {
            while (storyCount != storyPool.size()) {
                Story newStory = storyGenerator.generate();
                if (!unsafeStoryPool.contains(newStory)) {
                    unsafeStoryPool.add(newStory);
                    storyPool.add(newStory);
                }
                else {
                    try {
                        newStory.setShortName(newStory.getShortName() + " (" + indicator + ")");
                        unsafeStoryPool.add(newStory);
                        storyPool.add(newStory);
                        indicator++;
                    }
                    catch (Exception e) {
                        //Suppress because they are not relevant in this context.
                        int foo = 0; //because checkstyle
                    }
                }
            }
        }

        for (int i = 0; i < storyCount; i++) {
            // Remove the story so we can't pick it again.
            // We'll put it back when we're done
            Story story = storyPool.remove(GenerationHelper.random(storyPool.size()));
            generated.add(story);
        }
        return generated;
    }

    @Override
    @SuppressWarnings("checkstyle:magicnumber")
    public final Backlog generate() {
        final int longNameMax = 10;
        final int minStories = 10;
        final int maxStories = 20;

        Backlog backlog = new Backlog();

        String shortName = GenerationHelper.randomElement(backlogNames);
        String longName = GenerationHelper.randomString(longNameMax);
        String description = NameGenerator.randomDescription();

        List<Story> stories = generateStories(minStories, maxStories);

        try {
            backlog.setShortName(shortName);
            backlog.setLongName(longName);
            backlog.setDescription(description);
            Person po = personsPool.stream().filter(person -> person.canBeRole("PO")).findAny().get();
            backlog.setAssignedPO(po);
        }
        catch (Exception e) {
            ErrorReporter.get().reportErrorSecretly(e, "BacklogGenerator: creating backlog failed");
            return null;
            // Do nothing, don't have to deal with the exception
            // if only generating test data.
        }

        int size = stories.size();
        int prioritised = size - size / 4;

        try {
            for (Story story : stories.subList(0, prioritised)) {
                if (story.getAcceptanceCriteria().size() == 0) {
                    AcceptanceCondition ac = new AcceptanceCondition();
                    ac.setCondition(GenerationHelper.randomString(300));
                    story.addAcceptanceCondition(ac);
                }
                List<String> estimates = EstimateType.Fibonacci.getEstimates();
                story.setEstimate(estimates.get(GenerationHelper.random(estimates.size())));
                backlog.addStory(story, 1);
                story.setStoryState(Story.StoryState.Ready);
                assert backlog.getPrioritisedStories().contains(story);
                backlog.addToWorkspaceStories(story);
            }
            for (Story story : stories.subList(prioritised, size)) {
                backlog.addStory(story, null);
                backlog.addToWorkspaceStories(story);
            }
        } catch (CustomException e) {
            // Will never happen!! We hope.
            e.printStackTrace();
            ErrorReporter.get().reportErrorSecretly(e, "BacklogGenerator: adding stories to backlog failed");
        }
        backlog.setEstimateType(EstimateType.values()[GenerationHelper.random(EstimateType.values().length)]);

        return backlog;
    }

    /**
     * Sets the stories that should not be used when you're generating stories for the backlogs.
     * @param unsafeStories The stories not to be used when making backlogs.
     */
    public void setUnsafeStories(final Collection<Story> unsafeStories) {
        unsafeStoryPool = unsafeStories;
    }
}
