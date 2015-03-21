package sws.murcs.sampledata;

import sws.murcs.model.RelationalModel;

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

            //TODO: stop generation of duplicate objects
            /*int rand = random.nextInt(10);
            ArrayList<Team> unassignedTeams = new ArrayList<>();
            for (int i = 0; i < rand; i++) {
                unassignedTeams.add(teamGenerator.generate());
            }
            unassignedTeams = new ArrayList<Team>(new LinkedHashSet<Team>(unassignedTeams));
            model.addTeams(new ArrayList<>(new HashSet<>(unassignedTeams)));

            ArrayList<Person> unassignedPeople = new ArrayList<>();
            for (Team team : model.getUnassignedTeams()) {
                unassignedPeople.addAll(team.getMembers());
            }
            unassignedPeople = new ArrayList<Person>(new LinkedHashSet<Person>(unassignedPeople));
            model.addPeople(unassignedPeople);

            ArrayList<Skill> skills = new ArrayList<>();
            for (Team team : model.getProject().getTeams()) {
                for (Person person : team.getMembers()) {
                    skills.addAll(person.getSkills());
                }
            }
            for (Person person : model.getUnassignedPeople()) {
                skills.addAll(person.getSkills());
            }
            skills = new ArrayList<Skill>(new LinkedHashSet<Skill>(skills));
            model.addSkills(skills);*/

            return model;
        }
        catch (Exception e) {
            // do nothing
            return null;
        }
    }
}
