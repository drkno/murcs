package sws.murcs.controller.editor;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.controller.controls.ModelProgressBar;
import sws.murcs.model.EffortEntry;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;

import java.io.IOException;

/**
 * 3/09/2015
 *
 * @author Dion
 */
public class ScrumBoardStoryController {
    public GridPane StoryMainGridPane;
    public VBox StoryOuterVBox;
    public VBox storyStaticControlsVBox;
    public GridPane storyStaticControlsGridPane;
    public Button storyCollapseExpandButton;
    public ImageView collapseExpandStoryButton;
    public Hyperlink storyHyperLink;
    public Slider storyStateSlider;
    public VBox storyBaseInfoVBox;
    public VBox storyExtraInfoVBox;
    public VBox toDoOuterVBox;
    public VBox toDoBaseInfoVBox;
    public Label toDoBaseInfoLabel;
    public VBox toDoMoreInfoVBox;
    public VBox inProgressOuterVBox;
    public VBox inProgressBaseInfoVBox;
    public Label inProgressBaseInfoLabel;
    public VBox inProgressMoreInfoVBox;
    public VBox doneOuterVBox;
    public VBox doneBaseInfoVBox;
    public Label doneBaseInfoLabel;
    public VBox doneMoreInfoVBox;
    public AnchorPane mainPane;
    public Label storyStateLabel;

    /**
     * A container that shows where the progress bar will go on the screen.
     */
    public VBox progressBarContainer;

    /**
     * A progress bar that indicates sprint progress.
     */
    private ModelProgressBar progressBar;

    private Story story;

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

    @FXML
    public final void initialize() {

        mainPane.getStyleClass().add("root");
        progressBar = new ModelProgressBar(true);
        progressBarContainer.getChildren().addAll(progressBar);
    }

    protected void loadStory() {
        updateToggleStatus();
        progressBar.setStory(story);

        storyBaseInfoVBox.setVisible(true);
        storyExtraInfoVBox.setVisible(false);
        toDoBaseInfoVBox.setVisible(true);
        toDoMoreInfoVBox.setVisible(false);
        inProgressBaseInfoVBox.setVisible(true);
        inProgressMoreInfoVBox.setVisible(false);
        doneBaseInfoVBox.setVisible(true);
        doneMoreInfoVBox.setVisible(false);
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
                controller.configure(task, story);
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
                addDragDoneHandler(root, task, initialVBox, positions);
            } catch (IOException e) {
                ErrorReporter.get().reportError(e, "Failed to load task in scrumBoard");
            }
        }

        setupTaskMinInfo();
    }

    /**
     *
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private String calculateTime(float summary) {
        int dps = 0;
        String units = "minutes";

        //If we have more than 60 minutes we should measure in hours.
        if (summary >= 60) {
            summary /= 60;
            units = "hours";
            dps = 1;
        }

        //To round to a certain number of dps, we multipy by 10 to the power of the dps we want
        // 7.8934 to 2 dp: 7.8934 * 10 ^ 2 = 789.34, Round to 0 dps = 789, divide by 10 ^ 2 = 7.89
        float pow = (float) Math.pow(10, dps);
        summary = Math.round(summary * pow) / pow;

        if (dps == 0) {
            return (int) summary + " " + units;
        }
        else {
            return summary + " " + units;
        }
    }

    private void setupTaskMinInfo() {
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

        if (tasksToDo.size() > 0) {
            DoubleSummaryStatistics timeLeft = tasksToDo
                    .stream()
                    .collect(Collectors.summarizingDouble(Task::getCurrentEstimate));
            String[] timeLeftString = calculateTime((float) timeLeft.getSum()).split(" ");
            toDoBaseInfoLabel.setText(tasksToDo.size() + " tasks are ready to be started\n"
            + "with " + timeLeftString[0] + " " + timeLeftString[1] + " estimate left");
        }
        else {
            toDoBaseInfoLabel.setText("No tasks left to start :)");
        }

        if (tasksInProgress.size() > 0) {
            DoubleSummaryStatistics timeLeft = tasksInProgress
                    .stream()
                    .collect(Collectors.summarizingDouble(Task::getCurrentEstimate));
            DoubleSummaryStatistics timeSpent = tasksInProgress
                    .stream()
                    .collect(Collectors.summarizingDouble(t -> t.getEffort()
                            .stream()
                            .collect(Collectors.summarizingDouble(EffortEntry::getEffort))
                            .getSum()));
            String[] timeLeftString = calculateTime((float) timeLeft.getSum()).split(" ");
            String[] timeSpentString = calculateTime((float) timeSpent.getSum()).split(" ");
            inProgressBaseInfoLabel.setText(tasksInProgress.size() + " tasks are in progress "
                    + "with an estimated " + timeLeftString[0] + " " + timeLeftString[1] + " remaining "
                    + "and " + timeSpentString[0] + " " + timeSpentString[1] + " spent");
        }
        else {
            inProgressBaseInfoLabel.setText("No tasks are in progress");
        }

        if (tasksDone.size() > 0) {
            DoubleSummaryStatistics timeSpent = tasksDone
                    .stream()
                    .collect(Collectors.summarizingDouble(t -> t.getEffort()
                            .stream()
                            .collect(Collectors.summarizingDouble(EffortEntry::getEffort))
                            .getSum()));
            String[] timeSpentString = calculateTime((float) timeSpent.getSum()).split(" ");
            doneBaseInfoLabel.setText(tasksDone.size() + " tasks are done "
                    + "with " + timeSpentString[0] + " " + timeSpentString[1] + " spent");
        }
        else {
            doneBaseInfoLabel.setText("No tasks are done");
        }

    }

    public void setStory(Story pStory) {
        story = pStory;
    }

    public void setSprintContainer(SprintContainer pSprintContainer) {
        sprintContainer = pSprintContainer;
    }

    public void toggleInfoView(ActionEvent actionEvent) {
        if (infoViewStateMore) {
            hideLessInfo();
            showMoreInfo();
            infoViewStateMore = false;
        }
        else {
            hideMoreInfo();
            ShowLessInfo();
            infoViewStateMore = true;
        }
    }

    private void ShowLessInfo() {
        storyBaseInfoVBox.setVisible(true);
        toDoBaseInfoVBox.setVisible(true);
        inProgressBaseInfoVBox.setVisible(true);
        doneBaseInfoVBox.setVisible(true);
    }

    private void hideMoreInfo() {
        storyExtraInfoVBox.setVisible(false);
        toDoMoreInfoVBox.setVisible(false);
        inProgressMoreInfoVBox.setVisible(false);
        doneMoreInfoVBox.setVisible(false);
    }

    private void showMoreInfo() {
        storyExtraInfoVBox.setVisible(true);
        toDoMoreInfoVBox.setVisible(true);
        inProgressMoreInfoVBox.setVisible(true);
        doneMoreInfoVBox.setVisible(true);
    }

    private void hideLessInfo() {
        storyBaseInfoVBox.setVisible(false);
        toDoBaseInfoVBox.setVisible(false);
        inProgressBaseInfoVBox.setVisible(false);
        doneBaseInfoVBox.setVisible(false);
    }

    public void setup(Story pStory, SprintContainer pSprintContainer) {
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
                story.setStoryState(Story.StoryState.Ready);
                return;
            }
        }
        storyStateSlider.setDisable(false);
        if (story.getStoryState() == Story.StoryState.Done) {
            storyStateSlider.setValue(1);
        }
    }

    /**
     * Adds a drag detected handler to the node.
     * @param source The node to add the handler to
     * @param task The task that the dragged node represents
     * @param story The story that the task is from
     */
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
            if (story == draggingStory && event.getGestureSource() != target && event.getDragboard().hasString()) {
                // TODO: Style target with drop prompt
                //target.getStyleClass().add("-fx-border-color: red; -fx-border-width: 7px;");
                //target.applyCss();
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
     * @param initialPane The VBox on the scrum board where the task originated
     * @param places Array of Panes where the node can be dropped
     */
    private void addDragDoneHandler(final Node source, final Task task, final Pane initialPane, final Pane[] places) {
        source.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                initialPane.getChildren().remove(source);
                places[task.getState().ordinal()].getChildren().add(source);
            }
            event.consume();
        });
    }
}
