package sws.murcs.debug.sampledata;

import sws.murcs.model.Person;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Skill;
import sws.murcs.model.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;

/**
 * Generates random RelationalModels
 */
public class RelationalModelGenerator implements Generator<RelationalModel> {

    private ProjectGenerator projectGenerator;
    private TeamGenerator teamGenerator;
    private Random random;

    public RelationalModelGenerator() {
        projectGenerator = new ProjectGenerator();
        teamGenerator = new TeamGenerator();
        random = new Random();
    }

    @Override
    public RelationalModel generate() {
        try {
            RelationalModel model = new RelationalModel();
            model.setProject(projectGenerator.generate());


            int rand = random.nextInt(10);
            ArrayList<Team> unassignedTeams = new ArrayList<>();
            for (int i = 0; i < rand; i++) {
                Team newTeam = teamGenerator.generate();
                if (!unassignedTeams.stream().filter(team -> newTeam.equals(team)).findAny().isPresent()) {
                    unassignedTeams.add(newTeam);
                }
            }
            unassignedTeams = new ArrayList<Team>(new LinkedHashSet<Team>(unassignedTeams));
            model.addTeams(unassignedTeams);

            ArrayList<Person> people = new ArrayList<>();
            for (Team team : model.getUnassignedTeams()) {
                for (Person person: team.getMembers()) {
                    if (!people.stream().filter(existingPerson -> person.equals(existingPerson)).findAny().isPresent()) {
                        people.add(person);
                    }
                }
            }

            for (Team team : model.getTeams()) {
                for (Person person: team.getMembers()) {
                    if (!people.stream().filter(existingPerson -> person.equals(existingPerson)).findAny().isPresent()) {
                        people.add(person);
                    }
                }
            }

            people = new ArrayList<Person>(new LinkedHashSet<Person>(people));
            model.addPeople(people);

            ArrayList<Skill> skills = new ArrayList<>();
            for (Team team : model.getProject().getTeams()) {
                for (Person person : team.getMembers()) {
                    for (Skill newSkill: person.getSkills()) {
                        if (!skills.stream().filter(skill -> newSkill.equals(skill)).findAny().isPresent() && !newSkill.isProductOwnerSkill() && !newSkill.isScrumMasterSkill()) {
                            skills.add(newSkill);
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
            skills = new ArrayList<Skill>(new HashSet<Skill>(skills));
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
