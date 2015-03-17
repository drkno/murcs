package sws.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.concurrent.Callable;

/**
 * Creates a new Controller with an Ok and Cancel button
 */
public class CreateWindowController {
    private Callable<Void> okayClicked;
    private Callable<Void> cancelClicked;

    @FXML
    private GridPane contentPane;

    @FXML
    private void cancelButtonClicked(ActionEvent event) {
        if (cancelClicked != null) try {
            cancelClicked.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stage stage = (Stage)contentPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void okayButtonClicked(ActionEvent event) {
        if (okayClicked != null) try {
            okayClicked.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stage stage = (Stage)contentPane.getScene().getWindow();
        stage.close();
    }

    /**
     * Sets the method that is called when cancel is clicked
     * @param cancelClicked The method to call when cancel is clicked
     */
    public void setCancelClicked(Callable<Void> cancelClicked) {
        this.cancelClicked = cancelClicked;
    }

    /**
     * Sets the method that is called when okay is clicked
     * @param okayClicked The callable
     */
    public void setOkayClicked(Callable<Void> okayClicked) {
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
     * Creates a new form for with the 'content' node as it's content
     * @param content The content
     * @param okayClicked The okay callback
     * @param cancelClicked The cancel callback
     * @return The form
     */
    public static Parent newCreateNode(Node content, Callable<Void> okayClicked, Callable<Void> cancelClicked){
        try {
            FXMLLoader loader = new FXMLLoader(CreateWindowController.class.getResource("/sws/project/CreatorWindow.fxml"));
            Parent root = loader.load();

            CreateWindowController controller = loader.getController();
            controller.setOkayClicked(okayClicked);
            controller.setCancelClicked(cancelClicked);
            controller.setContent(content);

            return root;
        }catch (Exception e){
            System.err.println("Unable to create a project editor!(this is seriously bad)");
            e.printStackTrace();
        }
        return null;
    }

}
