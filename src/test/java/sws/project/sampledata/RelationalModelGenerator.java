package sws.project.sampledata;

import sws.project.model.RelationalModel;

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

            /*int rand = random.nextInt(10);
            for (int i = 0; i < rand; i++) {
                model.addUnassignedTeam(teamGenerator.generate());
            }

            for (Team team : model.getUnassignedTeams()) {
                model.addUnassignedPeople(team.getMembers());
            }

            HashSet<Skill> skills = new HashSet<>();
            for (Team team : model.getProject().getTeams()) {
                for (Person person : team.getMembers()) {
                    skills.addAll(person.getSkills());
                }
            }
            for (Person person : model.getUnassignedPeople()) {
                skills.addAll(person.getSkills());
            }
            ArrayList<Skill> skillsAl = new ArrayList<>();
            skillsAl.addAll(skills);
            model.addSkills(skillsAl);*/
            return model;
        }
        catch (Exception e) {
            // do nothing
            return null;
        }
    }
}
