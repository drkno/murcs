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

public class AssigneeController {

    @FXML
    private VBox recentlyUsedVBox, currentAssigneesVBox;

    @FXML
    private ComboBox assigneeComboBox;

    private TaskEditor parentEditor;

    private Collection<Person> recentAssignees;

    private Collection<Person> possibleAssignees;

    private Collection<Person> assignees;

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
        button.setOnAction((event) -> addAssignee(assignee));
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
