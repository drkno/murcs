package sws.murcs.controller.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import sws.murcs.controller.controls.ModelProgressBar;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A controller for stories on the scrum board. Handles dragging and dropping.
 */
public class ScrumBoardStoryController {
    /**
     * The main grid pane for the story.
     */
    @FXML
    private GridPane storyMainGridPane;

    /**
     * The outer VBox for the story column.
     */
    @FXML
    private VBox storyOuterVBox;

    /**
     * A container for static controls on the story.
     */
    @FXML
    private VBox storyStaticControlsVBox;

    /**
     * The grid pane containing static controls for the story.
     */
    @FXML
    private GridPane storyStaticControlsGridPane;

    /**
     * A button for expanding/collapsing the row.
     */
    @FXML
    private Button storyCollapseExpandButton;

    /**
     * The image used on the expand/collapse button.
     */
    @FXML
    private ImageView collapseExpandStoryButton;

    /**
     * The hyperlink for navigating to the story view.
     */
    @FXML
    private Hyperlink storyHyperLink;

    /**
     * The slider for controlling story state. This is disabled until all tasks in the story are
     * marked as done.
     */
    @FXML
    private Slider storyStateSlider;

    /**
     * A Vbox containing extra information about the story.
     */
    @FXML
    private VBox storyExtraInfoVBox;

    /**
     * The vBox representing the to do column.
     */
    @FXML
    private VBox toDoOuterVBox;

    /**
     * A vBox containing information about the
     * to do column (x tasks have not been started).
     */
    @FXML
    private VBox toDoBaseInfoVBox;

    /**
     * A VBox containing more information about the not started tasks.
     */
    @FXML
    private VBox toDoMoreInfoVBox;

    /**
     * A vBox representing the in progress column.
     */
    @FXML
    private VBox inProgressOuterVBox;

    /**
     * The vBox that contains information about the in progress tasks,
     * providing an overview, so to
     * speak.
     */
    @FXML
    private VBox inProgressBaseInfoVBox;

    /**
     * The VBox containing in progress tasks.
     */
    @FXML
    private VBox inProgressMoreInfoVBox;

    /**
     * The vBox representing the done column
     * of the scrum board.
     */
    @FXML
    private VBox doneOuterVBox;

    /**
     * The vBox containing an overview of the done
     * tasks.
     */
    @FXML
    private VBox doneBaseInfoVBox;

    /**
     * The vBox containing the
     * tasks that are done.
     */
    @FXML
    private VBox doneMoreInfoVBox;

    /**
     * The main border pane representing the
     * story on the scrum board.
     */
    @FXML
    private BorderPane mainPane;

    /**
     * A label containing the
     * current state of the
     * story.
     */
    @FXML
    private Label storyStateLabel;

    /**
     * A container that shows where the progress bar will go on the screen.
     */
    @FXML
    private VBox progressBarContainer;

    /**
     * The to do task number in the task summary view.
     */
    @FXML
    private Label todoTaskNumberLabel;

    /**
     * The in progress task number in the task summary view.
     */
    @FXML
    private Label inProgressTaskNumberLabel;

    /**
     * The done task number in the task summary view.
     */
    @FXML
    private Label doneTaskNumberLabel;

    /**
     * A progress bar that indicates sprint progress.
     */
    private ModelProgressBar progressBar;

    /**
     * The story that this controller
     * is responsible for.
     */
    private Story story;

    /**
     * A boolean indicating whether or
     * not the scrum board is currently
     * expanded, that is, whether or not
     * all the tasks should be displayed,
     * or just an overview.
     */
    private Boolean infoViewStateMore = true;

    /**
     * The parent SprintContainer of this view.
     */
    private SprintContainer sprintContainer;

    /**
     * The task currently being dragged.
     */
    private static Task draggingTask;

    /**
     * The story of the currently dragging task.
     */
    private static Story draggingStory;

    /**
     * The pane that the draggingTask originated from.
     */
    private static Pane sourcePane;

    /**
     * Initializes the controller.
     */
    @FXML
    private void initialize() {

        mainPane.getStyleClass().add("root");
        mainPane.getStyleClass().add("scrumBoard-story");
        progressBar = new ModelProgressBar(true);
        progressBarContainer.getChildren().addAll(progressBar);
        storyOuterVBox.getStyleClass().add("scrumBoard-separators");
        toDoOuterVBox.getStyleClass().add("scrumBoard-separators");
        inProgressOuterVBox.getStyleClass().add("scrumBoard-separators");

        storyStateSlider.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (storyStateSlider.getValue() == 1) {
                storyStateSlider.setValue(0);
            } else {
                storyStateSlider.setValue(1);
            }
        });

        storyStateSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            long currentValue = story.getStoryState() == Story.StoryState.Done ? 1L : 0L;
            if (newValue.longValue() != currentValue) {
                Story.StoryState newState = newValue.longValue() == 1L
                        ? Story.StoryState.Done : Story.StoryState.Ready;
                story.setStoryState(newState);

                updateToggleStatus();
            }
        });
    }

    /**
     * Loads or refreshes the story
     * that this controller is responsible
     * for. Note: This method will likely be
     * called far more often than you expect,
     * and for the story that is currently loaded.
     * As such, you should be sure it only loads
     * what it has to (e.g. no point setting the
     * title of the story again if it hasn't
     * changed).
     */
    protected void loadStory() {
        updateToggleStatus();

        hideMoreInfo();
        VBox[] positions = new VBox[] {toDoMoreInfoVBox, inProgressMoreInfoVBox, doneMoreInfoVBox};
        for (int i = 0; i < positions.length; i++) {
            addDragOverHandler(positions[i]);
            addDragEnteredHandler(positions[i]);
            addDragExitedHandler(positions[i]);
            addDragDroppedHandler(positions[i], TaskState.values()[i]);
        }

        storyHyperLink.setText(story.getShortName());
        storyHyperLink.setWrapText(true);
        storyHyperLink.setOnAction(event -> sprintContainer.getNavigationManager().navigateTo(story));

        FXMLLoader loader = new FXMLLoader(ScrumBoardStoryController.class.getResource("/sws/murcs/ScrumTask.fxml"));
        for (Task task : story.getTasks()) {
            try {
                loader.setRoot(null);
                loader.setController(null);
                Parent root = loader.load();
                ScrumTaskController controller = loader.getController();
                controller.configure(task, story, this);
                VBox initialVBox = null;
                switch (task.getState()) {
                    case NotStarted:
                        initialVBox = toDoMoreInfoVBox;
                        break;
                    case InProgress:
                        initialVBox = inProgressMoreInfoVBox;
                        break;
                    case Done:
                        initialVBox = doneMoreInfoVBox;
                        break;
                    default:
                        break;
                }
                initialVBox.getChildren().add(root);
                addDragDetectedHandler(root, task, story);
                addDragDoneHandler(root, task, positions);
            } catch (IOException e) {
                ErrorReporter.get().reportError(e, "Failed to load task in scrumBoard");
            }
        }

        updateTaskOverviews();
    }

    /**
     * Sets up the minute info for a task, such as to do, doing and done.
     */
    private void updateTaskOverviews() {
        List<Task> tasksToDo = story.getTasks()
                .stream()
                .filter(t -> t.getState().equals(TaskState.NotStarted))
                .collect(Collectors.toList());
        List<Task> tasksInProgress = story.getTasks()
                .stream()
                .filter(t -> t.getState().equals(TaskState.InProgress))
                .collect(Collectors.toList());
        List<Task> tasksDone = story.getTasks()
                .stream()
                .filter(t -> t.getState().equals(TaskState.Done))
                .collect(Collectors.toList());

        todoTaskNumberLabel.setText(String.valueOf(tasksToDo.size()));
        inProgressTaskNumberLabel.setText(String.valueOf(tasksInProgress.size()));
        doneTaskNumberLabel.setText(String.valueOf(tasksDone.size()));
    }

    /**
     * Sets the story for the controller.
     * Note: You need to manually call the
     * loadStory method if you want this to
     * have an immediate result. If you don't
     * we can't guarantee when the update
     * will happen.
     *
     * @param pStory The story
     */
    public void setStory(final Story pStory) {
        story = pStory;
    }

    /**
     * Set the sprint container for this story.
     * You should do this before trying to use
     * this controller.
     *
     * @param pSprintContainer The container controller
     */
    public void setSprintContainer(final SprintContainer pSprintContainer) {
        sprintContainer = pSprintContainer;
    }

    /**
     * Toggles between showing a brief overview
     * of the state of tasks within the story
     * and all the tasks in the draggable
     * scrum board view.
     *
     * @param actionEvent The event from the button in the UI.
     */
    @FXML
    private void toggleInfoView(final ActionEvent actionEvent) {
        if (infoViewStateMore) {
            hideLessInfo();
            showMoreInfo();
            infoViewStateMore = false;
        }
        else {
            hideMoreInfo();
            showLessInfo();
            infoViewStateMore = true;
        }
    }

    /**
     * Hides the "Less Info" view of
     * the story.
     * Note: Calling this alone will
     * not show the "More Info"
     * view.
     */
    private void hideLessInfo() {
        toDoOuterVBox.getChildren().remove(toDoBaseInfoVBox);
        inProgressOuterVBox.getChildren().remove(inProgressBaseInfoVBox);
        doneOuterVBox.getChildren().remove(doneBaseInfoVBox);
        toDoBaseInfoVBox.setVisible(false);
        inProgressBaseInfoVBox.setVisible(false);
        doneBaseInfoVBox.setVisible(false);
    }

    /**
     * Shows the "Less Info" view of the story.
     * Note: That calling this alone doesn't
     * hide the "More Info" view.
     */
    private void showLessInfo() {
        toDoOuterVBox.getChildren().add(toDoBaseInfoVBox);
        inProgressOuterVBox.getChildren().add(inProgressBaseInfoVBox);
        doneOuterVBox.getChildren().add(doneBaseInfoVBox);
        toDoBaseInfoVBox.setVisible(true);
        inProgressBaseInfoVBox.setVisible(true);
        doneBaseInfoVBox.setVisible(true);
        updateTaskOverviews();
    }

    /**
     * Hides the complete list of tasks
     * from view.
     * Note: Calling this method alone will
     * not show the "Less Info" view,
     * you have to do that manually.
     */
    private void hideMoreInfo() {
        toDoOuterVBox.getChildren().remove(toDoMoreInfoVBox);
        inProgressOuterVBox.getChildren().remove(inProgressMoreInfoVBox);
        doneOuterVBox.getChildren().remove(doneMoreInfoVBox);
        storyOuterVBox.getChildren().remove(storyExtraInfoVBox);
        storyExtraInfoVBox.setVisible(false);
        toDoMoreInfoVBox.setVisible(false);
        inProgressMoreInfoVBox.setVisible(false);
        doneMoreInfoVBox.setVisible(false);
    }

    /**
     * Shows the "More Info" view.
     * Note: When you use this method you
     * should also all "hideLessInfo" to
     * hide the little overview.
     */
    private void showMoreInfo() {
        toDoOuterVBox.getChildren().add(toDoMoreInfoVBox);
        inProgressOuterVBox.getChildren().add(inProgressMoreInfoVBox);
        doneOuterVBox.getChildren().add(doneMoreInfoVBox);
        storyOuterVBox.getChildren().add(storyExtraInfoVBox);
        storyExtraInfoVBox.setVisible(true);
        toDoMoreInfoVBox.setVisible(true);
        inProgressMoreInfoVBox.setVisible(true);
        doneMoreInfoVBox.setVisible(true);
    }

    /**
     * Sets up the ScrumBoardStoryController with a sprint container
     * and a story. You can also these individually with setStory and
     * setScrumBoardContainer methods.
     *
     * @param pStory           The story
     * @param pSprintContainer The sprint container.
     */
    public void setup(final Story pStory, final SprintContainer pSprintContainer) {
        story = pStory;
        sprintContainer = pSprintContainer;
    }

    /**
     * Updates the disabled state of the doneCheckBox.
     */
    private void updateToggleStatus() {
        for (Task task : story.getTasks()) {
            if (task.getState() != TaskState.Done) {
                storyStateSlider.setValue(0);
                storyStateSlider.setDisable(true);
                storyStateSlider.getStyleClass().removeAll("alt");
                storyStateLabel.setText("Story is ongoing");
                story.setStoryState(Story.StoryState.Ready);
                return;
            }
        }
        storyStateSlider.setDisable(false);
        if (story.getStoryState() == Story.StoryState.Done) {
            storyStateSlider.setValue(1);
            if (!storyStateLabel.getStyleClass().contains("alt")) {
                storyStateSlider.getStyleClass().add("alt");
            }
            storyStateLabel.setText("Story Done :)");
        }
        else {
            storyStateLabel.setText("Mark story as Done");
            if (!storyStateLabel.getStyleClass().contains("alt")) {
                storyStateSlider.getStyleClass().add("alt");
            }
        }
    }

    /**
     * Adds a drag detected handler to the node.
     * @param source The node to add the handler to
     * @param task The task that the dragged node represents
     * @param story The story that the task is from
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void addDragDetectedHandler(final Node source, final Task task, final Story story) {
        source.setOnDragDetected(event -> {
            draggingTask = task;
            draggingStory = story;
            Dragboard dragBoard = source.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(task.getName());
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
            if (story == draggingStory && event.getGestureSource() != target && event.getDragboard().hasString()) {
                sourcePane = target;
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
            if (event.getGestureSource() != target && event.getDragboard().hasString()) {
                if (story == draggingStory) {
                    // TODO: Style target with drop prompt
                } else {
                    // TODO: Potentially style it to indicate that you cannot drop here
                }
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
            // TODO: Return style to normal
            event.consume();
        });
    }

    /**
     * Adds a drag dropped handler to the node.
     * @param target The node to add the handler to
     * @param newState The new state to set the task to
     */
    private void addDragDroppedHandler(final Pane target, final TaskState newState) {
        target.setOnDragDropped(event -> {
            draggingTask.setState(newState);
            updateToggleStatus();
            event.setDropCompleted(true);
            event.consume();
        });
    }

    /**
     * Adds a drag done handler to the node.
     * @param source The node to add the handler to
     * @param task The task represented by the node
     * @param places Array of Panes where the node can be dropped
     */
    private void addDragDoneHandler(final Node source, final Task task, final Pane[] places) {
        source.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                sourcePane.getChildren().remove(source);
                places[task.getState().ordinal()].getChildren().add(source);
            }
            event.consume();
        });
    }

    /**
     * Updates the progress bar for the story.
     */
    protected void update() {
        progressBar.setStory(story);
    }
}
