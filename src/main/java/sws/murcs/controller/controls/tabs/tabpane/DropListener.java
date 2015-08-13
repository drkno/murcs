package sws.murcs.controller.controls.tabs.tabpane;

import javafx.scene.control.Tab;
import javafx.scene.input.DragEvent;

/**
 * A class that can be used to listen to drop events on
 * a DnDPane.
 */
public interface DropListener {
    void dropped(DragEvent event, Tab tab);
}
