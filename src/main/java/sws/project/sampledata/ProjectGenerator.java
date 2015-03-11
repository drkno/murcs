package sws.project.sampledata;

import sws.project.model.Project;
import sws.project.model.Team;

import java.util.ArrayList;

/**
 *
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

        project.setShortName(shortName);
        project.setLongName(longName);

        project.setDescription(description);

        project.getTeams().addAll(teams);

        return project;
    }
}
