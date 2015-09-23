package sws.murcs.controller.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import sws.murcs.model.Backlog;
import sws.murcs.model.Story;

/**
 * Controller for the estimate pane story.
 */
public class EstimatePaneStoryController {

    /**
     * Button for removing the story from the workspace.
     */
    @FXML
    private Button removeFromWorkSpaceButton;

    /**
     * Name of the story.
     */
    @FXML
    private Label storyLabel;

    /**
     * The outer container for the story.
     */
    @FXML
    private HBox storyContainer;

    /**
     * The story the pane represents.
     */
    private Story story;

    /**
     * The backlog the story is from.
     */
    private Backlog backlog;

    /**
     * The parent of the estimate story pane.
     */
    private EstimatePane parent;

    /**
     * Disposes of the estimate story pane.
     */
    public void dispose() {
        story = null;
        backlog = null;
        parent = null;
    }

    /**
     * Initializes the story pane.
     */
    @FXML
    private void initialize() {
        storyContainer.getStyleClass().add("workspace-story");
    }

    /**
     * Sets the story.
     * @param story The story.
     */
    public void setStory(final Story story) {
        this.story = story;
    }

    /**
     * Loads the estimate story pane.
     */
    public void loadStory() {
        storyLabel.setText(story.getShortName());
    }

    /**
     * Sets the parent.
     * @param estimatePane The parent.
     */
    public void setParent(final EstimatePane estimatePane) {
        parent = estimatePane;
    }

    /**
     * Function to remove the story from a workspace.
     * @param actionEvent The event (ignored)
     */
    public void removeStoryFromWorkspace(final ActionEvent actionEvent) {
        backlog.removeStoryFromWorkspace(story);
        parent.loadObject();
    }

    /**
     * Sets the backlog.
     * @param backlog The backlog.
     */
    public void setBacklog(final Backlog backlog) {
        this.backlog = backlog;
    }
}
