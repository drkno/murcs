package sws.murcs.debug.sampledata;

import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Project;
import sws.murcs.model.Team;

import java.util.ArrayList;

/**
 * Generates random projects with teams
 */
public class ProjectGenerator implements Generator<Project> {
    private String[] projectNames = {"A project", "Something exciting"};
    private String[] descriptions = {"A very exciting description", NameGenerator.getLoremIpsum()};
    private final Generator<Team> teamGenerator;

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
     * Generates a new random project.
     * @return a new random project.
     */
    @Override
    public Project generate() {
        Project project = new Project();

        String shortName = NameGenerator.randomElement(projectNames);
        String longName = NameGenerator.randomString(1000);

        String description = NameGenerator.randomElement(descriptions);

        int teamCount = NameGenerator.random(1, 50);
        ArrayList<Team> teams = new ArrayList<>();

        for (int i = 0; i < teamCount; ++i){
            Team newTeam = teamGenerator.generate();
            if (!teams.stream().filter(team -> newTeam.equals(team)).findAny().isPresent()) {
                teams.add(newTeam);
            }
        }

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

        try{
            project.addTeams(teams);
        } catch (CustomException e) {
            e.printStackTrace();
            return null;
            //Do nothing, don't have to deal with the exception if only generating test data.
        }

        return project;
    }
}
