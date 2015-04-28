package sws.murcs.model;

import sws.murcs.controller.ModelTypes;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.observable.ModelObservableArrayList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The top level relational model
 */
public class RelationalModel extends TrackableObject implements Serializable {

    @TrackableValue
    private ModelObservableArrayList<Project> projects;
    @TrackableValue
    private ModelObservableArrayList<Person> people;
    @TrackableValue
    private ModelObservableArrayList<Team> teams;
    @TrackableValue
    private ModelObservableArrayList<Skill> skills;
    @TrackableValue
    private ModelObservableArrayList<Release> releases;

    /**
     * Gets the current application version
     * @return The current application version.
     */
    public static float getVersion() {
        return version;
    }

    private static final float version = 0.02f;

    /**
     * Sets up a new Relational Model
     */
    public RelationalModel() {
        this.people = new ModelObservableArrayList<Person>();
        this.teams = new ModelObservableArrayList<Team>();
        this.skills = new ModelObservableArrayList<Skill>();
        this.releases = new ModelObservableArrayList<Release>();
        this.projects = new ModelObservableArrayList<Project>();

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
            //will never ever happen. ever. an exception is only thrown if you try to set the shortname as null/empty
        }
    }

    /**
     * Gets the projects
     * @return The projects
     */
    public ModelObservableArrayList<Project> getProjects() {
        return projects;
    }

    /**
     * Adds a new project
     * @param project The new project
     * @throws DuplicateObjectException if the project already exists
     */
    private void addProject(Project project) throws DuplicateObjectException {
        if (!this.getProjects().contains(project) &&
                !this.getProjects()
                        .stream()
                        .filter(s -> s.getShortName().toLowerCase().equals(project.getShortName().toLowerCase()))
                        .findAny()
                        .isPresent()) {
            this.projects.add(project);
        }
        else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds all given projects that are not already contained within the model
     * @param projects A List of projects to be added to the model
     * @throws DuplicateObjectException If one of the projects already exist
     */
    public void addProjects(List<Project> projects) throws DuplicateObjectException {
        boolean badProject = false;
        for (Project project : projects) {
            if (this.projects.contains(project) ||
                    this.getProjects()
                            .stream()
                            .filter(s -> s.getShortName().toLowerCase().equals(project.getShortName().toLowerCase()))
                            .findAny()
                            .isPresent()) {
                badProject = true;
            }
            else {
                this.projects.add(project);
            }
        }
        if (badProject)
            throw new DuplicateObjectException();
    }

    /**
     * Removes the given project if it exists
     * @param project The project to remove
     */
    private void removeProject(Project project) {
        if (this.projects.contains(project))
            this.projects.remove(project);
    }

    /**
     * Gets the unassigned people
     * @return The unassigned people
     */
    public ArrayList<Person> getUnassignedPeople() {

        ArrayList<Person> unassignedPeople = new ArrayList<>();
        unassignedPeople.addAll(getPeople());
        ModelObservableArrayList<Team> teams = getTeams();

        //Remove all the people who have a team
        for (Team team : teams) {
            team.getMembers().forEach(unassignedPeople::remove);
        }

        return unassignedPeople;
    }

    /**
     * Gets a list of all people.
     * @return The people
     */
    public ModelObservableArrayList<Person> getPeople() {
        return people;
    }

    /**
     * Adds a person to the model if it doesn't exits
     * @param person to be added
     * @throws DuplicateObjectException if the relational model already has the person
     */
    private void addPerson(Person person) throws DuplicateObjectException {
        if (!this.getPeople().contains(person) &&
                !this.getPeople()
                        .stream()
                        .filter(s -> s.getShortName().toLowerCase().equals(person.getShortName().toLowerCase()))
                        .findAny()
                        .isPresent()) {
            this.getPeople().add(person);
        }
        else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds a list of people to the model
     * @param people People to be added
     * @throws DuplicateObjectException if the relational model already has a person from the people to be added
     */
    public void addPeople(List<Person> people) throws DuplicateObjectException {
        for (Person person : people) {
            this.addPerson(person);
        }
    }

    /**
     * Removes a person from unassigned people
     * @param person The unassigned person to remove
     */
    private void removePerson(Person person) {
        if (this.getPeople().contains(person)) {
            this.getPeople().remove(person);
            //Remove the person from any team they might be in
            getTeams().stream().filter(team -> team.getMembers().contains(person)).forEach(team -> team.removeMember(person));
        }
    }

    /**
     * Gets all unassigned teams
     * @return The unassigned teams
     */
    public ArrayList<Team> getUnassignedTeams() {

        ArrayList<Team> unassignedTeams = new ArrayList<>();
        unassignedTeams.addAll(getTeams());

        //Remove all the teams that are assigned to a project
        getProjects().forEach(p -> p.getTeams().forEach(unassignedTeams::remove));

        return unassignedTeams;
    }

    /**
     * Gets a list of all teams
     * @return The teams
     */
    public ModelObservableArrayList<Team> getTeams() {
        return teams;
    }

    /**
     * Adds a team to the unassigned teams if the relational model does not already have that team
     * @param team The unassigned team to add
     * @throws DuplicateObjectException if the relational model already has the team.
     */
    private void addTeam(Team team) throws DuplicateObjectException {
        if (!this.teams.contains(team) &&
                !this.teams
                        .stream()
                        .filter(s -> s.getShortName().toLowerCase().equals(team.getShortName().toLowerCase()))
                        .findAny()
                        .isPresent()) {
            this.getTeams().add(team);
        }
        else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds a list of teams to add to the relational model
     * @param teams Teams to be added to the relational model
     * @throws DuplicateObjectException if the murcs already has a team from teams to be added
     */
    public void addTeams(List<Team> teams) throws DuplicateObjectException {
        for (Team team : teams) {
            this.addTeam(team);
        }
    }

    /**
     * Removes a team from the list of teams and from any projects
     * @param team The team to remove
     */
    private void removeTeam(Team team) {
        if (this.teams.contains(team)) {
            this.teams.remove(team);
        }

        this.getProjects().stream().filter(project -> project.getTeams().contains(team)).forEach(project -> project.getTeams().remove(team));
    }

    /**
     * Gets the skills
     * @return The skills
     */
    public ModelObservableArrayList<Skill> getSkills() {
        return skills;
    }

    /**
     * Adds a skill to skills only if the skill does not already exist
     * @param skill The skill to add
     * @throws DuplicateObjectException if the skill already exists in the relational model
     */
    private void addSkill(Skill skill) throws DuplicateObjectException {
        if (!skills.contains(skill)) {
            this.skills.add(skill);
        }
        else throw new DuplicateObjectException("Skill already exists");
    }

    /**
     * Adds a list of skills to the existing list of skills
     * @param skills Skills to be added existing skills
     * @throws DuplicateObjectException if a skill is already in the relational model
     */
    public void addSkills(List<Skill> skills) throws DuplicateObjectException {
        for (Skill skill : skills) {
            this.addSkill(skill);
        }
    }

    /**
     * Removes a skill from skills
     * @param skill The skill to remove
     */
    private void removeSkill(Skill skill) {
        if (skills.contains(skill)) {
            this.skills.remove(skill);

            //Remove the skill from any people who might have it
            getPeople().stream().filter(person -> person.getSkills().contains(skill)).forEach(person -> person.removeSkill(skill));
        }
    }

    /**
     * Tries to add the object to the model
     * @param model the object to add to the model
     * @throws DuplicateObjectException because you tried to add an object that already exists
     */
    public void add(Model model) throws DuplicateObjectException {
        ModelTypes type = ModelTypes.getModelType(model);

        long commitNumber = UndoRedoManager.getHead() == null ? 0 : UndoRedoManager.getHead().getCommitNumber();

        switch (type) {
            case Project:
                addProject((Project) model);
                break;
            case Team:
                addTeam((Team) model);
                break;
            case Skills:
                addSkill((Skill) model);
                break;
            case People:
                addPerson((Person) model);
                break;
            case Release:
                addRelease((Release) model);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        try {
            UndoRedoManager.assimilate(commitNumber);
        } catch (Exception e) {
            // This should never happen
            e.printStackTrace();
        }
        UndoRedoManager.add(model);
        commit("create " + type.toString().toLowerCase());
    }

    /**
     * Tries to remove an object from the model
     * @param model the object to remove from the model
     */
    public void remove(Model model) {
        ModelTypes type = ModelTypes.getModelType(model);
        long commitNumber = UndoRedoManager.getHead() == null ? 0 : UndoRedoManager.getHead().getCommitNumber();

        switch (type) {
            case Project:
                removeProject((Project) model);
                break;
            case Team:
                removeTeam((Team) model);
                break;
            case Skills:
                removeSkill((Skill) model);
                break;
            case People:
                removePerson((Person) model);
                break;
            case Release:
                removeRelease((Release) model);
                break;
            default:
                throw new UnsupportedOperationException();
        }

        try {
            UndoRedoManager.assimilate(commitNumber);
        } catch (Exception e) {
            // This should never happen
            e.printStackTrace();
        }
        UndoRedoManager.remove(model);
        commit("remove " + type.toString().toLowerCase());
    }

    /**
     * Adds the given release to the list of releases
     * @param release The release to add
     * @throws DuplicateObjectException Thrown if the releases given is a duplicate
     */
    private void addRelease(Release release) throws DuplicateObjectException {
        if (!releases.contains(release)) {
            this.releases.add(release);
        }
        else throw new DuplicateObjectException("This release already exists");
    }

    /**
     * Removes the specified release from the releases
     * @param release The release to remove
     */
    private void removeRelease(Release release) {
        if (this.releases.contains(release)) {
            releases.remove(release);
        }
    }

    /**
     * Gets the releases
     * @return The releases
     */
    public ModelObservableArrayList<Release> getReleases() {
        return releases;
    }

    /**
     * Adds an arraylist of releases to the project
     * @param releases The releases to be added
     * @throws DuplicateObjectException
     */
    public void addReleases(ArrayList<Release> releases) throws DuplicateObjectException{
        for (Release release : releases) {
            addRelease(release);
        }
    }

    /**
     * Determines whether or not a Model object is in use
     * @param model The model to check
     * @return Whether the model object is in use
     */
    public boolean inUse(Model model){
        return findUsages(model).size() != 0;
    }

    /**
     * Returns a list of the model objects that make use of this one
     * @param model The model to find the usages of
     * @return The different usages
     */
    public ArrayList<Model> findUsages(Model model){
        ModelTypes type = ModelTypes.getModelType(model);

        switch (type){
            case Project:
                return findUsages((Project)model);
            case Team:
                return findUsages((Team)model);
            case People:
                return findUsages((Person)model);
            case Skills:
                return findUsages((Skill)model);
        }
        return new ArrayList<>();
    }

    /**
     * Gets a list of places that a project is used
     * @param project The project to find the usages of
     * @return The usages of the project
     */
    private ArrayList<Model> findUsages(Project project){
        return new ArrayList<Model>();
    }

    /**
     * Gets a list of the places that a team is used
     * @param team The team to find the usages of
     * @return The usages of the team
     */
    private ArrayList<Model> findUsages(Team team){
        ArrayList<Model> usages = new ArrayList<>();
        for (Project p : getProjects()) {
            if (p.getTeams().contains(team)) {
                usages.add(p);
            }
        }
        return usages;
    }

    /**
     * Gets a list of all the places a person has been used
     * @param person The person to find usages for
     * @return The usages of the person
     */
    private ArrayList<Model> findUsages(Person person){
        ArrayList<Model> usages = new ArrayList<>();
        for (Team team : getTeams()){
            if (team.getMembers().contains(person)){
                usages.add(team);
            }
        }
        return usages;
    }

    /**
     * Gets a list of all the places a skill has been used
     * @param skill The skill to find usages for
     * @return The usages of the skill
     */
    private ArrayList<Model> findUsages(Skill skill){
        ArrayList<Model> usages = new ArrayList<>();
        for (Person person : getPeople()){
            if (person.getSkills().contains(skill)){
                usages.add(person);
            }
        }
        return usages;
    }

    /**
     * Checks to see if an object exists in the model
     * @param model The model
     * @return Whether it exists
     */
    public boolean exists(Model model) {
        if (model instanceof Project)
            return getProjects().contains(model);
        if (model instanceof Team)
            return getTeams().contains(model);
        if (model instanceof Person)
            return getPeople().contains(model);
        if (model instanceof Skill)
            return getSkills().contains(model);
        return false;
    }
}
