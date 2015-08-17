package sws.murcs.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import sws.murcs.controller.editor.GenericEditor;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.view.App;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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
    @SuppressWarnings("checkstyle:magicnumber")
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
            // This is due to problems between java 8u25 and java 8u40
            if (App.JAVA_UPDATE_VERSION < 40 && !Thread.currentThread().getName().toLowerCase().contains("fx")) {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    try {
                        view = loader.load();
                        controller = loader.getController();
                        controller.setModel(model);
                        controller.loadObject();
                        latch.countDown();
                    } catch (Exception e) {
                        latch.countDown();
                        ErrorReporter.get().reportError(e, "Failed to load a new editor");
                    }
                });
                latch.await();
            }
            else {
                view = loader.load();
                controller = loader.getController();
                controller.setModel(model);
                controller.loadObject();
            }
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
    @SuppressWarnings("checkstyle:magicnumber")
    public final void setModel(final Model pModel) {
        if (pModel != null) {
            model = pModel;
            controller.setModel(pModel);
            // This is because "Java sucks" - Dion
            // "You guys are dicks" - Dion, Daniel, Jay
            // It's a bug somewhere in between java 8u25 and 8u40
            if (App.JAVA_UPDATE_VERSION < 40 && !Thread.currentThread().getName().toLowerCase().contains("fx")) {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    controller.loadObject();
                    latch.countDown();
                });
                try {
                    latch.await();
                } catch (Exception e1) {
                    ErrorReporter.get().reportError(e1, "Failed to load editor while retrying");
                }
            }
            else {
                controller.loadObject();
            }
        }
    }
}
