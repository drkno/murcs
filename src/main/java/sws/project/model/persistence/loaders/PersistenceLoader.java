package sws.project.model.persistence.loaders;

import sws.project.model.Model;
import sws.project.model.RelationalModel;

import java.util.ArrayList;

public interface PersistenceLoader {

    /**
     * Loads model from the disk.
     * @param persistenceName The name of the persistent file to load
     * @return The loaded model.
     */
    RelationalModel loadModel(String persistenceName);

    /**
     * Saves a model out to a file in the default directory.
     * @param persistent Model to save.
     * @throws Exception When a model fails to save.
     */
    void saveModel(RelationalModel persistent) throws Exception;

    /**
     * Gets a list of models that exist.
     * @return List of models.
     */
    public ArrayList<String> getModelList();

    /**
     * Deletes the specified model
     * @param persistenceName The name of the persistent file
     * @return Whether the operation was successful
     */
    public boolean deleteModel(String persistenceName);
}
