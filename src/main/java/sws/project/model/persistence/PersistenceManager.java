package sws.project.model.persistence;

import sws.project.model.Model;
import sws.project.model.persistence.loaders.PersistenceLoader;

import java.util.ArrayList;
import java.util.Date;

/**
 * Provides methods for managing models
 */
public final class PersistenceManager {

    /**
     * Static persistence manager, used to keep a persistent manager where ever it is used from.
     */
    public static PersistenceManager CurrentPersistenceManager;

    /**
     * Indicates whether or not there is a current persistence manage
     * @return The persistence manager
     */
    public static boolean CurrentPersistenceManagerExists(){ return CurrentPersistenceManager != null; }

    /**
     * Currently in use user persistence.
     */
    private Model currentModel;

    /**
     * Currently in use persistence loader.
     */
    private PersistenceLoader persistenceLoader;

    /**
     * Instantiates a new persistence manager.
     * @param persistenceLoader Loader to use to read and write persistences
     */
    public PersistenceManager(PersistenceLoader persistenceLoader)
    {
        setPersistenceLoader(persistenceLoader);
    }

    /**
     * Gets the current in use persistence loader.
     * @return current persistence loader
     */
    public PersistenceLoader getPersistenceLoader()
    {
        return persistenceLoader;
    }

    /**
     * Sets the currently in use persistence loader.
     * @param persistenceLoader Persistence loader to use.
     */
    public void setPersistenceLoader(PersistenceLoader persistenceLoader)
    {
        this.persistenceLoader = persistenceLoader;
    }

    /**
     * Loads a model from the disk.
     * @param persistenceName The name of the model to load
     * @return The loaded persistence. Will be null if the persistence does not exist
     */
    public Model loadModel(String persistenceName)
    {
        // load the persistence using the default directory
        return persistenceLoader.loadModel(persistenceName);
    }

    /**
     * Saves a user persistence out to a file in the default directory.
     * @param persistence Persistence to save.
     * @throws Exception When a model fails to save.
     */
    public void savePersistence(Model persistence) throws Exception
    {
        // saves the model using the default directory
        persistenceLoader.saveModel(persistence);
    }

    /**
     * Checks to see if a model exists
     * @param persistenceName The name of the model
     * @return Whether the persistence exists
     */
    public boolean modelExists(String persistenceName){
        ArrayList<String> persistences = getModels();
        return persistences.contains(persistenceName);
    }

    /**
     * Returns a list of models that exist in the default location.
     * @return
     */
    public ArrayList<String> getModels()
    {
        return persistenceLoader.getModelList();
    }

    /**
     * Deletes the specified model
     * @param persistenceName The name of the model
     * @return Whether the operation was successful
     */
    public boolean deleteModel(String persistenceName)
    {
        return persistenceLoader.deleteModel(persistenceName);
    }

    /**
     * Gets the current model
     * @return The current model. Null if it has not been set.
     */
    public Model getCurrentModel(){
        return currentModel;
    }

    /**
     * Sets the current model
     * @param model The new model
     */
    public void setCurrentModel(Model model){
        currentModel = model;
    }
}
