package sws.murcs.exceptions;

/**
 * All custom exceptions inherit from this exception. This is done so
 * that all custom exceptions can be caught whilst ignoring built in exceptions.
 */
public abstract class CustomException extends Exception {
    public CustomException() {
        super();
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(String message, Throwable cause, boolean enableSuppession, boolean writableStackTrace) {
        super(message, cause, enableSuppession, writableStackTrace);
    }
}
