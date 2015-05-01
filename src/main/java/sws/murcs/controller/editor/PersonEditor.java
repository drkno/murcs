package sws.murcs.controller.editor;

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
import sws.murcs.controller.GenericPopup;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.model.persistence.PersistenceManager;

/**
 * Allows you to edit a edit
 */
public class PersonEditor extends GenericEditor<Person> {

    @FXML
    private TextField shortNameTextField, userIdTextField, longNameTextField;
    @FXML
    private Label labelErrorMessage;
    @FXML
    private ChoiceBox<Skill> skillChoiceBox;
    @FXML
    private VBox skillVBox;

    @FXML
    @Override
    public void initialize() {
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) saveChanges();
        });

        longNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) saveChanges();
        });

        userIdTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) saveChanges();
        });

        skillChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) saveChanges();
        });

        skillChoiceBox.getItems().clear();
        skillChoiceBox.getItems().addAll(PersistenceManager.Current.getCurrentModel().getSkills());

        setErrorCallback(message -> {
            if (message.getClass() == String.class)
                labelErrorMessage.setText(message);
        });
    }

    @Override
    public void loadObject() {
        String modelShortName = edit.getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName))
            shortNameTextField.setText(modelShortName);

        String modelLongName = edit.getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqual(modelLongName, viewLongName))
            longNameTextField.setText(modelLongName);

        String modelUserId = edit.getUserId();
        String viewUserId = userIdTextField.getText();
        if (isNotEqual(modelUserId, viewUserId))
            userIdTextField.setText(modelUserId);

        updateSkills();
    }

    @Override
    protected void saveChangesWithException() throws Exception {
        Skill selectedSkill = skillChoiceBox.getValue();

        if (selectedSkill != null) {
            generateSkillNode(selectedSkill);
            edit.addSkill(selectedSkill);
        }

        updateSkills();

        String modelShortName = edit.getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelShortName, viewShortName))
            edit.setShortName(viewShortName);

        String modelLongName = edit.getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelLongName, viewLongName))
            edit.setLongName(viewLongName);

        String modelUserId = edit.getUserId();
        String viewUserId = userIdTextField.getText();
        if (isNotEqualOrIsEmpty(modelUserId, viewUserId))
            edit.setUserId(viewUserId);
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
            GenericPopup popup = new GenericPopup();
            popup.setMessageText("Are you sure you want to remove " + skill.getShortName() + " from " + edit.getShortName());
            popup.setTitleText("Remove Skill?");
            popup.setWindowTitle("Remove Skill from Person");
            popup.addOkCancelButtons(s -> {
                edit.removeSkill(skill);
                skillChoiceBox.getItems().add(skill);
                saveChanges();
                popup.close();
            });
            popup.show();
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
}
