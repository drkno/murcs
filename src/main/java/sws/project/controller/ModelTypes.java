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
     * @return The model type
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

    /**
     * Gets the model type of an object.
     * This object should extend one of the model classes
     * @param object The object to get the model type for
     * @return The model type
     */
    public static ModelTypes getModelType(Object object){
        if (!(object instanceof Model)) throw new IllegalArgumentException("object must extend model! (was " + object.getClass().getName() + ")");

        Model model = (Model)object;
        return getModelType(model.getClass());
    }

    /**
     * Converts a model type to a class type
     * @param type the type
     * @return The class
     */
    public static Class<? extends Model> getTypeFromModel(ModelTypes type){
        switch (type){
            case Project:
                return sws.project.model.Project.class;
            case Team:
                return sws.project.model.Team.class;
            case People:
                return sws.project.model.Person.class;
            case Skills:
                return sws.project.model.Skill.class;
        }
        return null;
    }
}
