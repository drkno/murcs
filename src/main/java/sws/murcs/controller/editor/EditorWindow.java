package sws.murcs.controller.editor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import sws.murcs.controller.ModelTypes;
import sws.murcs.model.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * 2/05/2015
 */
public class EditorWindow extends Parent {

    private GenericEditor controller;
    private Model model;
    private Parent view;

    public EditorWindow(Model model){
        this.model = model;
        create();
    }

    public Parent getView(){
        return this.view;
    }

    public Model getModel() {
        return this.model;
    }

    public void create() {
        Map<ModelTypes, String> fxmlPaths = new HashMap<>();
        fxmlPaths.put(ModelTypes.Project, "ProjectEditor.fxml");
        fxmlPaths.put(ModelTypes.Team, "TeamEditor.fxml");
        fxmlPaths.put(ModelTypes.People, "PersonEditor.fxml");
        fxmlPaths.put(ModelTypes.Skills, "SkillEditor.fxml");
        fxmlPaths.put(ModelTypes.Release, "ReleaseEditor.fxml");

        String fxmlPath = "/sws/murcs/" + fxmlPaths.get(ModelTypes.getModelType(model));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            view = loader.load();
            controller = loader.getController();
            controller.setEdit(model);
            controller.loadObject();
        }
        catch (Exception e) {
            System.err.println("Unable to create editor!(this is seriously bad)");
            e.printStackTrace();
        }
    }

    public void dispose() {
        controller.dispose();
        view = null;
        controller = null;
        model = null;
    }
}
