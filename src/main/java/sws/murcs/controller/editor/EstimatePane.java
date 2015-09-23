package sws.murcs.controller.editor;

import com.sun.javafx.css.StyleManager;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Backlog;
import sws.murcs.model.Story;
import sws.murcs.view.App;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by wooll on 17/09/2015.
 */
public class EstimatePane implements UndoRedoChangeListener {

    @FXML
    private BorderPane estimateBorderPane;

    @FXML
    private GridPane estimateGridPane;

    @FXML
    private TilePane storiesContainerTilePane;

    @FXML
    private Label estimateLabel;

    private List<Story> stories;

    private Backlog backlog;

    private String estimate;

    private List<EstimatePaneStoryController> estimatePaneStories;

    /**
     * The thread used for loading stories.
     */
    private Thread thread;

    /**
     * Whether or not the thread that is loading stories should stop.
     */
    private boolean stop;

    /**
     * The story currently being dragged.
     */
    private static Story draggingStory;

    /**
     * The estimate of the currently dragging story.
     */
    private static String draggingEstimate;

    @FXML
    private void initialize() {
        UndoRedoManager.get().addChangeListener(this);
        estimateBorderPane.getStyleClass().add("separators");
    }

    public void configure(final String estimateType, final Backlog pBacklog) {
        estimate = estimateType;
        estimateLabel.setText(estimate);
        backlog = pBacklog;
        estimatePaneStories = new ArrayList<>();

        addDragOverHandler(storiesContainerTilePane);
        addDragEnteredHandler(storiesContainerTilePane);
        addDragExitedHandler(storiesContainerTilePane);
        addDragDroppedHandler(storiesContainerTilePane, estimate);
    }

    protected void loadObject() {
        storiesContainerTilePane.getChildren().clear();
        loadStories();
    }

    private void loadStories() {
        stories = backlog.getWorkspaceStories()
                .stream()
                .filter(s -> Objects.equals(s.getEstimate(), estimate))
                .collect(Collectors.toList());
        if (stories.size() > 0) {
            StoryLoadingTask storyThread = new StoryLoadingTask();
            storyThread.setStories(stories);
            thread = new Thread(storyThread);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * The task used to load all the stories into estimate pane.
     */
    private class StoryLoadingTask extends javafx.concurrent.Task {

        /**
         * The stories to load.
         */
        private List<Story> stories;

        /**
         * The current loading backlog.
         */
        private Backlog currentBacklog = backlog;

        /**
         * The loader that is used to load all of the stories.
         */
        private FXMLLoader threadStoryLoader = new FXMLLoader(getClass().getResource("/sws/murcs/EstimatePaneStory.fxml"));

        /**
         * Sets the list of stories to load.
         * @param newStories the stories to load.
         */
        protected void setStories(final List<Story> newStories) {
            stories = newStories;
        }

        @Override
        protected Story call() throws Exception {

            if (backlog == null || !backlog.equals(currentBacklog)) {
                disposeOfStories();
                return null;
            }

            for (Story story : stories) {
                if (stop) {
                    break;
                }
                try {
                    threadStoryLoader.setRoot(null);
                    EstimatePaneStoryController controller = new EstimatePaneStoryController();
                    threadStoryLoader.setController(controller);
                    Parent view;
                    view = threadStoryLoader.load();
                    controller.setStory(story);
                    controller.setBacklog(currentBacklog);
                    controller.setParent(EstimatePane.this);
                    controller.loadStory();
                    Platform.runLater(() -> {
                        if (backlog == null || !backlog.equals(currentBacklog)) {
                            disposeOfStories();
                            return;
                        }
                        else {
                            estimatePaneStories.add(controller);
                            synchronized (StyleManager.getInstance()) {
                                App.setOnStyleManagerThread(true);
                                storiesContainerTilePane.getChildren().add(view);
                                addDragDetectedHandler(view, story, estimate);
                                addDragDoneHandler(view);
                                App.setOnStyleManagerThread(false);
                            }

                        }
                    });
                }
                catch (Exception e) {
                    ErrorReporter.get().reportError(e, "Unable to create new story in estimate pane");
                }
            }
            // If the thread should stop because the story or sprint changed then return to halt execution.
            return null;
        }

        @Override
        protected void succeeded() {
            if (backlog == null || !backlog.equals(currentBacklog)) {
                disposeOfStories();
                return;
            }
        }
    }

    /**
     * Disposes of stories in the estimate pane.
     */
    public void disposeOfStories() {
        if (thread != null && thread.isAlive()) {
            stop = true;
            try {
                thread.join();
            } catch (Throwable t) {
                ErrorReporter.get().reportError(t, "Failed to stop the loading stories thread.");
            }
        }
        stop = false;
        estimatePaneStories.forEach(EstimatePaneStoryController::dispose);
    }

    public void dispose() {
        disposeOfStories();
        stop = true;
        stories = null;
        backlog = null;
        estimate = null;
        estimatePaneStories = null;
    }

    @Override
    public void undoRedoNotification(final ChangeState param) {
        if (param == ChangeState.Remake || param == ChangeState.Revert) {
            this.loadObject();
        }
    }

    /**
     * Adds a drag detected handler to the node.
     * @param source The node to add the handler to
     * @param story The task that the dragged node represents
     * @param estimate The story that the task is from
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void addDragDetectedHandler(final Node source, final Story story, final String estimate) {
        source.setOnDragDetected(event -> {
            draggingStory = story;
            draggingEstimate = estimate;
            Dragboard dragBoard = source.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(story.getShortName());
            dragBoard.setContent(content);
            WritableImage image = source.snapshot(new SnapshotParameters(), null);
            dragBoard.setDragView(image, image.getWidth() * 0.5, image.getHeight() * 0.5);
            event.consume();
        });
    }

    /**
     * Adds a drag over handler to the node.
     * @param target The node to add the handler to
     */
    private void addDragOverHandler(final Pane target) {
        target.setOnDragOver(event -> {
            if (event.getGestureSource() != target
                    && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
    }

    /**
     * Adds a drag entered handler to the node.
     * @param target The node to add the handler to
     */
    private void addDragEnteredHandler(final Pane target) {
        target.setOnDragEntered(event -> {
            if (event.getGestureSource() != target
                    && event.getDragboard().hasString()) {
              target.getStyleClass().add("scrumBoard-story-legal");
            }
            event.consume();
        });
    }

    /**
     * Adds a drag exited handler to the node.
     * @param target The node to add the handler to
     */
    private void addDragExitedHandler(final Pane target) {
        target.setOnDragExited(event -> {
            target.getStyleClass().removeAll("scrumBoard-story-legal");
            event.consume();
        });
    }

    /**
     * Adds a drag dropped handler to the node.
     * @param target The node to add the handler to
     * @param newEstimate The new state to set the task to
     */
    private void addDragDroppedHandler(final Pane target, final String newEstimate) {
        target.setOnDragDropped(event -> {
            draggingStory.setEstimate(newEstimate);
            event.setDropCompleted(true);
            loadObject();
            event.consume();
        });
    }

    /**
     * Adds a drag done handler to the node.
     * @param source The node to add the handler to
     */
    private void addDragDoneHandler(final Node source) {
        source.setOnDragDone(Event::consume);
    }
}
