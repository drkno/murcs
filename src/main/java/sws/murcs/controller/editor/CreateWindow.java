package sws.murcs.controller.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sws.murcs.controller.JavaFXHelpers;
import sws.murcs.controller.ModelTypes;
import sws.murcs.listeners.ViewUpdate;
import sws.murcs.model.Model;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * 2/05/2015
 */
public class CreateWindow extends EditorWindow {

    /**
     * The command to be issued on okay being clicked.
     */
    private ViewUpdate createClicked;
    /**
     * The command to be issued on cancel being clicked.
     */
    private Consumer cancelClicked;

    /**
     * The main content pane that contains all the
     * editable fields.
     */
    @FXML
    private GridPane contentPane;

    @FXML
    private Button createButton, cancelButton;

    private CreateWindow controller;

    private Stage stage;

    public CreateWindow(Model model, ViewUpdate createClicked, Consumer cancelClicked) {
        super(model);
        this.createClicked = createClicked;
        this.cancelClicked = cancelClicked;
        show();
    }

    /**
     * The function called on the cancel button being clicked.
     * @param actionEvent The event that calls this function.
     */
    @FXML
    private void cancelButtonClicked(final ActionEvent actionEvent) {
        if (cancelClicked != null) {
            cancelClicked.accept(null);
        }
        stage.close();
        this.dispose();
    }

    /**
     * The function called on the okay button being clicked.
     * @param event The event that fires this function.
     */
    @FXML
    private void okayButtonClicked(final ActionEvent event) {
        if (createClicked != null) {
            try {
                contentPane.requestFocus();
                Node node = JavaFXHelpers.getByID(contentPane.getParent(), "labelErrorMessage");
                if (node != null && node instanceof Label && (!(((Label) node).getText() == null) && !(((Label) node).getText().isEmpty()))) {
                    return;
                }
                if (super.getModel() == null) {
                    return;
                }
                PersistenceManager.Current.getCurrentModel().add(super.getModel());
                createClicked.selectItem(super.getModel());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        stage.close();
        this.dispose();
    }

    /**
     * Sets the method that is called when cancel is clicked.
     * @param cancelCommand The method to call when cancel is clicked
     */
    public final void setCancelClicked(final Consumer cancelCommand) {
        this.cancelClicked = cancelCommand;
    }

    /**
     * Sets the method that is called when okay is clicked.
     * @param okayCommand The Event to notify
     */
    public final void setCreateClicked(final ViewUpdate okayCommand) {
        this.createClicked = okayCommand;
    }

    @Override
    public void dispose() {
        createClicked = null;
        cancelClicked = null;
        controller = null;
        contentPane = null;
        super.dispose();
    }

    /**
     * Creates a new form for creating a new object of the specified type.
     */
    public void show () {
        try {
            String type = ModelTypes.getModelType(super.getModel()).toString();

            // Works around, As you can't add multiple people at a time, only a single person
            // This is just the title of the popup dialog.
            if (Objects.equals(type, "People")) {
                type = "Person";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/CreatorWindow.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            controller.getChildren().add(super.getView());

            if (root == null) return;
            Scene scene = new Scene(root);

            stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Create " + type);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Image iconImage = new Image(classLoader.getResourceAsStream(("sws/murcs/logo_small.png")));
            stage.getIcons().add(iconImage);

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
