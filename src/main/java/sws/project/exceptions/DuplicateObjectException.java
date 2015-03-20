package sws.project.exceptions;

import sws.project.model.Model;
import sws.project.model.Project;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;

import java.util.ArrayList;

/**
 * Duplicate Object Exception
 */
public class DuplicateObjectException extends Exception {

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
     * @param simpleName new simple name to check
     * @throws DuplicateObjectException if this object is a duplicate
     */
    public static void CheckForDuplicates(Model newModel, String simpleName) throws DuplicateObjectException {
        RelationalModel model = PersistenceManager.Current.getCurrentModel();
        if (model == null) return; // as is called in the constructor of RelationalModel
        String className = newModel.getClass().getSimpleName();
        ArrayList<? extends Model> list = null;
        switch (className) {
            case "Skill": {
                list = model.getSkills();
                break;
            }
            case "Person": {
                list = model.getPeople();
                break;
            }
            case "Project": {
                list = new ArrayList<Project>(1) {{model.getProject();}};
                break;
            }
            case "Team": {
                list = model.getTeams();
                break;
            }
        }

        if (list != null && list.stream()
                .filter(o -> o.getShortName().equals(simpleName) && o != newModel)
                .findAny()
                .isPresent()) {
            throw new DuplicateObjectException("This " + className + " already exists.");
        }
    }
}
