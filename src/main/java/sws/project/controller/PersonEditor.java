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
     * Creates a new form for editing a person. It will add the person to
     * the model automatically as soon as it is valid
     * @return The form
     */
    public static Parent createNew(){
        return createFor(new Person());
    }

    /**
     * Creates a new form for editing a person with a callback that is called every time
     * a change is made
     * @param onSaved The callback
     * @return the Form
     */
    public static Parent createNew(Callable<Void> onSaved){
        return createFor(new Person(), null);
    }

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
    public static Parent createFor(Person person, Callable<Void> onSaved){
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

    /**
     * Displays a new window for creating a new form
     * @param okay The okay callback
     * @param cancel The cancelled callback
     */
    public static void displayWindow(Callable<Void> okay, Callable<Void> cancel){
        Parent content = createNew();

        Parent root = CreateWindowController.newCreateNode(content, okay, cancel);
        Scene scene = new Scene(root);

        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.setTitle("Create Project");

        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.initOwner(App.stage);

        newStage.show();
    }

    /**
     * Saves the person being edited
     */
    private void savePerson() {

        try {
            person.setShortName(nameTextField.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        person.setLongName(usernameTextField.getText());

            RelationalModel model= PersistenceManager.Current.getCurrentModel();

            //If we haven't added the person yet, throw them in the list of unassigned people
            if (!model.getPeople().contains(person))
                model.getUnassignedPeople().add(person);

            //If we have a saved callBack, call it
            if (onSaved != null)
                try {
                    onSaved.call();
                } catch (Exception e) {
                    e.printStackTrace();
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