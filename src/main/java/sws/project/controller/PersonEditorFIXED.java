package sws.project.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sws.project.model.Person;
import sws.project.model.Skill;

/**
 *
 */
public class PersonEditorFIXED extends GenericEditor<Person> {

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

    }

    private void savePerson() {

    }
}
