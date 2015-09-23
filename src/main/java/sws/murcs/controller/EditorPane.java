package sws.murcs.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import sws.murcs.controller.editor.GenericEditor;
import sws.murcs.controller.pipes.Navigable;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.view.App;

/**
 * Creates the editor Pane.
 */
public class EditorPane {

    /**
     * Loads a different fzml for search and or creation type.
     */
    private Boolean isSearchOrCreation;

    /**
     * The controller for the editor.
     */
    private GenericEditor<Model> controller;

    /**
     * The Model to model.
     */
    private Model model;

    /**
     * The navigation manager.
     */
    private Navigable navigationManager;

    /**
     * The editor pane view.
     */
    private Parent view;

    /**
     * Better supported Java version.
     */
    private final int betterJavaVersion = 60;

    /**
     * Creates a new Editor pane, and sets the model.
     * @param pModel The model to set
     * @param navigationManager The navigation manager that the pane should make use of
     */
    public EditorPane(final Model pModel, final Navigable navigationManager) {
        this(pModel, navigationManager, false);
    }

    /**
     * Creates a new Editor pane, and sets the model.
     * @param pModel The model to set
     * @param navigationManager The navigation manager that the pane should make use of
     * @param pIsSearchOrCreation Loads a different fxml for search or creation window
     */
    public EditorPane(final Model pModel, final Navigable navigationManager, final Boolean pIsSearchOrCreation)  {
        this.navigationManager = navigationManager;
        isSearchOrCreation = pIsSearchOrCreation;
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
        fxmlPaths.put(ModelType.Person, "PersonEditor.fxml");
        fxmlPaths.put(ModelType.Skill, "SkillEditor.fxml");
        fxmlPaths.put(ModelType.Release, "ReleaseEditor.fxml");
        fxmlPaths.put(ModelType.Story, "StoryEditor.fxml");
        fxmlPaths.put(ModelType.Backlog, "BacklogEditor.fxml");
        if (isSearchOrCreation) {
            fxmlPaths.put(ModelType.Sprint, "SprintEditor.fxml");
            fxmlPaths.put(ModelType.Team, "TeamEditor.fxml");
        }
        else {
            fxmlPaths.put(ModelType.Sprint, "SprintContainer.fxml");
            fxmlPaths.put(ModelType.Team, "TeamContainer.fxml");
        }

        ModelType type = ModelType.getModelType(model);
        if (!fxmlPaths.containsKey(type)) {
            throw new UnsupportedOperationException("We don't seem to have that FXML yet. You should fix this");
        }

        String fxmlPath = "/sws/murcs/" + fxmlPaths.get(type);

        try {
            FXMLLoader loader = new AutoLanguageFXMLLoader(getClass().getResource(fxmlPath));
            // This is due to problems between java 8u25 and java 8u40
            if (App.JAVA_UPDATE_VERSION < betterJavaVersion
                    && !Thread.currentThread().getName().toLowerCase().contains("fx")) {
                CountDownLatch latch = new CountDownLatch(1);
                Platform.runLater(() -> {
                    try {
                        view = loader.load();
                        controller = loader.getController();
                        controller.setNavigationManager(navigationManager);
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
                controller.setNavigationManager(navigationManager);
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
    public final void setModel(final Model pModel) {
        if (pModel != null) {
            model = pModel;
            controller.setModel(pModel);
            // This is because "Java sucks" - Dion
            // "You guys are dicks" - Dion, Daniel, Jay
            // It's a bug somewhere in between java 8u25 and 8u40
            if (App.JAVA_UPDATE_VERSION < betterJavaVersion
                    && !Thread.currentThread().getName().toLowerCase().contains("fx")) {
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
