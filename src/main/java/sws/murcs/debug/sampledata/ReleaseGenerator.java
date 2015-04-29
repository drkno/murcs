package sws.murcs.debug.sampledata;

import sws.murcs.model.Project;
import sws.murcs.model.Release;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generates random releases
 */
public class ReleaseGenerator implements Generator<Release> {

    public static final int LOW_STRESS_MIN = 1;
    public static final int LOW_STRESS_MAX = 10;

    public static final int MEDIUM_STRESS_MIN = 10;
    public static final int MEDIUM_STRESS_MAX = 20;

    public static final int HIGH_STRESS_MIN = 20;
    public static final int HIGH_STRESS_MAX = 40;

    private String[] descriptions = {"A release date",
            "The time when it has to be ready",
            "That's not enough time",
            "Another random release dates",
            "Is anyone even reading this",
            "I don't like doing work so don't release this"
    };

    private ArrayList<Project> projectPool;
    private Generator<Project> projectGenerator;

    /**
     * Sets up a random release
     */
    public ReleaseGenerator() {
        this.projectGenerator = new ProjectGenerator();
    }

    /**
     * Sets up a random release with a from one of the given descriptions
     * @param descriptions The given descriptions
     * @param projectGenerator The generator to be used for the generation of projects
     */
    public ReleaseGenerator(Generator<Project> projectGenerator, String[] descriptions) {
        this.descriptions = descriptions;
        this.projectGenerator = projectGenerator;
    }

    /**
     * Sets the Project pool for the generator
     * @param projectPool The project pool
     */
    public void setProjectPool(ArrayList<Project> projectPool) {
        this.projectPool = projectPool;
    }

    /**
     * Sets the project generator for use in creating more projects if necessary
     * @param projectGenerator
     */
    public void setProjectGenerator(Generator<Project> projectGenerator) {
        this.projectGenerator = projectGenerator;
    }

    /**
     * Generates a list of projects if there isn't already a pool of projects to choose from
     * @param min The min number of projects
     * @param max The max number of projects
     * @return The array list of generated projects
     */
    private ArrayList<Project> generateProjects(int min, int max) {
        ArrayList<Project> generated = new ArrayList<>();
        int projectCount = NameGenerator.random(min, max);

        if (projectPool == null) {
            for (int i = 0; i < projectCount; i++) {
                Project newProject = projectGenerator.generate();
                if (!generated.stream().filter(project -> newProject.equals(project)).findAny().isPresent()) {
                    generated.add(newProject);
                }
            }
        } else {
            if (projectCount > projectPool.size()) projectCount = projectPool.size();

            for (int i = 0; i < projectCount; i++) {
                Project project = projectPool.remove(NameGenerator.random(projectPool.size()));
                generated.add(project);
            }

            for (Project project : generated)
                projectPool.add(project);
        }
        return generated;
    }

    @Override
    public Release generate() {
        Release r = new Release();

        Random random = new Random(47658758756875687L);

        String shortName = NameGenerator.randomName();
        String description = NameGenerator.randomElement(descriptions);
        LocalDate releaseDate = LocalDate.of(random.nextInt(10000), random.nextInt(12) + 1, random.nextInt(28) + 1);

        ArrayList<Project> projects = generateProjects(1,5);

        try {
            r.setShortName(shortName);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
            //Don't need to do anything here as it's just generation
        }

        try {
            projects.get(NameGenerator.random(projects.size())).addRelease(r);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        r.setDescription(description);
        r.setReleaseDate(releaseDate);

        return r;
    }
}
