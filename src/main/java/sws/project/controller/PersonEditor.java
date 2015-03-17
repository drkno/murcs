package sws.project.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jdk.nashorn.internal.codegen.CompilerConstants;
import sws.project.model.Person;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;
import sws.project.view.App;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
 * Allows you to edit a person
 */
public class PersonEditor implements Initializable{
    private Person person;

    @FXML
    private TextField nameTextField, usernameTextField;

    @FXML
    private Label labelErrorMessage;

    private Callable<Void> onSaved;

    /**
     * Creates a new form for editing a person
     *
     * @param person The person
     * @return The form
     */
    public static Parent createFor(Person person){
        return createFor(person, null);
    }

    /**
     * Creates a new form for editing a person which will call the saved callback
     * every time a change is saved
     * @param person The person
     * @param onSaved The save callback
     * @return The form
     */
    private static Parent createFor(Person person, Callable<Void> onSaved, boolean autoSaving) {
        try {
            FXMLLoader loader = new FXMLLoader(ProjectEditor.class.getResource("/sws/project/PersonEditor.fxml"));
            AnchorPane anchorPane = loader.load();

            PersonEditor controller = loader.getController();
            controller.person = person;
            controller.onSaved = onSaved;
            controller.loadProject();

            return anchorPane;
        }catch (Exception e){
            System.err.println("Unable to create a person editor!(this is seriously bad)");
            e.printStackTrace();
        }

        return null;
    }

    public static Parent createFor(Person person, Callable<Void> onSaved) {
        return createFor(person, onSaved, true);
    }

    /**
     * Displays a new window for creating a new form
     * @param okay The okay callback
     * @param cancel The cancelled callback
     */
    public static void displayWindow(Callable<Void> okay, Callable<Void> cancel) {
        try {
            Parent content = createFor(new Person(), null, false);

            Parent root = CreateWindowController.newCreateNode(content, okay, cancel);
            Scene scene = new Scene(root);

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Create Project");

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(App.stage);

            newStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Saves the person being edited
     */
    private void savePerson() {
        try {
            person.setShortName(nameTextField.getText());
            person.setUserId(usernameTextField.getText());

            RelationalModel model= PersistenceManager.Current.getCurrentModel();

            //If we haven't added the person yet, throw them in the list of unassigned people
            if (!model.getPeople().contains(person))
                model.addPerson(person);

            //If we have a saved callBack, call it
            if (onSaved != null)
                onSaved.call();

        }catch (Exception e){
            labelErrorMessage.setText(e.getMessage());
            return;
        }
    }

    /**
     * Loads the person into the form
     */
    private void loadProject(){
        nameTextField.setText(person.getShortName());
        usernameTextField.setText(person.getUserId());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) savePerson();
        });

        usernameTextField.focusedProperty().addListener((p, o, n) -> {
            if (o && !n)  savePerson();
        });
    }
}
