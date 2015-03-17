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
import sws.project.exceptions.DuplicateObjectException;
import sws.project.model.Project;
import sws.project.model.RelationalModel;
import sws.project.model.persistence.PersistenceManager;
import sws.project.sampledata.PersonGenerator;
import sws.project.sampledata.SkillGenerator;
import sws.project.sampledata.TeamGenerator;
import sws.project.view.App;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
 * Controller for the project creator popup window.
 * Since there should only be one instance of this PopUp
 */
public class ProjectEditor implements Initializable {
    private Project project;

    @FXML
    private TextField textFieldShortName, textFieldLongName, descriptionTextField;

    @FXML
    private Label labelErrorMessage;

    private Callable<Void> onSaved;

    /**
     * Creates a new form for editing a project. It will add the project to
     * the model automatically as soon as it is valid
     * @return The form
     */
    public static Parent createNew(){
        return createFor(new Project());
    }

    /**
     * Creates a new form for editing a project with a callback that is called every time
     * a change is made
     * @param onSaved The callback
     * @return the Form
     */
    public static Parent createNew(Callable<Void> onSaved){
        return createFor(new Project(), null);
    }

    /**
     * Creates a new form for editing a project
     * @param project The project
     * @return The form
     */
    public static Parent createFor(Project project){
        return createFor(project, null);
    }

    /**
     * Creates a new form for editing a project which will call the saved callback
     * every time a change is saved
     * @param project The project
     * @param onSaved The save callback
     * @return The form
     */
    public static Parent createFor(Project project, Callable<Void> onSaved){
        try {
            FXMLLoader loader = new FXMLLoader(ProjectEditor.class.getResource("/sws/project/ProjectEditor.fxml"));
            AnchorPane anchorPane = loader.load();

            ProjectEditor controller = loader.getController();
            controller.project = project;
            controller.onSaved = onSaved;
            controller.loadProject();

            return anchorPane;
        }catch (Exception e){
            System.err.println("Unable to create a project editor!(this is seriously bad)");
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
     * Saves the project being edited
     */
    private void saveProject() {
        try {
            project.setShortName(textFieldShortName.getText());
            project.setLongName(textFieldLongName.getText());
            project.setDescription(descriptionTextField.getText());


            //This line will need to be changed if we support multiple projects
            //What we're trying to do here is check if the current project already exist
            //or if we're creating a new one.
            RelationalModel model= PersistenceManager.Current.getCurrentModel();
            if (model == null || model.getProject() != project) {
                model = new RelationalModel();
                model.setProject(project);

                PersistenceManager.Current.setCurrentModel(model);

                try {
                    model.addTeam((new TeamGenerator()).generate());

                    //Generate us some people
                    for (int i = 0; i < 10; ++i)
                        model.addPerson((new PersonGenerator()).generate());

                    //Generate us some skill
                    for (int i = 0; i < 10; ++i)
                        model.addSkill((new SkillGenerator()).generate());
                }catch (DuplicateObjectException e){

                }
            }

            //If we have a saved callBack, call it
            if (onSaved != null)
                onSaved.call();

        }catch (Exception e){
            labelErrorMessage.setText(e.getMessage());
            return;
        }
    }

    /**
     * Loads the project into the form
     */
    private void loadProject(){
        textFieldShortName.setText(project.getShortName());
        textFieldLongName.setText(project.getLongName());
        descriptionTextField.setText(project.getDescription());
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
