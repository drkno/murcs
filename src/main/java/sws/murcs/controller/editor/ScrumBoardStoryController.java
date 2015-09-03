package sws.murcs.controller.editor;

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
import sws.murcs.model.Story;
import sws.murcs.model.Task;

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
    }

    protected void loadStory() {
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
