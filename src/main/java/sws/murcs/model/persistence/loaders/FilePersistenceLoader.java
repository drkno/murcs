package sws.murcs.model.persistence.loaders;

import sws.murcs.model.RelationalModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Manages loading persistent data from the local HD using binary serialization.
 */
public class FilePersistenceLoader implements PersistenceLoader {

    /**
     * The directory being worked in.
     */
    private String workingDirectory;

    /**
     * Instantiates a new FilePersistenceLoader, defaulting to the current working directory.
     */
    public FilePersistenceLoader() {
        this(System.getProperty("user.dir"));
    }

    /**
     * Instantiates a new FilePersistenceLoader.
     * @param directory Directory to use persistent data in.
     */
    public FilePersistenceLoader(final String directory) {
        this.workingDirectory = directory;
    }

    /**
     * Gets the storage directory of persistent data.
     * @return The location the persistent data that is stored on the HD.
     */
    public final String getCurrentWorkingDirectory() {
        // return the current working directory
        return workingDirectory;
    }

    /**
     * Sets the current working directory for future lookups.
     * @param newWorkingDirectory the new working directory.
     */
    public final void setCurrentWorkingDirectory(final String newWorkingDirectory) {
        this.workingDirectory = newWorkingDirectory;
    }

    /**
     * Loads model from the disk.
     * @param persistenceName The name of the persistent file to load
     * @return The loaded model.
     */
    @Override
    public final RelationalModel loadModel(final String persistenceName) {
        // load the persistent file using the default directory
        return loadModel(persistenceName, getCurrentWorkingDirectory());
    }

    /**
     * Loads a model from the disk.
     * @param persistenceName The name of the persistent file to load
     * @param directory The directory to load the persistent file from.
     * @return The loaded model.
     */
    public final RelationalModel loadModel(final String persistenceName, final String directory) {
        try {
            // Open the model file
            String persistentFileLocation = directory + File.separator + persistenceName;
            // Create an object reading stream
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(persistentFileLocation));
            // Input and case to correct type
            RelationalModel input = (RelationalModel) in.readObject();
            // Close input stream
            in.close();
            return input;
        }
        catch (Exception e) {
            // What the hell happened?
            System.err.println("An error occured while loading the persistent file:\n" + e.getMessage());
            return null;
        }
    }

    /**
     * Saves a model out to a file in the default directory.
     * @param name name to save as.
     * @param persistent Model to save.
     * @throws Exception When a model fails to save.
     */
    @Override
    public final void saveModel(final String name, final RelationalModel persistent) throws Exception {
        // saves the model using the default directory
        saveModel(name, persistent, getCurrentWorkingDirectory());
    }

    /**
     * Saves a model out to a file.
     * @param name name to save as.
     * @param persistent Model to save.
     * @param directory Directory to save the model in.
     * @throws Exception when the persistent file could not be loaded.
     */
    public final void saveModel(final String name, final RelationalModel persistent, final String directory)
            throws Exception {
        try {
            // Open the persistent file
            String persistenceFileLocation = directory + File.separator + name;
            // Open object stream to file
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(persistenceFileLocation));
            // Write the object out to the file
            out.writeObject(persistent);
            // close the stream
            out.close();
        }
        catch (Exception e) {
            // What the hell happened?
            System.err.println("An error occured while saving the persistent file:\n" + e.getMessage());
            throw new Exception("Persistent file not loaded.", e);
        }
    }

    /**
     * Gets a list of models that exist, are in the current working directory and have the default extension.
     * @return List of models.
     */
    @Override
    public final ArrayList<String> getModelList() {
        return getModelList(".project");
    }

    /**
     * Gets a list of models that exist and are in the current directory.
     * @param fileExtension file extension.
     * @return List of models.
     */
    public final ArrayList<String> getModelList(final String fileExtension) {
        return getModelList(fileExtension, getCurrentWorkingDirectory());
    }

    /**
     * Returns a list of models that exist in a directory.
     * @param fileExtension File extension to search for
     * @param directory Directory to search
     * @return list of models
     */
    public static ArrayList<String> getModelList(final String fileExtension, final String directory)
    {
        ArrayList<String> persistentList = new ArrayList<String>();
        File dir = new File(directory); // create handle to directory
        for (File f : dir.listFiles()) {
            String name = f.getName();
            if (name.endsWith(fileExtension)) // check if it ends with the correct ext
            {
                // if it does add
                persistentList.add(name.substring(0, name.indexOf(fileExtension)));
            }
        }
        return persistentList;
    }

    /**
     * Deletes the specified persistent file.
     * @param persistenceName The name of the persistent file
     * @return Whether the operation was successful
     */
    @Override
    public final boolean deleteModel(final String persistenceName) {
        return deletePersistence(persistenceName, getCurrentWorkingDirectory());
    }

    /**
     * Deletes the specified persistent file from the directory.
     * @param persistentName The name of the persistent file
     * @param directory The Directory to search
     * @return Whether the operation was successful
     */
    public final boolean deletePersistence(final String persistentName, final String directory) {
        try {
            File persistenceFile = new File(directory + File.separator + persistentName);

            if (persistenceFile.delete()) {
                return true;
            }
            else {
                throw new Exception("File delete failed");
            }
        }
        catch (Exception e) {
            System.err.println("Deleting persistent data failed with error:\n" + e.getMessage());
            return false;
        }
    }
}

