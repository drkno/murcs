package sws.murcs.controller.editor;

import com.sun.javafx.css.StyleManager;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.view.App;

/**
 * The Scrum Board controller. This consists of several story
 * controllers.
 */
public class ScrumBoard extends GenericEditor<Sprint> {

    /**
     * The container of the no stories message.
     */
    @FXML
    private VBox noStoriesMessageVBox;

    /**
     * The container of the loading indicator.
     */
    @FXML
    private VBox loadingIndicatorVBox;

    /**
     * The container of the stories.
     */
    @FXML
    private ScrollPane storiesScrollPane;

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

    /**
     * The duration of the fade time.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private Duration fadeDuration = Duration.millis(500);

    /**
     * The transition for fading in the stories after loading.
     */
    private SequentialTransition fadeInStoriesWhenLoaded;

    @Override
    protected void initialize() {
        scrumBoardStories = new ArrayList<>();
        mainView.getStyleClass().add("root");
        mainView.getStyleClass().add("scrumBoard");
        header.getStyleClass().add("scrumBoard-header");
        currentSprint = getModel();
        setupFadeAnimations();
    }

    /**
     * Sets up the fade animation for loading stories onto the scrum board.
     */
    private void setupFadeAnimations() {
        FadeTransition fadeInStories = new FadeTransition(fadeDuration, storiesScrollPane);
        fadeInStories.setAutoReverse(false);
        fadeInStories.setFromValue(0);
        fadeInStories.setToValue(1);

        FadeTransition fadeOutLoadingIndicator = new FadeTransition(fadeDuration, loadingIndicatorVBox);
        fadeOutLoadingIndicator.setAutoReverse(false);
        fadeOutLoadingIndicator.setFromValue(1);
        fadeOutLoadingIndicator.setToValue(0);

        fadeInStoriesWhenLoaded = new SequentialTransition(fadeOutLoadingIndicator, fadeInStories);
    }

    @Override
    public void loadObject() {
        if (getModel() != null) {
            if (currentSprint == null
                    || !getModel().equals(currentSprint)
                    || !(currentSprint.getStories().size() == scrumBoardStories.size())) {
                loadStories();
            }
            scrumBoardStories.stream().forEach(ScrumBoardStoryController::update);
        }
    }

    /**
     * Starts the thread for loading all the stories.
     */
    private void loadStories() {
        Platform.runLater(() -> {
            storiesVBox.getChildren().clear();
        });
        scrumBoardStories.clear();
        currentSprint = getModel();
        loadingIndicatorVBox.setOpacity(1);
        storiesScrollPane.setOpacity(0);
        storiesScrollPane.setVisible(false);
        noStoriesMessageVBox.setVisible(false);
        loadingIndicatorVBox.setVisible(false);
        if (currentSprint.getStories().size() != 0) {
            loadingIndicatorVBox.setVisible(true);
            storiesScrollPane.setVisible(true);
            StoryLoadingTask storyThread = new StoryLoadingTask();
            storyThread.setEditor(this);
            storyThread.setStories(getModel().getStories());
            thread = new Thread(storyThread);
            thread.setDaemon(true);
            thread.start();
        }
        else {
            noStoriesMessageVBox.setVisible(true);
        }
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
     */
    private class StoryLoadingTask extends javafx.concurrent.Task {

        /**
         * The stories to load.
         */
        private List<Story> stories;

        /**
         * The parent editor that the scrumBoard story editors belong to.
         */
        private ScrumBoard editor;

        /**
         * The current loading sprint.
         */
        private Sprint currentSprintLoading = getModel();

        /**
         * The loader that is used to load all of the stories.
         */
        private FXMLLoader threadStoryLoader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/ScrumBoardStory.fxml"));

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
        protected Story call() throws Exception {

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
            // If the thread should stop because the story or sprint changed then return to halt execution.
            return null;
        }

        @Override
        protected void succeeded() {
            if (getModel() == null || !getModel().equals(currentSprintLoading)) {
                disposeOfStories();
                return;
            }
            isLoaded = true;
            showStories();
        }
    }

    /**
     * Shows the stories when finished loading them.
     */
    private void showStories() {
        fadeInStoriesWhenLoaded.play();
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
                ErrorReporter.get().reportError(t, "Failed to stop the loading stories thread.");
            }
        }
        stop = false;
        scrumBoardStories.forEach(ScrumBoardStoryController::dispose);
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
