package sws.murcs.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.murcs.EventNotification;
import sws.murcs.model.Model;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    public static void createNew(Class<? extends Model> clazz, EventNotification<Model> updated){
        try {
            String type = ModelTypes.getModelType(clazz).toString();
            Model newModel = clazz.newInstance();

            // Works around, As you can't add multiple people at a time, only a single person
            // This is just the title of the popup dialog.
            if (Objects.equals(type, "People")) {
                type = "Person";
            }

            Node content = getEditForm(newModel, updated);
            Parent root = CreateWindowController.newCreateNode(content, newModel, updated, (model) -> {
                // TODO fix, this is not working as expected, newModel is always null
                // This is a place holder for a proper implementation
                PersistenceManager.Current.getCurrentModel().remove(newModel);
                updated.eventNotification(model);
            });
            Scene scene = new Scene(root);

            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.setTitle("Create " + type);

            newStage.setOnCloseRequest(event -> {
                // TODO fix, this is not working as expected, newModel is always null
                // This is a place holder for a proper implementation
                PersistenceManager.Current.getCurrentModel().remove(newModel);
                try {
                    updated.eventNotification(newModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                newStage.close();
            });

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(App.stage);

            newStage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Creates a new form for editing a team which will call the saved callback
     * every time a change is saved
     * @param model The model item to create
     * @param okayClicked The save callback
     * @return The form
     */
    public static Parent getEditForm(Model model, EventNotification<Model> okayClicked){
        Map<ModelTypes, String> fxmlPaths = new HashMap<>();
        fxmlPaths.put(ModelTypes.Project, "ProjectEditor.fxml");
        fxmlPaths.put(ModelTypes.Team, "TeamEditor.fxml");
        fxmlPaths.put(ModelTypes.People, "PersonEditor.fxml");
        fxmlPaths.put(ModelTypes.Skills, "SkillEditor.fxml");

        String fxmlPath = "/sws/murcs/" + fxmlPaths.get(ModelTypes.getModelType(model));

        try {
            FXMLLoader loader = new FXMLLoader(ProjectEditor.class.getResource(fxmlPath));
            Parent parent = loader.load();

            GenericEditor controller = loader.getController();
            controller.setEdit(model);
            controller.setSavedCallback(okayClicked);

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
