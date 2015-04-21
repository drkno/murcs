package sws.murcs.debug.sampledata;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.model.*;

import java.util.ArrayList;
import java.util.Random;

/**
 * Generates random RelationalModels
 */
public class RelationalModelGenerator implements Generator<RelationalModel> {
    public enum Stress{
        High,
        Medium,
        Low,
    }

    private final ProjectGenerator projectGenerator;
    private final TeamGenerator teamGenerator;
    private final PersonGenerator personGenerator;
    private final SkillGenerator skillGenerator;

    private final Random random;

    private Stress stress;

    /**
     * Instantiates a new random RelationalModel generator.
     */
    public RelationalModelGenerator(Stress stress) {
        this.stress = stress;

        skillGenerator = new SkillGenerator();
        personGenerator = new PersonGenerator();
        personGenerator.setSkillGenerator(skillGenerator);

        teamGenerator = new TeamGenerator();
        teamGenerator.setPersonGenerator(personGenerator);

        projectGenerator = new ProjectGenerator();
        projectGenerator.setTeamGenerator(teamGenerator);

        random = new Random();
    }

    private ArrayList<Model> generateItems(Generator<? extends Model> generator, int min, int max){
        ArrayList<Model> items = new ArrayList<>();

        int count = NameGenerator.random(min, max);

        for (int i = 0; i < count; i++){
            Model g = generator.generate();
            if (!items.stream().filter(m -> g.equals(m)).findAny().isPresent())
                items.add(g);
        }
        return items;
    }

    /**
     * Calculates a minimum given a stress level
     * @param stress The stress
     * @param lowMin The low min
     * @param mediumMin The medium min
     * @param highMin The high min
     * @return The min
     */
    private int getMin(Stress stress, int lowMin, int mediumMin, int highMin){
        switch (stress){
            case Low:
                return lowMin;
            case Medium:
                return mediumMin;
            case High:
                return highMin;
        }
        return lowMin;
    }

    /**
     * Gets a max clamp for a given stress level
     * @param stress The stress
     * @param lowMax The low max
     * @param mediumMax The medium max
     * @param highMax The high max
     * @return The max
     */
    private int getMax(Stress stress, int lowMax, int mediumMax, int highMax){
        switch (stress)     {
            case Low:
                return lowMax;
            case Medium:
                return mediumMax;
            case High:
                return highMax;
        }
        return lowMax;
    }

    @Override
    public RelationalModel generate(){
        try{
            RelationalModel model = new RelationalModel();

            int min = getMin(stress, SkillGenerator.LOW_STRESS_MIN, SkillGenerator.MEDIUM_STRESS_MIN, SkillGenerator.HIGH_STRESS_MIN),
                    max = getMax(stress, SkillGenerator.LOW_STRESS_MAX, SkillGenerator.MEDIUM_STRESS_MAX, SkillGenerator.HIGH_STRESS_MAX);
            ArrayList<Skill> skills = new ArrayList<>();
            for (Model m : generateItems(skillGenerator, min, max)){
                skills.add((Skill)m);
            }

            personGenerator.setSkillPool(skills);
            ArrayList<Person> people = new ArrayList<>();
            min = getMin(stress, PersonGenerator.LOW_STRESS_MIN, PersonGenerator.MEDIUM_STRESS_MIN, PersonGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, PersonGenerator.LOW_STRESS_MAX, PersonGenerator.MEDIUM_STRESS_MAX, PersonGenerator.HIGH_STRESS_MAX);
            for (Model m : generateItems(personGenerator, min, max)){
                people.add((Person)m);
            }

            teamGenerator.setPersonPool(people);
            ArrayList<Team> teams = new ArrayList<>();
            min = getMin(stress, TeamGenerator.LOW_STRESS_MIN, TeamGenerator.MEDIUM_STRESS_MIN, TeamGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, TeamGenerator.LOW_STRESS_MAX, TeamGenerator.MEDIUM_STRESS_MAX, TeamGenerator.HIGH_STRESS_MAX);
            for (Model m : generateItems(teamGenerator, min, max)){
                teams.add((Team)m);
            }

            projectGenerator.setTeamPool(teams);
            ArrayList<Project> projects = new ArrayList<>();
            min = getMin(stress, ProjectGenerator.LOW_STRESS_MIN, ProjectGenerator.MEDIUM_STRESS_MIN, ProjectGenerator.HIGH_STRESS_MIN);
            max = getMax(stress, ProjectGenerator.LOW_STRESS_MAX, ProjectGenerator.MEDIUM_STRESS_MAX, ProjectGenerator.HIGH_STRESS_MAX);
            for (Model m : generateItems(projectGenerator, min, max)){
                projects.add((Project)m);
            }

            model.addSkills(skills);
            model.addPeople(people);
            model.addTeams(teams);
            model.addProjects(projects);

            return model;
        }
        catch (DuplicateObjectException e){
            e.printStackTrace();
        }
        return null;
    }
}
