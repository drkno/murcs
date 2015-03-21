package sws.project.sampledata;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.model.Project;
import sws.murcs.model.Team;

import java.util.ArrayList;

/**
 * Generates random projects with teams
 */
public class ProjectGenerator implements Generator<Project> {
    private String[] projectNames = new String[]{"A project", "Something exciting"};
    private String[] descriptions = new String[]{"A very exciting description", NameGenerator.getLoremIpsum()};

    private Generator<Team> teamGenerator;

    public ProjectGenerator(){
        teamGenerator = new TeamGenerator();
    }

    public ProjectGenerator(Generator<Team> teamGenerator, String[] projectNames, String[] descriptions){
        this.teamGenerator = teamGenerator;
        this.projectNames = projectNames;
        this.descriptions = descriptions;
    }

    @Override
    public Project generate() {
        Project project = new Project();

        String shortName = NameGenerator.randomElement(projectNames);
        String longName = NameGenerator.randomString(1000);

        String description = NameGenerator.randomElement(descriptions);

        int teamCount = NameGenerator.random(1, 50);
        ArrayList<Team> teams = new ArrayList<>();

        for (int i = 0; i < teamCount; ++i){
            Team team = teamGenerator.generate();
            teams.add(team);
        }

        try {
            project.setShortName(shortName);
        }
        catch (Exception e) {
            //Do nothing, don't have to deal with the exception if only generating test data.
        }
            project.setLongName(longName);
            project.setDescription(description);

        try{
            project.addTeams(teams);
        } catch (DuplicateObjectException e) {
            //Do nothing, don't have to deal with the exception if only generating test data.
        }

        return project;
    }
}
