package sws.murcs.controller.pipes;

import sws.murcs.model.ModelType;

/**
 * Provides a method of connecting commands for managing
 * model objects to a controller.
 */
public interface ModelManagable {
    /**
     * Routes a command telling the controller that
     * it should create a new model of a specified type.
     * @param modelType The model type to create
     */
    public void create(ModelType modelType);

    /**
     * Routes a command indicating that the controller
     * should create a new model object of its choice.
     */
    public void create();

    /**
     * Routes a command telling the controller that
     * it should remove a model object.
     */
    public void remove();
}
