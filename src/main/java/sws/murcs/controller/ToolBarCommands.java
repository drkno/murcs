package sws.murcs.controller;

import javafx.event.ActionEvent;

/**
 * Interface that is implemented by any controller that is linked to the toolbar fxml.
 */
public interface ToolBarCommands {

    /**
     * The event to be fired when you click the back arrow on the toolbar.
     * @param event Clicking the back arrow on the toolbar.
     */
    void back(ActionEvent event);

    /**
     * The function to be called when you click the forward arrow on the keyboard.
     * @param event Clicking the forward arrow on the toolbar.
     */
    void forward(ActionEvent event);

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
     * The function to be called when you click the add button on the toolbar.
     * @param event Clicking either the add button on the toolbar or one of the menu items from the selection
     *              box.
     *              NOTE: make sure this function deals with the different menu items in some way or form. (i.e. works
     *              out which item was selected - see AppController add() for more details).
     */
    void add(ActionEvent event);

    void remove(ActionEvent event);

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
}
