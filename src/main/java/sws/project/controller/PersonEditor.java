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
import sws.project.model.persistence.PersistenceManager;

import java.util.Collection;

/**
 * Allows you to edit a edit
 */
public class PersonEditor extends GenericEditor<Person> {

    private boolean initialized;
    private boolean loaded;
    @FXML
    private TextField nameTextField, usernameTextField;

    @FXML
    private Label labelErrorMessage;

    @FXML
    private VBox skillContainer;

    @FXML
    private ChoiceBox<Skill> addSkillPicker;

    /**
     * Saves the edit being edited
     */
    private void savePerson() {
        if (!initialized || !loaded) return;
        try {
            edit.setShortName(nameTextField.getText());
            edit.setUserId(usernameTextField.getText());

            if (addSkillPicker.getSelectionModel().getSelectedItem() != null) {
                edit.addSkill((Skill) addSkillPicker.getSelectionModel().getSelectedItem());
            }

            RelationalModel model = PersistenceManager.Current.getCurrentModel();

            //If we haven't added the edit yet, throw them in the list of unassigned people
            if (!model.getPeople().contains(edit))
                model.addPerson(edit);

            //If we have a saved callBack, call it
            if (onSaved != null)
                onSaved.call();

            //HORRIBLE HORRIBLE WAY TO DO IT!!! IMPLEMENT A CHANGE LISTENER
            load();

        } catch (Exception e) {
            labelErrorMessage.setText(e.getMessage());
            return;
        }
    }

    /**
     * Loads the edit into the form
     */
    public void load() {

        nameTextField.setText(edit.getShortName());
        usernameTextField.setText(edit.getUserId());

        skillContainer.getChildren().clear();
        for (Skill skill : edit.getSkills()) {
            Node node = generateSkillNode(skill);
            skillContainer.getChildren().add(node);
        }

        addSkillPicker.getItems().clear();
        addSkillPicker.getItems().addAll(PersistenceManager.Current.getCurrentModel().getSkills());

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
     *
     * @param skill The team member
     * @return the node representing the team member
     */
    private Node generateSkillNode(final Skill skill) {
        Text nameText = new Text(skill + "");
        Button removeButton = new Button("X");
        removeButton.setOnAction(event -> {
            edit.removeSkill(skill);
            savePerson();
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
            if (oldValue && !newValue) savePerson();
        });

        usernameTextField.focusedProperty().addListener((p, o, n) -> {
            if (o && !n) savePerson();
        });

        addSkillPicker.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) savePerson();
        });

        initialized = true;
    }
}
