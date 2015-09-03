package sws.murcs.debug.errorreporting;

/**
 * Callback interface to report errors.
 */
public interface ErrorReporterOpening {

    /**
     * Sets if the error reporter window is open.
     * @param newValue The boolean value that the error reporter is open.
     */
    void setReporterIsOpen(boolean newValue);
}
