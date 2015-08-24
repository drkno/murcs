package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sws.murcs.controller.controls.SearchableComboBox;
import sws.murcs.model.Person;
import sws.murcs.model.helpers.RecentlyUsedHelper;

import java.util.Collection;
import java.util.List;

/**
 * The controller for the assignee popover GUI element.
 */
public class AssigneeController {

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
     * The editor the popover is linked to.
     */
    private TaskEditor parentEditor;

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

    public void setUp(final TaskEditor parent, List<Person> pPossibleAssignees) {
        parentEditor = parent;
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
        assignees = parentEditor.getTask().getAssignees();
        addAssignees(assignees);
    }

    private void addAssignees(Collection<Person> people) {
        people.stream().forEach(person -> Platform.runLater(() -> addAssignee(person)));
    }

    private void addRecentPeople() {
        recentAssignees.stream().filter(person -> possibleAssignees.contains(person)).forEach(this::addRecentButton);
    }

    private void addRecentButton(final Person assignee) {
        Button button = new Button();
        button.setText(assignee.getShortName());
        button.setOnAction((event) -> {
            if (!parentEditor.getTask().getAssignees().contains(assignee)) {
                addAssignee(assignee);
            }
        });
        recentlyUsedVBox.getChildren().add(button);
    }

    private void addAssignee(Person assignee) {
        Platform.runLater(() -> searchableComboBoxDecorator.remove(assignee));
        HBox container = new HBox();
        Button delete = new Button();
        delete.setText("X");
        delete.getStyleClass().addAll("mdr-button", "mdrd-button");
        delete.setOnAction((event) -> removeAssignee(assignee, container));
        Label personLabel = new Label(assignee.getShortName());
        container.getChildren().addAll(personLabel, delete);
        container.setSpacing(5);
        currentAssigneesVBox.getChildren().add(container);
        parentEditor.addAssignee(assignee);
        RecentlyUsedHelper.get().addToRecentPeople(assignee);
    }

    private void removeAssignee(Person assignee, HBox container) {
        currentAssigneesVBox.getChildren().remove(container);
        parentEditor.removeAssignee(assignee);
        Platform.runLater(() -> searchableComboBoxDecorator.add(assignee));
    }


}
