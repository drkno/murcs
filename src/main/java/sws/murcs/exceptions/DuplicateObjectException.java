package sws.murcs.exceptions;

import sws.murcs.model.Model;
import sws.murcs.model.observable.ModelObservableArrayList;
import sws.murcs.model.Person;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.persistence.PersistenceManager;

/**
 * Duplicate Object Exception
 */
public class DuplicateObjectException extends CustomException {

    public DuplicateObjectException() {}

    public DuplicateObjectException(String message) {
        super(message);
    }

    public DuplicateObjectException(Throwable cause) {
        super(cause);
    }

    public DuplicateObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateObjectException(String message, Throwable cause, boolean enableSuppession, boolean writableStackTrace) {
        super(message, cause, enableSuppession, writableStackTrace);
    }

    /**
     * Checks if a DuplicateObjectException should be thrown because of a duplicate short name
     * and throws one if needed.
     * @param newModel new model to check
     * @param param new simple name to check
     * @throws DuplicateObjectException if this object is a duplicate
     */
    public static void CheckForDuplicates(Model newModel, String param) throws DuplicateObjectException {
        if (PersistenceManager.Current == null) return;
        RelationalModel model = PersistenceManager.Current.getCurrentModel();
        if (model == null) return; // as is called in the constructor of RelationalModel
        String className = newModel.getClass().getSimpleName();
        ModelObservableArrayList<? extends Model> list = null;
        switch (className) {
            case "Skill": {
                list = model.getSkills();
                CheckForDuplicateNames(newModel, list, className, param);
                break;
            }
            case "Person": {
                list = model.getPeople();
                CheckForDuplicateNames(newModel, list, className, param);
                CheckForDuplicateUserIds(newModel, (ModelObservableArrayList<Person>)list, param);
                break;
            }
            case "Project": {
                list = model.getProjects();
                CheckForDuplicateNames(newModel, list, className, param);
                break;
            }
            case "Team": {
                list = model.getTeams();
                CheckForDuplicateNames(newModel, list, className, param);
                break;
            }
            case "Release": {
                list = model.getReleases();
                CheckForDuplicateNames(newModel, list, className, param);
            }
        }
    }

    private static void CheckForDuplicateNames(Model newModel, ModelObservableArrayList<? extends Model> modelClass, String className, String simpleName) throws DuplicateObjectException {
        if (modelClass != null && modelClass.stream()
                .filter(o -> o.getShortName().equals(simpleName) && o != newModel)
                .findAny()
                .isPresent()) {
            throw new DuplicateObjectException("A " + className + " with this Name already exists.");
        }
    }

    private static void CheckForDuplicateUserIds(Model newModel, ModelObservableArrayList<Person> modelClass, String simpleId) throws DuplicateObjectException {
        if (modelClass != null && modelClass.stream()
                .filter(o -> o.getUserId().equals(simpleId) && o != newModel)
                .findAny()
                .isPresent()) {
            throw new DuplicateObjectException("A Person with this User ID already exists.");
        }
    }
}
