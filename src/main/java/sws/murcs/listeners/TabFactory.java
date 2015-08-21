package sws.murcs.listeners;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * An interface that allows for the creation of tabs.
 */
public interface TabFactory {
    /**
     * Creates a new tab for the specified tab pane.
     * @param tabPane The tab pane that tab will belong to
     * @return The new tab.
     */
    Tab createTab(TabPane tabPane);
}
