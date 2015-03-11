package sws.project.model;

import java.util.ArrayList;

/**
 * The top level relational model
 *
 * 11/03/2015
 * @author Dion
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
     * Adds a skill to skills
     * @param skill The skill to add
     */
    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    /**
     * Removes a skill from skills
     * @param skill The skill to remove
     */
    public void removeSkill(Skill skill) {
        this.skills.remove(skill);
    }
}
