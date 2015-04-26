package sws.murcs.debug.sampledata;

import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.model.Project;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        List<WorkAllocation> allocations = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < teamCount; ++i) {

            // Generate a new team
            Team newTeam = teamGenerator.generate();
            if (allocations.stream().filter(team -> newTeam.equals(team)).findAny().isPresent()) {
                continue;
            }

            // Make the startDate somewhere between now and three weeks from now
            int wait = random.nextInt(21);
            LocalDate startDate = LocalDate.now().plus(wait, ChronoUnit.DAYS);

            // Make the endDate somewhere between the startDate and two weeks from then
            int duration = random.nextInt(14);
            LocalDate endDate = startDate.plus(duration, ChronoUnit.DAYS);

            try {
                WorkAllocation allocation = new WorkAllocation(project, newTeam, startDate, endDate);
                project.addAllocation(allocation);
            }
            catch (DuplicateObjectException e) {
                // Ignored
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

        try {
            project.addAllocations(allocations);
        } catch (CustomException e) {
            e.printStackTrace();
            return null;
            //Do nothing, don't have to deal with the exception if only generating test data.
        }

        return project;
    }
}
