package sws.murcs.controller;

import sws.murcs.controller.tabs.Navigable;
import sws.murcs.model.Model;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A class for helping with navigation.
 */
public final class NavigationManager implements Navigable {
    /**
     * Limit on the size of the stacks.
     */
    private static final int STACK_LIMIT = 5;

    /**
     * The app controller.
     */
    private ModelViewController modelViewController;

    /**
     * Whether the navigation manager should ignore changes.
     */
    private boolean toIgnore;

    /**
     * Sets the app controller.
     * @param controller the controller.
     */
    public void setModelViewController(final ModelViewController controller) {
        modelViewController = controller;
    }

    /**
     * The forward stack used in hyperlinking.
     */
    private Deque<Model> forwardStack = new ArrayDeque<>();

    /**
     * The back stack used in hyperlinking.
     */
    private Deque<Model> backStack = new ArrayDeque<>();

    /**
     * The current head of the back and forwards stacks.
     */
    private Model head;

    /**
     * Determines whether or not it is possible to go forward.
     * @return If it is possible to go forward.
     */
    public boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    /**
     * Determines whether or not it is possible to go back.
     * @return If it is possible to go back.
     */
    @Override
    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    /**
     * Traverses forward in the back forward history.
     */
    @Override
    public void goForward() {
        if (toIgnore) {
            return;
        }
        Model model = forwardStack.pop();
        backStack.push(head);

        head = model;
        navigateTo(model, false);
        if (backStack.size() > STACK_LIMIT) {
            backStack.removeLast();
        }
    }

    /**
     * Traverses backward in the back forward history.
     */
    @Override
    public void goBack() {
        if (toIgnore) {
            return;
        }
        Model model = backStack.pop();
        forwardStack.push(head);

        head = model;
        navigateTo(model, false);

        if (forwardStack.size() > STACK_LIMIT) {
            forwardStack.removeLast();
        }
    }

    /**
     * The public method for navigating to a model. (This always tracks the history).
     * @param model The model navigating to.
     */
    public void navigateTo(final Model model) {
        navigateTo(model, true);
    }

    /**
     * The private method for navigation to a model. This has the ability to not track the history.
     * @param model The model navigating to
     * @param addToStack Whether or not to add the model to the history stack
     */
    private void navigateTo(final Model model, final boolean addToStack) {

        if (head == null) {
            head = model;
        }
        else if (addToStack && !head.equals(model) && !toIgnore) {
            forwardStack.clear();
            backStack.push(head);
            head = model;

            if (backStack.size() > STACK_LIMIT) {
                backStack.removeLast();
            }
        }

        if (modelViewController != null) {
            modelViewController.selectItem(model);
        }
    }

    /**
     * Clears all the history (the back, the forward and head).
     */
    public void clearHistory() {
        backStack.clear();
        forwardStack.clear();
        head = null;
    }

    /**
     * Sets whether to ignore history.
     * @param ignore Whether to ignore history.
     */
    public void setIgnore(final boolean ignore) {
        toIgnore = ignore;
    }
}
