package sws.murcs.controller.editor;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import sws.murcs.controller.controls.ModelProgressBar;
import sws.murcs.model.EffortEntry;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;

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
    private Task draggingTask;

    /**
     * The story of the currently dragging task.
     */
    private Story draggingStory;

    @FXML
    public final void initialize() {

        mainPane.getStyleClass().add("root");
        progressBar = new ModelProgressBar(true);
        progressBarContainer.getChildren().addAll(progressBar);
    }

    protected void loadStory() {
        progressBar.setStory(story);

        storyBaseInfoVBox.setVisible(true);
        storyExtraInfoVBox.setVisible(false);
        toDoBaseInfoVBox.setVisible(true);
        toDoMoreInfoVBox.setVisible(false);
        inProgressBaseInfoVBox.setVisible(true);
        inProgressMoreInfoVBox.setVisible(false);
        doneBaseInfoVBox.setVisible(true);
        doneMoreInfoVBox.setVisible(false);

        storyHyperLink.setText(story.getShortName());
        storyHyperLink.setWrapText(true);
        storyHyperLink.setOnAction(event -> sprintContainer.getNavigationManager().navigateTo(story));

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
}
