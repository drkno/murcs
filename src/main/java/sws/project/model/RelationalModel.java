package sws.project.model;

import sws.project.exceptions.DuplicateObjectException;

import java.util.ArrayList;
import java.util.List;

/**
 * The top level relational model
 *
 * 11/03/2015
 */
public class RelationalModel {

    private Project project;
    private ArrayList<Person> unassignedPeople;
    private ArrayList<Team> unassignedTeams;
    private ArrayList<Skill> skills;

    public RelationalModel() {
        this.unassignedPeople = new ArrayList<>();
        this.unassignedTeams = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.project = new Project();
    }

    /**
     * Gets the project
     * @return The project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Sets the project
     * @param project The new project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Gets the unassigned people
     * @return The unassigned people
     */
    public ArrayList<Person> getUnassignedPeople() {
        return unassignedPeople;
    }

    /**
     * Adds a person to unassigned people
     * @param person The new unassigned person
     */
    public void addUnassignedPeron(Person person) {
        this.unassignedPeople.add(person);
    }

    /**
     * Removes a person from unassigned people
     * @param person The unassigned person to remove
     */
    public void removeUnassignedPerson(Person person) {
        this.unassignedPeople.remove(person);
    }

    /**
     * Gets the unassigned teams
     * @return The unassigned teams
     */
    public ArrayList<Team> getUnassignedTeams() {
        return unassignedTeams;
    }

    /**
     * Adds a team to the unassigned teams
     * @param team The new unassigned team
     */
    public void addUnassignedTeam(Team team) {
        this.unassignedTeams.add(team);
    }

    /**
     * Removes a team from the unassigned teams
     * @param team The unassigned team to remove
     */
    public void removeUnassignedTeam(Team team) {
        this.unassignedTeams.remove(team);
    }

    /**
     * Gets the skills
     * @return The skills
     */
    public ArrayList<Skill> getSkills() {
        return skills;
    }

    /**
     * Adds a skill to skills only if the skill does not already exist
     * @param skill The skill to add
     */
    public void addSkill(Skill skill) throws DuplicateObjectException {
        if (!skills.contains(skill) &&
                !skills
                        .stream()
                        .filter(s -> s.getShortName().toLowerCase().equals(skill.getShortName().toLowerCase()))
                        .findAny()
                        .isPresent()) {
            this.skills.add(skill);
        }
        else throw new DuplicateObjectException();
    }

    /**
     * Adds a list of skills to the existing list of skills
     * @param skills Skills to be added existing skills
     */
    public void addSkills(List<Skill> skills) throws DuplicateObjectException {
        for (Skill skill: skills) {
            this.addSkill(skill);
        }
    }

    /**
     * Removes a skill from skills
     * @param skill The skill to remove
     */
    public void removeSkill(Skill skill) {
        if (skills.contains(skill)) {
            this.skills.remove(skill);
        }
    }
}
