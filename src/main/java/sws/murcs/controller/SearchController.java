package sws.murcs.controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.controller.pipes.Navigable;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Model;
import sws.murcs.search.SearchHandler;
import sws.murcs.search.SearchResult;

import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import sws.murcs.view.App;

/**
 * Controller for search UI.
 */
public class SearchController {

    /**
     * Icon for search.
     */
    @FXML
    private ImageView searchIcon;

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
     * The search hash used to overcome model serialisation problems.
     * Who knows why Java is trying to serialise this class, but it is.
     */
    private String searchHash = "cGxlYXNlIGtpbGwgbWUgbm93";

    /**
     * Event to fire when an item is selected.
     */
    private EventHandler selectEvent;

    /**
     * Handler to control searching.
     */
    private SearchHandler searchHandler;

    /**
     * Thread to render previewing from.
     */
    private Thread previewRenderThread;

    /**
     * Panes to display data in within the search window.
     */
    @FXML
    private GridPane resultsPane, searchPane;

    /**
     * The navigation manager for the active tab. I'm not sure if this is
     * expected behaviour (try and select the item in the active tab). Alternatives
     * might be to open in a new tab.
     */
    private Navigable navigationManager;

    /**
     * Called when the form is instantiated.
     */
    @FXML
    private void initialize() {
        previewRenderThread = new Thread(this::renderPreview);
        previewRenderThread.setDaemon(true);
        previewRenderThread.start();

        searchHandler = new SearchHandler();
        searchHash = new String(Base64.getDecoder().decode(searchHash));
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
                case ENTER:
                    if (foundItems.getSelectionModel().getSelectedIndex() > 0) {
                        selectEvent.handle(null);
                    }
                    else if (foundItems.getItems().size() == 1) {
                        foundItems.getSelectionModel().select(0);
                        selectEvent.handle(null);
                    }
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
        searchText.getStyleClass().add("search-input");
        foundItems.getStyleClass().add("search-list");

        selectEvent = event -> {
            navigationManager.navigateTo(foundItems.getSelectionModel().getSelectedItem().getModel());
            popOverWindow.hide();
        };

        searchText.textProperty().addListener((a, b, c) -> {
            String search = searchText.getText();
            if (search.equals(searchHash)) { // prevent a special NPE
                noItemsLabel.setText("OK");
            }
            else {
                searchHandler.searchFor(searchText.getText());
            }

            if (c.length() == 0) {
                searchText.getStyleClass().add("search-input-placeholder");
            }
            else {
                searchText.getStyleClass().remove("search-input-placeholder");
            }
        });

        foundItems.itemsProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("");
        });
        foundItems.getItems().addListener((ListChangeListener<SearchResult>) c -> {
            if (c.getList().size() == 0) {
                //noItemsLabel.setText("No Items Found");
                searchPane.getChildren().remove(resultsPane);
            } else {
                searchPane.getChildren().add(1, resultsPane);
                noItemsLabel.setText("Hover over item to preview");
            }
        });

        try {
            FXMLLoader loader = new FXMLLoader();

            foundItems.setCellFactory(param -> {
                ListCell<SearchResult> cell = new ListCell<SearchResult>() {
                    @Override
                    public void updateItem(final SearchResult item, final boolean empty) {
                        super.updateItem(item, empty);
                        try {
                            getStyleClass().add("list-cell-background");
                        } catch (NullPointerException e) {
                            // something happened in JavaFX....
                            // Who knows what, or why. HELP!? Do YOU know?
                            // do not report
                        }

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                        /*if (item == null) {
                            setText("null");
                        }
                        else {
                            setText(item.toString());
                        }
                        setGraphic(null);*/
                            try {
                                Node n = loader.load(getClass().getResource("/sws/murcs/SearchResult.fxml"));
                                setGraphic(n);
                            } catch (Exception e) {

                            }

                        }
                    }
                };

                cell.setOnMouseEntered(event -> param.getSelectionModel().select(cell.getIndex()));
                cell.setOnMouseClicked(selectEvent);

                return cell;
            });
        }
        catch (Exception e) {

        }




        foundItems.setItems(searchHandler.getResults());
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
            foundItems.scrollTo(foundItems.getItems().size() - 1);
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
        synchronized (previewRenderThread) {
            previewRenderThread.notify();
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
     * Background worker method to render previews in.
     */
    private void renderPreview() {
        final int disableDelay = 250;

        VBox loader = new VBox();

        ImageView imageView = new ImageView();
        Image spinner = new Image(getClass().getResourceAsStream("/sws/murcs/spinner.gif"));
        imageView.setImage(spinner);
        loader.getChildren().add(imageView);

        Label helpfulMessage = new Label("*CLUNK* /whir/");
        helpfulMessage.setStyle("-fx-fill: darkgray");
        loader.getChildren().add(helpfulMessage);
        VBox.setMargin(helpfulMessage, new Insets(5));

        loader.setAlignment(Pos.CENTER);

        while (true) {
            try {
                synchronized (previewRenderThread) {
                    previewRenderThread.wait();
                }

                if (!popOverWindow.isShowing()) {
                    return;
                }

                while (editorPane == null || foundItems.getSelectionModel().getSelectedItem() != null
                        && !editorPane.getModel().equals(foundItems.getSelectionModel().getSelectedItem().getModel())) {
                    Model newValue = foundItems.getSelectionModel().getSelectedItem().getModel();

                    if (editorPane == null || previewPane.getChildren().get(0).equals(editorPane.getView())) {
                        final CountDownLatch latch = new CountDownLatch(1);
                        Platform.runLater(() -> {
                            previewPane.getChildren().clear();
                            previewPane.getChildren().add(loader);
                            GridPane.setHalignment(loader, HPos.CENTER);
                            latch.countDown();
                        });
                        latch.await();
                    }

                    if (editorPane == null) {
                        editorPane = new EditorPane(newValue, App.getMainController());
                    }
                    else if (editorPane.getModel().getClass() == newValue.getClass()) {
                        editorPane.setModel(newValue);
                    }
                    else {
                        editorPane.dispose();
                        editorPane = new EditorPane(newValue, App.getMainController());
                    }
                    Thread.sleep(disableDelay);
                    disableControlsAndUpdateButton();
                }

                final CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    previewPane.getChildren().clear();
                    previewPane.getChildren().add(editorPane.getView());
                    latch.countDown();
                });
                latch.await();
            }
            catch (Exception e) {
                Platform.runLater(() -> {
                    ErrorReporter.get().reportError(e, "A failure occurred while rendering a search preview.");
                });
                return;
            }
        }
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

    /**
     * Gets the navigation manager used by the search window
     * @return The navigation manager
     */
    public Navigable getNavigationManager() {
        return navigationManager;
    }

    /**
     * Sets the navigation manager for the Search UI
     * @param navigationManager The new navigation manager.
     */
    public void setNavigationManager(Navigable navigationManager) {
        this.navigationManager = navigationManager;
    }
}
