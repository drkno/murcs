package sws.murcs.model;

import sws.murcs.controller.ModelTypes;
import sws.murcs.exceptions.CustomException;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.observable.ModelObservableArrayList;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The top level relational model.
 */
public class RelationalModel extends TrackableObject implements Serializable {

    /**
     * The list of projects currently loaded in the application.
     */
    @TrackableValue
    private List<Project> projects;
    /**
     * The list of work allocations.
     */
    @TrackableValue
    private List<WorkAllocation> allocations;
    /**
     * The list of people.
     */
    @TrackableValue
    private List<Person> people;
    /**
     * The list of teams.
     */
    @TrackableValue
    private List<Team> teams;
    /**
     * The list of skills.
     */
    @TrackableValue
    private List<Skill> skills;
    /**
     * The list of releases.
     */
    @TrackableValue
    private List<Release> releases;

    /**
     * Gets the current application VERSION.
     * @return The current application VERSION.
     */
    public static float getVersion() {
        return VERSION;
    }

    /**
     * The version number of the application.
     */
    private static final float VERSION = 0.02f;

    /**
     * Sets up a new Relational Model.
     */
    public RelationalModel() {
        this.allocations = new ArrayList<>();
        this.people = new ModelObservableArrayList<Person>();
        this.teams = new ModelObservableArrayList<Team>();
        this.skills = new ModelObservableArrayList<Skill>();
        this.releases = new ModelObservableArrayList<Release>();
        this.projects = new ModelObservableArrayList<Project>();

        try {
            Skill productOwner = new Skill();
            productOwner.setShortName("PO");
            productOwner.setLongName("Product Owner");
            productOwner.setDescription("The projects main stakeholder. Responsible for making sure "
                    + "that their vision for the product is realised.");
            this.skills.add(productOwner);

            Skill scrumMaster = new Skill();
            scrumMaster.setShortName("SM");
            scrumMaster.setLongName("Scrum Master");
            scrumMaster.setDescription("Manages the efforts of a team, resolves difficulties and removes "
                    + "obsticles to task completion.");
            this.skills.add(scrumMaster);
        } catch (Exception e) {
            // will never ever happen. ever. an exception is only
            // thrown if you try to set the shortname as null/empty
            e.printStackTrace();
        }
    }

    /**
     * Gets the projects.
     * @return The projects
     */
    public final List<Project> getProjects() {
        return projects;
    }

    /**
     * Adds a new project.
     * @param project The new project
     * @throws DuplicateObjectException if the project already exists
     */
    private void addProject(final Project project) throws DuplicateObjectException {
        if (!this.getProjects().contains(project)
                && !this.getProjects()
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
     * Adds all given projects that are not already contained within the model.
     * @param projectsToAdd A List of projects to be added to the model
     * @throws DuplicateObjectException If one of the projects already exist
     */
    public final void addProjects(final Collection<Project> projectsToAdd) throws DuplicateObjectException {
        boolean badProject = false;
        for (Project project : projectsToAdd) {
            if (this.projects.contains(project)
                    || this.getProjects()
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
        if (badProject) {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Removes the given project if it exists.
     * @param project The project to remove
     */
    private void removeProject(final Project project) {
        if (this.projects.contains(project)) {
            this.projects.remove(project);
        }

        //Remove all the releases associated with the project
        for (Release release : project.getReleases()) {
            removeRelease(release);
        }
    }

    /**
     * Gets the unassigned people.
     * @return The unassigned people
     */
    public final Set<Person> getUnassignedPeople() {
        Set<Person> assignedPeople = new TreeSet<>((p1, p2) -> {
            return p1.getShortName().compareTo(p2.getShortName());
        });
        getTeams().forEach(t -> assignedPeople.addAll(t.getMembers()));
        Set<Person> unassignedPeople = new TreeSet<>((p1, p2) -> {
            return p1.getShortName().compareTo(p2.getShortName());
        });
        unassignedPeople.addAll(getPeople());
        unassignedPeople.removeAll(assignedPeople);
        return unassignedPeople;
    }

    /**
     * Gets a list of all people.
     * @return The people
     */
    public final List<Person> getPeople() {
        return people;
    }

    /**
     * Adds a person to the model if it doesn't exits.
     * @param person to be added
     * @throws DuplicateObjectException if the relational
     * model already has the person
     */
    private void addPerson(final Person person) throws DuplicateObjectException {
        if (!this.getPeople().contains(person)
                && !this.getPeople()
                        .stream()
                        .filter(s -> s.equals(person))
                        .findAny()
                        .isPresent()) {
            this.getPeople().add(person);
        }
        else {
            throw new DuplicateObjectException();
        }
    }

    /**
     * Adds a list of people to the model.
     * @param newPeople People to be added
     * @throws DuplicateObjectException if the
     * relational model already has a person from the people to be added
     */
    public final void addPeople(final ArrayList<Person> newPeople) throws DuplicateObjectException {
        for (Person person : newPeople) {
            this.addPerson(person);
        }
    }

    /**
     * Removes a person from unassigned people.
     * @param person The unassigned person to remove
     */
    private void removePerson(final Person person) {
        if (this.getPeople().contains(person)) {
            this.getPeople().remove(person);
            //Remove the person from any team they might be in
            //Check to see if they assigned a role in any
            //team and if so remove them from this role
            getTeams().stream().filter(team -> team.getMembers().contains(person)).forEach(team -> {
                try {
                    if (team.getProductOwner() != null && team.getProductOwner().equals(person)) {
                        team.setProductOwner(null);
                    }
                    if (team.getScrumMaster() != null && team.getScrumMaster().equals(person)) {
                        team.setScrumMaster(null);
                    }
                } catch (Exception e) {
                    //If this happens we're in deep doodoo
                    e.printStackTrace();
                }
                team.removeMember(person);
            });
        }
    }

    /**
     * Gets a list of all teams.
     * @return The teams
     */
    public final List<Team> getTeams() {
        return teams;
    }

    /**
     * Gets a list of all the teams that aren't assigned to
     * any project currently.
     * @return the unassigned teams
     */
    public final List<Team> getUnassignedTeams() {
        List<Team> unassignedTeams = new ArrayList<Team>();
        for (Team team : teams) {
            if (!allocations.stream().filter(a -> a.getTeam().equals(team)).findAny().isPresent()) {
                unassignedTeams.add(team);
            }
        }
        return unassignedTeams;
    }

    /**
     * Adds a team to the unassigned teams if the
     * relational model does not already have that team.
     * @param team The unassigned team to add
     * @throws DuplicateObjectException if the relational
     * model already has the team.
     */
    private void addTeam(final Team team) throws DuplicateObjectException {
        if (!this.teams.contains(team)
                && !this.teams
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
     * Adds a list of teams to add to the relational model.
     * @param teamsToAdd Teams to be added to the relational model
     * @throws DuplicateObjectException if the murcs already has
     * a team from teams to be added
     */
    public final void addTeams(final ArrayList<Team> teamsToAdd) throws DuplicateObjectException {
        for (Team team : teamsToAdd) {
            this.addTeam(team);
        }
    }

    /**
     * Removes a team from the list of teams and from any projects.
     * @param team The team to remove
     */
    private void removeTeam(final Team team) {
        if (this.teams.contains(team)) {
            this.teams.remove(team);
        }

        for (int i = 0; i < allocations.size(); i++) {
            WorkAllocation allocation = allocations.get(i);
            if (allocation.getTeam() == team) {
                removeAllocation(allocation);
                i--;
            }
        }
    }

    /**
     * Adds a work allocation to the model.
     * @param workAllocation The work period to be added
     * @throws CustomException when attempting to add a duplicate object.
     */
    public final void addAllocation(final WorkAllocation workAllocation) throws CustomException {
        Team team = workAllocation.getTeam();
        LocalDate startDate = workAllocation.getStartDate();
        LocalDate endDate = workAllocation.getEndDate();

        if (startDate.isAfter(endDate)) {
            throw new CustomException("End Date is before Start Date");
        }

        int index = 0;
        for (WorkAllocation allocation : this.allocations) {
            if (allocation.getTeam() == team) {
                // Check that this team isn't overlapping with itself
                if ((allocation.getStartDate().isBefore(endDate) && allocation.getEndDate().isAfter(startDate))) {
                    throw new DuplicateObjectException("Work Dates Overlap");
                }
            }
            if (allocation.getStartDate().isBefore(startDate)) {
                // Increment the index where the allocation will be placed
                // if it does get placed
                index++;
            }
            else if (allocation.getStartDate().isAfter(endDate)) {
                // At this point we've checked all overlapping allocations
                // and haven't found any errors
                break;
            }
        }
        this.allocations.add(index, workAllocation);
        commit("edit project");
    }

    /**
     * Adds a list of allocations to the existing allocations.
     * @param allocationsToAdd allocations to add.
     * @throws Exception when adding the allocations failed.
     */
    public final void addAllocations(final List<WorkAllocation> allocationsToAdd) throws Exception {
        long commitNumber;
        if (UndoRedoManager.getHead() == null) {
            commitNumber = 0;
        }
        else {
            commitNumber = UndoRedoManager.getHead().getCommitNumber();
        }
        for (WorkAllocation allocation : allocationsToAdd) {
            addAllocation(allocation);
        }
        UndoRedoManager.assimilate(commitNumber);
        commit("edit project");
    }

    /**
     * Removes a given allocation.
     * @param allocation The work allocation to remove
     */
    public final void removeAllocation(final WorkAllocation allocation) {
        if (this.allocations.contains(allocation)) {
            this.allocations.remove(allocation);
        }
        commit("edit project");
    }

    /**
     * Gets a list of all a projects work allocations.
     * @param project The project to check allocations for
     * @return A list of work allocations
     */
    public final List<WorkAllocation> getProjectsAllocations(final Project project) {
        return allocations.stream().filter(a -> a.getProject().equals(project)).collect(Collectors.toList());
    }

    /**
     * Gets a list of all allocations.
     * @return A list of allocations
     */
    public final List<WorkAllocation> getAllAllocations() {
        return allocations;
    }

    /**
     * Gets the skills.
     * @return The skills
     */
    public final List<Skill> getSkills() {
        return skills;
    }

    /**
     * Adds a skill to skills only if the skill does not already exist.
     * @param skill The skill to add
     * @throws DuplicateObjectException if the skill already exists in the relational model
     */
    private void addSkill(final Skill skill) throws DuplicateObjectException {
        if (!skills.contains(skill)) {
            this.skills.add(skill);
        }
        else {
            throw new DuplicateObjectException("Skill already exists");
        }
    }

    /**
     * Adds a list of skills to the existing list of skills.
     * @param skillsToAdd Skills to be added existing skills
     * @throws DuplicateObjectException if a skill is already in the relational model
     */
    public final void addSkills(final ArrayList<Skill> skillsToAdd) throws DuplicateObjectException {
        for (Skill skill : skillsToAdd) {
            this.addSkill(skill);
        }
    }

    /**
     * Removes a skill from skills.
     * @param skill The skill to remove
     */
    private void removeSkill(final Skill skill) {
        if (skills.contains(skill)) {
            this.skills.remove(skill);

            //Remove the skill from any people who might have it
            getPeople()
                    .stream()
                    .filter(person -> person.getSkills().contains(skill))
                    .forEach(person -> person.removeSkill(skill));
        }
    }

    /**
     * Tries to add the object to the model.
     * @param model the object to add to the model
     * @throws DuplicateObjectException because you tried to add an object that already exists
     */
    public final void add(final Model model) throws DuplicateObjectException {
        ModelTypes type = ModelTypes.getModelType(model);
        long commitNumber;
        if (UndoRedoManager.getHead() == null) {
            commitNumber = 0;
        }
        else {
            commitNumber = UndoRedoManager.getHead().getCommitNumber();
        }
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
     * Tries to remove an object from the model.
     * @param model the object to remove from the model
     */
    public final void remove(final Model model) {
        ModelTypes type = ModelTypes.getModelType(model);
        long commitNumber;
        if (UndoRedoManager.getHead() == null) {
            commitNumber = 0;
        }
        else {
            commitNumber = UndoRedoManager.getHead().getCommitNumber();
        }

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
                throw new UnsupportedOperationException("We don't know what to do with this model (remove for "
                        + model.getClass().getName()
                        + ") in Relational Model. You should fix this");
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
     * Adds the given release to the list of releases.
     * @param release The release to add
     * @throws DuplicateObjectException Thrown if the releases given is a duplicate
     */
    private void addRelease(final Release release) throws DuplicateObjectException {
        if (!releases.contains(release)) {
            this.releases.add(release);
        }
        else {
            throw new DuplicateObjectException("This release already exists");
        }
    }

    /**
     * Removes the specified release from the releases.
     * @param release The release to remove
     */
    private void removeRelease(final Release release) {
        if (this.releases.contains(release)) {
            releases.remove(release);
        }

        //Now remove it from the project
        projects.stream().filter(project -> project.getReleases().contains(release)).forEach(project -> {
            project.removeRelease(release);
        });
    }

    /**
     * Gets the releases.
     * @return The releases
     */
    public final List<Release> getReleases() {
        return releases;
    }

    /**
     * Adds an arraylist of releases to the project.
     * @param releasesToAdd The releases to be added
     * @throws DuplicateObjectException when attempting to add a duplicate release.
     */
    public final void addReleases(final ArrayList<Release> releasesToAdd) throws DuplicateObjectException {
        for (Release release : releasesToAdd) {
            addRelease(release);
        }
    }

    /**
     * Determines whether or not a Model object is in use.
     * @param model The model to check
     * @return Whether the model object is in use
     */
    public final boolean inUse(final Model model) {
        return findUsages(model).size() != 0;
    }

    /**
     * Returns a list of the model objects that make use of this one.
     * @param model The model to find the usages of
     * @return The different usages
     */
    public final ArrayList<Model> findUsages(final Model model) {
        ModelTypes type = ModelTypes.getModelType(model);

        switch (type) {
            case Project:
                return findUsages((Project) model);
            case Team:
                return findUsages((Team) model);
            case People:
                return findUsages((Person) model);
            case Skills:
                return findUsages((Skill) model);
            case Release:
                return findUsages((Release) model);
            default:
                throw new UnsupportedOperationException("We don't know what to do with this model (findUsages for "
                        + model.getClass().getName()
                        + ") in Relational Model. You should fix this");
        }
    }

    /**
     * Gets a list of all the places a release is used.
     * @param release The release
     * @return The places the release is used
     */
    private ArrayList<Model> findUsages(final Release release) {
        return new ArrayList<>();
    }

    /**
     * Gets a list of places that a project is used.
     * @param project The project to find the usages of
     * @return The usages of the project
     */
    private ArrayList<Model> findUsages(final Project project) {
        ArrayList<Model> usages = new ArrayList<>();
        for (Release release : project.getReleases()) {
            usages.add(release);
        }
        return usages;
    }

    /**
     * Gets a list of the places that a team is used.
     * @param team The team to find the usages of
     * @return The usages of the team
     */
    private ArrayList<Model> findUsages(final Team team) {
        ArrayList<Model> usages = new ArrayList<>();
        for (Project project : getProjects()) {
            for (WorkAllocation allocation : getProjectsAllocations(project)) {
                if (allocation.getTeam() == team) {
                    usages.add(project);
                    break; // Move to checking the next project for this team
                }
            }
        }
        return usages;
    }

    /**
     * Gets a list of all the places a person has been used.
     * @param person The person to find usages for
     * @return The usages of the person
     */
    private ArrayList<Model> findUsages(final Person person) {
        ArrayList<Model> usages = new ArrayList<>();
        for (Team team : getTeams()) {
            if (team.getMembers().contains(person)) {
                usages.add(team);
            }
        }
        return usages;
    }

    /**
     * Gets a list of all the places a skill has been used.
     * @param skill The skill to find usages for
     * @return The usages of the skill
     */
    private ArrayList<Model> findUsages(final Skill skill) {
        ArrayList<Model> usages = new ArrayList<>();
        for (Person person : getPeople()) {
            if (person.getSkills().contains(skill)) {
                usages.add(person);
            }
        }
        return usages;
    }

    /**
     * Checks to see if an object exists in the model.
     * @param model The model
     * @return Whether it exists
     */
    public final boolean exists(final Model model) {
        switch (ModelTypes.getModelType(model)) {
            case Project:
                return getProjects().contains(model);
            case People:
                return getPeople().contains(model);
            case Team:
                return getTeams().contains(model);
            case Skills:
                return getSkills().contains(model);
            case Release:
                return getReleases().contains(model);
            default:
                throw new UnsupportedOperationException("We don't know what to do with this model (exists for "
                        + model.getClass().getName()
                        + ") in Relational Model. You should fix this");
        }
    }
}
