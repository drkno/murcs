package sws.murcs.debug.sampledata;

import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Backlog;
import sws.murcs.model.Person;
import sws.murcs.model.Story;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates random Backlogs with stories.
 */
public class BacklogGenerator implements Generator<Backlog> {
    /**
     * The max number of projects generated when stress level is low.
     */
    public static final int LOW_STRESS_MAX = 5;
    /**
     * The min number of projects generated when stress level is low.
     */
    public static final int LOW_STRESS_MIN = 1;

    /**
     * The max number of projects generated when stress level is medium.
     */
    public static final int MEDIUM_STRESS_MAX = 10;
    /**
     * The min number of projects generated when stress level is medium.
     */
    public static final int MEDIUM_STRESS_MIN = 5;

    /**
     * The max number of projects generated when stress level is high.
     */
    public static final int HIGH_STRESS_MAX = 20;
    /**
     * The min number of projects generated when stress level is high.
     */
    public static final int HIGH_STRESS_MIN = 10;

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
     * A list of people to use in this team.
     */
    private List<Person> personsPool;

    /**
     * Instantiates a new story generator.
     */
    public BacklogGenerator() {
        storyGenerator = new StoryGenerator();
    }

    /**
     * Instantiates a new story generator.
     * @param generator story generator to use.
     */
    public BacklogGenerator(final Generator<Story> generator) {
        this.storyGenerator = generator;
    }

    /**
     * Sets the story generator.
     * @param generator The story generator
     */
    public final void setStoryGenerator(final Generator<Story> generator) {
        this.storyGenerator = generator;
    }

    /**
     * Sets the story pool. If null, stories will be randomly generated.
     * @param stories The story pool
     */
    public final void setStoryPool(final List<Story> stories) {
        storyPool = stories;
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
        int storyCount = NameGenerator.random(min, max);

        //If we haven't been given a pool of stories, make some up
        if (storyPool == null) {
            for (int i = 0; i < storyCount; i++) {
                Story newStory = storyGenerator.generate();
                if (!generated.stream().filter(newStory::equals).findAny().isPresent()) {
                    generated.add(newStory);
                }
            }
        }
        else {
            //If there are less stories than the story count, then reduce the story count
            if (storyCount > storyPool.size()) {
                storyCount = storyPool.size();
            }

            for (int i = 0; i < storyCount; i++) {
                // Remove the story so we can't pick it again.
                // We'll put it back when we're done
                Story story = storyPool.remove(NameGenerator.random(storyPool.size()));
                generated.add(story);
            }
        }
        return generated;
    }

    @Override
    public final Backlog generate() {
        final int longNameMax = 10;
        final int minStories = 3;
        final int maxStories = 6;

        Backlog backlog = new Backlog();

        String shortName = NameGenerator.randomElement(backlogNames);
        String longName = NameGenerator.randomString(longNameMax);
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
            e.printStackTrace();
            return null;
            // Do nothing, don't have to deal with the exception
            // if only generating test data.
        }

        int size = stories.size();
        int prioritised = size / 2;

        try {
            for (int i = 0; i < prioritised; i++) {
                backlog.addStory(stories.get(i), i);
            }
            for (Story story : stories.subList(prioritised, size)) {
                backlog.addStory(story, null);
            }
        } catch (CustomException e) {
            // Will never happen!! We hope.
        }

        return backlog;
    }
}
