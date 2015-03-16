package sws.project.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.project.model.Person;
import sws.project.model.Project;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;
import sws.project.view.App;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
 *
 */
public class PersonEditor {
    private Person person;

    @FXML
    TextField nameTextField, usernameTextField;

    @FXML
    Label labelErrorMessage;

    private Callable<Void> onSaved;

    /**
     * Creates a new form for editing a person. It will add the person to
     * the model automatically as soon as it is valid
     * @return The form
     */
    public static Parent createNew(){
        return createFor(new Project());
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
     * @param person
     * @param project The person
     * @return The form
     */
    public static Parent createFor(Person person, Project project){
        return createFor(project, null);
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
            FXMLLoader loader = new FXMLLoader(ProjectEditor.class.getResource("/sws/project/ProjectEdit.fxml"));
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
        try {
            Parent content = createNew();

            Parent root = CreateWindowController.newCreateNode(content, okay, cancel);
            Scene scene = new Scene(root);

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Create Project");

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(App.stage);

            newStage.show();
        }catch (Exception e){

        }
    }

    /**
     * Saves the person being edited
     */
    private void saveProject() {
        try {
            person.setShortName(nameTextField.getText());
            person.setLongName(usernameTextField.getText());

            RelationalModel model= PersistenceManager.Current.getCurrentModel();
            model.get

            //If we have a saved callBack, call it
            if (onSaved != null)
                onSaved.call();

        }catch (Exception e){
            labelErrorMessage.setText(e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    /**
     * Loads the person into the form
     */
    private void loadProject(){
        textFieldShortName.setText(person.getShortName());
        textFieldLongName.setText(person.getLongName());
        descriptionTextField.setText(person.getDescription());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldShortName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) saveProject();
        });

        textFieldLongName.focusedProperty().addListener((p, o, n) -> {
            if (o && !n)  saveProject();
        });

        descriptionTextField.focusedProperty().addListener((p, o, n) -> {
            if (o && !n)  saveProject();
        });
    }
}
