package sws.murcs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Controller for the toolbar.
 */
public class ToolBarController {

    /**
     * The controller that is linked to the toolbar that manages all of the commands coming from the toolbar.
     * This would usually be the controller for the fxml that you are injecting the toolbar into.
     */
    ToolBarCommands linkedController;

    /**
     * Sets the linked controller for the toolbar.
     * @param controller The controller that is linked to the toolbar.
     */
    public final void setLinkedController(final ToolBarCommands controller) {
        if (controller != null) {
            linkedController = controller;
        }
    }

    /**
     * The function called when you click the back button. It redirects it through the linkedController.
     * @param event Clicking the back button.
     */
    @FXML
    private void backButtonClick(final ActionEvent event) {
        linkedController.back(event);
    }

    /**
     * The function called when you click the forward button. It redirects it through the linkedController.
     * @param event Clicking the forward button.
     */
    @FXML
    private void forwardButtonClick(final ActionEvent event) {
        linkedController.forward(event);
    }

    /**
     * The function called when you click the undo button. It redirects it through the linkedController.
     * @param event Clicking the undo button.
     */
    @FXML
    private void undoButtonClick(final ActionEvent event) {
        linkedController.undo(event);
    }

    /**
     * The function called when you click the redo button. It redirects it through the linkedController.
     * @param event Clicking the redo button.
     */
    @FXML
    private void redoButtonClick(final ActionEvent event) {
        linkedController.redo(event);
    }

    /**
     * The function called when you click the revert button. It redirects it through the linkedController.
     * @param event Clicking the revert button.
     */
    @FXML
    private void revertButtonClick(final ActionEvent event) {
        linkedController.revert(event);
    }

    /**
     * The function called when you click the add button. It redirects it through the linkedController.
     * @param event Clicking the add button.
     */
    @FXML
    private void addButtonClick(final ActionEvent event) {
        linkedController.add(event);
    }

    /**
     * The function called when you click the save as button. It redirects it through the linkedController.
     * @param event Clicking the save button.
     */
    @FXML
    private void saveButtonClick(final ActionEvent event) {
        linkedController.save(event);
    }

    /**
     * The function called when you click the save as button. It redirects it through the linkedController.
     * @param event Clicking the save as button.
     */
    @FXML
    private void saveAsButtonClick(final ActionEvent event) {
        linkedController.saveAs(event);
    }

    /**
     * The function called when you click the open button. It redirects it through the linkedController.
     * @param event Clicking the open button.
     */
    @FXML
    private void openButtonClick(final ActionEvent event) {
        linkedController.open(event);
    }

    /**
     * The function called when you click the generate report button. It redirects it through the linkedController.
     * @param event Clicking the generate report button.
     */
    @FXML
    private void generateReportButtonClick(final ActionEvent event) {
        linkedController.generateReport(event);
    }

    /**
     * The function called when you send feedback the back button. It redirects it through the linkedController.
     * @param event Clicking the send feedback button.
     */
    @FXML
    private void sendFeedbackButtonClick(final ActionEvent event) {
        linkedController.reportBug();
    }
}
