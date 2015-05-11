package sws.murcs.debug.sampledata;

import sws.murcs.model.Person;
import sws.murcs.model.Story;

import java.util.List;

/**
 * A generator for stories.
 */
public class StoryGenerator implements Generator<Story> {
    public static final int LOW_STRESS_MAX = 3;
    public static final int LOW_STRESS_MIN = 1;

    public static final int MEDIUM_STRESS_MAX = 10;
    public static final int MEDIUM_STRESS_MIN = 3;

    public static final int HIGH_STRESS_MAX = 20;
    public static final int HIGH_STRESS_MIN = 10;

    /**
     * A list of story names for use in generation
     */
    private String[] storyNames = {
            "A story",
            "Haydons life",
    };

    /**
     * A list of descriptions for use in generation
     */
    private String[] descriptions = {
            "A description",
            "Lorem ipsum ect."
    };

    /**
     * A pool of persons.
     */
    private List<Person> personsPool;

    /**
     * A person generator, for use in the event that we don't have a pool
     */
    private PersonGenerator personGenerator;

    /**
     * Creates a new story generator
     */
    public StoryGenerator(){
        this(new PersonGenerator());
    }

    /**
     * Creates a new story generator with the specified person generator
     * @param personGenerator The person generator
     */
    public StoryGenerator(PersonGenerator personGenerator){
        this.personGenerator = personGenerator;
    }

    /**
     * Sets the person pool.
     * @param newPersonsPool The pool of persons.
     */
    public void setPersonsPool(final List<Person> newPersonsPool) {
        personsPool = newPersonsPool;
    }

    @Override
    public Story generate() {
        String name = storyNames[NameGenerator.random(storyNames.length)];
        String description = descriptions[NameGenerator.random(descriptions.length)];

        Person creator = personsPool == null ?
                personGenerator.generate() :
                personsPool.get(NameGenerator.random(personsPool.size()));

        Story story = new Story();
        try {
            story.setShortName(name);
        }catch (Exception e){
            //Do nothing this doesn't matter. Ever.
        }
        story.setDescription(description);
        story.setCreator(creator);

        return story;
    }
}
