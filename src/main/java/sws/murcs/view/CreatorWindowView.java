package sws.murcs.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.murcs.controller.ModelTypes;
import sws.murcs.controller.CreatorWindowController;
import sws.murcs.controller.EditorPane;
import sws.murcs.listeners.ViewUpdate;
import sws.murcs.model.Model;

import java.util.Objects;
import java.util.function.Consumer;

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
    private ViewUpdate<Model> createAction;
    /**
     * The cancel callback.
     */
    private Consumer<Model> cancelAction;

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
    public final ViewUpdate<Model> getCreateAction() {
        return createAction;
    }

    /**
     * Sets the create callback.
     * @param newCreateAction The new create callback.
     */
    public final void setCreateAction(final ViewUpdate<Model> newCreateAction) {
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
                             final ViewUpdate<Model> pCreateAction,
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
            String type = ModelTypes.getModelType(model).toString();

            // Work around, As you can't add multiple people at a time, only a single person
            // This is just the title of the popup dialog.
            if (Objects.equals(type, "People")) {
                type = "Person";
            }

            // Load the view
            FXMLLoader loader = new FXMLLoader(CreatorWindowController
                    .class
                    .getResource("/sws/murcs/CreatorWindow.fxml"));
            Parent root = loader.load();

            // Set up the controller and give it the necessary parameters
            CreatorWindowController controller = loader.getController();
            controller.setModel(model);
            controller.setCreateClicked(createAction);
            controller.setCancelClicked(cancelAction);
            EditorPane editorPane = new EditorPane(model);
            controller.setEditorPane(editorPane);

            // Set up the stage
            Stage stage = new Stage();
            controller.setStage(stage);
            if (root == null) {
                return;
            }
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Give the stage a name and icon
            stage.setTitle("Create " + type);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo_small.png")));
            stage.getIcons().add(iconImage);

            // Set modality of the stage on top of the App
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(App.getStage());

            stage.show();
        }
        catch (Exception e) {
            System.err.println("Something went wrong loading the creation window");
            e.printStackTrace();
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
