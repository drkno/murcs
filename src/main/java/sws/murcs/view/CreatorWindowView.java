package sws.murcs.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.murcs.controller.ModelTypes;
import sws.murcs.controller.editor.CreatorWindowController;
import sws.murcs.controller.editor.EditorPane;
import sws.murcs.listeners.ViewUpdate;
import sws.murcs.model.Model;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Sets up the creation window view
 */
public class CreatorWindowView {

    private final Model model;
    private final ViewUpdate<Model> createAction;
    private final Consumer<Model> cancelAction;

    /**
     * Sets up a new Creation window
     * @param model Model to set
     * @param createAction create action callback to set
     * @param cancelAction cancel action callback to set
     */
    public CreatorWindowView(Model model, ViewUpdate<Model> createAction, Consumer<Model> cancelAction) {
        this.model = model;
        this.createAction = createAction;
        this.cancelAction = cancelAction;
        create();
    }

    /**
     * Creates a new form for creating a new object of the specified type.
     */
    public void create() {
        try {
            String type = ModelTypes.getModelType(model).toString();

            // Work around, As you can't add multiple people at a time, only a single person
            // This is just the title of the popup dialog.
            if (Objects.equals(type, "People")) {
                type = "Person";
            }

            // Load the view
            FXMLLoader loader = new FXMLLoader(CreatorWindowController.class.getResource("/sws/murcs/CreatorWindow.fxml"));
            Parent root = loader.load();

            // Set up the controller and give it the necessary parameters
            CreatorWindowController controller = loader.getController();
            controller.setModel(model);
            controller.setCreateClicked(createAction);
            controller.setCancelClicked(cancelAction);
            controller.setContent(new EditorPane(model).getView());

            // Set up the stage
            Stage stage = new Stage();
            controller.setStage(stage);
            if (root == null) return;
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Give the stage a name and icon
            stage.setTitle("Create " + type);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo_small.png")));
            stage.getIcons().add(iconImage);

            // Set modality of the stage on top of the App
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(App.stage);

            stage.show();
        }
        catch (Exception e) {
            System.err.println("Something went wrong loading the creation window");
            e.printStackTrace();
        }
    }
}
