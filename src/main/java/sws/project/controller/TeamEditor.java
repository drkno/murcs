package sws.project.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sws.project.model.Person;
import sws.project.model.RelationalModel;
import sws.project.model.Team;
import sws.project.model.persistence.PersistenceManager;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 *  The controller for the team editor
 */
public class TeamEditor extends GenericEditor<Team> implements Initializable{

    @FXML
    private VBox teamMembersContainer;

    @FXML
    private TextField nameTextField, longNameTextField, descriptionTextField;

    @FXML
    private ChoiceBox productOwnerPicker, scrumMasterPicker;

    @FXML
    private ComboBox<Person> addTeamMemberPicker;

    @FXML
    private Label labelErrorMessage;

    private boolean intialized;
    private boolean loaded;

    /**
     * Saves the team being edited
     */
    private void saveTeam() {
        if (!intialized || !loaded) return;
        try {
            edit.setShortName(nameTextField.getText());
            edit.setLongName(longNameTextField.getText());
            edit.setDescription(descriptionTextField.getText());

            edit.setProductOwner((Person)productOwnerPicker.getSelectionModel().getSelectedItem());
            edit.setScrumMaster((Person) scrumMasterPicker.getSelectionModel().getSelectedItem());

            if (addTeamMemberPicker.getSelectionModel().getSelectedItem() != null) {
                edit.addMember((Person) addTeamMemberPicker.getSelectionModel().getSelectedItem());
            }

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

        }catch (Exception e){
            labelErrorMessage.setText(e.getMessage());
            return;
        }
    }

    /**
     * Loads the team into the form
     */
    public void load(){
        nameTextField.setText(edit.getShortName());
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
            saveTeam();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) saveTeam();
        });

        longNameTextField.focusedProperty().addListener((p, o, n) -> {
            if (o && !n)  saveTeam();
        });

        descriptionTextField.focusedProperty().addListener((p, o, n) -> {
            if (o && !n)  saveTeam();
        });

        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> saveTeam());
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> saveTeam());

        addTeamMemberPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) saveTeam();
        });

        intialized = true;
    }
}
