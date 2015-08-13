package sws.murcs.controller.tabs;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import sws.murcs.controller.ToolBarController;
import sws.murcs.controller.windowManagement.Window;

/**
 * Indicates the object can be
 * used as a tab
 */
public interface Tabbable extends Navigable, ModelManagable{
    public void setToolBarController(ToolBarController toolBarController);

    public String getTitle();
    public Parent getRoot();
    public Object getController();
}
