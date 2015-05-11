package sws.murcs.debug.sampledata;

import sws.murcs.model.Model;
import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Release;
import sws.murcs.model.Skill;
import sws.murcs.model.Story;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;

import java.util.ArrayList;

/**
 * Generates random RelationalModels.
 */
public class RelationalModelGenerator implements Generator<RelationalModel> {

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
        Low,
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
     * The stress level.
     */
    private Stress stress;

    /**
     * Instantiates a new random RelationalModel generator.
     * @param stressLevel the stress level to use. Stress level determines the amount of data generated.
     */
    public RelationalModelGenerator(final Stress stressLevel) {
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

        workAllocationGenerator = new WorkAllocationGenerator();
    }

    /**
     * Generates models of a particular type, given a generator
     * and a min and max number of items to generate.
     * @param generator The generator to tbe used.
     * @param min The min number of items to make.
     * @param max The max number of items to make.
     * @return The list of new model items.
     */
    private ArrayList<Model> generateItems(final Generator<? extends Model> generator, final int min, final int max) {
        ArrayList<Model> items = new ArrayList<>();

        int count = NameGenerator.random(min, max);

        for (int i = 0; i < count; i++){
            Model g = generator.generate();
            if (!items.stream().filter(g::equals).findAny().isPresent()) {
                items.add(g);
            }
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
     * @return The max
     */
    private int getMax(final Stress stressLevel, final int lowMax, final int mediumMax, final int highMax) {
        switch (stressLevel)     {
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
    public final RelationalModel generate() {
        try {
            RelationalModel model = new RelationalModel();

            int min = getMin(stress, SkillGenerator.LOW_STRESS_MIN, SkillGenerator.MEDIUM_STRESS_MIN,
                    SkillGenerator.HIGH_STRESS_MIN);
            int max = getMax(stress, SkillGenerator.LOW_STRESS_MAX, SkillGenerator.MEDIUM_STRESS_MAX,
                    SkillGenerator.HIGH_STRESS_MAX);
            ArrayList<Skill> skills = new ArrayList<>();
            for (Model m : generateItems(skillGenerator, min, max)) {
                skills.add((Skill) m);
            }

            personGenerator.setSkillPool(skills);
            ArrayList<Person> people = new ArrayList<>();
            min = getMin(stress, PersonGenerator.LOW_STRESS_MIN, PersonGenerator.MEDIUM_STRESS_MIN,
                    PersonGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, PersonGenerator.LOW_STRESS_MAX, PersonGenerator.MEDIUM_STRESS_MAX,
                    PersonGenerator.HIGH_STRESS_MAX);
            for (Model m : generateItems(personGenerator, min, max)) {
                people.add((Person) m);
            }

            teamGenerator.setPersonPool(people);
            ArrayList<Team> teams = new ArrayList<>();
            min = getMin(stress, TeamGenerator.LOW_STRESS_MIN, TeamGenerator.MEDIUM_STRESS_MIN,
                    TeamGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, TeamGenerator.LOW_STRESS_MAX, TeamGenerator.MEDIUM_STRESS_MAX,
                    TeamGenerator.HIGH_STRESS_MAX);
            for (Model m : generateItems(teamGenerator, min, max)) {
                teams.add((Team) m);
            }

            projectGenerator.setTeamPool(teams);
            ArrayList<Project> projects = new ArrayList<>();
            min = getMin(stress, ProjectGenerator.LOW_STRESS_MIN, ProjectGenerator.MEDIUM_STRESS_MIN,
                    ProjectGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, ProjectGenerator.LOW_STRESS_MAX, ProjectGenerator.MEDIUM_STRESS_MAX,
                    ProjectGenerator.HIGH_STRESS_MAX);
            for (Model m : generateItems(projectGenerator, min, max)) {
                projects.add((Project) m);
            }

            releaseGenerator.setProjectPool(projects);
            ArrayList<Release> releases = new ArrayList<>();
            min = getMin(stress, ReleaseGenerator.LOW_STRESS_MIN, ReleaseGenerator.MEDIUM_STRESS_MIN,
                    ReleaseGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, ReleaseGenerator.LOW_STRESS_MAX, ReleaseGenerator.MEDIUM_STRESS_MAX,
                    ReleaseGenerator.HIGH_STRESS_MAX);
            for (Model m : generateItems(releaseGenerator, min, max)) {
                releases.add((Release) m);
            }

            workAllocationGenerator.setProjectPool(projects);
            workAllocationGenerator.setTeamPool(teams);
            ArrayList<WorkAllocation> allocations = workAllocationGenerator.generate();

            storyGenerator.setPersonsPool(people);
            min = getMin(stress, StoryGenerator.LOW_STRESS_MIN, StoryGenerator.MEDIUM_STRESS_MIN,
                    StoryGenerator.HIGH_STRESS_MIN);
            max = getMin(stress, StoryGenerator.LOW_STRESS_MAX, StoryGenerator.MEDIUM_STRESS_MAX,
                    StoryGenerator.HIGH_STRESS_MAX);
            ArrayList<Story> stories = new ArrayList<>();
            for (Model m : generateItems(storyGenerator, min, max)) {
                stories.add((Story) m);
            }

            model.addSkills(skills);
            model.addPeople(people);
            model.addTeams(teams);
            model.addProjects(projects);
            model.addReleases(releases);
            model.addAllocations(allocations);
            model.addStories(stories);

            return model;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
