package sws.murcs.controller.editor;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
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

import java.util.Objects;

/**
 * The editor for a task contained within a story.
 */
public class TaskEditor {

    /**
     * The model of the tasks you are editing.
     */
    private Task task;

    /**
     * The parent node from the FXML.
     */
    private Parent parent;

    /**
     * The StoryEditor that this TaskEditror is contained within.
     */
    private StoryEditor storyEditor;

    /**
     * Whether or not the editor is minimised or not.
     */
    private boolean minimised;

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
     * The state choice boxes.
     */
    @FXML
    private ChoiceBox minimizedStateChoiceBox, maximizedStateChoiceBox;

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
    private TextArea maximizedDescriptionTextArea;

    /**
     * The name text field.
     */
    @FXML
    private TextField minimizedShortNameTextField, maximizedShortNameTextField, minimizedEstimateTextField,
            maximizedEstimateTextField;

    /**
     * Sets up the form with all its event handlers and things.
     */
    @FXML
    private void initialize() {
        minimizedStateChoiceBox.getItems().clear();
        maximizedStateChoiceBox.getItems().clear();
        minimizedStateChoiceBox.getItems().addAll(TaskState.values());
        maximizedStateChoiceBox.getItems().addAll(TaskState.values());

        ChangeListener changeListener = (observable, oldValue, newValue) -> update();

        minimizedShortNameTextField.focusedProperty().addListener(changeListener);
        maximizedShortNameTextField.focusedProperty().addListener(changeListener);
        minimizedEstimateTextField.focusedProperty().addListener(changeListener);
        maximizedEstimateTextField.focusedProperty().addListener(changeListener);
        minimizedStateChoiceBox.focusedProperty().addListener(changeListener);
        maximizedStateChoiceBox.focusedProperty().addListener(changeListener);
        maximizedDescriptionTextArea.focusedProperty().addListener(changeListener);
    }

    /**
     * Called whenever an element of the task is edited.
     * Updates the relevant fields.
     */
    @FXML
    private void update() {

        // Check short name on minimized name field
        String name = minimizedShortNameTextField.getText();
        if (!Objects.equals(name, task.getShortName())) {
            try {
                task.setShortName(name);
                maximizedShortNameTextField.setText(name);
            }
            catch (Exception e) {
                //this.poundFistsOnKeyboard();
            }
        }

        // Check short name on maximized name field
        name = maximizedShortNameTextField.getText();
        if (!Objects.equals(name, task.getShortName())) {
            try {
                task.setShortName(name);
                minimizedShortNameTextField.setText(name);
            }
            catch (Exception e) {
                //this.poundFistsOnKeyboard();
            }
        }

        // Check estimate on minimized estimate field
        if (!minimizedEstimateTextField.getText().isEmpty()) {
            Float estimate = Float.parseFloat(minimizedEstimateTextField.getText());
            if (estimate != task.getEstimate()) {
                task.setEstimate(estimate);
                maximizedEstimateTextField.setText(estimate.toString());
            }
        }

        // Check estimate on maximized estimate field
        if (!maximizedEstimateTextField.getText().isEmpty()) {
            Float estimate = Float.parseFloat(maximizedEstimateTextField.getText());
            if (estimate != task.getEstimate()) {
                task.setEstimate(estimate);
                minimizedEstimateTextField.setText(estimate.toString());
            }
        }

        // Check state on minimized state picker
        TaskState state = (TaskState) minimizedStateChoiceBox.getSelectionModel().getSelectedItem();
        if (state != task.getState()) {
            task.setState(state);
            maximizedStateChoiceBox.getSelectionModel().select(state);
        }

        // Check state on maximized state picker
        state = (TaskState) maximizedStateChoiceBox.getSelectionModel().getSelectedItem();
        if (state != task.getState()) {
            task.setState(state);
            minimizedStateChoiceBox.getSelectionModel().select(state);
        }

        // Check description on maximized description field
        String description = maximizedDescriptionTextArea.getText();
        if (!Objects.equals(description, task.getLongName())) {
            task.setLongName(description);
        }
    }

    /**
     * Sets the task for the editor.
     * @param newTask The new task to be edited
     * @param isCreationBox Whether or not this is a creation box
     * @param view The storyEditor node from the FXML
     * @param containingStoryEditor The storyEditor node from the fxml
     */
    public final void configure(final Task newTask, final boolean isCreationBox, final Parent view, final StoryEditor containingStoryEditor) {
        task = newTask;
        parent = view;
        storyEditor = containingStoryEditor;
        if (isCreationBox) {
            minimised = false;
            maximiseButtonClicked(null);
            createButton.setVisible(true);
            minimiseButton.setDisable(true);
            minimizedStateChoiceBox.getSelectionModel().select(0);
            maximizedStateChoiceBox.getSelectionModel().select(0);
        }
        else {
            minimised = true;
            minimizedShortNameTextField.setText(newTask.getShortName());
            maximizedShortNameTextField.setText(newTask.getShortName());
            minimizedEstimateTextField.setText(String.valueOf(newTask.getEstimate()));
            maximizedEstimateTextField.setText(String.valueOf(newTask.getEstimate()));
            minimizedStateChoiceBox.getSelectionModel().select(newTask.getState());
            maximizedStateChoiceBox.getSelectionModel().select(newTask.getState());
            maximizedDescriptionTextArea.setText(newTask.getLongName());
        }
    }

    /**
     * The function that is called to maximise the task editor.
     * @param event The event that maximises the task editor.
     */
    @FXML
    private void maximiseButtonClicked(final ActionEvent event) {
        editor.setPrefHeight(240.0);
        minimisedHBox.setVisible(false);
        maximisedGrid.setVisible(true);
    }

    /**
     * The function that minimises the task editor.
     * @param event The event that minimises the task editor.
     */
    @FXML
    private void minimiseButtonClicked(final ActionEvent event) {
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
            String name = maximizedShortNameTextField.getText();
            task.setShortName(name);
        }
        catch (CustomException e) {
            acceptable = false;
        }

        String description = maximizedDescriptionTextArea.getText();
        task.setLongName(description);

        try {
            float estimate = Float.parseFloat(maximizedEstimateTextField.getText());
            task.setEstimate(estimate);
        }
        catch (NumberFormatException e) {
            acceptable = false;
        }

        TaskState state = (TaskState) maximizedStateChoiceBox.getSelectionModel().getSelectedItem();
        task.setState(state);

        // TODO: Ensure this if statement actually makes sense
        if (acceptable && state != null) {
            createButton.setVisible(false);
            minimiseButton.setDisable(false);
            storyEditor.addTask(task);
        }
    }

    /**
     * Deletes the task or cancels the creation.
     * @param event The button press event.
     */
    @FXML
    private void deleteButtonClicked(final ActionEvent event) {
        storyEditor.removeTask(task);
        storyEditor.removeTaskEditor(parent);
    }
}
