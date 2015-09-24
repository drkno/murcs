package sws.murcs.view;

import java.util.function.Consumer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.murcs.controller.CreatorWindowController;
import sws.murcs.controller.EditorPane;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;

/**
 * Sets up the creation window view.
 */
public class CreatorWindowView {

    /**
     * The model object to create.
     */
    private Model model;

    /**
     * The create callback.
     */
    private Consumer<Model> createAction;

    /**
     * The cancel callback.
     */
    private Consumer<Model> cancelAction;

    /**
     * The minimum height of the application.
     */
    private final int minimumApplicationHeight = 577;

    /**
     * The minimum width of the application.
     */
    private final int minimumApplicationWidth = 300;

    /**
     * Gets the model.
     * @return The model.
     */
    public final Model getModel() {
        return model;
    }

    /**
     * Sets the model.
     * @param newModel The new model.
     */
    public final void setModel(final Model newModel) {
        model = newModel;
    }

    /**
     * Get the create callback.
     * @return The create callback.
     */
    public final Consumer<Model> getCreateAction() {
        return createAction;
    }

    /**
     * Sets the create callback.
     * @param newCreateAction The new create callback.
     */
    public final void setCreateAction(final Consumer<Model> newCreateAction) {
        createAction = newCreateAction;
    }

    /**
     * Gets the cancel callback.
     * @return The cancel callback.
     */
    public final Consumer<Model> getCancelAction() {
        return cancelAction;
    }

    /**
     * Sets the cancel callback.
     * @param newCancelAction The new cancel callback.
     */
    public final void setCancelAction(final Consumer<Model> newCancelAction) {
        cancelAction = newCancelAction;
    }

    /**
     * Sets up a new Creation window.
     * @param pModel Model to set
     * @param pCreateAction create action callback to set
     * @param pCancelAction cancel action callback to set
     */
    public CreatorWindowView(final Model pModel,
                             final Consumer<Model> pCreateAction,
                             final Consumer<Model> pCancelAction) {
        model = pModel;
        createAction = pCreateAction;
        cancelAction = pCancelAction;
    }

    /**
     * Creates a new form for creating a new object of the specified type.
     */
    public final void show() {
        try {
            String type = ModelType.getModelType(model).toString();

            // Load the view
            FXMLLoader loader = new AutoLanguageFXMLLoader(CreatorWindowController
                    .class
                    .getResource("/sws/murcs/CreatorWindow.fxml"));
            Parent root = loader.load();

            // Set up the controller and give it the necessary parameters
            CreatorWindowController controller = loader.getController();
            controller.setModel(model);
            controller.setCreateClicked(createAction);
            controller.setCancelClicked(cancelAction);
            EditorPane editorPane = new EditorPane(model, App.getMainController(), true);
            controller.setEditorPane(editorPane);

            // Set up the stage
            Stage stage = new Stage();
            stage.setResizable(true);
            stage.setMinHeight(minimumApplicationHeight);
            stage.setMinWidth(minimumApplicationWidth);
            controller.setStage(stage);
            if (root == null) {
                return;
            }
            Scene scene = new Scene(root);
            scene.getStylesheets()
                    .add(getClass()
                            .getResource("/sws/murcs/styles/global.css")
                            .toExternalForm());
            stage.setScene(scene);

            // Give the stage a name and icon
            stage.setTitle("Create " + type);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo/logo_small.png")));
            stage.getIcons().add(iconImage);

            // Set modality of the stage on top of the App
            stage.initModality(Modality.NONE);

            controller.setupWindow();
            controller.show();
            stage.sizeToScene();
        }
        catch (Exception e) {
            ErrorReporter.get().reportError(e, "Something went wrong loading the creation window");
        }
    }

    /**
     * Disposes of the creator view.
     */
    public final void dispose() {
        model = null;
        cancelAction = null;
        createAction = null;
    }
}
