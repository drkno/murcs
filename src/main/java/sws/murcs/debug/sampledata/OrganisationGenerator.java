package sws.murcs.debug.sampledata;

import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Backlog;
import sws.murcs.model.Model;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.Release;
import sws.murcs.model.Skill;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates random Organisations.
 */
public class OrganisationGenerator implements Generator<Organisation> {

    /**
     * The various stress level the generator can produce.
     */
    public enum Stress {

        /**
         * High stress level.
         */
        High,

        /**
         * Medium stress level.
         */
        Medium,

        /**
         * Low stress level.
         */
        Low
    }

    /**
     * The project generator.
     */
    private final ProjectGenerator projectGenerator;

    /**
     * The team generator.
     */
    private final TeamGenerator teamGenerator;

    /**
     * The person generator.
     */
    private final PersonGenerator personGenerator;

    /**
     * The skills generator.
     */
    private final SkillGenerator skillGenerator;

    /**
     * The release generator.
     */
    private final ReleaseGenerator releaseGenerator;

    /**
     * The work allocator generator.
     */
    private final WorkAllocationGenerator workAllocationGenerator;

    /**
     * The story generator.
     */
    private final StoryGenerator storyGenerator;

    /**
     * The backlog generator.
     */
    private final BacklogGenerator backlogGenerator;

    /**
     * The sprint generator.
     */
    private final SprintGenerator sprintGenerator;

    /**
     * The stress level.
     */
    private Stress stress;

    /**
     * Set if the last generation had an error.
     */
    private boolean lastWasError;

    /**
     * Put numbers next to the short names.
     */
    private static boolean numbering = false;

    /**
     * Set numbering on the model objects (short names).
     * @param isNumbering is numbering on
     */
    public static final void isNumbering(final boolean isNumbering) {
        numbering = isNumbering;
    }

    /**
     * The last generation of an Organisation incurred an error.
     * @return true if an error occurred, false otherwise.
     */
    public final boolean lastGenerationHadError() {
        return lastWasError;
    }

    /**
     * Instantiates a new random Organisation generator.
     * @param stressLevel the stress level to use. Stress level determines the amount of data generated.
     */
    public OrganisationGenerator(final Stress stressLevel) {
        this.stress = stressLevel;

        skillGenerator = new SkillGenerator();
        personGenerator = new PersonGenerator();
        personGenerator.setSkillGenerator(skillGenerator);

        teamGenerator = new TeamGenerator();
        teamGenerator.setPersonGenerator(personGenerator);

        projectGenerator = new ProjectGenerator();
        projectGenerator.setTeamGenerator(teamGenerator);

        releaseGenerator = new ReleaseGenerator();

        storyGenerator = new StoryGenerator();

        backlogGenerator = new BacklogGenerator();
        backlogGenerator.setStoryGenerator(storyGenerator);

        workAllocationGenerator = new WorkAllocationGenerator();

        sprintGenerator = new SprintGenerator();
    }

    /**
     * Generates models of a particular type, given a generator
     * and a min and max number of items to generate.
     * @param generator The generator to tbe used.
     * @param min The min number of items to make.
     * @param max The max number of items to make.
     * @return The list of new model items.
     */
    private List<Model> generateItems(final Generator<? extends Model> generator, final int min, final int max) {
        List<Model> items = new ArrayList<>();

        int count = GenerationHelper.random(min, max);

        for (int i = 0; i < count; i++) {
            Model g = generator.generate();
            if (numbering) {
                try {
                    g.setShortName(g.getShortName() + " (" + i + ")");
                } catch (CustomException e) {
                    //never here... EVER.
                    ErrorReporter.get().reportErrorSecretly(e, "OrganisationGenerator: setting short name failed");
                }
            }
            while (items.stream().filter(g::equals).findAny().isPresent()) {
                try {
                    g.setShortName(g.getShortName() + " " + NameGenerator.randomName());
                } catch (CustomException e) {
                    ErrorReporter.get().reportErrorSecretly(e, "OrganisationGenerator: setting short name failed");
                }
            }
            items.add(g);
        }
        return items;
    }

    /**
     * Calculates a minimum given a stress level.
     * @param stressLevel The stress
     * @param lowMin The low min
     * @param mediumMin The medium min
     * @param highMin The high min
     * @return The min
     */
    private int getMin(final Stress stressLevel, final int lowMin, final int mediumMin, final int highMin) {
        switch (stressLevel) {
            case Low:
                return lowMin;
            case Medium:
                return mediumMin;
            case High:
                return highMin;
            default:
                break;
        }
        return lowMin;
    }

    /**
     * Gets a max clamp for a given stress level.
     * @param stressLevel The stress
     * @param lowMax The low max
     * @param mediumMax The medium max
     * @param highMax The high max
     * @return The maximum clamp for the given stress level.
     */
    private int getMax(final Stress stressLevel, final int lowMax, final int mediumMax, final int highMax) {
        switch (stressLevel) {
            case Low:
                return lowMax;
            case Medium:
                return mediumMax;
            case High:
                return highMax;
            default:
                break;
        }
        return lowMax;
    }

    @Override
    public final Organisation generate() {
        try {
            Organisation model = new Organisation();
            model.setIsUsingGeneratedData(true);

            int min = getMin(stress, SkillGenerator.LOW_STRESS_MIN, SkillGenerator.MEDIUM_STRESS_MIN,
                    SkillGenerator.HIGH_STRESS_MIN);
            int max = getMax(stress, SkillGenerator.LOW_STRESS_MAX, SkillGenerator.MEDIUM_STRESS_MAX,
                    SkillGenerator.HIGH_STRESS_MAX);
            List<Skill> skills = generateItems(skillGenerator, min, max)
                    .stream().map(m -> (Skill) m).collect(Collectors.toList());

            personGenerator.setSkillPool(skills);
            List<Person> people = new ArrayList<>();
            min = getMin(stress, PersonGenerator.LOW_STRESS_MIN, PersonGenerator.MEDIUM_STRESS_MIN,
                    PersonGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, PersonGenerator.LOW_STRESS_MAX, PersonGenerator.MEDIUM_STRESS_MAX,
                    PersonGenerator.HIGH_STRESS_MAX);
            people.addAll(generateItems(personGenerator, min, max)
                    .stream().map(m -> (Person) m).collect(Collectors.toList()));
            Person dionVader = people.get(GenerationHelper.random(people.size()));
            try {
                dionVader.setShortName("Dion Vader");
                dionVader.setLongName("DA DA DA Dun daDAA Dun DaDAAA (to the darth vader theme)");
                dionVader.setDescription("He finds your lack of SENG disturbing");

                Skill allOfThem = new Skill();
                allOfThem.setShortName("All of them");
                allOfThem.setLongName("Really. Every last one. Even SLEEPING");
                allOfThem.setDescription("Dion finds your lack of SENG disturbing...");

                dionVader.addSkill(allOfThem);
                skills.add(allOfThem);
            } catch (Exception e) {
                //Never. Ever. Dion wouldn't let it. He is the dark lord. He is all powerful.
                e.printStackTrace(); //Because checkstyle.
            }


            // deal with pass by reference issues
            List<Person> teamPeople = new ArrayList<>(people);
            teamGenerator.setPersonPool(teamPeople);
            List<Team> teams = new ArrayList<>();
            min = getMin(stress, TeamGenerator.LOW_STRESS_MIN, TeamGenerator.MEDIUM_STRESS_MIN,
                    TeamGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, TeamGenerator.LOW_STRESS_MAX, TeamGenerator.MEDIUM_STRESS_MAX,
                    TeamGenerator.HIGH_STRESS_MAX);
            teams.addAll(generateItems(teamGenerator, min, max)
                    .stream().map(m -> (Team) m).collect(Collectors.toList()));

            projectGenerator.setTeamPool(teams);
            List<Project> projects = new ArrayList<>();
            min = getMin(stress, ProjectGenerator.LOW_STRESS_MIN, ProjectGenerator.MEDIUM_STRESS_MIN,
                    ProjectGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, ProjectGenerator.LOW_STRESS_MAX, ProjectGenerator.MEDIUM_STRESS_MAX,
                    ProjectGenerator.HIGH_STRESS_MAX);
            projects.addAll(generateItems(projectGenerator, min, max)
                    .stream().map(m -> (Project) m).collect(Collectors.toList()));

            releaseGenerator.setProjectPool(projects);
            List<Release> releases = new ArrayList<>();
            min = getMin(stress, ReleaseGenerator.LOW_STRESS_MIN, ReleaseGenerator.MEDIUM_STRESS_MIN,
                    ReleaseGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, ReleaseGenerator.LOW_STRESS_MAX, ReleaseGenerator.MEDIUM_STRESS_MAX,
                    ReleaseGenerator.HIGH_STRESS_MAX);
            releases.addAll(generateItems(releaseGenerator, min, max)
                    .stream().map(m -> (Release) m).collect(Collectors.toList()));

            workAllocationGenerator.setProjectPool(projects);
            workAllocationGenerator.setTeamPool(teams);
            List<WorkAllocation> allocations = workAllocationGenerator.generate();

            storyGenerator.setPersonsPool(people);
            min = getMin(stress, StoryGenerator.LOW_STRESS_MIN, StoryGenerator.MEDIUM_STRESS_MIN,
                    StoryGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, StoryGenerator.LOW_STRESS_MAX, StoryGenerator.MEDIUM_STRESS_MAX,
                    StoryGenerator.HIGH_STRESS_MAX);
            List<Story> stories = generateItems(storyGenerator, min, max)
                    .stream().map(m -> (Story) m).collect(Collectors.toList());
            storyGenerator.addDependencies(stories, max, min);

            backlogGenerator.setUnsafeStories(stories);
            backlogGenerator.setPersonsPool(people);
            min = getMin(stress, BacklogGenerator.LOW_STRESS_MIN, BacklogGenerator.MEDIUM_STRESS_MIN,
                    BacklogGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, BacklogGenerator.LOW_STRESS_MAX, BacklogGenerator.MEDIUM_STRESS_MAX,
                    BacklogGenerator.HIGH_STRESS_MAX);
            List<Backlog> backlogs = generateItems(backlogGenerator, min, max)
                    .stream().map(m -> (Backlog) m).collect(Collectors.toList());

            sprintGenerator.setReleasePool(releases);
            sprintGenerator.setBacklogPool(backlogs);
            sprintGenerator.setTeamPool(teams);
            min = getMin(stress, SprintGenerator.LOW_STRESS_MIN, SprintGenerator.MEDIUM_STRESS_MIN,
                    SprintGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, SprintGenerator.LOW_STRESS_MAX, SprintGenerator.MEDIUM_STRESS_MAX,
                    SprintGenerator.HIGH_STRESS_MAX);
            List<Sprint> sprints = generateItems(sprintGenerator, min, max).stream()
                    .map(m -> (Sprint) m).collect(Collectors.toList());

            model.addCollection(skills);
            model.addCollection(people);
            model.addCollection(teams);
            model.addCollection(projects);
            model.addCollection(releases);
            model.addCollection(stories);
            model.addCollection(backlogs);
            model.addAllocations(allocations);
            model.addCollection(sprints);

            lastWasError = false;
            return model;
        }
        catch (Exception e) {
            ErrorReporter.get().reportErrorSecretly(e, "OrganisationGenerator: generating organisation failed");
            lastWasError = true;
        }
        return null;
    }
}
