package sws.project.controller;

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
import sws.project.model.Person;
import sws.project.model.RelationalModel;
import sws.project.model.Team;
import sws.project.model.persistence.PersistenceManager;

import java.util.Collection;

/**
 *  The controller for the team editor
 */
public class TeamEditor extends GenericEditor<Team> {

    @FXML private VBox teamMembersContainer;
    @FXML private TextField teamNameTextField, longNameTextField, descriptionTextField;
    @FXML private ComboBox<Person> productOwnerPicker, scrumMasterPicker, addTeamMemberPicker;
    @FXML private Label labelErrorMessage;

    private boolean initialized;
    private boolean loaded;

    /**
     * Saves the team being edited
     */
    public void update() {
        if (!initialized || !loaded) return;
        try {
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

            //Load the team again, to make sure everything is updated. We could probably do this
            //more nicely
            load();

        }
        catch (Exception e) {
            labelErrorMessage.setText(e.getMessage());
        }
    }

    /**
     * Loads the team into the form
     */
    public void load(){
        teamNameTextField.setText(edit.getShortName());
        longNameTextField.setText(edit.getLongName());
        descriptionTextField.setText(edit.getDescription());

        teamMembersContainer.getChildren().clear();
        for (Person p : edit.getMembers()){
            Node node = generateTeamMemberNode(p);
            teamMembersContainer.getChildren().add(node);
        }

        maintainList(productOwnerPicker.getItems(), edit.getMembers());
        productOwnerPicker.getSelectionModel().select(edit.getProductOwner());

        maintainList(scrumMasterPicker.getItems(), edit.getMembers());
        scrumMasterPicker.getSelectionModel().select(edit.getScrumMaster());

        //We don't have to maintain the list here, as we want it to clear the selection
        addTeamMemberPicker.getItems().clear();
        addTeamMemberPicker.getItems().addAll(PersistenceManager.Current.getCurrentModel().getUnassignedPeople());

        //Set the loaded flag
        loaded = true;
    }

    /**
     * Updates a list so it only has items from a second list. This is necessary because
     * javafx does trippy things when you clear a list and add all the items back to it
     * @param update The list to update
     * @param match The list to match
     */
    private void maintainList(Collection update, Collection match){
        //Add all the items in 'match' but not 'update' to 'update'
        match.stream().filter(p -> !update.contains(p)).forEach(p -> update.add(p));
        //Remove all the items from 'update' that aren't in 'match'
        update.stream().filter(p -> !match.contains(p)).forEach(p -> update.remove(p));

    }

    /**
     * Generates a node for a team member
     * @param person The team member
     * @return the node representing the team member
     */
    private Node generateTeamMemberNode(final Person person){
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

    /**
     * Initializes the editor for use, sets up listeners etc.
     */
    @FXML
    public void initialize() {
        teamNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) update();
        });

        longNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)  update();
        });

        descriptionTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue)  update();
        });

        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> update());
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> update());

        addTeamMemberPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // due to a bug in javafx, this prints a stack trace to the console.
                // we cant do anything about it at the moment
                System.err.println("JavaFX has a bug that prints a stack trace here. There is nothing we can do about it. " +
                        "If there wasn't a stack trace, it's a miracle, something in Java got BETTER!");
                update();
            }
        });

        initialized = true;
    }
}
