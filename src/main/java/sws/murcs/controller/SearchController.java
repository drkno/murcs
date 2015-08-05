package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import javax.naming.directory.SearchResult;

/**
 * A controller for the search UI
 */
public class SearchController {

    @FXML
    private TextField searchText;

    @FXML
    private ListView<SearchResult> foundItems;

    @FXML
    private AnchorPane previewPane;

    @FXML
    private Label noItemsLabel;

    /**
     * Called when the form is instantiated
     */
    @FXML
    private void initialize() {
        noItemsLabel.setVisible(false);
        foundItems.setCellFactory(param -> new SearchResultCell());
    }

    private class SearchResultCell extends ListCell<SearchResult> {
        @Override
        protected void updateItem(SearchResult searchResult, boolean empty) {
            //searchResult.
        }
    /**
     * Disables all the controls within an editor and updates
     * the save changes button to be an edit button.
     * Note: this is really expensive.
     */
    private void disableControlsAndUpdateButton() {
        Parent view = editorPane.getView();
        JavaFXHelpers.findAndDestroyControls(view);
        view.setFocusTraversable(false);
        previewPane.setFocusTraversable(false);
        Button saveButton = editorPane.getController().getSaveChangesButton();
        saveButton.setVisible(true);
        saveButton.setText("Open In Window");
        saveButton.setOnAction(selectEvent);
    }
}
