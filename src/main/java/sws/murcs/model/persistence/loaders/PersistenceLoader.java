package sws.murcs.model.persistence.loaders;

import sws.murcs.model.RelationalModel;

import java.util.ArrayList;

/**
 * An interface for persistence loaders.
 */
public interface PersistenceLoader {

    /**
     * Loads model from the disk.
     * @param persistenceName The name of the persistent file to load
     * @return The loaded model.
     */
    RelationalModel loadModel(String persistenceName);

    /**
     * Saves a model out to a file in the default directory.
     * @param saveName name to save as.
     * @param persistent Model to save.
     * @throws Exception When a model fails to save.
     */
    void saveModel(String saveName, RelationalModel persistent) throws Exception;

    /**
     * Gets a list of models that exist.
     * @return List of models.
     */
    ArrayList<String> getModelList();

    /**
     * Deletes the specified model.
     * @param persistenceName The name of the persistent file
     * @return Whether the operation was successful
     */
    boolean deleteModel(String persistenceName);

    /**
     * Gets the current working directory.
     * @return current working directory.
     * @throws Exception Exception if current working directory is unapplicable to the current PersistenceLoader
     */
    String getCurrentWorkingDirectory() throws Exception;

    /**
     * Sets the current working directory.
     * @param directory new working directory
     * @throws Exception if current working directory is unapplicable to the current PersistenceLoader
     */
    void setCurrentWorkingDirectory(String directory) throws Exception;
}
