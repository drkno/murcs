package sws.murcs.controller;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Control;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;

/**
 * Controller for the toolbar.
 */
public class ToolBarController {

    /**
     * The back and forward buttons on the toolbar. Also the undo, redo and revert buttons.
     */
    @FXML
    private Button backButton, forwardButton, undoButton, redoButton, revertButton, removeButton;

    /**
     * The toolbar sections for the toolbar.
     */
    @FXML
    private HBox navigationToolBar, historyToolBar, editToolBar, reportingToolBar;

    /**
     * The overall container for the toolbar.
     */
    @FXML
    private ToolBar toolBar;

    /**
     * The controller that is linked to the toolbar that manages all of the commands coming from the toolbar.
     * This would usually be the controller for the fxml that you are injecting the toolbar into.
     */
    private ToolBarCommands linkedController;

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
     * The function called when you click the send feedback button. It redirects it through the linkedController.
     * @param event Clicking the send feedback button.
     */
    @FXML
    private void sendFeedbackButtonClick(final ActionEvent event) {
        linkedController.reportBug();
    }

    /**
     * The function called when you click the remove button. It redirects it through the linkedController.
     * @param event Clicking the remove button in the toolbar.
     */
    @FXML
    private void removeButtonClick(final ActionEvent event) {
        linkedController.remove(event);
    }

    /**
     * Toggles the state of the back and forward buttons if they disabled or enabled.
     */
    public final void updateBackForwardButtons() {
        backButton.setDisable(!NavigationManager.canGoBack());
        forwardButton.setDisable(!NavigationManager.canGoForward());
    }

    /**
     * Updates the undo button on the toolbar. Changing whether or not it is disabled and also changing the tooltip.
     * @param disabled Whether or not the button should be disabled.
     * @param tooltip The tooltip that should be displayed on the button.
     */
    public final void updateUndoButton(final boolean disabled, final String tooltip) {
        undoButton.setDisable(disabled);
        undoButton.getTooltip().setText(tooltip);
    }

    /**
     * Updates the redo button on the toolbar. Changing whether or not it is disabled and also changing the tooltip.
     * @param disabled Wether or not the button should be disabled.
     * @param tooltip The tooltip that should be displayed on the button.
     */
    public final void updateRedoButton(final boolean disabled, final String tooltip) {
        redoButton.setDisable(disabled);
        redoButton.getTooltip().setText(tooltip);
    }

    /**
     * Updates the revert button, depending on whether or not it should be enabled or disabled.
     * @param disabled Whether or not the button should be enabled or disabled.
     */
    public final void updateRevertButton(final boolean disabled) {
        revertButton.setDisable(disabled);
    }

    /**
     * Sets wether or not the remove button is disabled or not.
     * @param disabled Whether or not it is disabled.
     */
    public final void removeButtonDisabled(final boolean disabled) {
        removeButton.setDisable(disabled);
    }

    /**
     * Toggles the toolbar section based on the check menu item that you have clicked on. This hides or shows that
     * section on the toolbar. The code is slightly messy but it does work well.
     * @param event Clicking on a check menu item in the context menu for the toolbar.
     */
    @FXML
    private void toolBarToggle(final ActionEvent event) {
        CheckMenuItem menuItem = (CheckMenuItem) event.getSource();
        HBox associatedToolBar;
        switch (menuItem.getId()) {
            case "navigation": associatedToolBar = navigationToolBar; break;
            case "history": associatedToolBar = historyToolBar; break;
            case "edit": associatedToolBar = editToolBar; break;
            case "reporting": associatedToolBar = reportingToolBar; break;
            default: throw new UnsupportedOperationException("EXPLOSION!!!!!!!!!(unsupported toolbar)");
        }

        boolean showing = !associatedToolBar.visibleProperty().getValue();
        associatedToolBar.setVisible(showing);
        if (showing) {
            associatedToolBar.setPrefWidth(Control.USE_COMPUTED_SIZE);
        }
        else {
            associatedToolBar.setPrefWidth(0);
        }

        killThoseSeparators();
    }

    /**
     * As the name implies it kills the separators between the sections of the toolbar depending on which ones are
     * currently visible.
     */
    private void killThoseSeparators() {
        List<Node> toolBarItems = toolBar.getItems();

        for (int i = 0; i < toolBarItems.size(); ++i) {
            Node current = toolBarItems.get(i);

            boolean isHBox = current instanceof HBox;
            if (!isHBox) {
                continue;
            }

            Separator separator;
            if (i == toolBarItems.size() - 1) {
                if (!current.isVisible()) {
                    separator = (Separator) toolBarItems.get(i - 1);
                    separator.setVisible(false);
                    separator.setPrefWidth(0);
                }
                break;
            }

            separator = (Separator) toolBarItems.get(i + 1);
            if (!current.isVisible()) {
                separator.setVisible(false);
                separator.setPrefWidth(0);
            }
            else {
                separator.setVisible(true);
                separator.setPrefWidth(Control.USE_COMPUTED_SIZE);
            }
        }
    }
}
