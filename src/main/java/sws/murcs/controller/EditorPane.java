package sws.murcs.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import sws.murcs.controller.editor.Editor;
import sws.murcs.model.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates the editor Pane.
 */
public class EditorPane {

    /**
     * The controller for the editor.
     */
    private Editor<Model> controller;
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
            this.model = pModel;
            create();
        }
    }

    /**
     * Gets the view of the editor pane.
     * @return editor pane view
     */
    public final Parent getView() {
        return this.view;
    }

    /**
     * Gets the model to model.
     * @return Model to model
     */
    public final Model getModel() {
        return this.model;
    }

    /**
     * Creates the editor pane.
     */
    public final void create() {
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
            controller.setModel(model);
            controller.loadObject();
        }
        catch (Exception e) {
            System.err.println("Unable to create editor! (this is seriously bad)");
            e.printStackTrace();
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
            this.model = pModel;
            controller.setModel(pModel);
            controller.loadObject();
        }
    }
}
