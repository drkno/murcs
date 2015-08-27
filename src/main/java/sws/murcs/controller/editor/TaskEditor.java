package sws.murcs.controller.editor;

import com.sun.javafx.css.StyleManager;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.controls.popover.ArrowLocation;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.controller.pipes.TaskEditorParent;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Person;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.TaskState;
import sws.murcs.model.helpers.UsageHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The editor for a task contained within a story.
 */
public class TaskEditor implements UndoRedoChangeListener {

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
    private TaskEditorParent editorController;

    /**
     * Whether this is a creation box or not.
     */
    private boolean creationBox;

    /**
     * Whether or not the box is maximised.
     */
    private boolean descriptionVisible;

    /**
     * The popover that is used for adding and removing assignees.
     */
    private PopOver assigneePopOver;

    /**
     * The list of people who can possibly be added as assignees to the task.
     */
    private List<Person> possibleAssignees;

    /**
     * The anchor pane that contains the entire editor.
     */
    @FXML
    private AnchorPane editor;

    /**
     * The grid pane containing the task.
     */
    @FXML private GridPane taskGridPane;

    /**
     * The minimise editor, delete task, create and edit assignees buttons.
     */
    @FXML
    private Button toggleButton, createButton, editAssignedButton;

    /**
     * The state choice boxes.
     */
    @FXML
    private ChoiceBox stateChoiceBox;

    /**
     * The separator between tasks.
     */
    @FXML
    private Separator separator;

    /**
     * The description text area.
     */
    @FXML
    private TextArea descriptionTextArea;

    /**
     * The name and estimate text fields.
     */
    @FXML
    private TextField nameTextField, estimateTextField;

    /**
     * The label that has the assigned people's names on it.
     */
    @FXML
    private Label assigneesLabel;

    /**
     * The height of the window during creation.
     */
    private static final double CREATION_HEIGHT = 270.0;

    /**
     * The height of the window when it is collapsed.
     */
    private static final double COLLAPSED_HEIGHT = 80.0;

    /**
     * The height of the window when it is expanded.
     */
    private static final double EXPANDED_HEIGHT = 240.0;

    /**
     * Sets up the form with all its event handlers and things.
     */
    @FXML
    private void initialize() {
        ChangeListener changeListener = (observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue) {
                saveChanges();
            }
        };

        stateChoiceBox.getItems().clear();
        stateChoiceBox.getItems().addAll(TaskState.values());
        stateChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue) {
                updateStateChoiceBox((TaskState) newValue);
            }
        });

        nameTextField.focusedProperty().addListener(changeListener);
        estimateTextField.focusedProperty().addListener(changeListener);
        stateChoiceBox.focusedProperty().addListener(changeListener);
        descriptionTextArea.focusedProperty().addListener(changeListener);
    }

    /**
     * Sets the task for the editor.
     * @param newTask The new task to be edited
     * @param isCreationBox Whether or not this is a creation box
     * @param view The editorController node from the FXML
     * @param containingStoryEditor The editorController node from the fxml
     */
    public final void configure(final Task newTask,
                                final boolean isCreationBox,
                                final Parent view,
                                final TaskEditorParent containingStoryEditor) {
        task = newTask;
        parent = view;
        editorController = containingStoryEditor;
        descriptionVisible = false;
        creationBox = isCreationBox;
        if (isCreationBox) {
            toggleButtonClicked(null);
            editor.setPrefHeight(CREATION_HEIGHT);
            createButton.setVisible(true);
            toggleButton.setVisible(false);
            separator.setVisible(false);
            stateChoiceBox.getSelectionModel().select(0);
        }
        else {
            nameTextField.setText(newTask.getName());
            estimateTextField.setText(String.valueOf(newTask.getEstimate()));
            stateChoiceBox.getSelectionModel().select(newTask.getState());
            descriptionTextArea.setText(newTask.getDescription());
        }
        updateAssigneesLabel();
        updateAddAssigneesButton();
    }

    /**
     * Updates whether or not you should be able to set assignees for the task, based on if there are any possible
     * people that you can have as assignees. These are people who are in any of the teams that are assigned to any of
     * the sprints the story is in.
     */
    private void updateAddAssigneesButton() {
        if (getStory() != null) {
            possibleAssignees = new ArrayList<>();
            List<Sprint> sprints = UsageHelper.findUsages(getStory())
                    .stream()
                    .filter(model -> model instanceof Sprint).map(model -> (Sprint) model)
                    .collect(Collectors.toList());
            if (sprints.size() > 0) {
                sprints.forEach(sprint -> possibleAssignees.addAll(sprint.getTeam().getMembers()));
                return;
            }
        }
        editAssignedButton.setDisable(true);
        assigneesLabel.setText("To add assignees this story must be in a sprint with a team assigned to it");
    }

    /**
     * Updates the assignees label to display all the current assigness as a list.
     */
    private void updateAssigneesLabel() {
        if (task.getAssignees().size() > 0) {
            assigneesLabel.setText(task.getAssigneesAsString());
        }
        else {
            assigneesLabel.setText("Not assigned!");
        }
    }

    /**
     * Called whenever an element of the task is edited and saves the changes.
     */
    @FXML
    private void saveChanges() {
        editorController.clearErrors("tasks");
        boolean changes = false;
        // Check name
        String name = nameTextField.getText();
        if (name != null && !nameExists(name) && !name.isEmpty()) {
            if (!Objects.equals(name, task.getName())) {
                task.setName(name);
                changes = true;
            }
        }
        else {
            nameTextField.setText(task.getName());
            editorController.addFormError("tasks", nameTextField,
                    "Task names must be unique and have at least one character!");
        }

        // Check estimate
        try {
            Float estimate = Float.parseFloat(estimateTextField.getText());
            if (estimate != task.getEstimate()) {
                task.setEstimate(estimate);
                changes = true;
            }
        }
        catch (NumberFormatException e) {
            editorController.addFormError("tasks", estimateTextField, "Estimate must be a number!");
        }

        // Check state
        TaskState state = (TaskState) stateChoiceBox.getSelectionModel().getSelectedItem();
        if (state != task.getState()) {
            task.setState(state);
            changes = true;
        }

        // Check description on maximized description field
        String description = descriptionTextArea.getText();
        if (!Objects.equals(description, task.getDescription())) {
            task.setDescription(description);
        }

        if (changes) {
            editorController.changesMade(this);
        }
    }

    /**
     * Updates the style that should be applied to the task state choice box depending on the state that it is
     * in.
     * @param state the state that it is in.
     */
    private void updateStateChoiceBox(final TaskState state) {
        if (state == TaskState.NotStarted) {
            stateChoiceBox.getStyleClass().removeAll("in-progress", "done");
            stateChoiceBox.getStyleClass().add("not-started");
        }
        else if (state == TaskState.InProgress) {
            stateChoiceBox.getStyleClass().removeAll("not-started", "done");
            stateChoiceBox.getStyleClass().add("in-progress");
        }
        else if (state == TaskState.Done) {
            stateChoiceBox.getStyleClass().removeAll("not-started", "in-progress");
            stateChoiceBox.getStyleClass().add("done");
        }
    }

    /**
     * Checks whether a task with a specified name already exists.
     * @param name The name to check for
     * @return Whether a task already exists with that name
     */
    private boolean nameExists(final String name) {
        return editorController.getTasks()
                .stream()
                //First filters it down to just the tasks that belong to the same story as the current task.
                .filter(task1 -> getStory().getTasks().contains(task1))
                //Now remove the current task from this list using equals (as this checks for complete equality,
                //not just the name).
                .filter(task2 -> !task2.equals(task))
                //Now see if there are any with the same name.
                .anyMatch(t -> t.getName().equals(name));
    }

    /**
     * Called by the create button in the task editor.
     * @param event The button press event.
     */
    @FXML
    private void createButtonClicked(final ActionEvent event) {
        editorController.clearErrors("tasks");
        boolean acceptable = true;

        String name = nameTextField.getText();
        if (!nameExists(name) && name != null && !name.isEmpty()) {
            task.setName(name);
        }
        else {
            acceptable = false;
            editorController.addFormError("tasks", nameTextField,
                    "Task names must be unique and have at least one character!");
        }

        try {
            float estimate = Float.parseFloat(estimateTextField.getText());
            task.setEstimate(estimate);
        }
        catch (NumberFormatException e) {
            acceptable = false;
            editorController.addFormError("tasks", estimateTextField, "Estimate must be a number!");
        }

        if (acceptable) {
            creationBox = false;
            createButton.setVisible(false);
            toggleButton.setVisible(true);
            editorController.addTask(task);
            toggleButtonClicked(null);
            separator.setVisible(true);
        }
    }

    /**
     * The function that is called toggle the state of the editor.
     * @param event The event that maximises/minimises the task editor
     */
    @FXML
    private void toggleButtonClicked(final ActionEvent event) {
        if (descriptionVisible) {
            descriptionTextArea.setVisible(false);
            editor.setPrefHeight(COLLAPSED_HEIGHT);
            toggleButton.setText("+");
            descriptionVisible = false;
        }
        else {
            descriptionTextArea.setVisible(true);
            editor.setPrefHeight(EXPANDED_HEIGHT);
            toggleButton.setText("-");
            descriptionVisible = true;
        }
    }

    /**
     * Deletes the task or cancels the creation.
     * @param event The button press event.
     */
    @FXML
    private void deleteButtonClicked(final ActionEvent event) {
        if (creationBox) {
            editorController.removeTask(task);
            editorController.removeTaskEditor(parent);
            return;
        }

        GenericPopup popup = new GenericPopup();
        popup.setTitleText("Really?");
        popup.setMessageText("Are you sure you wish to remove this task?");
        popup.addYesNoButtons(() -> {
            editorController.removeTask(task);
            editorController.removeTaskEditor(parent);
            popup.close();
        });
        popup.show();
    }

    /**
     * The opens a new popover window to add and remove assigned people.
     * @param event An event, probably clicking.
     */
    @FXML
    private void editAssignedButtonClicked(final ActionEvent event) {
        if (assigneePopOver == null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(TaskEditor.class.getResource("/sws/murcs/AssigneesPopOver.fxml"));

            try {
                Parent parent = loader.load();
                assigneePopOver = new PopOver(parent);
                AssigneeController controller = loader.getController();
                controller.setUp(this, possibleAssignees);
                assigneePopOver.hideOnEscapeProperty().setValue(true);
            }
            catch (IOException e) {
                ErrorReporter.get().reportError(e, "Could not create a assignee popover");
            }
        }

        assigneePopOver.arrowLocationProperty().setValue(ArrowLocation.RIGHT_CENTER);
        assigneePopOver.show(editAssignedButton);
    }

    /**
     * Adss an assignee to the task and updates the assignee label to display it.
     * @param assignee the assignee to add to the task.
     */
    public void addAssignee(final Person assignee) {
        task.addAssignee(assignee);
        updateAssigneesLabel();
        editorController.changesMade(this);
    }

    /**
     * Removes the given person from the assignees of the task.
     * @param assignee the assignee to remove from the task.
     */
    public void removeAssignee(final Person assignee) {
        task.removeAssignee(assignee);
        updateAssigneesLabel();
        editorController.changesMade(this);
    }

    /**
     * Gets the task that is associated with this task editor.
     * @return the task associated with this task editor.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Gets the story that the task associated with this editor is a part of.
     * @return The story that this task is a part of.
     */
    public Story getStory() {
        return editorController.getAssociatedStory(task);
    }

    @Override
    public void undoRedoNotification(final ChangeState param) {
        if (param == ChangeState.Remake || param == ChangeState.Revert) {
            synchronized (StyleManager.getInstance()) {
                configure(task, creationBox, parent, editorController);
            }
        }
    }

    /**
     * Gets the node that is the parent of this editor.
     * @return the parent of the editor.
     */
    public Node getParent() {
        return parent;
    }
}
