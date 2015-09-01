package sws.murcs.controller.editor;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;

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
        GridPane storyPane = new GridPane();
        storyPane.setGridLinesVisible(true);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25);
        storyPane.getColumnConstraints().addAll(col1, col2, col3, col4);
        VBox notStartedVBox = new VBox();
        notStartedVBox.setPadding(new Insets(5));
        VBox inProgressVBox = new VBox();
        inProgressVBox.setPadding(new Insets(5));
        VBox doneVBox = new VBox();
        doneVBox.setPadding(new Insets(5));
        VBox[] stateBoxes = new VBox[3];
        stateBoxes[0] = notStartedVBox;
        stateBoxes[1] = inProgressVBox;
        stateBoxes[2] = doneVBox;
        GridPane.setColumnIndex(notStartedVBox, 1);
        GridPane.setColumnIndex(inProgressVBox, 2);
        GridPane.setColumnIndex(doneVBox, 3);
        storyPane.getChildren().addAll(notStartedVBox, inProgressVBox, doneVBox);
        for (Task task : story.getTasks()) {
            insertTask(stateBoxes, task);
        }
        vBox.getChildren().add(storyPane);
    }

    /**
     * Creates a GUI element for the task and adds it to the list.
     * @param stateBoxes The VBoxes that tasks can be added to
     * @param task The task to add to the list
     */
    private void insertTask(final VBox[] stateBoxes, final Task task) {
        Label label = new Label(task.getName());
        addDragDetectedHandler(label);
        stateBoxes[task.getState().ordinal()].getChildren().add(label);
    }

    /**
     * Adds a drag detected handler to the node.
     * @param node The node to add the handler to
     */
    private void addDragDetectedHandler(final Node node) {
        node.setOnDragDetected(event -> {
            Dragboard dragBoard = node.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString("foo");
            dragBoard.setContent(content);

            WritableImage image = node.snapshot(new SnapshotParameters(), null);

            //TODO make a cooler screen shot
            dragBoard.setDragView(image, image.getWidth() * 0.5, 0);
            event.consume();
        });
    }

    /**
     * Adds a drag over handler to the node.
     * @param node The node to add the handler to
     */
    private void addDragOverHandler(final Node node) {
        node.setOnDragOver(event -> {
            if (event.getGestureSource() != node && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
    }

    /**
     * Adds a drag entered handler to the node.
     * @param node The node to add the handler to
     */
    private void addDragEnteredHandler(final Node node) {

    }

    /**
     * Adds a drag exited handler to the node.
     * @param node The node to add the handler to
     */
    private void addDragExitedHandler(final Node node) {

    }

    /**
     * Adds a drag dropped handler to the node.
     * @param node The node to add the handler to
     */
    private void addDragDroppedHandler(final Node node) {

    }

    /**
     * Adds a drag done handler to the node.
     * @param node The node to add the handler to
     */
    private void addDragDoneHandler(final Node node) {

    }

    @Override
    protected void saveChangesAndErrors() {

    }
}
