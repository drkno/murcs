package sws.murcs.exceptions;

import sws.murcs.model.Model;

/**
 * 22/03/2015
 */
public class InvalidParameterException extends CustomException {
    
    public InvalidParameterException() {}

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(Throwable cause) {
        super(cause);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidParameterException(String message, Throwable cause, boolean enableSuppession, boolean writableStackTrace) {
        super(message, cause, enableSuppession, writableStackTrace);
    }

    public static void validate(String type, String value) throws InvalidParameterException {
       if (value == null || value.trim().isEmpty()) throw new InvalidParameterException(type + " cannot be empty");
    }

    public static void validate(String type, Model value) throws InvalidParameterException {
        if (value == null) throw new InvalidParameterException(type + " cannot be empty");
    }
}
