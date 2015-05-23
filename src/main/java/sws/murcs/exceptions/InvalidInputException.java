package sws.murcs.exceptions;

/**
 * Exception for invalid input in the GUI.
 */
public class InvalidInputException extends CustomException {

    /**
     * Creates an empty invalid input exception.
     */
    public InvalidInputException() {
        // Empty constructor
    }

    /**
     * Creates a invalid input exception with a given message.
     * @param message The message that goes with the exception
     */
    public InvalidInputException(final String message) {
        super(message);
    }

    /**
     * Creates a invalid input exception with a given throwable.
     * @param cause The cause of the exception
     */
    public InvalidInputException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a invalid input exception with a given throwable and a message.
     * @param message The message with the exception.
     * @param cause The cause of the exception.
     */
    public InvalidInputException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a invalid input exception with a given throwable, a message, a boolean for enabling suppression
     * and a boolean for whether or not the stack trace is writable.
     * @param message The message of the exception.
     * @param cause The cause of the exception.
     * @param enableSuppression Enable suppression of the exception.
     * @param writableStackTrace Is the stack trace writable.
     */
    public InvalidInputException(final String message, final Throwable cause, final boolean enableSuppression,
                                    final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
