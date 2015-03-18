package sws.project.controller;

/**
 * Enumerates each of the types that can be displayed in the sidebar list.
 */
public enum ModelTypes {
    Project,
    People,
    Team,
    Skills;

    /**
     * Gets the model type for a provided index.
     * @param index index to get model type for.
     * @return ModelType equivalent.
     */
    public static ModelTypes getModelType(int index) {
        return values()[index];
    }
}
