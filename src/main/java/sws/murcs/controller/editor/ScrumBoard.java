package sws.murcs.controller.editor;

import com.sun.javafx.css.StyleManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.view.App;

import java.util.ArrayList;
import java.util.List;

/**
 * The Scrum Board controller. This consists of several story
 * controllers.
 */
public class ScrumBoard extends GenericEditor<Sprint> {

    /**
     * The VBox to add the stories to.
     */
    @FXML
    private VBox storiesVBox;

    /**
     * The parent AnchorPane.
     */
    @FXML
    private AnchorPane mainView;

    /**
     * The header grid pane.
     */
    @FXML
    private GridPane header;

    /**
     * The sprint this scrum board is displaying.
     */
    private Sprint currentSprint;

    /**
     * The list of controllers for the stories on the scrum board.
     */
    private List<ScrumBoardStoryController> scrumBoardStories;

    /**
     * The thread used for loading stories.
     */
    private Thread thread;

    /**
     * Whether or not the thread that is loading stories should stop.
     */
    private boolean stop;

    /**
     * The controller of the container of the scrum board.
     */
    private SprintContainer sprintContainer;

    @Override
    protected void initialize() {
        scrumBoardStories = new ArrayList<>();
        mainView.getStyleClass().add("root");
        mainView.getStyleClass().add("scrumBoard");
        header.getStyleClass().add("scrumBoard-header");
        currentSprint = getModel();
    }

    @Override
    public void loadObject() {
        if (currentSprint == null || !getModel().equals(currentSprint) || !(currentSprint.getStories().size() == scrumBoardStories.size())) {
            loadStories();
        }
        scrumBoardStories.stream().forEach(ScrumBoardStoryController::update);
    }

    /**
     * Starts the thread for loading all the stories.
     */
    private void loadStories() {
        storiesVBox.getChildren().clear();
        scrumBoardStories.clear();
        currentSprint = getModel();
        StoryLoadingTask<Void> storyThread = new StoryLoadingTask<>();
        storyThread.setEditor(this);
        storyThread.setStories(getModel().getStories());
        thread = new Thread(storyThread);
        thread.setDaemon(true);
        thread.start();

    }

    /**
     * Sets the sprint container for the scrum board.
     * @param pSprintContainer The new sprint container.
     */
    public void setSprintContainer(final SprintContainer pSprintContainer) {
        sprintContainer = pSprintContainer;
    }

    /**
     * The task used to load all the stories into the editor.
     * @param <T> The type that you want the call function to return. (Void in this case).
     */
    private class StoryLoadingTask<T> extends javafx.concurrent.Task {

        /**
         * The stories to load.
         */
        private List<Story> stories;

        /**
         * The parent editor that the task editors belong to.
         */
        private ScrumBoard editor;

        /**
         * The current loading sprint.
         */
        private Sprint currentSprintLoading = getModel();

        /**
         * The loader that is used to load all of the stories.
         */
        private FXMLLoader threadStoryLoader = new FXMLLoader(getClass().getResource("/sws/murcs/ScrumBoardStory.fxml"));

        /**
         * Sets the list of stories to load.
         * @param newStories the stories to load.
         */
        protected void setStories(final List<Story> newStories) {
            stories = newStories;
        }

        /**
         * The editor that all the story editors generated belong to.
         * @param parent the parent editor of all the story editors generated.
         */
        protected void setEditor(final ScrumBoard parent) {
            editor = parent;
        }

        @Override
        protected T call() throws Exception {

            if (getModel() == null || !getModel().equals(currentSprintLoading)) {
                disposeOfStories();
                return null;
            }

            for (Story story : stories) {
                if (stop) {
                    break;
                }
                try {
                    threadStoryLoader.setRoot(null);
                    ScrumBoardStoryController controller = new ScrumBoardStoryController();
                    threadStoryLoader.setController(controller);
                    Parent view;
                    view = threadStoryLoader.load();
                    controller.setSprintContainer(sprintContainer);
                    controller.setStory(story);
                    controller.loadStory();
                    Platform.runLater(() -> {
                        if (getModel() == null || !getModel().equals(currentSprintLoading)) {
                            disposeOfStories();
                            return;
                        }
                        else {
                            scrumBoardStories.add(controller);
                                synchronized (StyleManager.getInstance()) {
                                    App.setOnStyleManagerThread(true);
                                    storiesVBox.getChildren().add(view);
                                    App.setOnStyleManagerThread(false);
                                }

                        }
                    });
                }
                catch (Exception e) {
                    ErrorReporter.get().reportError(e, "Unable to create new story in scrumBoard");
                }
            }
            return null;
        }

        @Override
        protected void succeeded() {
                if (getModel() == null || !getModel().equals(currentSprintLoading)) {
                    disposeOfStories();
                    return;
                }
                isLoaded = true;
        }
    }

    @Override
    protected void saveChangesAndErrors() {
    }

    /**
     * Disposes of stories in a sprint.
     */
    public void disposeOfStories() {
        if (thread != null && thread.isAlive()) {
            stop = true;
            try {
                thread.join();
            } catch (Throwable t) {
                ErrorReporter.get().reportError(t, "Failed to stop the loading tasks thread.");
            }
        }
        stop = false;
        for (ScrumBoardStoryController storyController: scrumBoardStories) {
            storyController.dispose();
        }
    }

    @Override
    public void dispose() {
        disposeOfStories();
        stop = true;
        scrumBoardStories = null;
        thread = null;
        sprintContainer = null;
        super.dispose();
    }
}
