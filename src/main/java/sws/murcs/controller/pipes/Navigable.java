package sws.murcs.controller.pipes;

import sws.murcs.model.Model;
import sws.murcs.model.ModelType;

/**
 * An interface that provides methods for going back and
 * forward.
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
     * @return Whether forward navigation is possible
     */
    boolean canGoForward();

    /**
     * Indicates whether the navigable can go back.
     * @return Whether backward navigation is possible
     */
    boolean canGoBack();

    /**
     * Routes a message to the controller telling it to
     * navigate to a model item.
     * @param model The item to navigate to.
     */
    void navigateTo(Model model);

    /**
     * The model type to navigate to.
     * @param type The type to select
     */
    void navigateTo(ModelType type);

    /**
     * Routes a message to the controller telling it to
     * navigate to a model item in a new tab.
     * @param model The model item to select
     */
    void navigateToNewTab(Model model);
}
