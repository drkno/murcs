package sws.murcs.model.helpers;

import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Team;
import sws.murcs.model.Skill;
import sws.murcs.model.Person;
import sws.murcs.model.Release;
import sws.murcs.model.Story;
import sws.murcs.model.WorkAllocation;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helps to find usages of a model within the current relational model.
 */
public final class UsageHelper {

    /**
     * Empty private constructor as this is a utility class.
     */
    private UsageHelper() {
    }

    /**
     * The current relational model.
     */
    private static RelationalModel currentModel = PersistenceManager.getCurrent().getCurrentModel();

    /**
     * Determines whether or not a Model object is in use.
     * @param model The model to check
     * @return Whether the model object is in use
     */
    public static boolean inUse(final Model model) {
        return findUsages(model).size() != 0;
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
            default:
                throw new UnsupportedOperationException("We don't know what to do with this model (findUsages for "
                        + model.getClass().getName()
                        + ") in Relational Model. You should fix this");
        }
    }

    /**
     * Gets a list of all the places a release is used.
     * Releases are not currently in use anywhere, so this will return an empty list.
     * @param release The release.
     * @return The places the release is used.
     */
    private static List<Model> findUsages(final Release release) {
        return new ArrayList<>();
    }

    /**
     * Gets a list of places that a project is used.
     * @param project The project to find the usages of
     * @return The usages of the project
     */
    private static List<Model> findUsages(final Project project) {
        List<Model> usages = project.getReleases().stream().collect(Collectors.toList());
        return usages;
    }

    /**
     * Gets a list of the places that a team is used.
     * @param team The team to find the usages of
     * @return The usages of the team
     */
    private  static List<Model> findUsages(final Team team) {
        List<Model> usages = new ArrayList<>();
        for (Project project : currentModel.getProjects()) {
            for (WorkAllocation allocation : currentModel.getProjectsAllocations(project)) {
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
    private static List<Model> findUsages(final Person person) {
        List<Model> usages = new ArrayList<>();
        for (Team team : currentModel.getTeams()) {
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
    private static List<Model> findUsages(final Skill skill) {
        List<Model> usages = new ArrayList<>();
        for (Person person : currentModel.getPeople()) {
            if (person.getSkills().contains(skill)) {
                usages.add(person);
            }
        }
        return usages;
    }

    /**
     * Gets a list of all the places that a story has been used.
     * @param story The story to find the usages for
     * @return The usages of the story
     */
    private static List<Model> findUsages(final Story story) {
        List<Model> usages = new ArrayList<>();

        //TODO find all the backlogs the story is used within

        return usages;
    }

    /**
     * Checks to see if an object exists in the model.
     * @param model The model
     * @return Whether it exists
     */
    public static boolean exists(final Model model) {
        switch (ModelType.getModelType(model)) {
            case Project:
                return currentModel.getProjects().contains(model);
            case Person:
                return currentModel.getPeople().contains(model);
            case Team:
                return currentModel.getTeams().contains(model);
            case Skill:
                return currentModel.getSkills().contains(model);
            case Release:
                return currentModel.getReleases().contains(model);
            default:
                throw new UnsupportedOperationException("We don't know what to do with this model (exists for "
                        + model.getClass().getName()
                        + ") in Relational Model. You should fix this");
        }
    }
}
