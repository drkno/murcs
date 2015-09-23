package sws.murcs.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import sws.murcs.controller.SearchCommandsController;
import sws.murcs.controller.SearchController;
import sws.murcs.controller.controls.popover.ArrowLocation;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;

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
    private Node hanger;

    /**
     * Setups the view and initializes the controller.
     * @param searchController The search controller which is required for the search commands controller.
     * @param pHanger The hanger which the popover hangs from.
     */
    public final void setup(final SearchController searchController, final Node pHanger) {
        if (popOver != null) {
            if (!popOver.isShowing()) {
                popOver.show(pHanger);
            }
            return;
        }

        hanger = pHanger;
        FXMLLoader loader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/SearchCommands.fxml"));
        try {
            Parent view = loader.load();
            SearchCommandsController controller = loader.getController();
            controller.setup(searchController);
            popOver = new PopOver(view);
            popOver.arrowLocationProperty().setValue(ArrowLocation.TOP_LEFT);
            popOver.detachableProperty().setValue(false);
            popOver.show(hanger);
            ((Parent) popOver.getSkin().getNode()).getStylesheets()
                    .add(controller.getClass().getResource("/sws/murcs/styles/search.css").toExternalForm());
        } catch (Exception e) {
            hide();
            ErrorReporter.get().reportError(e, "Unable to create search commands popOver");
        }
    }

    /**
     * Hides the popover if it is currently being shown.
     */
    public final void hide() {
        if (popOver != null) {
            popOver.hide();
        }
    }
}
