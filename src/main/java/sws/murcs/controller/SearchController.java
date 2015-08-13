package sws.murcs.controller;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Model;
import sws.murcs.search.SearchHandler;
import sws.murcs.search.SearchResult;
import sws.murcs.view.SearchCommandsView;

import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

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
    protected TextField searchText;

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
    private String searchHash = "d2hhdCBpcyB0aGUgYW5zd2VyIHRvIGxpZmUgdGhlIHVuaXZlcnNlIGFuZCBldmVyeXRoaW5n";

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
    private Parent searchCommandsPane;
    private boolean searchCommandButtonActive = false;
    private SequentialTransition fadeInResultsPane;
    private SequentialTransition fadeOutResultsPane;
    private SequentialTransition fadeInCommandsPane;
    private SequentialTransition fadeOutCommandsPane;
    private Duration fadeDuration = Duration.millis(500);
    private boolean emptySearch = true;

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
            NavigationManager.navigateTo(foundItems.getSelectionModel().getSelectedItem().getModel());
            popOverWindow.hide();
        };

        searchText.textProperty().addListener((observable, oldValue, newValue) -> {
            String search = searchText.getText();
            if (Objects.equals(search, "")) {
                hideSearchList();
                emptySearch = true;
            }
            else if (search.equals(searchHash)) { // prevent a special NPE
                if (emptySearch) {
                    showSearchList();
                    emptySearch = false;
                }
                noItemsLabel.setText("42");
            }
            else {
                if (emptySearch) {
                    showSearchList();
                    emptySearch = false;
                }
                searchHandler.searchFor(searchText.getText());
            }

            if (newValue.length() == 0) {
                searchText.getStyleClass().add("search-input-placeholder");
            }
            else {
                searchText.getStyleClass().remove("search-input-placeholder");
            }
        });

        foundItems.getItems().addListener((ListChangeListener<SearchResult>) c -> {
            if (c.getList().size() == 0) {
                searchPane.getChildren().remove(resultsPane);
            } else {
                searchPane.getChildren().add(1, resultsPane);
                noItemsLabel.setText("Hover over item to preview");
            }
        });

        foundItems.setCellFactory(createItemsCellFactory());
        foundItems.setItems(searchHandler.getResults());

        searchIcon.setOnMousePressed(event -> showSearchCommandsPopOver());
        injectSearchCommands();

        resultsPane.setOpacity(0);

        FadeTransition fadeInResults = new FadeTransition(fadeDuration, resultsPane);
        fadeInResults.setAutoReverse(false);
        fadeInResults.setFromValue(0);
        fadeInResults.setToValue(1);

        FadeTransition fadeInCommands = new FadeTransition(fadeDuration, searchCommandsPane);
        fadeInCommands.setAutoReverse(false);
        fadeInCommands.setFromValue(0);
        fadeInCommands.setToValue(1);

        FadeTransition fadeOutResults = new FadeTransition(fadeDuration, resultsPane);
        fadeOutResults.setAutoReverse(false);
        fadeOutResults.setFromValue(1);
        fadeOutResults.setToValue(0);

        FadeTransition fadeOutCommands = new FadeTransition(fadeDuration, searchCommandsPane);
        fadeOutCommands.setAutoReverse(false);
        fadeOutCommands.setFromValue(1);
        fadeOutCommands.setToValue(0);

        PauseTransition pauseTransition = new PauseTransition(Duration.millis(200));

        fadeInResultsPane = new SequentialTransition(fadeInResults, pauseTransition);
        fadeInCommandsPane = new SequentialTransition(fadeInCommands, pauseTransition);
        fadeOutResultsPane = new SequentialTransition(fadeOutResults, pauseTransition);
        fadeOutCommandsPane = new SequentialTransition(fadeOutCommands, pauseTransition);
    }

    private void showSearchCommandsPopOver() {
        if (searchCommandButtonActive) {
            SearchCommandsView searchCommandsView = new SearchCommandsView();
            searchCommandsView.setup(this, searchIcon);
        }
    }

    private void showSearchList() {
        fadeInResultsPane.play();
        fadeOutCommandsPane.play();
        fadeInResultsPane.setOnFinished(event -> resultsPane.setVisible(true));
        fadeOutCommandsPane.setOnFinished(event -> searchCommandsPane.setVisible(false));
        searchCommandButtonActive = true;
    }

    private void hideSearchList() {
        fadeOutResultsPane.play();
        fadeInCommandsPane.play();
        fadeOutResultsPane.setOnFinished(event -> resultsPane.setVisible(false));
        fadeInCommandsPane.setOnFinished(event -> searchCommandsPane.setVisible(true));
        searchCommandButtonActive = false;
    }

    /**
     * Creates a new CellFactory for SearchResult ListCells.
     * @return a new CellFactory.
     */
    private Callback<ListView<SearchResult>, ListCell<SearchResult>> createItemsCellFactory() {
        return param -> {
            ListCell<SearchResult> cell = new ListCell<SearchResult>() {
                @Override
                public void updateItem(final SearchResult item, final boolean empty) {
                    super.updateItem(item, empty);
                    Platform.runLater(() -> {
                        // This is to get around JavaFX bug https://bugs.openjdk.java.net/browse/JDK-8097541
                        getStyleClass().add("list-cell-background");
                    });

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        HBox box = new HBox();
                        ObservableList<Node> children = box.getChildren();

                        Label context = new Label(item.getModelType() + ": " + item.getFieldName());
                        context.getStyleClass().add("search-result-context");

                        Label selectionBefore = new Label(item.selectionBefore());
                        children.add(selectionBefore);

                        List<String> matches = item.getMatches();
                        for (int i = 0; i < matches.size(); i++) {
                            Label matchLabel = new Label(matches.get(i));
                            if (i % 2 == 0) {
                                matchLabel.getStyleClass().add("search-result");
                            }
                            children.add(matchLabel);
                        }

                        Label selectionAfter = new Label(item.selectionAfter());
                        children.add(selectionAfter);


                        VBox vbox = new VBox();
                        vbox.getChildren().add(context);
                        VBox.setVgrow(context, Priority.ALWAYS);
                        vbox.setFillWidth(true);
                        vbox.getChildren().add(box);
                        VBox.setVgrow(box, Priority.ALWAYS);
                        setGraphic(vbox);
                    }
                }
            };

            cell.setOnMouseEntered(event -> param.getSelectionModel().select(cell.getIndex()));
            cell.setOnMouseClicked(selectEvent);

            return cell;
        };
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
        if (newValue == null) {
            previewPane.getChildren().clear();
            previewPane.getChildren().add(noItemsLabel);
            return;
        }

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
        final int helpfulMessageMargin = 5;

        VBox loader = new VBox();

        ImageView imageView = new ImageView();
        Image spinner = new Image(getClass().getResourceAsStream("/sws/murcs/spinner.gif"));
        imageView.setImage(spinner);
        loader.getChildren().add(imageView);

        Label helpfulMessage = new Label("*CLUNK* /whir/");
        helpfulMessage.getStyleClass().add("search-preview-message");
        loader.getChildren().add(helpfulMessage);
        VBox.setMargin(helpfulMessage, new Insets(helpfulMessageMargin));
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
                        editorPane = new EditorPane(newValue);
                    }
                    else if (editorPane.getModel().getClass() == newValue.getClass()) {
                        editorPane.setModel(newValue);
                    }
                    else {
                        editorPane.dispose();
                        editorPane = new EditorPane(newValue);
                    }
                    editorPane.getView().getStyleClass().add("search-preview");
                    Thread.sleep(disableDelay);
                    disableControlsAndUpdateButton();
                }

                final CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    previewPane.getChildren().clear();
                    if (foundItems.getSelectionModel().getSelectedItem() == null) {
                        previewPane.getChildren().add(noItemsLabel);
                    }
                    else {
                        previewPane.getChildren().add(editorPane.getView());
                    }
                    latch.countDown();
                });
                latch.await();
            }
            catch (Exception e) {
                Platform.runLater(() ->
                        ErrorReporter.get().reportError(e, "A failure occurred while rendering a search preview."));
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
        saveButton.getStyleClass().add("button-default");
        saveButton.setVisible(true);
        saveButton.setDisable(false);
        saveButton.setText("Open In Window");
        saveButton.setOnAction(selectEvent);
    }

    /**
     * Injects a task editor tied to the given task.
     */
    private void injectSearchCommands() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/SearchCommands.fxml"));
        try {
            searchCommandsPane = loader.load();
            SearchCommandsController controller = loader.getController();
            controller.setup(this);
            searchPane.add(searchCommandsPane, 0, 1);
        } catch (Exception e) {
            ErrorReporter.get().reportError(e, "Unable to create search commands");
        }
    }
}
