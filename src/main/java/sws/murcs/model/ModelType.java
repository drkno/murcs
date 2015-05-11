package sws.murcs.model;

/**
 * A enum of the different types of model that
 * are in the application.
 */
public enum ModelType {

    /**
     * Represents the class Project.
     */
    Project(0, sws.murcs.model.Project.class),
    /**
     * Represents the class Person.
     */
    Person(1, sws.murcs.model.Person.class),
    /**
     * Represents the class Team.
     */
    Team(2, sws.murcs.model.Team.class),
    /**
     * Represents the class Skill.
     */
    Skill(3, sws.murcs.model.Skill.class),
    /**
     * Represents the class Release.
     */
    Release(4, sws.murcs.model.Release.class),

    /**
     * Indicates a model type, story
     */
    Story(5, sws.murcs.model.Story.class);

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
    ModelType(final int modelIndex, final Class classType) {
        index = modelIndex;
        clazz = classType;
    }

    /**
     * Gets the model type from an index.
     * @param index The index
     * @return The model type
     */
    public static ModelType getModelType(final int index) {
        return values()[index];
    }

    /**
     * Gets the index from a model type
     * 0: Project
     * 1: Person
     * 2: Team
     * 3: Skill.
     * @param type The type of model object.
     * @return The index for selection
     */
    public static int getSelectionType(final ModelType type) {
        return type.index;
    }

    /**
     * Gets the model type from a model object.
     * @param clazz The type of the model object
     * @return The model type
     */
    public static ModelType getModelType(final Class<? extends Model> clazz) {
        for (ModelType type : values()) {
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
    public static ModelType getModelType(final Object object) {
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
    public static Class<? extends Model> getTypeFromModel(final ModelType type) {
        return type.clazz;
    }
}
