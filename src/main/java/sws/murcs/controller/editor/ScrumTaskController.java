package sws.murcs.controller.editor;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sws.murcs.controller.controls.popover.ArrowLocation;
import sws.murcs.controller.controls.popover.PopOver;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.magic.tracking.listener.UndoRedoChangeListener;
import sws.murcs.model.Person;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.helpers.UsageHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for a task within a scrum board.
 */
public class ScrumTaskController implements UndoRedoChangeListener {

    /**
     * The task that this GUI element represents.
     */
    private Task task;

    /**
     * The story that this task is a part of.
     */
    private Story story;

    /**
     * The VBox that this form is contained within.
     */
    private VBox currentColumn;

    /**
     * The parent of this form.
     */
    private ScrumBoardStoryController parent;

    /**
     * Assignees popover window for adding and removing assignees.
     */
    private PopOver taskPopover;

    /**
     * Effort spent popover window for adding, removing and editing effort spent.
     */
    private PopOver effortPopOver;

    /**
     * The label that displays the name of this task.
     */
    @FXML
    private Label nameLabel;

    /**
     * The textfield for editing this tasks estimate.
     */
    @FXML
    private TextField estimateTextField;

    /**
     * The buttons for editing who is assigned to this task and how much effort people have logged.
     */
    @FXML
    private Button editAssignedButton, logEffortButton;

    /**
     * The effort popover fxml.
     */
    private FXMLLoader effortPopOverFxml = new FXMLLoader(TaskEditor.class
            .getResource("/sws/murcs/EffortPopOver.fxml"));

    /**
     * The assignees popover fxml.
     */
    private FXMLLoader assigneesPopOverFxml = new FXMLLoader(TaskEditor.class
            .getResource("/sws/murcs/AssigneesPopOver.fxml"));

    /**
     * Sets up the form.
     */
    @FXML
    private void initialize() {
        ChangeListener changeListener = (observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue && task != null) {
                saveChanges();
            }
        };

        estimateTextField.focusedProperty().addListener(changeListener);
    }

    /**
     * Called when changes to a task field is made.
     */
    private void saveChanges() {
        try {
            Float estimate = Float.parseFloat(estimateTextField.getText());
            if (estimate < 0) {
                throw new NumberFormatException("estimate cannot be negative");
            }
            else if (estimate != task.getCurrentEstimate()) {
                task.setCurrentEstimate(estimate);
                estimateTextField.getStyleClass().removeAll("error");
            }
        }
        catch (NumberFormatException e) {
            estimateTextField.getStyleClass().add("error");
        }
    }

    /**
     * Configures this task card.
     * @param pTask The task to display
     * @param pStory The story that this task is a part of
     * @param pParent The parent controller of this form.
     */
    public void configure(final Task pTask, final Story pStory, final ScrumBoardStoryController pParent) {
        task = pTask;
        story = pStory;
        parent = pParent;
        nameLabel.setText(task.getName());
        estimateTextField.setText(Float.toString(task.getCurrentEstimate()));
    }

    /**
     * Called when the edit assignees button is clicked.
     * @param event The event that called the method
     */
    @FXML
    private void editAssignedButtonClicked(final ActionEvent event) {
        if (taskPopover == null) {
            try {
                Parent parent = assigneesPopOverFxml.load();
                taskPopover = new PopOver(parent);
                AssigneeController controller = assigneesPopOverFxml.getController();
                taskPopover.hideOnEscapeProperty().setValue(true);
                List<Person> possibleAssignees = getPossibleAssignees();
                controller.setUp(task, possibleAssignees);
                taskPopover.arrowLocationProperty().setValue(ArrowLocation.RIGHT_CENTER);
            } catch (IOException e) {
                ErrorReporter.get().reportError(e, "Could not create an assignee popover");
            }
        }
        taskPopover.show(editAssignedButton);
    }

    /**
     * Called when the log effort button is clicked.
     * @param event The event that called the method
     */
    @FXML
    private void logEffortButtonClick(final ActionEvent event) {
        if (effortPopOver == null) {
            try {
                Parent parent = effortPopOverFxml.load();
                effortPopOver = new PopOver(parent);
                EffortController controller = effortPopOverFxml.getController();
                List<Person> possibleAssignees = getPossibleAssignees();
                controller.setUp(task, possibleAssignees);
                effortPopOver.hideOnEscapeProperty().setValue(true);
                effortPopOver.arrowLocationProperty().setValue(ArrowLocation.RIGHT_CENTER);
            } catch (IOException e) {
                ErrorReporter.get().reportError(e, "Could not create an effort popover");
            }
        }
        effortPopOver.show(logEffortButton);
    }

    /**
     * Gets a list of all people that can be assigned to this story.
     * @return The possible assigness
     */
    private List<Person> getPossibleAssignees() {
        List<Person> possibleAssignees = new ArrayList<>();
        List<Sprint> sprints = UsageHelper.findUsages(story)
                .stream()
                .filter(model -> model instanceof Sprint).map(model -> (Sprint) model)
                .collect(Collectors.toList());
        if (sprints.size() > 0) {
            sprints.forEach(sprint -> possibleAssignees.addAll(sprint.getTeam().getMembers()));
        }
        return possibleAssignees;
    }

    @Override
    public void undoRedoNotification(final ChangeState param) {
        if (param == ChangeState.Remake || param == ChangeState.Revert) {
            parent.update();
        }
    }
}
