package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.*;
import sws.murcs.model.Story.StoryState;

/**
 * Scrum Board controller.
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
     * The task currently being dragged.
     */
    private Task draggingTask;

    /**
     * The story of the currently dragging task.
     */
    private Story draggingStory;

    @Override
    protected void initialize() {
        mainView.getStyleClass().add("root");
    }

    @Override
    public void loadObject() {
        storiesVBox.getChildren().clear();
        for (Story story : getModel().getStories()) {
            insertStory(storiesVBox, story);
        }
    }

    /**
     * Adds a story to the list.
     * @param vBox The VBox to add the story to
     * @param story The story to add to the VBox
     */
    private void insertStory(final VBox vBox, final Story story) {

        // Create the grid pane and define its columns and properties
        GridPane storyPane = new GridPane();
        vBox.getChildren().add(storyPane);
        storyPane.setGridLinesVisible(true);
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();
        ColumnConstraints col4 = new ColumnConstraints();
        col1.setPercentWidth(25);
        col2.setPercentWidth(25);
        col3.setPercentWidth(25);
        col4.setPercentWidth(25);
        storyPane.getColumnConstraints().addAll(col1, col2, col3, col4);

        // Add the nodes of the story column
        col1.setHalignment(HPos.CENTER);
        VBox detailsVBox = new VBox();
        GridPane.setColumnIndex(detailsVBox, 0);
        detailsVBox.setFillWidth(true);
        detailsVBox.setAlignment(Pos.CENTER);
        detailsVBox.setSpacing(5);
        detailsVBox.setPadding(new Insets(5));
        Hyperlink storyNameLink = new Hyperlink(story.getShortName());
        storyNameLink.setOnAction(event -> getNavigationManager().navigateTo(story));
        Label doneLabel = new Label("Done:");
        CheckBox doneCheckBox = new CheckBox();
        updateDoneCheckBox(doneCheckBox, story);
        doneCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            story.setStoryState(newValue ? StoryState.Done : StoryState.Ready);
        });
        HBox doneBox = new HBox(doneLabel, doneCheckBox);
        doneBox.setSpacing(10);
        doneBox.setAlignment(Pos.CENTER);
        detailsVBox.getChildren().addAll(storyNameLink, doneBox);
        storyPane.getChildren().add(detailsVBox);

        // Create the VBoxes that will populate the grid pane
        VBox notStartedVBox = new VBox();
        VBox inProgressVBox = new VBox();
        VBox doneVBox = new VBox();
        notStartedVBox.setPadding(new Insets(5));
        inProgressVBox.setPadding(new Insets(5));
        doneVBox.setPadding(new Insets(5));
        VBox[] places = new VBox[] {notStartedVBox, inProgressVBox, doneVBox};
        storyPane.getChildren().addAll(places);
        for (int i = 0; i < places.length; i++) {
            GridPane.setColumnIndex(places[i], i + 1);
            addDragOverHandler(places[i], story);
            addDragEnteredHandler(places[i], story);
            addDragExitedHandler(places[i]);
            addDragDroppedHandler(places[i], TaskState.values()[i], story, doneCheckBox);
        }
        for (Task task : story.getTasks()) {
            insertTask(places, task, story);
        }
    }

    /**
     * Creates a GUI element for the task and adds it to the list.
     * @param stateBoxes The VBoxes that tasks can be added to
     * @param task The task to add to the list
     * @param story The story from which that task came
     */
    private void insertTask(final Pane[] stateBoxes, final Task task, final Story story) {
        Label label = new Label(task.getName());
        stateBoxes[task.getState().ordinal()].getChildren().add(label);
        addDragDetectedHandler(label, task, story);
        addDragDoneHandler(label, task, stateBoxes[task.getState().ordinal()], stateBoxes);
        addDoubleClickHandler(label, story);
    }

    /**
     * Updates the disabled state of the doneCheckBox.
     * @param doneCheckBox The check box to update
     * @param story The story relating to this checkbox
     */
    private void updateDoneCheckBox(final CheckBox doneCheckBox, final Story story) {
        for (Task task : story.getTasks()) {
            if (task.getState() != TaskState.Done) {
                doneCheckBox.setSelected(false);
                doneCheckBox.setDisable(true);
                story.setStoryState(StoryState.Ready);
                return;
            }
        }
        doneCheckBox.setDisable(false);
        if (story.getStoryState() == StoryState.Done) {
            doneCheckBox.setSelected(true);
        }
    }

    /**
     * Adds a double click handler to a node that loads a TaskEditor into a new window.
     * @param node The node to apply the handler to
     * @param story The story to load
     */
    private void addDoubleClickHandler(final Node node, final Story story) {
        node.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                getNavigationManager().navigateToNewTab(story);
            }
        });
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
     * @param story The story from which this node is a part
     */
    private void addDragOverHandler(final Pane target, final Story story) {
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
     * @param story The story from which this node is a part
     */
    private void addDragEnteredHandler(final Pane target, final Story story) {
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
     * @param story The story from which this node is a part
     * @param doneCheckBox The check box that sets the state of this story
     */
    private void addDragDroppedHandler(final Pane target, final TaskState newState, final Story story, final CheckBox doneCheckBox) {
        target.setOnDragDropped(event -> {
            draggingTask.setState(newState);
            updateDoneCheckBox(doneCheckBox, story);
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

    @Override
    protected void saveChangesAndErrors() {
    }

    @Override
    public void dispose() {
    }
}
