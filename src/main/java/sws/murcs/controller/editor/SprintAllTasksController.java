package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import sws.murcs.model.Sprint;

import java.security.acl.Group;

public class SprintAllTasksController {

    enum Filters {
        Allocated,
        Unallocated,
        All
    }

    enum Groups {
        Story,
        None
    }

    enum Orders {
        Alphabetical,
        None
    }

    @FXML
    private ChoiceBox filteringChoiceBox, groupingChoiceBox, orderingChoiceBox;

    @FXML
    private VBox taskVBox;

    private Sprint sprint;

    @FXML
    private void initialize() {
        filteringChoiceBox.getItems().addAll(Filters.values());
        groupingChoiceBox.getItems().addAll(Groups.values());
        orderingChoiceBox.getItems().addAll(Orders.values());
    }

    public void setUpController(Sprint associatedSprint) {
        sprint = associatedSprint;
    }
}
