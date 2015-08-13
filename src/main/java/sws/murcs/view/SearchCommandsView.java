package sws.murcs.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Window;
import sws.murcs.controller.SearchCommandsController;
import sws.murcs.controller.SearchController;
import sws.murcs.controller.controls.popover.ArrowLocation;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.debug.errorreporting.ErrorReporter;

/**
 * Search commands view for the pop over.
 */
public class SearchCommandsView {

    /**
     * The instance of the pop over.
     */
    private PopOver popOver;

    /**
     * The node which the pop over hangs from.
     */
    private Window hanger;

    /**
     * Setups the view and initializes the controller.
     * @param searchController The search controller which is required for the search commands controller.
     * @param pHanger The hanger which the popover hangs from.
     */
    public final void setup(final SearchController searchController, final Window pHanger) {
        hanger = pHanger;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/SearchCommands.fxml"));
        try {
            Parent view = loader.load();
            SearchCommandsController controller = loader.getController();
            controller.setup(searchController);
            popOver = new PopOver(view);
            popOver.show(hanger);
            popOver.detachedProperty().setValue(true);
            popOver.arrowLocationProperty().setValue(ArrowLocation.RIGHT_TOP);
            ((Parent) popOver.getSkin().getNode()).getStylesheets()
                    .add(controller.getClass().getResource("/sws/murcs/styles/search.css").toExternalForm());
        } catch (Exception e) {
            ErrorReporter.get().reportError(e, "Unable to create search commands popOver");
        }

    }
}
