package sws.project.exceptions;

/**
 * Duplicate Skill Exception
 *
 * 3/11/2015
 * @author Dion
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
}
