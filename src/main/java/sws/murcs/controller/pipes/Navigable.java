package sws.murcs.controller.pipes;

import sws.murcs.model.Model;
import sws.murcs.model.ModelType;

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

    /**
     * Routes a message to the controller telling it to
     * navigate to a model item.
     * @param model The item to navigate to.
     */
    void navigateTo(Model model);

    /**
     * Routes a message to the controller telling it to
     * navigate to a model item in a new tab.
     * @param model The model item to select
     */
    void navigateToNewTab(Model model);
}
