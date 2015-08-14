package sws.murcs.controller.pipes;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import sws.murcs.controller.MainController;
import sws.murcs.controller.ToolBarController;

/**
 * Indicates the object can be
 * used as a tab.
 */
public interface Tabbable extends Navigable, ModelManagable{
    /**
     * Registers a MainController as the controller in charge of the Tabbable.
     * @param controller The new controller
     */
    public void registerMainController(MainController controller);

    /**
     * Sets the toolbar controller associated with the tabbable.
     * @param toolBarController
     */
    public void setToolBarController(ToolBarController toolBarController);

    /**
     * A string property representing the title of the tab.
     * @return The title
     */
    public SimpleStringProperty getTitle();

    /**
     * The root node for the tab.
     * @return The root node
     */
    public Parent getRoot();
}
