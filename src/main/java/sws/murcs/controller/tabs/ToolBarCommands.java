package sws.murcs.controller.tabs;

import javafx.event.ActionEvent;

/**
 * Interface that is implemented by any controller that is linked to the toolbar fxml.
 */
public interface ToolBarCommands {
    /**
     * The function to be called when you click the undo button on the toolbar.
     * @param event Clicking the undo button on the toolbar.
     */
    void undo(ActionEvent event);

    /**
     * The function to be called when you click the redo button.
     * @param event Clicking the redo button on the toolbar.
     */
    void redo(ActionEvent event);

    /**
     * The function to be called when you click the revert button on the toolbar.
     * @param event Clicking the return button on the toolbar.
     */
    void revert(ActionEvent event);

    /**
     * The function to be called when you click the generate report button on the toolbar.
     * @param event Clicking the generate report button on the toolbar.
     */
    void generateReport(ActionEvent event);

    /**
     * The function to be called when you click the report bug button on the toolbar.
     */
    void reportBug();

    /**
     * The function to be called when you click the save button on the toolbar.
     * @param event Clicking the save button on the toolbar.
     * @return Whether or not it saved successfully
     */
    boolean save(ActionEvent event);

    /**
     * The function to be called when you click the save as button on the toolbar.
     * @param event Clicking the save as button on the toolbar
     * @return Whether or not it saved successfully
     */
    boolean saveAs(ActionEvent event);

    /**
     * The function to be called when you click the open button on the toolbar.
     * @param event Clicking the open button on the toolbar.
     */
    void open(ActionEvent event);

    /**
     * The function to be called when you click the search button on the toolbar.
     * @param event Clicking the search button on the toolbar.
     */
    void search(ActionEvent event);
}
