package sws.murcs.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Window;
import sws.murcs.controller.SearchController;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.controller.pipes.Navigable;
import sws.murcs.debug.errorreporting.ErrorReporter;

import java.io.IOException;

/**
 * Provides methods for starting the search window/pane.
 */
public final class SearchView {
    /**
     * Singleton instance of the SearchView.
     */
    private static SearchView instance;

    /**
     * PopOver window used to display this SearchView instance.
     */
    private PopOver popOver;

    /**
     * SearchController used by this SearchView instance.
     */
    private SearchController controller;

    /**
     * Gets the singleton SearchView instance, instantiating it if required.
     * @return the SearchView instance.
     */
    public static SearchView get() {
        if (instance == null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SearchView.class.getResource("/sws/murcs/Search.fxml"));

            try {
                Parent parent = loader.load();
                PopOver popOver = new PopOver(parent);
                SearchController controller = loader.getController();
                controller.setPopOver(popOver);
                popOver.detachableProperty().setValue(true);
                popOver.detachedProperty().setValue(true);
                popOver.hideOnEscapeProperty().setValue(true);

                instance = new SearchView(popOver, controller);
            }
            catch (IOException e) {
                ErrorReporter.get().reportError(e, "Could not create a search dialog");
            }
        }
        return instance;
    }

    /**
     * Creates a new SearchView that can be used get a window from which
     * searches can be performed.
     * @param popOverWindow the PopOver window that will be used to display the SearchView.
     * @param searchController the Controller of the SearchView.
     */
    private SearchView(final PopOver popOverWindow, final SearchController searchController) {
        popOver = popOverWindow;
        controller = searchController;
    }

    /**
     * Shows the SearchView, attaching it to the provided Window.
     * @param attachedWindow window to make the SearchView attach to. If this
     * window is closed, the SearchView will be similarly closed. The SearchView will
     * also appear on the same screen as this Window.
     * @throws NullPointerException if attachedWindow is null.
     */
    public void show(final Window attachedWindow) {
        popOver.show(attachedWindow);
        ((Parent) popOver.getSkin().getNode()).getStylesheets()
                .add(controller.getClass().getResource("/sws/murcs/styles/search.css").toExternalForm());
        controller.selectText();
    }

    /**
     * Sets the navigation manager on the search view.
     * @param navigationManager The navigation manager
     */
    public void setNavigationManager(final Navigable navigationManager) {
        controller.setNavigationManager(navigationManager);
    }
}
