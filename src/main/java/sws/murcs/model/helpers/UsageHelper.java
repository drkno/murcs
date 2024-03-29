package sws.murcs.model.helpers;

import sws.murcs.model.Backlog;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Organisation;
import sws.murcs.model.Person;
import sws.murcs.model.Project;
import sws.murcs.model.Release;
import sws.murcs.model.Skill;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;
import sws.murcs.model.persistence.PersistenceManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Helps to find usages of a model within the current organisation. This is a singleton class and is not designed to be
 * extended or to have new instances of the class made. In fact it won't allow you to make new instances of this class.
 * There are three public functions to use. inUse, findUsages or exists. All of these take a Model parameter and search
 * the current organisation model to give you the appropriate results.
 */
public final class UsageHelper {

    /**
     * Empty private constructor as this is a utility class.
     */
    private UsageHelper() {
    }

    /**
     * Determines whether or not a Model object is in use.
     * @param model The model to check
     * @return Whether the model object is in use
     */
    public static boolean inUse(final Model model) {
        return findUsages(model).size() != 0;
    }

    /**
     * Finds usages of a model and filters them using a predicate.
     * @param model The model to find usages for
     * @param filter The filter
     * @return The usages
     */
    public static List<Model> findUsages(final Model model, final Predicate<Model> filter) {
        return findUsages(model).stream().filter(filter).collect(Collectors.toList());
    }

    /**
     * Returns a list of the model objects that make use of this one.
     * @param model The model to find the usages of
     * @return The different usages
     */
    public static List<Model> findUsages(final Model model) {
        ModelType type = ModelType.getModelType(model);
        switch (type) {
            case Project:
                return findUsages((Project) model);
            case Team:
                return findUsages((Team) model);
            case Person:
                return findUsages((Person) model);
            case Skill:
                return findUsages((Skill) model);
            case Release:
                return findUsages((Release) model);
            case Story:
                return findUsages((Story) model);
            case Backlog:
                return findUsages((Backlog) model);
            case Sprint:
                return findUsages((Sprint) model);
            default:
                throw new UnsupportedOperationException("We don't know what to do with this model (findUsages for "
                        + model.getClass().getName()
                        + ") in organisation. You should fix this");
        }
    }

    /**
     * Gets a list of all the places a release is used.
     * Releases are not currently in use anywhere, so this will return an empty list.
     * @param release The release.
     * @return The places the release is used.
     */
    private static List<Model> findUsages(final Release release) {
        Organisation currentModel = PersistenceManager.getCurrent().getCurrentModel();
        List<Model> usages = new ArrayList<>();
        currentModel.getSprints().stream()
                .filter(sprint -> release.equals(sprint.getAssociatedRelease()))
                .forEach(sprint -> usages.add(sprint));
        return usages;
    }

    /**
     * Gets a list of places that a project is used.
     * @param project The project to find the usages of
     * @return The usages of the project
     */
    private static List<Model> findUsages(final Project project) {
        return project.getReleases()
                .stream()
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of the places that a team is used.
     * @param team The team to find the usages of
     * @return The usages of the team
     */
    private static List<Model> findUsages(final Team team) {
        Organisation currentModel = PersistenceManager.getCurrent().getCurrentModel();
        List<Model> usages = new ArrayList<>();
        for (Project project : currentModel.getProjects()) {
            for (WorkAllocation allocation : currentModel.getProjectsAllocations(project)) {
                if (allocation.getTeam() == team) {
                    usages.add(project);
                    break; // Move to checking the next project for this team
                }
            }
        }

        //Add all the sprints that use this team
        currentModel.getSprints()
                .stream()
                .filter(sprint -> team.equals(sprint.getTeam()))
                .forEach(sprint -> usages.add(sprint));

        return usages;
    }

    /**
     * Gets a list of all the places a person has been used.
     * @param person The person to find usages for
     * @return The usages of the person
     */
    private static List<Model> findUsages(final Person person) {
        Organisation currentModel = PersistenceManager.getCurrent().getCurrentModel();
        return currentModel.getTeams().stream()
                .filter(team -> team.getMembers().contains(person))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of all the places a skill has been used.
     * @param skill The skill to find usages for
     * @return The usages of the skill
     */
    private static List<Model> findUsages(final Skill skill) {
        Organisation currentModel = PersistenceManager.getCurrent().getCurrentModel();
        return currentModel.getPeople().stream()
                .filter(person -> person.getSkills().contains(skill))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of places that sprint is used.
     * @param sprint The sprint to find usages for
     * @return The usages of the sprint
     */
    private static List<Model> findUsages(final Sprint sprint) {
        return new ArrayList<>();
    }

    /**
     * Gets a list of all the places that a backlog has been used.
     * @param backlog The backlog to find the usages for
     * @return The usages of the backlog
     */
    private static List<Model> findUsages(final Backlog backlog) {
        Organisation currentModel = PersistenceManager.getCurrent().getCurrentModel();

        List<Model> usages = new ArrayList<>();

        //Add all the projects
        currentModel.getProjects().stream()
                .filter(project -> project.getBacklogs().contains(backlog))
                .forEach(project -> usages.add(project));

        //Add all the sprints
        currentModel.getSprints().stream()
                .filter(sprint -> backlog.equals(sprint.getBacklog()))
                .forEach(sprint -> usages.add(sprint));

        return usages;
    }

    /**
     * Gets a list of all the places that a story has been used.
     * @param story The story to find the usages for
     * @return The usages of the story
     */
    private static List<Model> findUsages(final Story story) {
        Organisation currentModel = PersistenceManager.getCurrent().getCurrentModel();

        List<Model> usages = new ArrayList<>();

        currentModel.getBacklogs().stream()
                .filter(backlog -> backlog.getAllStories().contains(story))
                .forEach(usages::add);

        currentModel.getStories().stream()
                .filter(s -> s.getDependencies().contains(story))
                .forEach(usages::add);

        currentModel.getSprints().stream()
                .filter(sprint -> sprint.getStories().contains(story))
                .forEach(usages::add);

        return usages;
    }

    /**
     * Gets a list of all the places that a task has been used.
     * @param task The task to find the usages for
     * @return The usages of the story
     */
    private static List<Model> findUsages(final Task task) {
        Organisation currentModel = PersistenceManager.getCurrent().getCurrentModel();
        for (Story story : currentModel.getStories()) {
            if (story.getTasks().contains(task)) {
                List<Model> list = new ArrayList<>();
                list.add(story);
                return list;
            }
        }
        return null;
    }

    /**
     * Finds all model objects in the organisation from a predicate.
     * If there is no Organisation to search, null will be returned.
     * @param type type that should be search.
     * @param predicate the predicate to match.
     * @param <T> the model type to return and search.
     * @return the first instance that meets the criteria, or null if not found.
     */
    public static <T extends Model> List<T> findAllBy(final ModelType type, final Predicate<T> predicate) {
        if (PersistenceManager.getCurrent() == null) {
            return null;
        }

        Organisation currentModel = PersistenceManager.getCurrent().getCurrentModel();
        if (currentModel == null) {
            return null;
        }

        List<T> list = null;
        switch (type) {
            case Skill: list = (List<T>) currentModel.getSkills(); break;
            case Person: list = (List<T>) currentModel.getPeople(); break;
            case Project: list = (List<T>) currentModel.getProjects(); break;
            case Team: list = (List<T>) currentModel.getTeams(); break;
            case Release: list = (List<T>) currentModel.getReleases(); break;
            case Story: list = (List<T>) currentModel.getStories(); break;
            case Backlog: list = (List<T>) currentModel.getBacklogs(); break;
            case Sprint: list = (List<T>) currentModel.getSprints(); break;
            default: throw new UnsupportedOperationException("This type of model is unsupported (fixme!).");
        }
        return list.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Finds a model object in the organisation from a predicate.
     * If there is no Organisation to search, null will be returned.
     * @param type type that should be search.
     * @param predicate the predicate to match.
     * @param <T> the model type to return and search.
     * @return the first instance that meets the criteria, or null if not found.
     */
    public static <T extends Model> T findBy(final ModelType type, final Predicate<T> predicate) {
        if (PersistenceManager.getCurrent() == null) {
            return null;
        }

        Organisation currentModel = PersistenceManager.getCurrent().getCurrentModel();
        if (currentModel == null) {
            return null;
        }

        List<T> list = null;
        switch (type) {
            case Skill: list = (List<T>) currentModel.getSkills(); break;
            case Person: list = (List<T>) currentModel.getPeople(); break;
            case Project: list = (List<T>) currentModel.getProjects(); break;
            case Team: list = (List<T>) currentModel.getTeams(); break;
            case Release: list = (List<T>) currentModel.getReleases(); break;
            case Story: list = (List<T>) currentModel.getStories(); break;
            case Backlog: list = (List<T>) currentModel.getBacklogs(); break;
            case Sprint: list = (List<T>) currentModel.getSprints(); break;
            default: throw new UnsupportedOperationException("This type of model is unsupported (fixme!).");
        }
        return list.stream()
                .filter(predicate)
                .findAny().orElseGet(() -> null);
    }

    /**
     * Checks to see if an object exists in the model.
     * @param model The model
     * @return Whether it exists
     */
    public static boolean exists(final Model model) {
        Organisation currentModel = PersistenceManager.getCurrent().getCurrentModel();
        switch (ModelType.getModelType(model)) {
            case Project:
                return currentModel.getProjects().contains(model);
            case Release:
                return currentModel.getReleases().contains(model);
            case Team:
                return currentModel.getTeams().contains(model);
            case Person:
                return currentModel.getPeople().contains(model);
            case Skill:
                return currentModel.getSkills().contains(model);
            case Backlog:
                return currentModel.getBacklogs().contains(model);
            case Story:
                return currentModel.getStories().contains(model);
            case Sprint:
                return currentModel.getSprints().contains(model);
            default:
                throw new UnsupportedOperationException("We don't know what to do with this model (exists for "
                        + model.getClass().getName()
                        + ") in organisation. You should fix this");
        }
    }
}
