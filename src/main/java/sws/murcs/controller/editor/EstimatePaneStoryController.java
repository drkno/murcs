package sws.murcs.controller.editor;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import sws.murcs.model.Story;

import java.util.List;

/**
 * Created by wooll on 20/09/2015.
 */
public class EstimatePaneStoryController {
    public Button removeFromWorkSpaceButton;
    public Label storyLabel;
    public BorderPane storyBoarderPane;
    private EstimatePane estimatePane;
    private List<Story> stories;
    private Story story;

    public void dispose() {

    }



    public void setEstimatePane(EstimatePane estimatePane) {
        this.estimatePane = estimatePane;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public void loadStory() {
        storyLabel.setText(story.getShortName());
    }

    public void removeStoryFromWorkspace(ActionEvent actionEvent) {

    }
}
