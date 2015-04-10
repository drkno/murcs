package sws.murcs.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Person;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Skill;
import sws.murcs.model.Team;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.ArrayList;

/**
 * The controller for the team editor
 */
public class TeamEditor extends GenericEditor<Team> {

    @FXML
    private VBox teamMembersContainer;

    @FXML
    private TextField teamNameTextField, longNameTextField, descriptionTextField;

    @FXML
    private ChoiceBox<Person> productOwnerPicker, scrumMasterPicker, addTeamMemberPicker;

    @FXML
    private Label labelErrorMessage;

    private ChangeListener<Person> smpoChangeListener;

    /**
     * Saves the team being edited
     */
    public void update() throws Exception {
        labelErrorMessage.setText("");
        edit.setShortName(teamNameTextField.getText());
        edit.setLongName(longNameTextField.getText());
        edit.setDescription(descriptionTextField.getText());

        Person productOwner = productOwnerPicker.getValue();
        edit.setProductOwner(productOwner);

        Person scrumMaster = scrumMasterPicker.getValue();
        edit.setScrumMaster(scrumMaster);

        RelationalModel model = PersistenceManager.Current.getCurrentModel();
        Person person = addTeamMemberPicker.getValue();

        if (person != null) {
            edit.addMember(person);
        }

        //If we haven't added the team yet, throw them in the list of unassigned people
        if (!model.getTeams().contains(edit))
            model.addTeam(edit);

        //If we have a saved callBack, call it
        if (onSaved != null)
            onSaved.eventNotification(edit);

        //Load the team again, to make sure everything is updated. We could probably do this better
        load();
    }

    /**
     * Updates the object in memory and handles any exception
     */
    public void updateAndHandle(){
        try {
            labelErrorMessage.setText("");
            update();
        }
        catch (CustomException e) {
            labelErrorMessage.setText(e.getMessage());
        }
        catch (Exception e) {
            //Don't show the user this.
        }
    }

    /**
     * Loads the team into the form
     */
    public void load(){
        teamNameTextField.setText(edit.getShortName());
        longNameTextField.setText(edit.getLongName());
        descriptionTextField.setText(edit.getDescription());

        addTeamMemberPicker.getItems().clear();
        addTeamMemberPicker.getItems().addAll(PersistenceManager.Current.getCurrentModel().getUnassignedPeople());

        teamMembersContainer.getChildren().clear();
        for (Person person : edit.getMembers()) {
            Node node = generateMemberNode(person);
            teamMembersContainer.getChildren().add(node);
        }

        Person productOwner = edit.getProductOwner();
        Person scrumMaster = edit.getScrumMaster();

        //Add all the people with the PO skill to the list of POs
        ArrayList<Person> productOwners = new ArrayList<>();
        edit.getMembers().stream()
                .filter(p -> p.canBeRole(Skill.PO_NAME))
                .forEach(p -> productOwners.add(p));
        productOwners.remove(scrumMaster);
        productOwnerPicker.getSelectionModel().selectedItemProperty().removeListener(smpoChangeListener);
        productOwnerPicker.getItems().clear();
        productOwnerPicker.getItems().addAll(productOwners);
        if (productOwner != null) {
            productOwnerPicker.getSelectionModel().select(productOwner);
        }
        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener(smpoChangeListener);

        //Add all the people with the scrum master skill to the list of scrum masters
        ArrayList<Person> scrumMasters = new ArrayList<>();
        edit.getMembers().stream()
                .filter(p -> p.canBeRole(Skill.SM_NAME))
                .forEach(p -> scrumMasters.add(p));
        scrumMasters.remove(productOwner);
        scrumMasterPicker.getSelectionModel().selectedItemProperty().removeListener(smpoChangeListener);
        scrumMasterPicker.getItems().clear();
        scrumMasterPicker.getItems().addAll(scrumMasters);
        scrumMasterPicker.getSelectionModel().clearSelection();
        if (scrumMaster != null) {
            scrumMasterPicker.getSelectionModel().select(scrumMaster);
        }
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener(smpoChangeListener);
    }

    /**
     * Generates a node for a team member
     * @param person The team member
     * @return the node representing the team member
     */
    private Node generateMemberNode(final Person person) {
        Text nameText = new Text(person.toString());
        Button removeButton = new Button("X");
        removeButton.setOnAction(event -> {
            edit.removeMember(person);
            updateAndHandle();
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

    /**
     * Initializes the editor for use, sets up listeners etc.
     */
    @FXML
    public void initialize() {
        teamNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        longNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        descriptionTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        // Use a removable listener to work around a selected index flip-flop bug
        smpoChangeListener = (observable, oldValue, newValue) -> {
            if (newValue != null) updateAndHandle();
        };
        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener(smpoChangeListener);
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener(smpoChangeListener);

        addTeamMemberPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) updateAndHandle();
        });
    }
}
