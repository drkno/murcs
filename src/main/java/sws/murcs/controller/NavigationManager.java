package sws.murcs.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import sws.murcs.model.Model;

import java.util.Stack;

/**
 * A class for helping with navigation.
 */
public class NavigationManager {

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
    private static Stack<Model> forwardStack = new Stack<>();
    /**
     * The back stack used in hyperlinking.
     */
    private static Stack<Model> backStack = new Stack<>();

    private static Model head;

    public static boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    public static boolean canGoBack() {
        return !backStack.isEmpty();
    }

    public static void goForward() {
        Model model = forwardStack.pop();
        navigateTo(model, false);
        backStack.push(head);
        head = model;
    }

    public static void goBackward() {
        Model model = backStack.pop();
        navigateTo(model, false);
        forwardStack.push(head);
        head = model;
    }

    public static void navigateTo(final Model model) {
        navigateTo(model, true);
    }

    private static void navigateTo(final Model model, final boolean addToStack) {
        if (addToStack) {
            forwardStack.clear();
            backStack.add(head);
            head = model;
        }
        if (appController != null) {
            appController.selectItem(model);
        }
    }
}
