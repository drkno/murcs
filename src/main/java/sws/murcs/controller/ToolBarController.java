package sws.murcs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ToolBarController {

    ToolBarCommands linkedController;

    public void setLinkedController(ToolBarCommands controller) {
        if (controller != null) {
            linkedController = controller;
        }
    }

    @FXML
    private void backButtonClick(ActionEvent event) {
        linkedController.back(event);
    }

    @FXML
    private void forwardButtonClick(ActionEvent event) {
        linkedController.forward(event);
    }

    @FXML
    private void undoButtonClick(ActionEvent event) {
        linkedController.undo(event);
    }

    @FXML
    private void redoButtonClick(ActionEvent event) {
        linkedController.redo(event);
    }

    @FXML
    private void revertButtonClick(ActionEvent event) {
        linkedController.revert(event);
    }

    @FXML
    private void addButtonClick(ActionEvent event) {
        linkedController.add(event);
    }

    @FXML
    private void saveButtonClick(ActionEvent event) {
        linkedController.save(event);
    }

    @FXML
    private void saveAsButtonClick(ActionEvent event) {
        linkedController.saveAs(event);
    }

    @FXML
    private void openButtonClick(ActionEvent event) {
        linkedController.open(event);
    }

    @FXML
    private void generateReportButtonClick(ActionEvent event) {
        linkedController.generateReport(event);
    }

    @FXML
    private void sendFeedbackButtonClick(ActionEvent event) {
        linkedController.reportBug();
    }
}
