package sws.murcs.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import sws.murcs.controller.SearchCommandsController;
import sws.murcs.controller.SearchController;
import sws.murcs.controller.controls.popover.ArrowLocation;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.debug.errorreporting.ErrorReporter;

/**
 * Created by Dion on 13/08/2015.
 */
public class SearchCommandsView {

    private PopOver popOver;
    private Node hanger;

    public final void setup(final SearchController searchController, final Node pHanger) {
        hanger = pHanger;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/SearchCommands.fxml"));
        try {
            Parent view = loader.load();
            SearchCommandsController controller = loader.getController();
            controller.setup(searchController);
            popOver = new PopOver(view);
            popOver.arrowLocationProperty().setValue(ArrowLocation.RIGHT_CENTER);
            popOver.show(hanger);
            ((Parent) popOver.getSkin().getNode()).getStylesheets()
                    .add(controller.getClass().getResource("/sws/murcs/styles/search.css").toExternalForm());
        } catch (Exception e) {
            ErrorReporter.get().reportError(e, "Unable to create search commands popOver");
        }

    }
}
