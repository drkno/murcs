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

/**
 * Allows you to edit a edit
 */
public class PersonEditor extends GenericEditor<Person> {
    @FXML
    private TextField personNameTextField, usernameTextField;

    @FXML
    private Label labelErrorMessage;
    
    @FXML
    private ChoiceBox<Skill> skillChoiceBox;

    @FXML
    private VBox skillVBox;

    @FXML
    private Label errorMessageLabel;

    /**
     * Initializes the editor for use, sets up listeners etc.
     */
    @FXML
    public void initialize() {
        personNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) update();
        });

        usernameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) update();
        });

        skillChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) update();
        });
    }

    /**
     * Loads the team into the form
     */
    @Override
    public void load() {
        labelErrorMessage.setText("");
        personNameTextField.setText(edit.getShortName());
        usernameTextField.setText(edit.getUserId());

        skillChoiceBox.getItems().clear();
        skillChoiceBox.getItems().addAll(PersistenceManager.Current.getCurrentModel().getSkills());

        updateSkills();
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
            update();
            load();
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
     * Updates the list of skills the person has
     */
    private void updateSkills() {
        skillVBox.getChildren().clear();
        for (Skill skill : edit.getSkills()) {
            Node node = generateSkillNode(skill);
            skillVBox.getChildren().add(node);
            skillChoiceBox.getItems().remove(skill);
        }
    }

    /**
     * Saves the edit being edited
     */
    public void update() {

        try {
            labelErrorMessage.setText("");
            edit.setShortName(personNameTextField.getText());
            edit.setUserId(usernameTextField.getText());

            RelationalModel model= PersistenceManager.Current.getCurrentModel();
            Skill selectedSkill = skillChoiceBox.getValue();
            
            if (selectedSkill != null) {
                generateSkillNode(selectedSkill);
                edit.addSkill(selectedSkill);
            }

            // Save the person if it hasn't been yet
            if (!model.getPeople().contains(edit))
                model.addPerson(edit);

            // Call the callback if it exists
            if (onSaved != null)
                onSaved.call();

        }
        catch (Exception e){
            labelErrorMessage.setText(e.getMessage());
        }
        finally {
            updateSkills();
        }
    }
}
