package sws.murcs.controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.search.SearchResult;

/**
 * Controller for search UI.
 */
public class SearchController {

    /**
     * TextField to contain search query.
     */
    @FXML
    private TextField searchText;

    /**
     * ListView to display results.
     */
    @FXML
    private ListView<SearchResult> foundItems;

    /**
     * Pane to preview a result in.
     */
    @FXML
    private GridPane previewPane;

    /**
     * Label to contain prompting information when no items are present
     * or no items are currently previewed.
     */
    @FXML
    private Label noItemsLabel;

    /**
     * PopOver window that the search UI is contained within.
     * Note: a reference to this is passed because we cannot
     * get the stage of a PopOver.
     */
    private PopOver popOverWindow;

    /**
     * Current editor pane.
     */
    private EditorPane editorPane;

    /**
     * Event to fire when an item is selected.
     */
    private EventHandler selectEvent;

    /**
     * Called when the form is instantiated.
     */
    @FXML
    private void initialize() {
        Parent parent = searchText.getParent();
        parent.getStylesheets()
                .add(getClass().getResource("/sws/murcs/styles/search.css").toExternalForm());

        EventHandler<KeyEvent> keyPressed = t -> {
            switch (t.getCode()) {
                case ESCAPE:
                    popOverWindow.hide();
                    break;
                case DOWN:
                    handleKeyDown(t);
                    break;
                case UP:
                    handleKeyUp(t);
                    break;
                default: break;
            }
        };

        parent.addEventHandler(KeyEvent.KEY_PRESSED, keyPressed);
        searchText.addEventHandler(KeyEvent.KEY_PRESSED, keyPressed);
        foundItems.addEventHandler(KeyEvent.KEY_PRESSED, keyPressed);
        previewPane.addEventHandler(KeyEvent.KEY_PRESSED, keyPressed);
        noItemsLabel.addEventHandler(KeyEvent.KEY_PRESSED, keyPressed);
        foundItems.getSelectionModel().selectedItemProperty().addListener(this::handleSelectionChanged);
        searchText.getStyleClass().add("search-input-placeholder");
        foundItems.getStyleClass().add("search-list");

        selectEvent = event -> {
            NavigationManager.navigateTo(foundItems.getSelectionModel().getSelectedItem().getModel());
            popOverWindow.hide();
        };

        searchText.textProperty().addListener((a, b, c) -> {
            if (c.length() == 0) {
                searchText.getStyleClass().add("search-input-placeholder");
            } else {
                searchText.getStyleClass().remove("search-input-placeholder");
            }
        });

        foundItems.getItems().addListener((ListChangeListener<SearchResult>) c -> {
            if (c.getList().size() == 0) {
                noItemsLabel.setText("No Items Found");
            } else {
                noItemsLabel.setText("Hover over item to preview");
            }
        });

        foundItems.setCellFactory(param -> {
            ListCell<SearchResult> cell = new ListCell<SearchResult>() {
                @Override
                public void updateItem(final SearchResult item, final boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (item == null) {
                            setText("null");
                        }
                        else {
                            setText(item.toString());
                        }
                        setGraphic(null);
                    }
                }
            };

            cell.setOnMouseEntered(event -> param.getSelectionModel().select(cell.getIndex()));
            cell.setOnMouseClicked(selectEvent);

            return cell;
        });

        // fixme: remove this
        PersistenceManager.getCurrent().getCurrentModel().getBacklogs().forEach(b -> {
            foundItems.getItems().add(new SearchResult(b, ""));
        });
    }

    /**
     * Handles presses of the UP key.
     * @param event up key event.
     */
    private void handleKeyUp(final KeyEvent event) {
        if (foundItems.getItems().size() == 0) {
            return;
        }

        if (searchText.isFocused()) {
            foundItems.requestFocus();
            foundItems.getSelectionModel().select(foundItems.getItems().size() - 1);
        }
        else if (foundItems.isFocused()
                && foundItems.getSelectionModel().getSelectedIndex() == 0) {
            searchText.requestFocus();
            searchText.selectAll();
            foundItems.getSelectionModel().clearSelection();
            event.consume();
        }
    }

    /**
     * Handles presses of the DOWN key.
     * @param event down key event.
     */
    private void handleKeyDown(final KeyEvent event) {
        if (foundItems.getItems().size() == 0) {
            return;
        }

        if (searchText.isFocused()) {
            foundItems.requestFocus();
            foundItems.getSelectionModel().select(0);
        }
        else if (foundItems.isFocused()
                && foundItems.getSelectionModel().getSelectedIndex() == foundItems.getItems().size() - 1) {
            searchText.requestFocus();
            searchText.selectAll();
            foundItems.getSelectionModel().clearSelection();
            event.consume();
        }
    }

    /**
     * Handles changes in the selection of the found items list.
     * Depending on selection it will update the preview pane to show the appropriate item.
     * @param observable observable list that caused the event.
     * @param oldValue previous selection.
     * @param newValue new selection.
     */
    private void handleSelectionChanged(final ObservableValue<? extends SearchResult> observable,
                                        final SearchResult oldValue, final SearchResult newValue) {
        if (newValue == null) {
            if (editorPane != null) {
                previewPane.getChildren().clear();
                editorPane.dispose();
                editorPane = null;
            }

            if (!previewPane.getChildren().contains(noItemsLabel)) {
                previewPane.getChildren().add(noItemsLabel);
            }
            return;
        }

        foundItems.scrollTo(newValue);

        if (editorPane == null) {
            editorPane = new EditorPane(newValue.getModel());
            disableControlsAndUpdateButton();
            if (previewPane.getChildren().size() > 0) {
                previewPane.getChildren().clear();
            }
            previewPane.getChildren().add(editorPane.getView());
        } else if (editorPane.getModel().getClass() == newValue.getModel().getClass()) {
            editorPane.setModel(newValue.getModel());
        } else {
            previewPane.getChildren().remove(editorPane.getView());
            editorPane.dispose();
            editorPane = new EditorPane(newValue.getModel());
            disableControlsAndUpdateButton();
            if (previewPane.getChildren().size() > 0) {
                previewPane.getChildren().clear();
            }
            previewPane.getChildren().add(editorPane.getView());
        }
    }

    /**
     * Sets the PopOver window this SearchController is displayed within.
     * Passed because we cannot get the stage of a PopupControl.
     * @param popOver The PopOver control this is displayed within.
     */
    public final void setPopOver(final PopOver popOver) {
        popOverWindow = popOver;
    }

    /**
     * Selects all search text. Used when the window is re-shown (this controller
     * has no independent way of detecting when that happens).
     */
    public final void selectText() {
        searchText.requestFocus();
        searchText.selectAll();
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
