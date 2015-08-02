package sws.murcs.controller.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import sws.murcs.model.Task;

/**
 * The edtitor for a task contained within a story.
 */
public class TaskEditor {

    /**
     * The model of the taks you are editing.
     */
    private Task task;

    /**
     * The HBox which contains the minimised details about the task.
     */
    @FXML
    private HBox minimisedHBox;

    /**
     * The GridPane which contains the maximised details about the task.
     */
    @FXML
    private GridPane maximisedGrid;

    /**
     * The anchor pane that contains the entire editor.
     */
    @FXML
    private AnchorPane editor;

    /**
     * Gets the task currently associated with the editor.
     * @return The currently edited task.
     */
    public final Task getTask() {
        return task;
    }

    /**
     * Sets the task for the editor.
     * @param newTask The new task to be edited.
     */
    public final void setTask(final Task newTask) {
        if (newTask != null) {
            task = newTask;
        }
    }

    /**
     * The function that is called to maximise the task editor.
     * @param event The event that maximises the task editor.
     */
    @FXML
    private void maximiseButtonClick(final ActionEvent event) {
        editor.setPrefHeight(240.0);
        minimisedHBox.setVisible(false);
        maximisedGrid.setVisible(true);
    }

    /**
     * The function that minimises the task editor.
     * @param event The event that minimises the task editor.
     */
    @FXML
    private void minimiseButtonClick(final ActionEvent event) {
        editor.setPrefHeight(50.0);
        minimisedHBox.setVisible(true);
        maximisedGrid.setVisible(false);
    }
}
