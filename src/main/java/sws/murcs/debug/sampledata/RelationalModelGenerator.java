package sws.murcs.debug.sampledata;

import sws.murcs.model.*;

import java.util.*;

/**
 * Generates random RelationalModels
 */
public class RelationalModelGenerator implements Generator<RelationalModel> {
    private final ProjectGenerator projectGenerator;
    private final TeamGenerator teamGenerator;
    private final Random random;

    /**
     * Instantiates a new random RelationalModel generator.
     */
    public RelationalModelGenerator() {
        projectGenerator = new ProjectGenerator();
        teamGenerator = new TeamGenerator();
        random = new Random();
    }

    /**
     * Generates a new random relational model.
     * @return a new random relational model.
     */
    @Override
    public RelationalModel generate() {
        try {
            RelationalModel model = new RelationalModel();
            model.addProject(projectGenerator.generate());

            int rand = random.nextInt(10);
            List<Team> unassignedTeams = new ArrayList<>();
            for (int i = 0; i < rand; i++) {
                Team newTeam = teamGenerator.generate();
                if (!unassignedTeams.stream().filter(team -> newTeam.equals(team)).findAny().isPresent()) {
                    unassignedTeams.add(newTeam);
                }
            }
            unassignedTeams = new ArrayList<>(new LinkedHashSet<>(unassignedTeams));
            model.addTeams(unassignedTeams);

            ArrayList<Person> people = new ArrayList<>();
            for (Team team : model.getTeams()) {
                for (Person person: team.getMembers()) {
                    if (!people.stream().filter(existingPerson -> person.equals(existingPerson)).findAny().isPresent()) {
                        people.add(person);
                    }
                }
            }
            people = new ArrayList<>(new LinkedHashSet<>(people));
            model.addPeople(people);

            ArrayList<Skill> skills = new ArrayList<>();
            for (Project project : model.getProjects()) {
                for (WorkAllocation allocation : project.getAllocations()) {
                    for (Person person : allocation.getTeam().getMembers()) {
                        for (Skill newSkill : person.getSkills()) {
                            if (!skills.stream().filter(skill -> newSkill.equals(skill)).findAny().isPresent() && !newSkill.isProductOwnerSkill() && !newSkill.isScrumMasterSkill()) {
                                skills.add(newSkill);
                            }
                        }
                    }
                }
            }
            for (Person person : model.getUnassignedPeople()) {
                for (Skill newSkill: person.getSkills()) {
                    if (!skills.stream().filter(skill -> newSkill.equals(skill)).findAny().isPresent() && !newSkill.isProductOwnerSkill() && !newSkill.isScrumMasterSkill()) {
                        skills.add(newSkill);
                    }
                }
            }
            skills = new ArrayList<>(new HashSet<>(skills));
            model.addSkills(skills);

            return model;
        }
        catch (Exception e) {
            // do nothing
            e.printStackTrace();
            return null;
        }
    }
}
