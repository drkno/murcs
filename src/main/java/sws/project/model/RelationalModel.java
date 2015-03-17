package sws.project.model;

import sws.project.exceptions.DuplicateObjectException;
import sws.project.magic.tracking.TrackableValue;
import sws.project.magic.tracking.TrackableObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The top level relational model
 *
 * 11/03/2015
 */
public class RelationalModel extends TrackableObject implements Serializable{

    @TrackableValue
    private Project project;
    @TrackableValue
    private ArrayList<Person> unassignedPeople;
    @TrackableValue
    private ArrayList<Team> unassignedTeams;
    @TrackableValue
    private ArrayList<Skill> skills;

    /***
     * Gets the current application version
     * @return The current application version.
     */
    public static float getVersion() {
        return version;
    }

    private static final float version = 0.01f;

    public RelationalModel() {
        this.unassignedPeople = new ArrayList<>();
        this.unassignedTeams = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.project = new Project();
        saveCurrentState("Set relational model");
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
        saveCurrentState("Project change");
    }

    /**
     * Gets the unassigned people
     * @return The unassigned people
     */
    public ArrayList<Person> getUnassignedPeople() {
        return unassignedPeople;
    }

    /**
     * Gets a list of all people.
     * @return The people
     */
    public ArrayList<Person> getPeople(){
        ArrayList<Person> people = new ArrayList<>();
        people.addAll(unassignedPeople);

        for (Team t : getTeams())
            people.addAll(t.getMembers());

        return people;
    }

    /**
     * Gets a list of all teams
     * @return The teams
     */
    public ArrayList<Team> getTeams(){
        ArrayList<Team> teams = new ArrayList<>();

        teams.addAll(unassignedTeams);
        if (getProject() != null)
            teams.addAll(getProject().getTeams());
        return teams;
    }

    /**
     * Adds a person to the unassigned people only if that person is not already in 
     * @param person to be added
     * @throws sws.project.exceptions.DuplicateObjectException if the relational model already has the person
     */
    public void addUnassignedPerson(Person person) throws DuplicateObjectException{
        if (!this.getPeople().contains(person) &&
                !this.getPeople()
                        .stream()
                        .filter(s -> s.getShortName().toLowerCase().equals(person.getShortName().toLowerCase()))
                        .findAny()
                        .isPresent()) {
            this.unassignedPeople.add(person);
            saveCurrentState("Unassigned person added");
        }
        else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds a list of people to the unassigned people
     * @param people People to be added to unassigned people
     * @throws sws.project.exceptions.DuplicateObjectException if the relational model already has a person from the people to be addeed
     */
    public void addUnassignedPeople(List<Person> people) throws DuplicateObjectException {
        for (Person person: people) {
            this.addUnassignedPerson(person);
        }
    }

    /**
     * Removes a person from unassigned people
     * @param person The unassigned person to remove
     */
    public void removeUnassignedPerson(Person person) {
        if (this.unassignedPeople.contains(person)) {
            this.unassignedPeople.remove(person);
            saveCurrentState("Unassigned person removed");
        }
    }

    /**
     * Gets the unassigned teams
     * @return The unassigned teams
     */
    public ArrayList<Team> getUnassignedTeams() {
        return unassignedTeams;
    }

    /**
     * Adds a team to the unassigned teams if the relational model does not already have that team
     * @param team The unassigned team to add
     * @throws sws.project.exceptions.DuplicateObjectException if the relational model already has that team
     */
    public void addUnassignedTeam(Team team) throws DuplicateObjectException {
        if (!this.unassignedTeams.contains(team) &&
                !this.unassignedTeams
                        .stream()
                        .filter(s -> s.getShortName().toLowerCase().equals(team.getShortName().toLowerCase()))
                        .findAny()
                        .isPresent()) {
            this.unassignedTeams.add(team);
            saveCurrentState("Unassigned team added");
        }
        else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds a list of teams to add to the relational model
     * @param teams Teams to be added to the relational model
     * @throws sws.project.exceptions.DuplicateObjectException if the project already has a team from teams to be added
     */
    public void addUnassignedTeams(List<Team> teams) throws DuplicateObjectException {
        for (Team team: teams) {
            this.addUnassignedTeam(team);
        }
    }

    /**
     * Removes a team from the unassigned teams
     * @param team The unassigned team to remove
     */
    public void removeUnassignedTeam(Team team) {
        if (this.unassignedTeams.contains(team)) {
            this.unassignedTeams.remove(team);
            saveCurrentState("Unassigned team removed");
        }
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
     * @throws sws.project.exceptions.DuplicateObjectException if the skill already exists in the relational model
     */
    public void addSkill(Skill skill) throws DuplicateObjectException {
        if (!skills.contains(skill) &&
                !skills
                        .stream()
                        .filter(s -> s.getShortName().toLowerCase().equals(skill.getShortName().toLowerCase()))
                        .findAny()
                        .isPresent()) {
            this.skills.add(skill);
            saveCurrentState("Skill added");
        }
        else throw new DuplicateObjectException();
    }

    /**
     * Adds a list of skills to the existing list of skills
     * @param skills Skills to be added existing skills
     * @throws sws.project.exceptions.DuplicateObjectException if a skill is aleady in the relational model
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
            saveCurrentState("Skill added");
        }
    }
}
