package sws.murcs.controller;

import sws.murcs.model.Model;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A class for helping with navigation.
 */
public final class NavigationManager {

    /**
     * Limit on the size of the stacks.
     */
    private static final int STACK_LIMIT = 5;
    /**
     * The app controller.
     */
    private static AppController appController;

    /**
     * Empty constructor for utility class.
     */
    private NavigationManager() {
        // Not called as this is a utility class.
    }

    /**
     * Sets the app controller.
     * @param controller the controller.
     */
    public static void setAppController(final AppController controller) {
        appController = controller;
    }
    /**
     * The forward stack used in hyperlinking.
     */
    private static Deque<Model> forwardStack = new ArrayDeque<>();
    /**
     * The back stack used in hyperlinking.
     */
    private static Deque<Model> backStack = new ArrayDeque<>();

    /**
     * The head, the current model selected.
     */
    private static Model head;

    /**
     * Indicates if the forward stack is not empty.
     * @return if the stack is not empty.
     */
    public static boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    /**
     * Indicates if the back stack is not empty.
     * @return if the stack is not empty.
     */
    public static boolean canGoBack() {
        return !backStack.isEmpty();
    }

    /**
     * Moves forward.
     */
    public static void goForward() {
        Model model = forwardStack.pop();
        backStack.push(head);

        head = model;
        navigateTo(model, false);
        if (backStack.size() > STACK_LIMIT) {
            backStack.removeLast();
        }
    }

    /**
     * Moves backwards.
     */
    public static void goBackward() {
        Model model = backStack.pop();
        forwardStack.push(head);

        head = model;
        navigateTo(model, false);

        if (forwardStack.size() > STACK_LIMIT) {
            forwardStack.removeLast();
        }
    }

    /**
     * Navigates to a specific model.
     * @param model the model to navigate to.
     */
    public static void navigateTo(final Model model) {
        navigateTo(model, true);
    }

    /**
     * Navigates to a specific model.
     * @param model the model to navigate to.
     * @param addToStack indicates if the navigation model should be added to the stack.
     */
    private static void navigateTo(final Model model, final boolean addToStack) {

        if (head == null) {
            head = model;
        }
        else if (addToStack && head != model) {
            forwardStack.clear();
            backStack.push(head);
            head = model;

            if (backStack.size() > STACK_LIMIT) {
                backStack.removeLast();
            }
        }

        if (appController != null) {
            appController.selectItem(model);
        }
    }

    /**
     * Clears both the back and forward stack and resets the head.
     */
    public static void clearHistory() {
        backStack.clear();
        forwardStack.clear();
        head = null;
    }
}
