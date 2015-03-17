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
     * Sets the the cancel Callable
     * @param cancelClicked to set to cancel Callable
     */
    public void setCancelClicked(Callable<Void> cancelClicked) {
        this.cancelClicked = cancelClicked;
    }

    /**
     * Sets the ok Callable
     * @param okayClicked to set to ok Callabel
     */
    public void setOkayClicked(Callable<Void> okayClicked) {
        this.okayClicked = okayClicked;
    }

    /**
     * Sets the content of the pane
     * @param content content to add to the pane
     */
    public void setContent(Node content){
        contentPane.add(content, 0, 0);
    }

    /**
     * Creates a new Node
     * @param content content of the node
     * @param okayClicked ok button action
     * @param cancelClicked cancel button action
     * @return Parent
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
