package sws.murcs.exceptions;

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
    public DuplicateObjectException(final String message, final Throwable cause, final boolean enableSuppression,
                                    final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
