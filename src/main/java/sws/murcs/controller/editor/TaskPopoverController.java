package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import sws.murcs.controller.controls.SearchableComboBox;
import sws.murcs.model.Person;
import sws.murcs.model.Task;
import sws.murcs.model.helpers.RecentlyUsedHelper;

import java.util.Collection;
import java.util.List;

/**
 * The controller for the task editor popover GUI element.
 */
public class TaskPopoverController {

    /**
     * The recently used VBox and the current assignees vbox.
     */
    @FXML
    private VBox recentlyUsedVBox, currentAssigneesVBox;

    /**
     * The combobox used for selecting a new assignee.
     */
    @FXML
    private ComboBox assigneeComboBox;

    /**
     * The task that this GUI represents.
     */
    private Task task;

    /**
     * The recent assignees (this is only updated when the popover is opened otherwise you'd get
     * people turning up in here that you'd literally just added).
     */
    private Collection<Person> recentAssignees;

    /**
     * The list of people who can possibly be assigned to the task.
     */
    private Collection<Person> possibleAssignees;

    /**
     * The people who are currently assigned to the task.
     */
    private Collection<Person> assignees;

    /**
     * A decorator for making the add assignees combobox searchable.
     */
    private SearchableComboBox<Person> searchableComboBoxDecorator;

    /**
     * Container spacing.
     */
    private static final double CONTAINER_SPACING = 5.0;

    /**
     * Sets up the assignee controller initially with the list of possible assignees and the task editor that the
     * assignee controller belongs to.
     * @param pTask The task that this GUI represents.
     * @param pPossibleAssignees The list of possible assignees.
     */
    public void setUp(final Task pTask, final List<Person> pPossibleAssignees) {
        currentAssigneesVBox.setSpacing(CONTAINER_SPACING);
        recentlyUsedVBox.setSpacing(CONTAINER_SPACING);
        task = pTask;
        recentAssignees = RecentlyUsedHelper.get().getRecentPeople();
        possibleAssignees = pPossibleAssignees;
        if (recentAssignees != null) {
            addRecentPeople();
        }
        searchableComboBoxDecorator = new SearchableComboBox<>(assigneeComboBox);
        assigneeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Person selectedPerson = (Person) assigneeComboBox.getValue();
                if (selectedPerson != null) {
                    Platform.runLater(() -> {
                        assigneeComboBox.getSelectionModel().clearSelection();
                    });
                    addAssignee(selectedPerson);
                }
            }
        });
        searchableComboBoxDecorator.addAll(possibleAssignees);
        assignees = task.getAssignees();
        addAssignees(assignees);
    }

    /**
     * Adds the given list of people to the assignees section.
     * @param people The people to add.
     */
    private void addAssignees(final Collection<Person> people) {
        people.stream().forEach(person -> Platform.runLater(() -> addAssignee(person)));
    }

    /**
     * Adds all of the recent people as button options.
     */
    private void addRecentPeople() {
        recentAssignees.stream().filter(possibleAssignees::contains).forEach(this::addRecentButton);
    }

    /**
     * Adds a given person to the list of buttons for recently used people.
     * @param assignee the person to create the button for.
     */
    private void addRecentButton(final Person assignee) {
        Button button = new Button();
        button.setText(assignee.getShortName());
        button.setOnAction((event) -> {
            if (!task.getAssignees().contains(assignee)) {
                addAssignee(assignee);
            }
        });
        recentlyUsedVBox.getChildren().add(button);
    }

    /**
     * Adds an assignee to the list of assignees and updates the main controller to reflect this as well.
     * @param assignee the assignee to add to the task.
     */
    private void addAssignee(final Person assignee) {
        Platform.runLater(() -> searchableComboBoxDecorator.remove(assignee));
        AnchorPane container = new AnchorPane();
        Button delete = new Button();
        delete.setText("X");
        delete.getStyleClass().addAll("mdr-button", "mdrd-button");
        delete.setOnAction((event) -> removeAssignee(assignee, container));
        Label personLabel = new Label(assignee.getShortName());
        container.getChildren().addAll(personLabel, delete);
        AnchorPane.setLeftAnchor(personLabel, 0.0);
        AnchorPane.setRightAnchor(delete, 0.0);
        currentAssigneesVBox.getChildren().add(container);
        task.addAssignee(assignee);
        RecentlyUsedHelper.get().addToRecentPeople(assignee);
    }

    /**
     * Removes an assignee from the parent task and the assignee editor.
     * @param assignee the assignee to remove.
     * @param container the container to remove from the editor.
     */
    private void removeAssignee(final Person assignee, final AnchorPane container) {
        currentAssigneesVBox.getChildren().remove(container);
        task.removeAssignee(assignee);
        Platform.runLater(() -> searchableComboBoxDecorator.add(assignee));
    }
}
