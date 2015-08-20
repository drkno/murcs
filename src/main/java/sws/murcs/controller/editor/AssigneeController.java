package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sws.murcs.controller.controls.SearchableComboBox;
import sws.murcs.model.Person;

import java.util.Collection;
import java.util.List;

public class AssigneeController {

    @FXML
    private VBox recentlyUsedVBox, currentAssigneesVBox;

    @FXML
    private ComboBox assigneeComboBox;

    private TaskEditor parentEditor;

    private Collection<Person> recentAssignees;

    private SearchableComboBox<Person> searchableComboBoxDecorator;

    private void setUp(final TaskEditor parent, List<Person> recentPeople) {
        parentEditor = parent;
        recentAssignees = recentPeople;
        if (recentAssignees != null) {
            addRecentPeople();
        }
        
    }

    private void addRecentPeople() {
        for (Person assignee : recentAssignees) {
            addRecentButton(assignee);
        }
    }

    private void addRecentButton(final Person assignee) {
        Button button = new Button();
        button.setText(assignee.getShortName());
        button.setOnAction((event) -> {
            addAssignee(assignee);
        });
        recentlyUsedVBox.getChildren().add(button);
    }

    private void addAssignee(Person assignee) {
        HBox container = new HBox();
        Button delete = new Button();
        delete.setText("X");
        delete.getStyleClass().addAll("mdr-button", "mdrd-button");
        delete.setOnAction((event) -> {
            removeAssignee(assignee, container);
        });
        Label personLabel = new Label(assignee.getShortName());
        container.getChildren().addAll(personLabel, delete);
        container.setSpacing(5);
        currentAssigneesVBox.getChildren().add(container);
        parentEditor.addAssignee(assignee);
    }

    private void removeAssignee(Person assignee, HBox container) {
        currentAssigneesVBox.getChildren().remove(assignee);
        parentEditor.removeAssignee(assignee);
    }


}
