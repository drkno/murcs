package sws.project.model;

import sws.project.controller.ModelTypes;
import sws.project.exceptions.DuplicateObjectException;
import sws.project.magic.tracking.TrackableObject;
import sws.project.magic.tracking.TrackableValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The top level relational model
 */
public class RelationalModel extends TrackableObject implements Serializable {

    @TrackableValue
    private Project project;
    @TrackableValue
    private ArrayList<Person> people;
    @TrackableValue
    private ArrayList<Team> teams;
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
        this.people = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.project = null;
        saveCurrentState("Set relational model");

        try {
            Skill productOwner = new Skill();
            productOwner.setShortName("PO");
            productOwner.setLongName("Product Owner");
            productOwner.setDescription("has ability to insult design teams efforts");
            this.skills.add(productOwner);

            Skill scrumMaster = new Skill();
            scrumMaster.setShortName("SM");
            scrumMaster.setLongName("Scrum Master");
            scrumMaster.setDescription("is able to manage the efforts of a team and resolve difficulties");
            this.skills.add(scrumMaster);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        ArrayList<Person> unassignedPeople = new ArrayList<>();
        unassignedPeople.addAll(getPeople());
        ArrayList<Team> teams = getTeams();

        //Remove all the people who have a team
        for (Team team : teams){
            for (Person person : team.getMembers())
                unassignedPeople.remove(person);
        }

        return unassignedPeople;
    }

    /**
     * Gets a list of all people.
     * @return The people
     */
    public ArrayList<Person> getPeople(){
        return people;
    }

    /**
     * Adds a person to the model if it doesn't exits
     * @param person to be added
     * @throws sws.project.exceptions.DuplicateObjectException if the relational model already has the person
     */
    public void addPerson(Person person) throws DuplicateObjectException{
        if (!this.getPeople().contains(person) &&
                !this.getPeople()
                        .stream()
                        .filter(s -> s.getShortName().toLowerCase().equals(person.getShortName().toLowerCase()))
                        .findAny()
                        .isPresent()) {
            this.getPeople().add(person);
            saveCurrentState("Unassigned person added");
        }
        else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds a list of people to the model
     * @param people People to be added
     * @throws sws.project.exceptions.DuplicateObjectException if the relational model already has a person from the people to be addeed
     */
    public void addPeople(List<Person> people) throws DuplicateObjectException {
        for (Person person: people) {
            this.addPerson(person);
        }
    }

    /**
     * Removes a person from unassigned people
     * @param person The unassigned person to remove
     */
    public void removePerson(Person person) {
        if (this.getPeople().contains(person)) {
            this.getPeople().remove(person);
            //Remove the person from any team they might be in
            getTeams().stream().filter(team -> team.getMembers().contains(person)).forEach(team -> team.removeMember(person));
            saveCurrentState("Unassigned person removed");
        }
    }

    /**
     * Gets the unassigned teams
     * @return The unassigned teams
     */
    public ArrayList<Team> getUnassignedTeams() {

        ArrayList<Team> unassignedTeams = new ArrayList<>();
        unassignedTeams.addAll(getTeams());

        if (getProject() != null) {
            //Remove all the teams that are assigned to a project
            for (Team team : project.getTeams()){
                unassignedTeams.remove(team);
            }
        }

        return unassignedTeams;
    }

    /**
     * Gets a list of all teams
     * @return The teams
     */
    public ArrayList<Team> getTeams(){
        return teams;
    }

    /**
     * Adds a team to the unassigned teams if the relational model does not already have that team
     * @param team The unassigned team to add
     * @throws sws.project.exceptions.DuplicateObjectException if the relational model already has that team
     */
    public void addTeam(Team team) throws DuplicateObjectException {
        if (!this.teams.contains(team) &&
                !this.teams
                        .stream()
                        .filter(s -> s.getShortName().toLowerCase().equals(team.getShortName().toLowerCase()))
                        .findAny()
                        .isPresent()) {
            this.getTeams().add(team);
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
    public void addTeams(List<Team> teams) throws DuplicateObjectException {
        for (Team team: teams) {
            this.addTeam(team);
        }
    }

    /**
     * Removes a team from the unassigned teams
     * @param team The team to remove
     */
    public void removeTeam(Team team) {
        if (this.teams.contains(team)) {
            this.teams.remove(team);

            if (this.getProject().getTeams().contains(team))
                this.getProject().getTeams().remove(team);

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
        if (!skills.contains(skill)) {
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

            //Remove the skill from any people who might have it
            getPeople().stream().filter(person -> person.getSkills().contains(skill)).forEach(person -> person.removeSkill(skill));

            saveCurrentState("Skill added");
        }
    }

    /**
     * Tries to add the object to the model
     * @param model the object to add to the model
     * @throws sws.project.exceptions.DuplicateObjectException Because you did something silly, like try to add an object that already exists
     */
    public void add(Model model) throws DuplicateObjectException{
        ModelTypes type = ModelTypes.getModelType(model);

        switch (type){
            case Project:
                setProject((Project)model);
                break;
            case Team:
                addTeam((Team)model);
                break;
            case Skills:
                addSkill((Skill)model);
                break;
            case People:
                addPerson((Person)model);
                break;
        }
    }

    /**
     * Tries to remove an object from the model
     * @param model the object to remove from the model
     */
    public void remove(Model model){
        ModelTypes type = ModelTypes.getModelType(model);

        switch (type){
            case Project:
                if (getProject() == model)
                    setProject(null);
                break;
            case Team:
                removeTeam((Team)model);
                break;
            case Skills:
                removeSkill((Skill)model);
                break;
            case People:
                removePerson((Person)model);
                break;
        }
    }

    /**
     * Checks to see if an object exists in the model
     * @param model The model
     * @return Whether it exists
     */
    public boolean exists(Model model){
        if (model instanceof Project)
            return getProject() == model;
        if (model instanceof Team)
            return getTeams().contains(model);
        if  (model instanceof Person)
            return getPeople().contains(model);
        if (model instanceof Skill)
            return getSkills().contains(model);
        return false;
    }
}
