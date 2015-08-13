package sws.murcs.controller.controls.tabs.tabpane;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * A lightly modified version of the class found here:
 * https://github.com/sibvisions/javafx.DndTabPane
 *
 * A simple extension of the {@link TabPane} that allows to disable dragging.
 */
public class DnDTabPane1 extends TabPane {
	private BooleanProperty draggingEnabled = new SimpleBooleanProperty(true);
	private ObjectProperty<EventHandler<TabDraggedEvent>> onTabDragged = new SimpleObjectProperty<>(null);
	
	/**
	 * Creates a new instance of {@link DnDTabPane1}.
	 */
	public DnDTabPane1() {
		super();
	}
	
	/**
	 * The property for enabling and disabling the dragging support.
	 * 
	 * @return the property.
	 */
	public BooleanProperty draggingEnabledProperty() {
		return draggingEnabled;
	}
	
	/**
	 * Fires the {@link TabDraggedEvent}.
	 * 
	 * @param draggedTab the {@link Tab} that was dragged.
	 * @param fromIndex the index from which the {@link Tab} was dragged.
	 * @param toIndex the index to which the {@link Tab} was dragged.
	 */
	public void fireTabDragged(Tab draggedTab, int fromIndex, int toIndex) {
		TabDraggedEvent event = new TabDraggedEvent(draggedTab, fromIndex, toIndex);
		
		if (onTabDragged.get() != null) {
			onTabDragged.get().handle(event);
		}
		
		fireEvent(event);
	}
	
	/**
	 * Gets the event handler for the tab dragged event.
	 * 
	 * @return the event handler.
	 */
	public EventHandler<TabDraggedEvent> getOnTabDragged() {
		return onTabDragged.get();
	}
	
	/**
	 * Gets if the dragging of tabs is enabled.
	 * 
	 * @return {@code true} if the dragging of tabs is enabled.
	 */
	public boolean isDraggingEnabled() {
		return draggingEnabled.get();
	}
	
	/**
	 * Gets the property of the tab dragged event.
	 * 
	 * @return the property for the tab dragged event.
	 */
	public ObjectProperty<EventHandler<TabDraggedEvent>> onTabDragged() {
		return onTabDragged;
	}
	
	/**
	 * Sets if the dragging of tabs is enabled.
	 * 
	 * @param enabled {@code true} if the dragging of tabs should be enabled.
	 */
	public void setDraggingEnabled(boolean enabled) {
		draggingEnabled.set(enabled);
	}
	
	/**
	 * Sets the event handler for the tab dragged event.
	 * 
	 * @param value the event handle for the on tab dragged event.
	 */
	public void setOnTabDragged(EventHandler<TabDraggedEvent> value) {
		onTabDragged.setValue(value);
	}
}
