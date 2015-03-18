package sws.project.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sws.project.model.Person;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Allows you to edit a edit
 */
public class PersonEditor extends GenericEditor<Person> implements Initializable{
    @FXML
    private TextField personNameTextField, usernameTextField;

    @FXML
    private Label labelErrorMessage;

    /**
     * Saves the edit being edited
     */
    private void savePerson() {

        try {
            labelErrorMessage.setText("");
            edit.setShortName(personNameTextField.getText());
            edit.setUserId(usernameTextField.getText());

            RelationalModel model= PersistenceManager.Current.getCurrentModel();

            //If we haven't added the edit yet, throw them in the list of unassigned people
            if (!model.getPeople().contains(edit))
                model.addPerson(edit);

            //If we have a saved callBack, call it
            if (onSaved != null)
                onSaved.call();

        }catch (Exception e){
            labelErrorMessage.setText(e.getMessage());
            return;
        }
    }

    /**
     * Loads the edit into the form
     */
    public void load(){
        personNameTextField.setText(edit.getShortName());
        usernameTextField.setText(edit.getUserId());
        savePerson();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        personNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) savePerson();
        });

        usernameTextField.focusedProperty().addListener((p, o, n) -> {
            if (o && !n)  savePerson();
        });
    }
}
