package sws.project.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.project.model.Model;
import sws.project.model.persistence.PersistenceManager;
import sws.project.view.App;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Provides helper methods for generating forms for editing and
 * creating new model objects
 */
public class EditorHelper {
    /**
     * Creates a new form for creating a new object of the specified type
     * @param clazz The type of object to create
     * @param updated Called when the object is successully updated
     */
    public static void createNew(Class<? extends Model> clazz, Callable<Void> updated){
        try {
            ModelTypes type = ModelTypes.getModelType(clazz);
            Model newModel = clazz.newInstance();

            Node content = getEditForm(newModel, updated);
            Parent root = CreateWindowController.newCreateNode(content, updated, () -> {
                PersistenceManager.Current.getCurrentModel().remove(newModel);
                updated.call();
                return null;
            });
            Scene scene = new Scene(root);

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Create " + type.toString());

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(App.stage);

            newStage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Creates a new form for editing a team which will call the saved callback
     * every time a change is saved
     * @param model The model item to create
     * @param onSaved The save callback
     * @return The form
     */
    public static Parent getEditForm(Model model, Callable<Void> onSaved){
        Map<ModelTypes, String> fxmlPaths = new HashMap<>();
        fxmlPaths.put(ModelTypes.Project, "ProjectEditor.fxml");
        fxmlPaths.put(ModelTypes.Team, "TeamEditor.fxml");
        fxmlPaths.put(ModelTypes.People, "PersonEditor.fxml");
        fxmlPaths.put(ModelTypes.Skills, "SkillEditor.fxml");

        String fxmlPath = "/sws/project/" + fxmlPaths.get(ModelTypes.getModelType(model));

        try {
            FXMLLoader loader = new FXMLLoader(ProjectEditor.class.getResource(fxmlPath));
            Parent parent = loader.load();

            GenericEditor controller = loader.getController();
            controller.setEdit(model);
            controller.setSavedCallback(onSaved);

            controller.load();

            return parent;
        }
        catch (Exception e){
            System.err.println("Unable to create editor!(this is seriously bad)");
            e.printStackTrace();
        }

        return null;
    }
}
