package sws.murcs.controller.tabs;

import javafx.scene.Node;
import javafx.scene.Parent;
import sws.murcs.controller.ToolBarController;
import sws.murcs.controller.windowManagement.Window;

/**
 * Indicates the object can be
 * used as a tab
 */
public interface Tabbable extends Navigable{
    public void setToolBarController(ToolBarController toolBarController);

    public String getTitle();
    public Parent getRoot();
    public Object getController();
}
