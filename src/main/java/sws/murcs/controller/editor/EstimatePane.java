package sws.murcs.controller.editor;

import com.sun.javafx.css.StyleManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import sws.murcs.debug.errorreporting.ErrorReporter;
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
public class EstimatePane implements UndoRedoChangeListener{

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


    public void configure(final String estimateType, final Backlog pBacklog) {
        estimate = estimateType;
        estimateLabel.setText(estimate);
        backlog = pBacklog;
        estimatePaneStories = new ArrayList<>();
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
        if (stories.size() != 0) {
            StoryLoadingTask storyThread = new StoryLoadingTask();
            storyThread.setStories(stories);
            thread = new Thread(storyThread);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @Override
    public void undoRedoNotification(ChangeState param) {

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
//            showStories();
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

}
