package sws.murcs.controller.tabs;

import sws.murcs.model.ModelType;

/**
 * Provides a method of connecting commands for managing
 * model objects to a controller.
 */
public interface ModelManagable {
    /**
     * Creates a new model object of the specified type.
     * @param modelType The model type to create
     */
    public void create(ModelType modelType);
    
    public void create();
    public void remove();
}
