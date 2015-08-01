package sws.murcs.view;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sws.murcs.controller.SearchController;

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

            Scene scene = new Scene(parent);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.setScene(scene);

            stage.show();

        } catch (IOException e) {
            //We should never get here
            e.printStackTrace();
            return;
        }
    }
}
