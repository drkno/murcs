package sws.murcs.debug.errorreporting;

/**
 * Types of error that can be displayed byt the ErrorReportPopup.
 */
public enum ErrorType {

    /**
     * Any error that is automatically generated. An example would be an exception.
     */
    Automatic,

    /**
     * Any error that is manually input by the user.
     */
    Manual
}
