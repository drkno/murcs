package sws.murcs.controller.editor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sws.murcs.controller.JavaFXHelpers;
import sws.murcs.listeners.ViewUpdate;
import sws.murcs.model.Model;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.function.Consumer;

/**
 * Creates a new Controller with an Ok and Cancel button.
 */
public class CreateWindowController {

    /**
     * The command to be issued on okay being clicked.
     */
    private ViewUpdate okayClicked;
    /**
     * The command to be issued on cancel being clicked.
     */
    private Consumer cancelClicked;

    /**
     * The newModel that the window is being created for.
     */
    private Model model;

    /**
     * The main content pane that contains all the
     * editable fields.
     */
    @FXML
    private GridPane contentPane;

    /**
     * The function called on the cancel button being clicked.
     * @param actionEvent The event that calls this function.
     */
    @FXML
    private void cancelButtonClicked(final ActionEvent actionEvent) {
        GridPane pane = contentPane;
        if (cancelClicked != null) {
            cancelClicked.accept(null);
        }
        Stage stage = (Stage) contentPane.getScene().getWindow();
        stage.close();
    }

    /**
     * The function called on the okay button being clicked.
     * @param event The event that fires this function.
     */
    @FXML
    private void okayButtonClicked(final ActionEvent event) {
        if (okayClicked != null) {
            try {
                contentPane.requestFocus();
                Node node = JavaFXHelpers.getByID(contentPane.getParent(), "labelErrorMessage");
                if (node != null && node instanceof Label && (!(((Label) node).getText() == null) && !(((Label) node).getText().isEmpty()))) {
                    return;
                }
                if (model == null) {
                    return;
                }
                PersistenceManager.Current.getCurrentModel().add(model);
                okayClicked.selectItem(model);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        Stage stage = (Stage) contentPane.getScene().getWindow();
        stage.close();
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
    public final void setOkayClicked(final ViewUpdate okayCommand) {
        this.okayClicked = okayCommand;
    }

    /**
     * Sets the content of the form.
     * @param content The form
     */
    public final void setContent(final Node content){
        contentPane.getChildren().add(content);
    }

    /**
     * Sets the newModel of the form.
     * @param newModel The newModel
     */
    public final void setModel(final Model newModel){
        this.model = newModel;
    }

    public void dispose(){
        this.cancelClicked = null;
        this.okayClicked = null;
        this.model = null;
    }

    /**
     * Creates a new form for with the 'content' node as it's content.
     * @param content The content
     * @param model The newModel
     * @param okayClicked The okay callback
     * @param cancelClicked The cancel callback
     * @return The form
     */
    public static Parent newCreateNode(final Node content, final Model model, final ViewUpdate okayClicked, final Consumer cancelClicked) {
        try {
            FXMLLoader loader = new FXMLLoader(CreateWindowController.class.getResource("/sws/murcs/CreatorWindow.fxml"));
            Parent root = loader.load();

            CreateWindowController controller = loader.getController();
            controller.setOkayClicked(okayClicked);
            controller.setCancelClicked(cancelClicked);
            controller.setContent(content);
            controller.setModel(model);

            return root;
        } catch (Exception e) {
            System.err.println("Unable to create a model editor! Reference the stack trace for the cause and inform the developers.");
            e.printStackTrace();
        }
        return null;
    }

}
