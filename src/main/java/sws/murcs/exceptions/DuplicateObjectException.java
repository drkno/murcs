package sws.murcs.exceptions;

import sws.murcs.model.Model;
import sws.murcs.model.Person;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.observable.ModelObservableArrayList;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.List;

/**
 * Duplicate Object Exception.
 */
public class DuplicateObjectException extends CustomException {

    /**
     * Creates an empty duplicate object exception.
     */
    public DuplicateObjectException() {
        // Empty constructor
    }

    /**
     * Creates a duplicate object exception with a given message.
     * @param message The message that goes with the exception
     */
    public DuplicateObjectException(final String message) {
        super(message);
    }

    /**
     * Creates a duplicate object exception with a given throwable.
     * @param cause The cause of the exception
     */
    public DuplicateObjectException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a duplicate object exception with a given throwable and a message.
     * @param message The message with the exception.
     * @param cause The cause of the exception.
     */
    public DuplicateObjectException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a duplicate object exception with a given throwable, a message, a boolean for enabling suppression
     * and a boolean for whether or not the stack trace is writable.
     * @param message The message of the exception.
     * @param cause The cause of the exception.
     * @param enableSuppression Enable suppression of the exception.
     * @param writableStackTrace Is the stack trace writable.
     */
    public DuplicateObjectException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Checks if a DuplicateObjectException should be thrown because of a duplicate short name
     * and throws one if needed.
     * @param newModel new model to check
     * @param param new simple name to check
     * @throws DuplicateObjectException if this object is a duplicate
     */
    public static void checkForDuplicates(final Model newModel, final String param) throws DuplicateObjectException {
        if (PersistenceManager.Current == null) {
            return;
        }
        RelationalModel model = PersistenceManager.Current.getCurrentModel();
        if (model == null) {
            return; // as is called in the constructor of RelationalModel
        }
        String className = newModel.getClass().getSimpleName();
        List<? extends Model> list = null;
        switch (className) {
            case "Skill": {
                list = model.getSkills();
                checkForDuplicateNames(newModel, list, className, param);
                break;
            }
            case "Person": {
                list = model.getPeople();
                checkForDuplicateNames(newModel, list, className, param);
                checkForDuplicateUserIds(newModel, (ModelObservableArrayList<Person>) list, param);
                break;
            }
            case "Project": {
                list = model.getProjects();
                checkForDuplicateNames(newModel, list, className, param);
                break;
            }
            case "Team": {
                list = model.getTeams();
                checkForDuplicateNames(newModel, list, className, param);
                break;
            }
            case "Release": {
                list = model.getReleases();
                checkForDuplicateNames(newModel, list, className, param);
            }
            default:
                break;
        }
    }

    /**
     * Checks for a duplicate names for the model given.
     * @param newModel The new model
     * @param modelClass The class of the model
     * @param className The name of the class
     * @param simpleName The simple name
     * @throws DuplicateObjectException The exception if there is a duplicate
     */
    private static void checkForDuplicateNames(final Model newModel, final List<? extends Model> modelClass, final String className, final String simpleName) throws DuplicateObjectException {
        if (modelClass != null && modelClass.stream()
                .filter(o -> o.getShortName().equals(simpleName) && o != newModel)
                .findAny()
                .isPresent()) {
            throw new DuplicateObjectException("A " + className + " with this Name already exists.");
        }
    }

    /**
     * Checks for duplicate user ids of the given user id.
     * @param newModel The model with the id.
     * @param modelClass The class of the model.
     * @param simpleId The user id.
     * @throws DuplicateObjectException The exception thrown if there is a person with the same user id.
     */
    private static void checkForDuplicateUserIds(final Model newModel, final List<Person> modelClass, final String simpleId) throws DuplicateObjectException {
        if (modelClass != null && modelClass.stream()
                .filter(o -> o.getUserId().equals(simpleId) && o != newModel)
                .findAny()
                .isPresent()) {
            throw new DuplicateObjectException("A Person with this User ID already exists.");
        }
    }
}
