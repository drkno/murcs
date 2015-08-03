package sws.murcs.controller.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;

import java.util.function.Consumer;

/**
 * The editor for a task contained within a story.
 */
public class TaskEditor {

    /**
     * The model of the tasks you are editing.
     */
    private Task task;

    /**
     * Whether or not this is a creation box.
     */
    private boolean creator;

    /**
     * Parent Story object's addTask method.
     */
    private Consumer<Task> saveTask;

    /**
     * The anchor pane that contains the entire editor.
     */
    @FXML
    private AnchorPane editor;

    /**
     * The minimise editor, delete task and create buttons.
     */
    @FXML
    private Button minimiseButton, createButton;

    /**
     * The state choice box.
     */
    @FXML
    private ChoiceBox stateChoiceBox;

    /**
     * The GridPane which contains the maximised details about the task.
     */
    @FXML
    private GridPane maximisedGrid;

    /**
     * The HBox which contains the minimised details about the task.
     */
    @FXML
    private HBox minimisedHBox;

    /**
     * The description text area.
     */
    @FXML
    private TextArea descriptionArea;

    /**
     * The name text field.
     */
    @FXML
    private TextField shortNameTextField, estimateTextField;

    /**
     * Sets up the form with all its event handlers and things.
     */
    @FXML
    private void initialize() {
        stateChoiceBox.getItems().clear();
        stateChoiceBox.getItems().addAll(TaskState.values());
    }

    /**
     * Sets the task for the editor.
     * @param newTask The new task to be edited
     * @param isCreator Whether or not this is a creation box
     * @param consumer Parent Story object's addTask method
     */
    public final void configure(final Task newTask, final boolean isCreator, final Consumer<Task> consumer) {
        if (newTask != null) {
            task = newTask;
        }
        creator = isCreator;
        saveTask = consumer;
        if (isCreator) {
            maximiseButtonClick(null);
            createButton.setVisible(true);
            minimiseButton.setDisable(true);
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

    /**
     * Called by the create button in the task editor.
     * @param event The button press event.
     */
    @FXML
    private void createButtonClicked(final ActionEvent event) {
        boolean acceptable = true;

        try {
            String name = shortNameTextField.getText();
            task.setShortName(name);
        }
        catch (CustomException e) {
            acceptable = false;
        }

        String description = descriptionArea.getText();
        task.setLongName(description);

        try {
            float estimate = Float.parseFloat(estimateTextField.getText());
            task.setEstimate(estimate);
        }
        catch (NumberFormatException e) {
            acceptable = false;
        }

        TaskState state = (TaskState) stateChoiceBox.getSelectionModel().getSelectedItem();
        task.setState(state);

        if (acceptable && state != null) {
            createButton.setVisible(false);
            minimiseButton.setDisable(false);
            saveTask.accept(task);
        }
    }

    /**
     * Deletes the task or cancels the creation.
     * @param event The button press event.
     */
    @FXML
    private void deleteButtonClicked(final ActionEvent event) {

    }
}
