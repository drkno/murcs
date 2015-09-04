package sws.murcs.controller.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import sws.murcs.model.Person;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.model.Task;
import sws.murcs.model.helpers.UsageHelper;

/**
 * Controller for a task within a scrum board.
 */
public class ScrumTaskController {

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
     * Sets up the form.
     */
    @FXML
    private void initialize() {
        estimateTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
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
                    //estimateTextField.setText("" + task.getCurrentEstimate());
                    estimateTextField.getStyleClass().add("error");
                }
            }
        });
    }

    /**
     * Configures this task card.
     * @param pTask The task to display
     * @param pStory The story that this task is a part of
     */
    public void configure(final Task pTask, final Story pStory) {
        task = pTask;
        story = pStory;
        nameLabel.setText(task.getName());
        estimateTextField.setText(Float.toString(task.getCurrentEstimate()));
    }

    /**
     * Called when the edit assignees button is clicked.
     * @param event The event that called the method
     */
    @FXML
    private void editAssignedButtonClicked(final ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(TaskEditor.class.getResource("/sws/murcs/AssigneesPopOver.fxml"));
            Parent parent = loader.load();
            PopOver taskPopover = new PopOver(parent);
            AssigneeController controller = loader.getController();
            taskPopover.hideOnEscapeProperty().setValue(true);
            List<Person> possibleAssignees = getPossibleAssignees();
            controller.setUp(task, possibleAssignees);
            taskPopover.arrowLocationProperty().setValue(ArrowLocation.RIGHT_CENTER);
            taskPopover.show(editAssignedButton);
        }
        catch (IOException e) {
            ErrorReporter.get().reportError(e, "Could not create an assignee popover");
        }
    }

    /**
     * Called when the log effort button is clicked.
     * @param event The event that called the method
     */
    @FXML
    private void logEffortButtonClick(final ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(TaskEditor.class.getResource("/sws/murcs/EffortPopOver.fxml"));
            Parent parent = loader.load();
            PopOver effortPopOver = new PopOver(parent);
            EffortController controller = loader.getController();
            List<Person> possibleAssignees = getPossibleAssignees();
            controller.setUp(task, possibleAssignees);
            effortPopOver.hideOnEscapeProperty().setValue(true);
            effortPopOver.arrowLocationProperty().setValue(ArrowLocation.RIGHT_CENTER);
            effortPopOver.show(logEffortButton);
        }
        catch (IOException e) {
            ErrorReporter.get().reportError(e, "Could not create an effort popover");
        }
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
}
