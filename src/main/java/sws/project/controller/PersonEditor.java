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
 *
 */
public class PersonEditor extends GenericEditor<Person> {

    @FXML
    private TextField nameTextField, usernameTextField;

    @FXML
    private ChoiceBox<Skill> skillChoiceBox;

    @FXML
    private VBox skillVBox;

    @FXML
    private Label errorMessageLabel;

    @FXML
    public void initialize() {
        nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) savePerson();
        });

        usernameTextField.focusedProperty().addListener((p, o, n) -> {
            if (o && !n) savePerson();
        });

        skillChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) savePerson();
        });
    }

    @Override
    public void load() {
        nameTextField.setText(edit.getShortName());
        usernameTextField.setText(edit.getUserId());

        skillChoiceBox.getItems().clear();
        skillChoiceBox.getItems().addAll(PersistenceManager.Current.getCurrentModel().getSkills());

        skillVBox.getChildren().clear();
        for (Skill skill : edit.getSkills()) {
            Node node = generateSkillNode(skill);
            skillVBox.getChildren().add(node);
            skillChoiceBox.getItems().remove(skill);
        }

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

    private void savePerson() {

        try {
            RelationalModel model = PersistenceManager.Current.getCurrentModel();

            edit.setShortName(nameTextField.getText());
            edit.setUserId(usernameTextField.getText());

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
        } catch (Exception e) {
            errorMessageLabel.setText(e.getMessage());
        } finally {
            load();
        }
    }
}
