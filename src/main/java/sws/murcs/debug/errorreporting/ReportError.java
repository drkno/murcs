package sws.murcs.debug.errorreporting;

/**
 * Callback interface to report errors.
 */
public interface ReportError {
    /**
     * The function to report errors.
     * @param description The user description of the error.
     */
    void sendReport(String description);
}
