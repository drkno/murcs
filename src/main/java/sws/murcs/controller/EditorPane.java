package sws.murcs.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import sws.murcs.controller.editor.GenericEditor;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates the editor Pane.
 */
public class EditorPane {

    /**
     * The controller for the editor.
     */
    private GenericEditor<Model> controller;

    /**
     * The Model to model.
     */
    private Model model;

    /**
     * The editor pane view.
     */
    private Parent view;

    /**
     * Creates a new Editor pane, and sets the model.
     * @param pModel The model to set
     */
    public EditorPane(final Model pModel) {
        if (pModel != null) {
            model = pModel;
            create();
        }
    }

    /**
     * Gets the view of the editor pane.
     * @return editor pane view
     */
    protected final Parent getView() {
        return view;
    }

    /**
     * Gets the model to model.
     * @return Model to model
     */
    protected final Model getModel() {
        return model;
    }

    /**
     * Gets the controller of the editor pane.
     * @return The editor pane controller.
     */
    protected final GenericEditor<Model> getController() {
        return controller;
    }

    /**
     * Creates the editor pane.
     */
    public final void create() {
        Map<ModelType, String> fxmlPaths = new HashMap<>();
        fxmlPaths.put(ModelType.Project, "ProjectEditor.fxml");
        fxmlPaths.put(ModelType.Team, "TeamEditor.fxml");
        fxmlPaths.put(ModelType.Person, "PersonEditor.fxml");
        fxmlPaths.put(ModelType.Skill, "SkillEditor.fxml");
        fxmlPaths.put(ModelType.Release, "ReleaseEditor.fxml");
        fxmlPaths.put(ModelType.Story, "StoryEditor.fxml");
        fxmlPaths.put(ModelType.Backlog, "BacklogEditor.fxml");

        String fxmlPath = "/sws/murcs/" + fxmlPaths.get(ModelType.getModelType(model));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            view = loader.load();
            controller = loader.getController();
            controller.setModel(model);
            controller.loadObject();
        }
        catch (Exception e) {
            ErrorReporter.get().reportError(e, "Unable to create editor");
        }
    }

    /**
     * Cleans up the editor pane.
     */
    public final void dispose() {
        controller.dispose();
        view = null;
        controller = null;
        model = null;
    }

    /**
     * Changes the model.
     * @param pModel the new model
     */
    public final void setModel(final Model pModel) {
        if (pModel != null) {
            model = pModel;
            controller.setModel(pModel);
            controller.loadObject();
        }
    }
}
