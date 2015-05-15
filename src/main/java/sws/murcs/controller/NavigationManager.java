package sws.murcs.controller;

import sws.murcs.model.Model;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A class for helping with navigation.
 */
public class NavigationManager {

    /**
     * Limit on the size of the stacks.
     */
    private static final int STACK_LIMIT = 5;
    /**
     * The app controller.
     */
    private static AppController appController;

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

    private static Model head;

    public static boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    public static boolean canGoBack() {
        return !backStack.isEmpty();
    }

    public static void goForward() {
        Model model = forwardStack.pop();
        backStack.push(head);

        head = model;
        navigateTo(model, false);
        if (backStack.size() > STACK_LIMIT) {
            backStack.removeLast();
        }
    }

    public static void goBackward() {
        Model model = backStack.pop();
        forwardStack.push(head);

        head = model;
        navigateTo(model, false);

        if (forwardStack.size() > STACK_LIMIT) {
            forwardStack.removeLast();
        }
    }

    public static void navigateTo(final Model model) {
        navigateTo(model, true);
    }

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

    public static void clearHistory() {
        backStack.clear();
        forwardStack.clear();
    }
}
