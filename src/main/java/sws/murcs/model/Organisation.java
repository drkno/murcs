package sws.murcs.model;

import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.InvalidParameterException;
import sws.murcs.exceptions.OverlappedDatesException;
import sws.murcs.magic.tracking.TrackableObject;
import sws.murcs.magic.tracking.TrackableValue;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.observable.ModelObservableArrayList;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The top level organisation. Manages the Model types within the application. This involves the adding, removing,
 * and examining of the different types of model.
 */
public class Organisation extends TrackableObject implements Serializable {

    /**
     * The list of projects currently loaded in the application.
     */
    @TrackableValue
    private final List<Project> projects;
    /**
     * The list of releases.
     */
    @TrackableValue
    private final List<Release> releases;

    /**
     * The list of work allocations.
     */
    @TrackableValue
    private final List<WorkAllocation> allocations;

    /**
     * The list of teams.
     */
    @TrackableValue
    private final List<Team> teams;

    /**
     * The list of people.
     */
    @TrackableValue
    private final List<Person> people;

    /**
     * The list of skills.
     */
    @TrackableValue
    private final List<Skill> skills;

    /**
     * The list of backlogs.
     */
    @TrackableValue
    private final List<Backlog> backlogs;

    /**
     * The list of stories.
     */
    @TrackableValue
    private final List<Story> stories;

    /**
     * Gets the current application VERSION.
     * @return The current application VERSION.
     */
    public static String getVersion() {
        return VERSION;
    }

    /**
     * The version number of the application.
     */
    private static final String VERSION = "0.0.3";

    /**
     * Sets up a new organisation.
     */
    public Organisation() {
        this.projects = new ModelObservableArrayList<>();
        this.releases = new ModelObservableArrayList<>();
        this.allocations = new ArrayList<>();
        this.teams = new ModelObservableArrayList<>();
        this.people = new ModelObservableArrayList<>();
        this.skills = new ModelObservableArrayList<>();
        this.backlogs = new ModelObservableArrayList<>();
        this.stories = new ModelObservableArrayList<>();

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
            // Will never ever happen! Like, ever! An exception is only thrown
            // if you try to set the shortName as null or empty.
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
     * Gets the releases.
     * @return The releases
     */
    public final List<Release> getReleases() {
        return releases;
    }

    /**
     * Gets a list of all allocations.
     * @return A list of allocations
     */
    public final List<WorkAllocation> getAllocations() {
        return allocations;
    }

    /**
     * Gets a list of all teams.
     * @return The teams
     */
    public final List<Team> getTeams() {
        return teams;
    }

    /**
     * Gets a list of all people.
     * @return The people
     */
    public final List<Person> getPeople() {
        return people;
    }

    /**
     * Gets the skills.
     * @return The skills
     */
    public final List<Skill> getSkills() {
        return skills;
    }

    /**
     * Gets the backlogs.
     * @return The backlogs
     */
    public final List<Backlog> getBacklogs() {
        return backlogs;
    }

    /**
     * Gets a stories.
     * @return The stories
     */
    public final List<Story> getStories() {
        return stories;
    }

    /**
     * Adds a new project.
     * @param project The new project.
     * @throws DuplicateObjectException if the project already exists.
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
     * Removes the given project if it exists.
     * @param project The project to remove
     */
    private void removeProject(final Project project) {
        if (this.projects.contains(project)) {
            this.projects.remove(project);
        }

        //Remove all work allocations associated with the project
        getProjectsAllocations(project).forEach(allocations::remove);

        //Remove all the releases associated with the project
        project.getReleases().forEach(this::removeRelease);
    }

    /**
     * Gets the unassigned people.
     * @return The unassigned people
     */
    public final Collection<Person> getUnassignedPeople() {
        Set<Person> assignedPeople = new TreeSet<>((p1, p2) -> {
            if (p1.equals(p2)) {
                return 0;
            }

            return p1.getShortName().toLowerCase().compareTo(p2.getShortName().toLowerCase());

        });
        getTeams().forEach(t -> assignedPeople.addAll(t.getMembers()));
        Set<Person> unassignedPeople = new TreeSet<>((p1, p2) -> {
            if (p1.equals(p2)) {
                return 0;
            }

            return p1.getShortName().toLowerCase().compareTo(p2.getShortName().toLowerCase());
        });
        unassignedPeople.addAll(getPeople());
        unassignedPeople.removeAll(assignedPeople);
        return unassignedPeople;
    }

    /**
     * Gets a list of all the stories that aren't assigned to any backlog currently.
     * @return the unassigned stories.
     */
    public final Collection<Story> getUnassignedStories() {
        Set<Story> assignedStories = new TreeSet<>((s1, s2) -> {
            if (s1.equals(s2)) {
                return 0;
            }

            return s1.getShortName().toLowerCase().compareTo(s2.getShortName().toLowerCase());

        });
        getBacklogs().forEach(t -> assignedStories.addAll(t.getStories()));
        Set<Story> unassignedStories = new TreeSet<>((s1, s2) -> {
            if (s1.equals(s2)) {
                return 0;
            }

            return s1.getShortName().toLowerCase().compareTo(s2.getShortName().toLowerCase());
        });
        unassignedStories.addAll(getStories());
        unassignedStories.removeAll(assignedStories);
        return unassignedStories;
    }

    /**
     * Adds a person to the model if it doesn't exits.
     * @param person to be added
     * @throws DuplicateObjectException if the organisation
     * already has the person
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
                    //If this happens we're in deep doo doo
                    e.printStackTrace();
                }
                team.removeMember(person);
            });
        }
    }

    /**
     * Gets a list of all the teams that aren't assigned to any project currently.
     * @return the unassigned teams.
     */
    public final List<Team> getUnassignedTeams() {
        return teams
                .stream()
                .filter(team -> !allocations
                        .stream()
                        .filter(a -> a.getTeam().equals(team)).findAny()
                        .isPresent())
                .collect(Collectors.toList());
    }

    /**
     * Adds a team to the unassigned teams if the
     * organisation does not already have that team.
     * @param team The unassigned team to add
     * @throws DuplicateObjectException if the organisation
     * already has the team.
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
     * Removes a team from the list of teams and from any projects.
     * @param team The team to remove
     */
    private void removeTeam(final Team team) {
        if (this.teams.contains(team)) {
            this.teams.remove(team);
        }

        // Do not convert to .forEach() or .stream(). It will cause a ConcurrentModificationException.
        for (int i = 0; i < allocations.size(); i++) {
            if (allocations.get(i).getTeam() == team) {
                removeAllocation(allocations.get(i));
                i--;
            }
        }
    }

    /**
     * Adds a work allocation to the model.
     * @param workAllocation The work period to be added.
     * @throws InvalidParameterException when attempting to add a duplicate object or if the object is null.
     * @throws OverlappedDatesException when dates for the work allocation are invalid.
     */
    public final void addAllocation(final WorkAllocation workAllocation)
            throws InvalidParameterException, OverlappedDatesException {
        if (workAllocation == null) {
            throw new InvalidParameterException("Cannot add a null WorkAllocation.");
        }

        Team team = workAllocation.getTeam();
        LocalDate startDate = workAllocation.getStartDate();
        LocalDate endDate = workAllocation.getEndDate();

        if (endDate != null && startDate.isAfter(endDate)) {
            throw new InvalidParameterException("End Date is before Start Date");
        }

        if (endDate != null) {
            for (WorkAllocation allocation : allocations) {
                if (allocation.getTeam().equals(team)) {
                    // Check that this team isn't overlapping with itself
                    if (allocation.getEndDate() != null) {
                        if ((allocation.getStartDate().isBefore(endDate)
                                && allocation.getEndDate().isAfter(startDate))) {
                            throw new OverlappedDatesException("Work Dates Overlap");
                        }
                    }
                    else if (allocation.getStartDate().isBefore(endDate)) {
                        throw new OverlappedDatesException("Work Dates Overlap");
                    }
                }
            }
        }
        else {
            for (WorkAllocation allocation : allocations) {
                if (allocation.getTeam() == team) {
                    if (allocation.getEndDate() == null || allocation.getEndDate().isAfter(startDate)) {
                        throw new OverlappedDatesException("Work Dates Overlap");
                    }
                }
            }
        }
        allocations.add(workAllocation);
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
     * @param allocation The work allocation to remove.
     */
    public final void removeAllocation(final WorkAllocation allocation) {
        if (allocations.contains(allocation)) {
            allocations.remove(allocation);
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
     * Adds a skill to skills only if the skill does not already exist.
     * @param skill The skill to add
     * @throws DuplicateObjectException if the skill already exists in the organisation
     */
    private void addSkill(final Skill skill) throws DuplicateObjectException {
        if (!skills.contains(skill)) {
            skills.add(skill);
        }
        else {
            throw new DuplicateObjectException("Skill already exists");
        }
    }

    /**
     * Removes a skill from skills.
     * @param skill The skill to remove
     */
    private void removeSkill(final Skill skill) {
        if (skills.contains(skill)) {
            skills.remove(skill);

            //Remove the skill from any people who might have it
            getPeople()
                    .stream()
                    .filter(person -> person.getSkills().contains(skill))
                    .forEach(person -> person.removeSkill(skill));
        }
    }

    /**
     * Gets the skills that have not been already assigned to a person.
     * @param person the person to check skills against.
     * @return collection of skills.
     */
    public final Collection<Skill> getAvailableSkills(final Person person) {
        Set<Skill> assignedSkills = new TreeSet<>((s1, s2) -> {
            if (s1.equals(s2)) {
                return 0;
            }

            return s1.getShortName().compareTo(s2.getShortName());

        });
        assignedSkills.addAll(person.getSkills());
        Set<Skill> allSkills = new TreeSet<>((s1, s2) -> {
            if (s1.equals(s2)) {
                return 0;
            }

            return s1.getShortName().compareTo(s2.getShortName());
        });
        allSkills.addAll(skills);
        allSkills.removeAll(assignedSkills);
        return Collections.unmodifiableCollection(allSkills);
    }

    /**
     * Adds a backlog to backlogs only if the backlog does not already exist.
     * @param backlog The backlog to add
     * @throws DuplicateObjectException if the backlog already exists in the organisation
     */
    private void addBacklog(final Backlog backlog) throws DuplicateObjectException {
        if (!backlogs.contains(backlog)) {
            backlogs.add(backlog);
        }
        else {
            throw new DuplicateObjectException("Backlog already exists");
        }
    }

    /**
     * Remove a backlog from backlogs.
     * @param backlog The backlog to remove
     */
    private void removeBacklog(final Backlog backlog) {
        if (backlogs.contains(backlog)) {
            backlogs.remove(backlog);
        }

        //Remove the backlog from any project that might contain it.
        getProjects().stream()
                .forEach(project -> project.removeBacklog(backlog));
    }

    /**
     * Tries to add the object to the model.
     * @param model the object to add to the model.
     * @throws DuplicateObjectException because you tried to add an object that already exists.
     * @throws InvalidParameterException if the object that is being added is invalid.
     */
    public final void add(final Model model) throws DuplicateObjectException, InvalidParameterException {
        ModelType type = ModelType.getModelType(model);

        if (model.getShortName() == null || model.getShortName().equals("")) {
            throw new InvalidParameterException("Model objects must have a name before being added.");
        }

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
            case Skill:
                addSkill((Skill) model);
                break;
            case Person:
                addPerson((Person) model);
                break;
            case Release:
                addRelease((Release) model);
                break;
            case Story:
                addStory((Story) model);
                break;
            case Backlog:
                addBacklog((Backlog) model);
                break;
            default:
                throw new UnsupportedOperationException("Adding of this model type has not yet been implemented.");
        }

        try {
            UndoRedoManager.assimilate(commitNumber);
        }
        catch (Exception e) {
            // This will never happen
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
        ModelType type = ModelType.getModelType(model);
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
            case Skill:
                removeSkill((Skill) model);
                break;
            case Person:
                removePerson((Person) model);
                break;
            case Release:
                removeRelease((Release) model);
                break;
            case Story:
                removeStory((Story) model);
                break;
            case Backlog:
                removeBacklog((Backlog) model);
                break;
            default:
                throw new UnsupportedOperationException("We don't know what to do with this model (remove for "
                        + model.getClass().getName()
                        + ") in organisation. You should fix this");
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
            releases.add(release);
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
        projects.stream()
                .filter(project -> project.getReleases().contains(release))
                .forEach(project -> project.removeRelease(release));
    }

    /**
     * Adds a story to the model.
     * @param story The story to add to the model
     * @throws sws.murcs.exceptions.DuplicateObjectException If the model already contains the story you are trying
     * to add
     */
    private void addStory(final Story story) throws DuplicateObjectException {
        if (!stories.contains(story)) {
            stories.add(story);
        }
        else {
            throw new DuplicateObjectException("We already have that story!!!");
        }
    }

    /**
     * Removes the story from the organisation.
     * @param story The story to remove
     */
    private void removeStory(final Story story) {
        if (stories.contains(story)) {
            stories.remove(story);
        }

        backlogs.forEach(backlog -> backlog.removeStory(story));
    }

    /**
     * Adds all model items from a collection to the Organisation.
     * For use with generators that mass produce model items.
     * @param items model objects to add to Organisation.
     * @throws DuplicateObjectException when a duplicate object already exists in the organisation.
     * @throws InvalidParameterException when an item being added is invalid (eg null).
     */
    public final void addCollection(final Collection<? extends Model> items)
            throws DuplicateObjectException, InvalidParameterException {
        for (Model item : items) {
            add(item);
        }
    }
}
