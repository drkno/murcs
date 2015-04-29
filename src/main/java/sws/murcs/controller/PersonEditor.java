package sws.murcs.controller;

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
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.model.persistence.PersistenceManager;

/**
 * Allows you to edit a edit
 */
public class PersonEditor extends GenericEditor<Person> {

    @FXML
    private TextField personNameTextField, usernameTextField, personFullNameTextField;

    @FXML
    private Label labelErrorMessage;

    @FXML
    private ChoiceBox<Skill> skillChoiceBox;

    @FXML
    private VBox skillVBox;

    /**
     * Initializes the editor for use, sets up listeners etc.
     */
    @FXML
    public void initialize() {
        personNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        personFullNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        usernameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        skillChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) updateAndHandle();
        });

        skillChoiceBox.getItems().clear();
        skillChoiceBox.getItems().addAll(PersistenceManager.Current.getCurrentModel().getSkills());
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
            //Output any other exception to the console
            e.printStackTrace();
        }
        finally {
            updateSkills();
        }
    }

    /**
     * Loads the person into the form
     */
    @Override
    public void load() {
        skillChoiceBox.getSelectionModel().clearSelection();
        updateSkills();

        if (edit.getShortName() != null && !personNameTextField.getText().equals(edit.getShortName())) {
            personNameTextField.setText(edit.getShortName());
        }
        if (edit.getLongName() != null && !personFullNameTextField.getText().equals(edit.getLongName())) {
            personFullNameTextField.setText(edit.getLongName());
        }
        if (edit.getUserId() != null && !usernameTextField.getText().equals(edit.getUserId())) {
            usernameTextField.setText(edit.getUserId());
        }

        updateAndHandle();
    }

    /**
     * Generates a node for a skill
     * @param skill The skill
     * @return the node representing the skill
     */
    private Node generateSkillNode(final Skill skill) {
        Text nameText = new Text(skill.toString());
        Button removeButton = new Button("X");
        removeButton.setOnAction(event -> {
            edit.removeSkill(skill);
            skillChoiceBox.getItems().add(skill);
            updateAndHandle();
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
    public void update() throws Exception {
        if (edit.getShortName() == null || !personNameTextField.getText().equals(edit.getShortName())) {
            edit.setShortName(personNameTextField.getText());
        }
        if (edit.getLongName() == null || !personFullNameTextField.getText().equals(edit.getLongName())) {
            edit.setLongName(personFullNameTextField.getText());
        }
        if (edit.getUserId() == null || !usernameTextField.getText().equals(edit.getUserId())) {
            edit.setUserId(usernameTextField.getText());
        }
        Skill selectedSkill = skillChoiceBox.getValue();

        if (selectedSkill != null) {
            generateSkillNode(selectedSkill);
            edit.addSkill(selectedSkill);
        }
    }

    /**
     * Updates the fields in the edit form based on an Undo/Redo callback.
     */
    public void updateFields() {
        load();
    }
}
