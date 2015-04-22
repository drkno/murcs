package sws.murcs.exceptions;

import sws.murcs.model.Model;

/**
 * An exception used when an invalid parameter is used
 */
public class InvalidParameterException extends CustomException {

    /**
     * Empty constructor for an InvalidParameterException
     */
    public InvalidParameterException() {}

    /**
     * Constructor for an InvalidParameterException that takes a message
     * @param message The message that goes with the exception
     */
    public InvalidParameterException(String message) {
        super(message);
    }

    /**
     * Constructor for an InvalidParameterException that takes a throwable
     * @param cause the cause of the exception
     */
    public InvalidParameterException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor for an InvalidParameterException that takes a message and a throwable
     * @param message The message that goes with the exception (reason)
     * @param cause the cause of the exception
     */
    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for an InvalidParameterException that takes a message, a throwable and options for enabling
     * suppression and whether the stacktrace is writable
     * @param message The message that goes with the exception (reason)
     * @param cause the cause of the exception
     * @param enableSuppession Whether to enable suppressing of the exception
     * @param writableStackTrace Whether the stacktrace is writable
     */
    public InvalidParameterException(String message, Throwable cause, boolean enableSuppession, boolean writableStackTrace) {
        super(message, cause, enableSuppession, writableStackTrace);
    }

    /**
     * Validates a string (basically if it's null or empty it will throw an exception)
     * @param type The type of value it is (description of it)
     * @param value The string value itself to check for null or empty
     * @throws InvalidParameterException
     */
    public static void validate(String type, String value) throws InvalidParameterException {
       if (value == null || value.trim().isEmpty()) throw new InvalidParameterException(type + " cannot be empty");
    }

    /**
     * Validates any Model object, checks to see if it is null and throws the exception if it is
     * @param type The type of model it is (ie, release, project, etc.)
     * @param value The value of the model being checked for null
     * @throws InvalidParameterException
     */
    public static void validate(String type, Model value) throws InvalidParameterException {
        if (value == null) throw new InvalidParameterException(type + " cannot be empty");
    }
}
