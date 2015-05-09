package sws.murcs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sws.murcs.model.Model;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.function.Consumer;

/**
 * Controller for the creation window.
 */
public class CreatorWindowController {

    /**
     * The main editorPane pane that contains all the
     * editable fields.
     */
    @FXML
    private GridPane contentPane;
    /**
     * The buttons in the create window.
     */
    @FXML
    private Button createButton, cancelButton;
    /**
     * The command to be issued on okay being clicked.
     */
    private Consumer<Model> createClicked;
    /**
     * The command to be issued on cancel being clicked.
     */
    private Consumer<Model> cancelClicked;
    /**
     * The stage of the creation window.
     */
    private Stage stage;
    /**
     * The model to be created.
     */
    private Model model;
    /**
     * The editor of the grid pane.
     */
    private EditorPane editorPane;

    /**
     * Empty Constructor for fxml creation.
     */
    public CreatorWindowController() {
    }

    /**
     * Sets the editor pane for the creation window controller.
     * @param editor The editor pane being used in the controller window.
     */
    public final void setEditorPane(final EditorPane editor) {
        editorPane = editor;
        contentPane.getChildren().add(editorPane.getView());
    }

    /**
     * Sets the model to create.
     * @param pModel Model to create.
     */
    public final void setModel(final Model pModel) {
        model = pModel;
    }

    /**
     * Sets the stage of the creation window.
     * @param pStage The Stage to set
     */
    public final void setStage(final Stage pStage) {
        stage = pStage;
    }

    /**
     * Sets the method that is called when cancel is clicked.
     * @param cancelCommand The method to call when cancel is clicked
     */
    public final void setCancelClicked(final Consumer<Model> cancelCommand) {
        cancelClicked = cancelCommand;
    }

    /**
     * Sets the method that is called when okay is clicked.
     * @param okayCommand The Event to notify
     */
    public final void setCreateClicked(final Consumer<Model> okayCommand) {
        createClicked = okayCommand;
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
        dispose();
    }

    /**
     * The function called on the okay button being clicked.
     * @param event The event that fires this function.
     */
    @FXML
    private void createButtonClicked(final ActionEvent event) {
        if (createClicked != null) {
            try {
                contentPane.requestFocus();
                //Save changes to the editor pane before proceeding
                editorPane.getController().saveChanges();
                Node node = JavaFXHelpers.getByID(contentPane.getParent(), "labelErrorMessage");
                if (node != null && node instanceof Label
                        && (!(((Label) node).getText() == null)
                        && !(((Label) node).getText().isEmpty()))) {
                    return;
                }
                if (model == null) {
                    return;
                }
                PersistenceManager.Current.getCurrentModel().add(model);
                createClicked.accept(model);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        stage.close();
        dispose();
    }

    /**
     * Cleans up references when closing the creation window.
     */
    public final void dispose() {
        createClicked = null;
        cancelClicked = null;
        contentPane = null;
        if (editorPane != null) {
            editorPane.dispose();
        }
        editorPane = null;
        model = null;
        stage = null;
    }
}
