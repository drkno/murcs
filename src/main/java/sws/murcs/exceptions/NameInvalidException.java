package sws.murcs.exceptions;

/**
 * 22/03/2015
 */
public class NameInvalidException extends CustomException {
    
    public NameInvalidException() {}

    public NameInvalidException(String message) {
        super(message);
    }

    public NameInvalidException(Throwable cause) {
        super(cause);
    }

    public NameInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameInvalidException(String message, Throwable cause, boolean enableSuppession, boolean writableStackTrace) {
        super(message, cause, enableSuppession, writableStackTrace);
    }

    public static void validate(String type, String value) throws NameInvalidException {
       if (value == null || value.trim().isEmpty()) throw new NameInvalidException(type + " cannot be empty");
    }
}
