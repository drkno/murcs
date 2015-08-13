package sws.murcs.controller.tabs;

/**
 * An interface that provides methods for going back and
 * forward
 */
public interface Navigable {
    /**
     * Tells the navigable to go forward.
     */
    void goForward();

    /**
     * Tells the navigable to go back.
     */
    void goBack();

    /**
     * Indicates whether the navigable can go forward.
     */
    boolean canGoForward();

    /**
     * Indicates whether the navigable can go back.
     */
    boolean canGoBack();
}
