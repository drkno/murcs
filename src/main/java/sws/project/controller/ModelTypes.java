package sws.project.controller;

import sws.project.model.Model;

/**
 * A enum of the different types of model that
 * are in the application
 */
public enum ModelTypes {

    Project,
    People,
    Team,
    Skills;

    /**
     * Gets the model type from an index
     * 0: Project
     * 1: People
     * 2: Team
     * 3: Skills
     * @param index The index
     * @return The model type
     */
    public static ModelTypes getModelType(int index) {
        return values()[index];
    }

    /**
     * Gets the model type from a model object
     * @param clazz The type of the model object
     * @return
     */
    public static ModelTypes getModelType(Class<? extends Model> clazz){
        if (clazz == sws.project.model.Project.class)
            return Project;
        else if (clazz == sws.project.model.Team.class)
            return Team;
        else if (clazz == sws.project.model.Person.class)
            return People;
        else if (clazz == sws.project.model.Skill.class)
            return Skills;

        throw new IllegalArgumentException("Clazz " + clazz.getName() + " is not supported");
    }
}
