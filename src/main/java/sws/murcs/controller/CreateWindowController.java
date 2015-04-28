package sws.murcs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sws.murcs.listeners.ViewUpdate;
import sws.murcs.model.Model;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.function.Consumer;

/**
 * Creates a new Controller with an Ok and Cancel button
 */
public class CreateWindowController {
    private ViewUpdate okayClicked;
    private Consumer cancelClicked;

    private Model model;

    @FXML
    private GridPane contentPane;

    @FXML
    private void cancelButtonClicked(ActionEvent event) {
        GridPane pane = contentPane;
        cancelClicked.accept(null);
        Stage stage = (Stage)contentPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void okayButtonClicked(ActionEvent event) {
        if (okayClicked != null) {
            try {
                PersistenceManager.Current.getCurrentModel().add(model);
                Node node = JavaFXHelpers.getByID(contentPane.getParent(), "labelErrorMessage");
                if (node != null && node instanceof Label && (!(((Label) node).getText() == null) && !(((Label) node).getText().isEmpty())))
                    return;
                okayClicked.selectItem(model);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        Stage stage = (Stage)contentPane.getScene().getWindow();
        stage.close();
    }

    /**
     * Sets the method that is called when cancel is clicked
     * @param cancelClicked The method to call when cancel is clicked
     */
    public void setCancelClicked(Consumer cancelClicked) {
        this.cancelClicked = cancelClicked;
    }

    /**
     * Sets the method that is called when okay is clicked
     * @param okayClicked The Event to notify
     */
    public void setOkayClicked(ViewUpdate okayClicked) {
        this.okayClicked = okayClicked;
    }

    /**
     * Sets the content of the form
     * @param content The form
     */
    public void setContent(Node content){
        contentPane.getChildren().add(content);
    }

    /**
     * Sets the model of the form
     * @param model The model
     */
    public void setModel(Model model){
        this.model = model;
    }

    /**
     * Creates a new form for with the 'content' node as it's content
     * @param content The content
     * @param model The model
     * @param okayClicked The okay callback
     * @param cancelClicked The cancel callback
     * @return The form
     */
    public static Parent newCreateNode(Node content, Model model, ViewUpdate okayClicked, Consumer cancelClicked){
        try {
            FXMLLoader loader = new FXMLLoader(CreateWindowController.class.getResource("/sws/murcs/CreatorWindow.fxml"));
            Parent root = loader.load();

            CreateWindowController controller = loader.getController();
            controller.setOkayClicked(okayClicked);
            controller.setCancelClicked(cancelClicked);
            controller.setContent(content);
            controller.setModel(model);

            return root;
        }catch (Exception e){
            System.err.println("Unable to create a project editor!(this is seriously bad)");
            e.printStackTrace();
        }
        return null;
    }

}
