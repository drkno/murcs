package sws.murcs.controller;

import sws.murcs.model.Model;

/**
 * A enum of the different types of model that
 * are in the application.
 */
public enum ModelTypes {

    /**
     * Represents the class Project.
     */
    Project(0, sws.murcs.model.Project.class),
    /**
     * Represents the class People.
     */
    People(1, sws.murcs.model.Person.class),
    /**
     * Represents the class Team.
     */
    Team(2, sws.murcs.model.Team.class),
    /**
     * Represents the class Skills.
     */
    Skills(3, sws.murcs.model.Skill.class),
    /**
     * Represents the class Release.
     */
    Release(4, sws.murcs.model.Release.class);

    /**
     * The index of the model type in the enum.
     */
    private int index;
    /**
     * The class type of the linked class to the model type.
     */
    private Class clazz;

    /**
     * Instantiates a new ModelType.
     * @param modelIndex index of the ModelType.
     * @param classType class of the ModelType.
     */
    ModelTypes(final int modelIndex, final Class classType) {
        index = modelIndex;
        clazz = classType;
    }

    /**
     * Gets the model type from an index.
     * @param index The index
     * @return The model type
     */
    public static ModelTypes getModelType(final int index) {
        return values()[index];
    }

    /**
     * Gets the index from a model type
     * 0: Project
     * 1: People
     * 2: Team
     * 3: Skills.
     * @param type The type of model object.
     * @return The index for selection
     */
    public static int getSelectionType(final ModelTypes type) {
        return type.index;
    }

    /**
     * Gets the model type from a model object.
     * @param clazz The type of the model object
     * @return The model type
     */
    public static ModelTypes getModelType(final Class<? extends Model> clazz) {
        for (ModelTypes type : values()) {
            if (type.clazz == clazz) {
                return type;
            }
        }

        throw new IllegalArgumentException("Class " + clazz.getName() + " is not supported");
    }

    /**
     * Gets the model type of an object.
     * This object should extend one of the model classes
     * @param object The object to get the model type for
     * @return The model type
     */
    public static ModelTypes getModelType(final Object object) {
        if (!(object instanceof Model)) {
            throw new IllegalArgumentException("object must extend model! (was " + object.getClass().getName() + ")");
        }

        Model model = (Model) object;
        return getModelType(model.getClass());
    }

    /**
     * Converts a model type to a class type.
     * @param type the type
     * @return The class
     */
    public static Class<? extends Model> getTypeFromModel(final ModelTypes type) {
        return type.clazz;
    }
}
