package sws.murcs.exceptions;

/**
 * All custom exceptions inherit from this exception. This is done so
 * that all custom exceptions can be caught whilst ignoring built in exceptions.
 */
public class CustomException extends Exception {
    /**
     * Instantiates a new CustomException.
     */
    public CustomException() {
        super();
    }

    /**
     * Instantiates a new CustomException.
     * @param message exception message to use.
     */
    public CustomException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new CustomException.
     * @param cause exception that caused this exception.
     */
    public CustomException(final Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new CustomException.
     * @param message exception message to use.
     * @param cause exception that caused this exception.
     */
    public CustomException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new CustomException.
     * @param message exception message to use.
     * @param cause exception that caused this exception.
     * @param enableSuppression ignore when thrown inside a finally block.
     * @param writableStackTrace whether or not a stack trace should be writable.
     */
    public CustomException(final String message, final Throwable cause, final boolean enableSuppression,
                           final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
