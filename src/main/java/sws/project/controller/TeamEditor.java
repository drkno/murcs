package sws.project.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sws.project.model.Person;
import sws.project.model.RelationalModel;
import sws.project.model.Skill;
import sws.project.model.Team;
import sws.project.model.persistence.PersistenceManager;

import java.util.Collection;

/**
 * The controller for the team editor
 */
public class TeamEditor extends GenericEditor<Team> {

    @FXML
    private VBox teamMembersContainer;
    @FXML
    private TextField nameTextField, longNameTextField, descriptionTextField;
    @FXML
    private ChoiceBox<Person> productOwnerPicker, scrumMasterPicker, addTeamMemberPicker;
    @FXML
    private Label labelErrorMessage;

    /**
     * Saves the team being edited
     */
    private void update() {
        try {
            edit.setShortName(nameTextField.getText());
            edit.setLongName(longNameTextField.getText());
            edit.setDescription(descriptionTextField.getText());

            edit.setProductOwner(productOwnerPicker.getSelectionModel().getSelectedItem());
            edit.setScrumMaster(scrumMasterPicker.getSelectionModel().getSelectedItem());

            if (addTeamMemberPicker.getSelectionModel().getSelectedItem() != null) {
                edit.addMember(addTeamMemberPicker.getSelectionModel().getSelectedItem());
            }

            // Sets the product owner and scrum master, no need to check if it's been set
            Person productOwner = productOwnerPicker.getSelectionModel().getSelectedItem();
            edit.setProductOwner(productOwner);
            Person scrumMaster = scrumMasterPicker.getSelectionModel().getSelectedItem();
            edit.setScrumMaster(scrumMaster);

            RelationalModel model = PersistenceManager.Current.getCurrentModel();

            //If we haven't added the team yet, throw them in the list of unassigned people
            if (!model.getTeams().contains(edit))
                model.addTeam(edit);

            //If we have a saved callBack, call it
            if (onSaved != null)
                onSaved.call();

            //Load the team again, to make sure everything is updated. We could probably do this
            //more nicely
            load();

        } catch (Exception e) {
            labelErrorMessage.setText(e.getMessage());
        }
    }

    /**
     * Loads the team into the form
     */
    public void load() {
        nameTextField.setText(edit.getShortName());
        longNameTextField.setText(edit.getLongName());
        descriptionTextField.setText(edit.getDescription());

        //We don't have to maintain the list here, as we want it to clear the selection
        addTeamMemberPicker.getItems().clear();
        addTeamMemberPicker.getItems().addAll(PersistenceManager.Current.getCurrentModel().getUnassignedPeople());

        teamMembersContainer.getChildren().clear();
        for (Person person : edit.getMembers()) {
            Node node = generateMemberNode(person);
            teamMembersContainer.getChildren().add(node);
        }

        productOwnerPicker.getItems().clear();
        productOwnerPicker.getItems().addAll(edit.getMembers());
        productOwnerPicker.getSelectionModel().select(edit.getProductOwner());

        scrumMasterPicker.getItems().clear();
        scrumMasterPicker.getItems().addAll(edit.getMembers());
        scrumMasterPicker.getSelectionModel().select(edit.getScrumMaster());
    }

    /**
     * Generates a node for a team member
     *
     * @param person The team member
     * @return the node representing the team member
     */
    private Node generateMemberNode(final Person person) {
            Text nameText = new Text(person + "");
        Button removeButton = new Button("X");
        removeButton.setOnAction(event -> {
            edit.removeMember(person);
            update();
        });

        GridPane pane = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        column1.fillWidthProperty().setValue(true);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.SOMETIMES);

        pane.getColumnConstraints().add(column1);
        pane.getColumnConstraints().add(column2);

        pane.add(nameText, 0, 0);
        pane.add(removeButton, 1, 0);

        return pane;
    }

    @FXML
    public void initialize() {
        nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) update();
        });

        longNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) update();
        });

        descriptionTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) update();
        });

        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> update());
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> update());

        addTeamMemberPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) update();
        });
    }
}
