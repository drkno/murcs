package sws.murcs.controller.pipes;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import sws.murcs.controller.MainController;
import sws.murcs.controller.ToolBarController;

/**
 * Indicates the object can be
 * used as a tab.
 */
public interface Tabbable extends Navigable, ModelManagable{
    /**
     * Sets the tab that this tabbable exists within.
     * @param tab The tab
     */
    void setTab(Tab tab);

    /**
     * Gets the tab that the tabbable exists within.
     * @return The tab
     */
    Tab getTab();

    /**
     * Registers a MainController as the controller in charge of the Tabbable.
     * @param controller The new controller
     */
    void registerMainController(MainController controller);

    /**
     * Sets the toolbar controller associated with the tabbable.
     * @param toolBarController The toolbar controller
     */
    void setToolBarController(ToolBarController toolBarController);

    /**
     * A string property representing the title of the tab.
     * @return The title
     */
    SimpleStringProperty getTitle();

    /**
     * The root node for the tab.
     * @return The root node
     */
    Parent getRoot();

    /**
     * Used to toggle the sidebar.
     * @param sidebar Whether the sidebar should be shown
     */
    void toggleSideBar(boolean sidebar);

    /**
     * Indicates whether the sidebar is currently visible
     * @return Whether the side bar is visible
     */
    boolean sideBarVisible();

    /**
     * Indicates whether the sidebar can be toggled.
     * @return Whether the sidebar can be toggled.
     */
    boolean canToggleSideBar();
}
