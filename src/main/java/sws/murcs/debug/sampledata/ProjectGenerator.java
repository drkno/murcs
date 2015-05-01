package sws.murcs.debug.sampledata;

import sws.murcs.model.Project;
import sws.murcs.model.Team;

import java.util.ArrayList;

/**
 * Generates random projects with teams
 */
public class ProjectGenerator implements Generator<Project> {
    public static final int LOW_STRESS_MAX = 5;
    public static final int LOW_STRESS_MIN = 1;

    public static final int MEDIUM_STRESS_MAX = 10;
    public static final int MEDIUM_STRESS_MIN = 5;

    public static final int HIGH_STRESS_MAX = 20;
    public static final int HIGH_STRESS_MIN = 10;

    private String[] projectNames = {"A project",
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
    private String[] descriptions = {"A very exciting description", NameGenerator.getLoremIpsum()};
    private Generator<Team> teamGenerator;
    private ArrayList<Team> teamPool;

    /**
     * Instantiates a new project generator.
     */
    public ProjectGenerator(){
        teamGenerator = new TeamGenerator();
    }

    /**
     * Instantiates a new project generator.
     * @param teamGenerator team generator to use.
     * @param projectNames project names to generate project from.
     * @param descriptions descriptions for projects to generate from.
     */
    public ProjectGenerator(Generator<Team> teamGenerator, String[] projectNames, String[] descriptions){
        this.teamGenerator = teamGenerator;
        this.projectNames = projectNames;
        this.descriptions = descriptions;
    }

    /**
     * Sets the team generator for this generator
     * @param teamGenerator the team generator
     */
    public void setTeamGenerator(Generator<Team> teamGenerator){
        this.teamGenerator = teamGenerator;
    }

    /**
     * the pool of teams to cho0se from. If null then they will be generated
     * @param teamPool The pool of teams
     */
    public void setTeamPool(ArrayList<Team> teamPool){
        this.teamPool = teamPool;
    }

    /**
     * Generates the teams working on this project
     * @param min The minimum number of teams
     * @param max The maximum number of teams
     * @return The teams
     */
    private ArrayList<Team> generateTeams(int min, int max){
        ArrayList<Team> generated = new ArrayList<>();
        int teamCount = NameGenerator.random(min, max);

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
            if (teamCount > teamPool.size()) teamCount = teamPool.size();

            for (int i = 0; i < teamCount; i++) {
                //Remove the team so we can't pick it again. We'll put it back when we're done
                Team team = teamPool.remove(NameGenerator.random(teamPool.size()));
                generated.add(team);
            }

            //Put all the teams we took out back
            for (Team team : generated)
                teamPool.add(team);
        }

        return generated;
    }

    @Override
    public Project generate() {
        Project project = new Project();

        String shortName = NameGenerator.randomElement(projectNames);
        String longName = NameGenerator.randomString(1000);
        String description = NameGenerator.randomElement(descriptions);

        //List<Team> teams = generateTeams(10, 50);

        try {
            project.setShortName(shortName);
        }
        catch (Exception e) {
            //Do nothing, don't have to deal with the exception if only generating test data.
            e.printStackTrace();
            return null;
        }
        project.setLongName(longName);
        project.setDescription(description);

        return project;
    }
}
