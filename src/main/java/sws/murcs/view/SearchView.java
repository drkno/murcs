package sws.murcs.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import sws.murcs.controller.SearchController;
import sws.murcs.controller.controls.popover.PopOver;

import java.io.IOException;

/**
 * Provides methods for starting a new search pane
 */
public class SearchView {
    public static void show() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SearchView.class.getResource("/sws/murcs/Search.fxml"));

        try {
            Parent parent = loader.load();

            SearchController controller = loader.getController();

            PopOver popOver = new PopOver(parent);
            popOver.detachableProperty().setValue(true);
            popOver.detachedProperty().setValue(true);
            popOver.show(App.getStage().getScene().getWindow());

        } catch (IOException e) {
            //We should never get here
            e.printStackTrace();
            return;
        }
    }
}
