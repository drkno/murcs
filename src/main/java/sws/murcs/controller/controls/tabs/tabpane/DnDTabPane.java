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
public class DnDTabPane extends TabPane {
    /**
     * Indicates whether dragging is enabled for the tabpane.
     */
	private BooleanProperty draggingEnabled = new SimpleBooleanProperty(true);

    /**
     * A property representing the tagdragged event for the currently being dragged tab.
     */
	private ObjectProperty<EventHandler<TabDraggedEvent>> onTabDragged = new SimpleObjectProperty<>(null);

	/**
	 * Creates a new instance of {@link DnDTabPane}.
	 */
	public DnDTabPane() {
		super();
	}

	/**
	 * The property for enabling and disabling the dragging support.
	 * @return the property.
	 */
	public final BooleanProperty draggingEnabledProperty() {
		return draggingEnabled;
	}

	/**
	 * Fires the {@link TabDraggedEvent}.
	 * @param draggedTab the {@link Tab} that was dragged.
	 * @param fromIndex the index from which the {@link Tab} was dragged.
	 * @param toIndex the index to which the {@link Tab} was dragged.
	 */
	public final void fireTabDragged(final Tab draggedTab, final int fromIndex, final int toIndex) {
		TabDraggedEvent event = new TabDraggedEvent(draggedTab, fromIndex, toIndex);

		if (onTabDragged.get() != null) {
            onTabDragged.get().handle(event);
        }

		fireEvent(event);
	}

	/**
	 * Gets the event handler for the tab dragged event.
	 * @return the event handler.
	 */
	public final EventHandler<TabDraggedEvent> getOnTabDragged() {
		return onTabDragged.get();
	}

	/**
	 * Gets if the dragging of tabs is enabled.
	 * @return {@code true} if the dragging of tabs is enabled.
	 */
	public final boolean isDraggingEnabled() {
		return draggingEnabled.get();
	}

	/**
	 * Gets the property of the tab dragged event.
	 * @return the property for the tab dragged event.
	 */
	public final ObjectProperty<EventHandler<TabDraggedEvent>> onTabDragged() {
		return onTabDragged;
	}

	/**
	 * Sets if the dragging of tabs is enabled.
	 * @param enabled {@code true} if the dragging of tabs should be enabled.
	 */
	public final void setDraggingEnabled(final boolean enabled) {
		draggingEnabled.set(enabled);
	}

	/**
	 * Sets the event handler for the tab dragged event.
	 * @param value the event handle for the on tab dragged event.
	 */
	public final void setOnTabDragged(final EventHandler<TabDraggedEvent> value) {
		onTabDragged.setValue(value);
	}
}
