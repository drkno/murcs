package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sws.murcs.exceptions.DuplicateObjectException;
import sws.murcs.exceptions.NameInvalidException;
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

    @FXML private VBox teamMembersContainer;
    @FXML private TextField teamNameTextField, longNameTextField, descriptionTextField;
    @FXML private ComboBox<Person> productOwnerPicker, scrumMasterPicker, addTeamMemberPicker;
    @FXML private Label labelErrorMessage;

    /**
     * Saves the team being edited
     */
    public void update() throws Exception {
        labelErrorMessage.setText("");
        edit.setShortName(teamNameTextField.getText());
        edit.setLongName(longNameTextField.getText());
        edit.setDescription(descriptionTextField.getText());

        edit.setProductOwner((Person) productOwnerPicker.getSelectionModel().getSelectedItem());
        edit.setScrumMaster((Person) scrumMasterPicker.getSelectionModel().getSelectedItem());

        if (addTeamMemberPicker.getSelectionModel().getSelectedItem() != null) {
            edit.addMember((Person) addTeamMemberPicker.getSelectionModel().getSelectedItem());
        }

        // Sets the product owner and scrum master, no need to check if it's been set
        Person productOwner = (Person) productOwnerPicker.getSelectionModel().getSelectedItem();
        edit.setProductOwner(productOwner);
        Person scrumMaster = (Person) scrumMasterPicker.getSelectionModel().getSelectedItem();
        edit.setScrumMaster(scrumMaster);

        RelationalModel model = PersistenceManager.Current.getCurrentModel();

        //If we haven't added the team yet, throw them in the list of unassigned people
        if (!model.getTeams().contains(edit))
            model.addTeam(edit);

        //If we have a saved callBack, call it
        if (onSaved != null)
            onSaved.call();

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
        catch (DuplicateObjectException | NameInvalidException e) {
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

        //We don't have to maintain the list here, as we want it to clear the selection
        addTeamMemberPicker.getItems().clear();
        addTeamMemberPicker.getItems().addAll(PersistenceManager.Current.getCurrentModel().getUnassignedPeople());

        teamMembersContainer.getChildren().clear();
        for (Person person : edit.getMembers()) {
            Node node = generateMemberNode(person);
            teamMembersContainer.getChildren().add(node);
        }

        ArrayList<Person> productOwners = new ArrayList<>();
        //Add all the people with the PO skill to the list of PO's
        edit.getMembers().stream().filter(p -> p.canBeRole(Skill.PO_NAME)).forEach(p -> productOwners.add(p));
        productOwners.remove(edit.getScrumMaster());

        productOwnerPicker.getItems().clear();
        productOwnerPicker.getItems().addAll(productOwners);
        productOwnerPicker.getSelectionModel().select(edit.getProductOwner());

        ArrayList<Person> scrumMasters = new ArrayList<>();
        //Add all the people with the scrum master skill to the list of scrum masters's
        edit.getMembers().stream().filter(p -> p.canBeRole(Skill.SM_NAME)).forEach(p -> scrumMasters.add(p));
        scrumMasters.remove(edit.getProductOwner());

        scrumMasterPicker.getItems().clear();
        scrumMasterPicker.getItems().addAll(scrumMasters);
        scrumMasterPicker.getSelectionModel().select(edit.getScrumMaster());
    }

    /**
     * Generates a node for a team member
     * @param person The team member
     * @return the node representing the team member
     */
    private Node generateMemberNode(final Person person) {
            Text nameText = new Text(person + "");
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
            if (oldValue && !newValue)  updateAndHandle();
        });

        descriptionTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)  updateAndHandle();
        });

        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // due to a bug in javafx, this prints a stack trace to the console.
                // we cant do anything about it at the moment
                System.err.println("JavaFX has a bug that prints a stack trace here. There is nothing we can do about it. " +
                        "If there wasn't a stack trace, it's a miracle, something in Java got BETTER!");
                updateAndHandle();
            }
        });
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // due to a bug in javafx, this prints a stack trace to the console.
                // we cant do anything about it at the moment
                System.err.println("JavaFX has a bug that prints a stack trace here. There is nothing we can do about it. " +
                        "If there wasn't a stack trace, it's a miracle, something in Java got BETTER!");
                updateAndHandle();
            }
        });

        addTeamMemberPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // due to a bug in javafx, this prints a stack trace to the console.
                // we cant do anything about it at the moment
                System.err.println("JavaFX has a bug that prints a stack trace here. There is nothing we can do about it. " +
                        "If there wasn't a stack trace, it's a miracle, something in Java got BETTER!");
                updateAndHandle();
            }
        });
    }
}