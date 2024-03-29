package sws.murcs.view;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Window;
import sws.murcs.controller.SearchController;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;

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
            loadInstance();
        }
        return instance;
    }

    /**
     * Loads a new instance of the search view.
     */
    private static void loadInstance() {
        FXMLLoader loader = new AutoLanguageFXMLLoader();
        loader.setLocation(SearchView.class.getResource("/sws/murcs/Search.fxml"));
        PopOver popOver = null;
        try {
            Parent parent = loader.load();
            popOver = new PopOver(parent);
            SearchController controller = loader.getController();
            controller.setPopOver(popOver);
            popOver.detachableProperty().setValue(true);
            popOver.detachedProperty().setValue(true);
            popOver.hideOnEscapeProperty().setValue(true);

            instance = new SearchView(popOver, controller);
        }
        catch (IOException e) {
            if (popOver != null) {
                popOver.hide();
            }
            ErrorReporter.get().reportError(e, "Could not create a search dialog");
        }
    }

    /**
     * Restarts the search pane (say, if someone changed the language, it would
     * be a good idea to call this).
     */
    public static void restart() {
        if (instance != null) {
            instance.controller.kill();
        }

        loadInstance();
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
}
