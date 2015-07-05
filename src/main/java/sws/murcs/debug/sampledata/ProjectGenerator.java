package sws.murcs.debug.sampledata;

import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Project;
import sws.murcs.model.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates random projects with teams.
 */
public class ProjectGenerator implements Generator<Project> {
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
     * A list of project names.
     */
    private String[] projectNames = {
            "A project",
            "Something exciting",
            "Cold Star",
            "Unique Jazz",
            "Tasty Heart",
            "Frostbite Serious",
            "Accidentally Alarm",
            "Nervous Butter",
            "Deserted Tea",
            "Rare Albatross"
    };

    /**
     * A team generator for the project.
     */
    private Generator<Team> teamGenerator;
    /**
     * A pool of teams for adding to projects.
     */
    private List<Team> teamPool;

    /**
     * Instantiates a new project generator.
     */
    public ProjectGenerator() {
        teamGenerator = new TeamGenerator();
    }

    /**
     * Instantiates a new project generator.
     * @param generator team generator to use.
     * @param names project names to generate project from.
     */
    public ProjectGenerator(final Generator<Team> generator, final String[] names) {
        this.teamGenerator = generator;
        this.projectNames = names;
    }

    /**
     * Sets the team generator for this generator.
     * @param generator the team generator
     */
    public final void setTeamGenerator(final Generator<Team> generator) {
        this.teamGenerator = generator;
    }

    /**
     * The pool of teams to cho0se from. If null then they will be generated.
     * @param teams The pool of teams
     */
    public final void setTeamPool(final List<Team> teams) {
        this.teamPool = teams;
    }

    /**
     * Generates the teams working on this project.
     * @param min The minimum number of teams
     * @param max The maximum number of teams
     * @return The teams
     */
    private List<Team> generateTeams(final int min, final int max) {
        List<Team> generated = new ArrayList<>();
        int teamCount = GenerationHelper.random(min, max);

        //If we haven't been given a pool of teams, make some up
        if (teamPool == null) {
            for (int i = 0; i < teamCount; i++) {
                Team newTeam = teamGenerator.generate();
                if (!generated.stream().filter(newTeam::equals).findAny().isPresent()) {
                    generated.add(newTeam);
                }
            }
        }
        else {
            //If there are more teams than we have just assign all of them
            if (teamCount > teamPool.size()) {
                teamCount = teamPool.size();
            }

            for (int i = 0; i < teamCount; i++) {
                //Remove the team so we can't pick it again. We'll put it back when we're done
                Team team = teamPool.remove(GenerationHelper.random(teamPool.size()));
                generated.add(team);
            }

            //Put all the teams we took out back
            for (Team team : generated) {
                teamPool.add(team);
            }
        }

        return generated;
    }

    @Override
    public final Project generate() {
        final int longNameLength = 1000;

        Project project = new Project();

        String shortName = GenerationHelper.randomElement(projectNames);
        String longName = GenerationHelper.randomString(longNameLength);
        String description = NameGenerator.randomDescription();

        //List<Team> teams = generateTeams(10, 50);

        try {
            project.setShortName(shortName);
        }
        catch (Exception e) {
            // Do nothing, don't have to deal with the
            // exception if only generating test data.
            ErrorReporter.get().reportErrorSecretly(e, "ProjectGenerator: setting short name failed");
            return null;
        }
        project.setLongName(longName);
        project.setDescription(description);

        return project;
    }
}
