package sws.project.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.project.model.Person;
import sws.project.model.RelationalModel;
import sws.project.model.Team;
import sws.project.model.persistence.PersistenceManager;
import sws.project.view.App;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
 *
 */
public class TeamEditor implements Initializable{
    private Team team;

    @FXML
    Text teamMembersText;

    @FXML
    TextField nameTextField, longNameTextField, descriptionTextField;

    @FXML
    ComboBox productOwnerPicker, scrumMasterPicker;

    @FXML
    Label labelErrorMessage;

    private Callable<Void> onSaved;

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
            controller.loadProject();

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
        try {
            team.setShortName(nameTextField.getText());
            team.setLongName(longNameTextField.getText());
            team.setDescription(descriptionTextField.getText());

            team.setProductOwner((Person)productOwnerPicker.getSelectionModel().getSelectedItem());
            team.setScrumMaster((Person) scrumMasterPicker.getSelectionModel().getSelectedItem());

            RelationalModel model= PersistenceManager.Current.getCurrentModel();

            //If we haven't added the team yet, throw them in the list of unassigned people
            if (!model.getTeams().contains(team))
                model.addUnassignedTeam(team);

            //If we have a saved callBack, call it
            if (onSaved != null)
                onSaved.call();

        }catch (Exception e){
            labelErrorMessage.setText(e.getMessage());
            return;
        }
    }

    /**
     * Loads the team into the form
     */
    private void loadProject(){
        nameTextField.setText(team.getShortName());
        longNameTextField.setText(team.getLongName());
        descriptionTextField.setText(team.getDescription());

        String teamMembers = "";
        for (Person member : team.getMembers()) {
            if (!teamMembers.isEmpty())
                teamMembers += ", ";
            teamMembers += member.toString();
        }

        productOwnerPicker.getItems().clear();
        productOwnerPicker.getItems().addAll(team.getMembers());
        productOwnerPicker.getSelectionModel().select(team.getProductOwner());

        scrumMasterPicker.getItems().clear();
        scrumMasterPicker.getItems().addAll(team.getMembers());
        productOwnerPicker.getSelectionModel().select(team.getScrumMaster());
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
        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> saveTeam());
    }
}
