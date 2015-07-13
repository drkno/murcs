package sws.murcs.debug.sampledata;

import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.CyclicDependencyException;
import sws.murcs.model.AcceptanceCondition;
import sws.murcs.model.Person;
import sws.murcs.model.Story;

import java.util.ArrayList;
import java.util.List;

/**
 * A generator for stories.
 */
public class StoryGenerator implements Generator<Story> {

    /**
     * The minimum number of Acceptance Conditions a story can have.
     */
    public static final int MIN_ACS = 2;

    /**
     * The maximum number of Acceptance Conditions a story can have.
     */
    public static final int MAX_ACS = 10;

    /**
     * The max number of stories to generate at low stress.
     */
    public static final int LOW_STRESS_MAX = 5;

    /**
     * The min number of stories to generate at low stress.
     */
    public static final int LOW_STRESS_MIN = 1;

    /**
     * The max number of stories to generate at medium stress.
     */
    public static final int MEDIUM_STRESS_MAX = 20;

    /**
     * The min number of stories to generate at medium stress.
     */
    public static final int MEDIUM_STRESS_MIN = 10;

    /**
     * The max number of stories to generate at high stress.
     */
    public static final int HIGH_STRESS_MAX = 100;

    /**
     * The min number of stories to generate at high stress.
     */
    public static final int HIGH_STRESS_MIN = 50;

    /**
     * A list of story names for use in generation.
     */
    private String[] storyNames = {
            "A story",
            "Haydon's life",
            "Stranger Of Next Year",
            "Angel Of The West",
            "Priests Without Sin",
            "Wives With Pride",
            "Strangers And Foes",
            "Thieves And Invaders",
            "Creation Of The Day",
            "Love Of Fortune",
            "Ending The Champions",
            "Meeting At Myself",
            "Sage Of The North",
            "Hero Without Glory",
            "Turtles Of The Day",
            "Officers Without Duty",
            "Swindlers And Turtles",
            "Officers And Witches",
            "Shield Of The East",
            "Source Of Dread",
            "Smile At The King",
            "Welcome To The Mist",
            "Rebel Of Desire",
            "Savior Of Perfection",
            "Spies Of Destruction",
            "Serpents Of Stone",
            "Priests And Butchers",
            "Horses And Gods",
            "Argument Of Tomorrow",
            "Strife Of The Ocean",
            "Avoiding Eternity",
            "Separated In The Mist",
            "The Black Coffin",
            "The Crooked Stool",
            "Tree Of The Dagger",
            "Woman Without Flaws",
            "Companions Of Hope",
            "Mermen Of Stone",
            "Blacksmiths And Enchanters",
            "Foreigners And Snakes",
            "Country Without Flaws",
            "Planet Of The Eternal",
            "Wrong About History",
            "Shelter In The World",
            "Mouse Prophecy",
            "Buffoon Strategy",
            "Dog Of Parody",
            "Pig During My Travel",
            "Child And Robot",
            "Mime And Friend",
            "Farts Abroad",
            "Stunts Loves Sugar",
            "Greed Of His Laugh",
            "Amused By The Joke",
            "Cook In The River",
            "Friend With Curly Hair",
            "Visitors Of Yearning",
            "Guests In The Night",
            "Angels And Boys",
            "Roommates And Dears",
            "Body Of Romance",
            "Restoration In My Town",
            "Strange Myself",
            "Clinging To Affection",
            "Creator Of Honor",
            "Pilot Of The Orbit",
            "Hunters Of Men's Legacy",
            "Friends Of Death",
            "Girls And Children",
            "Boys And Guardians",
            "Culling Of The Dead",
            "Betrayal From Outer Space",
            "Greed Of The Machines",
            "Secrets Of New Earth",
            "Blacksmith Of History",
            "Blacksmith With Silver Hair",
            "Doctors Of Dusk",
            "Men Of History",
            "Guardians And Builders",
            "Inventors And Angels",
            "Love Of Darkness",
            "Harvest With Sins",
            "Separated By The Mountains",
            "Crying In The Night",
            "Traitor Of The World",
            "Patron Of Gold",
            "Emissaries Of Repentance",
            "Descendants With Debt",
            "Butchers And Servants",
            "Collectors And Widows",
            "Result Of Despair",
            "Dishonor Without Fear",
            "Prepare For The End",
            "Meeting At The End"
    };

    /**
     * A list of descriptions for use in generation.
     */
    private String[] descriptions = {
            "A description",
            "Lorem ipsum ect.",
            "A story",
            "Is anyone actually reading this?",
            "Stories are things you read",
            "I love scrum",
            "Sarcasm is a virtue",
            "A long description",
            "Description",
            "A story that accomplishes nothing",
            "The meaning of life is 42",
            "Monkeys like stories",
            "Implement the hyperdrive and go to the moon(that's no moon)",
            "There's just too much to describe here, I'll do it later",
            "I'm tired of describing this",
            "No Luke, I am your father",
            "We're going to need a bigger boat",
            "I'll be back",
            "Your lack of faith disturbs me",
            "What's in the box?",
            "There is no spoon"
    };

    /**
     * A pool of persons.
     */
    private List<Person> personsPool;

    /**
     * A person generator, for use in the event that we don't have a pool.
     */
    private PersonGenerator personGenerator;

    /**
     * Creates a new story generator.
     */
    public StoryGenerator() {
        this(new PersonGenerator());
    }

    /**
     * Creates a new story generator with the specified person generator.
     * @param generator The person generator
     */
    public StoryGenerator(final PersonGenerator generator) {
        this.personGenerator = generator;
    }

    /**
     * Sets the person pool.
     * @param newPersonsPool The pool of persons.
     */
    public final void setPersonsPool(final List<Person> newPersonsPool) {
        personsPool = newPersonsPool;
    }

    @Override
    public final Story generate() {
        String name = storyNames[GenerationHelper.random(storyNames.length)];
        String description = descriptions[GenerationHelper.random(descriptions.length)];

        Person creator;
        if (personsPool == null || personsPool.isEmpty()) {
            creator = personGenerator.generate();
        }
        else {
            creator = personsPool.get(GenerationHelper.random(personsPool.size()));
        }

        Story story = new Story();
        try {
            story.setShortName(name);
        } catch (Exception e) {
            //Do nothing this doesn't matter. Ever.
            ErrorReporter.get().reportErrorSecretly(e, "StoryGenerator: setting short name failed");
        }
        story.setDescription(description);
        story.setCreator(creator);

        //Generate and add the acceptance criteria
        List<AcceptanceCondition> acceptanceConditions = generateAcceptanceCriteria();
        for (AcceptanceCondition condition : acceptanceConditions) {
            story.addAcceptanceCondition(condition);
        }

        return story;
    }

    /**
     * Generates Acceptance Criteria for a story.
     * @return The acceptance criteria
     */
    private List<AcceptanceCondition> generateAcceptanceCriteria() {
        List<AcceptanceCondition> conditions = new ArrayList<>();

        int count = GenerationHelper.random(MIN_ACS, MAX_ACS);
        for (int i = 0; i < count; i++) {
            AcceptanceCondition condition = new AcceptanceCondition();
            condition.setCondition(NameGenerator.randomDescription());
            conditions.add(condition);
        }

        return conditions;
    }

    /**
     * Add dependencies between items of a story collection.
     * @param stories stories to add dependencies between.
     * @param max maximum number of dependencies to have.
     * @param min minimum number of dependencies to have.
     */
    public final void addDependencies(final List<Story> stories, final int max, final int min) {
        stories.forEach(s -> {
            int count = GenerationHelper.random(min, max);
            for (int i = 0; i < count; i++) {
                try {
                    int index = GenerationHelper.random(stories.size());
                    s.addDependency(stories.get(index));
                }
                catch (CyclicDependencyException e) {
                    // Ignore this, there is no effective way of checking for and
                    // dealing with these in an acceptable time frame.
                }
            }
        });
    }
}
