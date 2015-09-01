package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;

import java.util.Objects;

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
    private static Task draggingTask;

    /**
     * The story of the currently dragging task.
     */
    private static Story draggingStory;

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

        // Add the name of the story to the left column
        col1.setHalignment(HPos.CENTER);
        Label storyNameLabel = new Label(story.getShortName());
        GridPane.setColumnIndex(storyNameLabel, 0);
        storyPane.getChildren().add(storyNameLabel);

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
            addDragExitedHandler(places[i], story);
            addDragDroppedHandler(places[i], story, TaskState.values()[i]);
        }
        for (Task task : story.getTasks()) {
            insertTask(places, task, story);
        }

        // Add the grid pane to the story VBox
        vBox.getChildren().add(storyPane);
    }

    /**
     * Creates a GUI element for the task and adds it to the list.
     * @param stateBoxes The VBoxes that tasks can be added to
     * @param task The task to add to the list
     * @param story The story from which that task came
     */
    private void insertTask(final VBox[] stateBoxes, final Task task, final Story story) {
        Label label = new Label(task.getName());
        addDragDetectedHandler(label, task, story);
        addDragDoneHandler(label, stateBoxes[task.getState().ordinal()]);
        stateBoxes[task.getState().ordinal()].getChildren().add(label);
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
    private void addDragOverHandler(final Node target, final Story story) {
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
    private void addDragEnteredHandler(final Node target, final Story story) {
        target.setOnDragEntered(event -> {
            if (story == draggingStory && event.getGestureSource() != target && event.getDragboard().hasString()) {
                // TODO: Properly style the target element
                //target.getStyleClass().add("-fx-border-color: red; -fx-border-width: 7px;");
                //target.applyCss();
            }
            event.consume();
        });
    }

    /**
     * Adds a drag exited handler to the node.
     * @param target The node to add the handler to
     * @param story The story from which this node is a part
     */
    private void addDragExitedHandler(final Node target, final Story story) {
        target.setOnDragExited(event -> {
            // TODO: Return style to normal
            event.consume();
        });
    }

    /**
     * Adds a drag dropped handler to the node.
     * @param target The node to add the handler to
     * @param story The story from which this node is a part
     * @param newState The new state to set the task to
     */
    private void addDragDroppedHandler(final VBox target, final Story story, final TaskState newState) {
        target.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasString()) {
                String taskName = dragboard.getString();
                for (Task task : story.getTasks()) {
                    if (Objects.equals(task.getName(), taskName)) {
                        task.setState(newState);
                        Label label = new Label(task.getName());
                        addDragDetectedHandler(label, task, story);
                        addDragDoneHandler(label, target);
                        target.getChildren().add(label);
                        success = true;
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Adds a drag done handler to the node.
     * @param source The node to add the handler to
     * @param initialPosition The position on the scrum board where the task originated
     */
    private void addDragDoneHandler(final Node source, final VBox initialPosition) {
        source.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                initialPosition.getChildren().remove(source);
            }
            event.consume();
        });
    }

    @Override
    protected void saveChangesAndErrors() {
    }
}
