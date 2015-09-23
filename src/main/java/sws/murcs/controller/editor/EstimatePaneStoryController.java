package sws.murcs.controller.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Backlog;
import sws.murcs.model.Story;

import java.util.List;

/**
 * Created by wooll on 20/09/2015.
 */
public class EstimatePaneStoryController {

    @FXML
    private Button removeFromWorkSpaceButton;

    @FXML
    private Label storyLabel;

    @FXML
    private HBox storyContainer;

    private Story story;
    private Backlog backlog;
    private EstimatePane parent;

    public void dispose() {
        story = null;
        backlog = null;
    }

    @FXML
    private void initialize() {
        storyContainer.getStyleClass().add("workspace-story");
    }

    public void setStory(final Story story) {
        this.story = story;
    }

    public void loadStory() {
        storyLabel.setText(story.getShortName());
    }

    public void setParent(final EstimatePane estimatePane) {
        parent = estimatePane;
    }

    public void removeStoryFromWorkspace(final ActionEvent actionEvent) {
        backlog.removeStoryFromWorkspace(story);
        parent.loadObject();
    }

    public void setBacklog(final Backlog backlog) {
        this.backlog = backlog;
    }
}
