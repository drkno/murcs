package sws.project.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.project.model.Person;
import sws.project.model.RelationalModel;
import sws.project.model.Team;
import sws.project.model.persistence.PersistenceManager;
import sws.project.view.App;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
 *
 */
public class TeamEditor implements Initializable{
    private Team team;

    @FXML
    VBox teamMembersContainer;

    @FXML
    TextField nameTextField, longNameTextField, descriptionTextField;

    @FXML
    ChoiceBox productOwnerPicker, scrumMasterPicker, addTeamMemberPicker;

    @FXML
    Label labelErrorMessage;

    private Callable<Void> onSaved;
    private boolean intialized;
    private boolean loaded;

    /**
     * Creates a new form for editing a team
     *
     * @param team The team
     * @return The form
     */
    public static Parent createFor(Team team){
        return createFor(team, null);
    }

    /**
     * Creates a new form for editing a team which will call the saved callback
     * every time a change is saved
     * @param team The team
     * @param onSaved The save callback
     * @return The form
     */
    public static Parent createFor(Team team, Callable<Void> onSaved){
        try {
            FXMLLoader loader = new FXMLLoader(ProjectEditor.class.getResource("/sws/project/TeamEditor.fxml"));
            Parent parent = loader.load();

            TeamEditor controller = loader.getController();
            controller.team = team;
            controller.onSaved = onSaved;
            controller.loadTeam();

            return parent;
        }catch (Exception e){
            System.err.println("Unable to create a team editor!(this is seriously bad)");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Displays a new window for creating a new form
     * @param okay The okay callback
     * @param cancel The cancelled callback
     */
    public static void displayWindow(Callable<Void> okay, Callable<Void> cancel){
        try {
            Parent content = createFor(new Team());

            Parent root = CreateWindowController.newCreateNode(content, okay, cancel);
            Scene scene = new Scene(root);

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Create Team");

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(App.stage);

            newStage.show();
        }catch (Exception e){

        }
    }

    /**
     * Saves the team being edited
     */
    private void saveTeam() {
        if (!intialized || !loaded) return;
        try {
            team.setShortName(nameTextField.getText());
            team.setLongName(longNameTextField.getText());
            team.setDescription(descriptionTextField.getText());

            team.setProductOwner((Person)productOwnerPicker.getSelectionModel().getSelectedItem());
            team.setScrumMaster((Person) scrumMasterPicker.getSelectionModel().getSelectedItem());

            if (addTeamMemberPicker.getSelectionModel().getSelectedItem() != null) {
                team.addMember((Person) addTeamMemberPicker.getSelectionModel().getSelectedItem());
            }

            RelationalModel model= PersistenceManager.Current.getCurrentModel();

            //If we haven't added the team yet, throw them in the list of unassigned people
            if (!model.getTeams().contains(team))
                model.addTeam(team);

            //If we have a saved callBack, call it
            if (onSaved != null)
                onSaved.call();

            //Load the team again, to make sure everything is updated. We could probably do this
            //more nicely
            loadTeam();

        }catch (Exception e){
            labelErrorMessage.setText(e.getMessage());
            return;
        }
    }

    /**
     * Loads the team into the form
     */
    private void loadTeam(){
        nameTextField.setText(team.getShortName());
        longNameTextField.setText(team.getLongName());
        descriptionTextField.setText(team.getDescription());

        teamMembersContainer.getChildren().clear();
        for (Person p : team.getMembers()){
            Node node = generateTeamMemberNode(p);
            teamMembersContainer.getChildren().add(node);
        }

        maintainList(productOwnerPicker.getItems(), team.getMembers());
        productOwnerPicker.getSelectionModel().select(team.getProductOwner());

        maintainList(scrumMasterPicker.getItems(), team.getMembers());
        scrumMasterPicker.getSelectionModel().select(team.getScrumMaster());

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
    private Node generateTeamMemberNode(Person person){
        return new Text(person.getShortName());
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
