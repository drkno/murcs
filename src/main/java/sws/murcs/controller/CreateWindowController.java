package sws.murcs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sws.murcs.EventNotification;
import sws.murcs.model.Model;

/**
 * Creates a new Controller with an Ok and Cancel button
 */
public class CreateWindowController {
    private ViewUpdate okayClicked;
    private EventNotification<Model> cancelClicked;

    private Model model;

    @FXML
    private GridPane contentPane;

    @FXML
    private void cancelButtonClicked(ActionEvent event) {
        GridPane pane = contentPane;
        if (cancelClicked != null) try {
            cancelClicked.eventNotification(model);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Stage stage = (Stage)contentPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void okayButtonClicked(ActionEvent event) {
        if (okayClicked != null) {
            try {
                Node node = JavaFXHelpers.getByID(contentPane.getParent(), "labelErrorMessage");
                if (node != null && node instanceof Label && (!(((Label) node).getText() == null) && !(((Label) node).getText().isEmpty())))
                    return;
                okayClicked.updateListView(model);
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
    public void setCancelClicked(EventNotification<Model> cancelClicked) {
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
    public static Parent newCreateNode(Node content, Model model, ViewUpdate okayClicked, EventNotification<Model> cancelClicked){
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
