package sws.murcs.controller.controls.tabs.tabpane;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Tab;

/**
 * A modified version of the class from this repository:
 * https://github.com/sibvisions/javafx.DndTabPane
 *
 * The event that occurrs if a tab is dragged.
 */
@SuppressWarnings("serial")
public class TabDraggedEvent extends Event {
	private int fromIndex;
	private int toIndex;
	
	public static final EventType<TabDraggedEvent> TAB_DRAGGED = new EventType<>(Event.ANY, "TAB_DRAGGED");
	
	/**
	 * Creates a new instance of {@link TabDraggedEvent}.
	 * 
	 * @param draggedTab the dragged tab.
	 * @param fromIndex the from index.
	 * @param toIndex the to index.
	 */
	public TabDraggedEvent(Tab draggedTab, int fromIndex, int toIndex) {
		super(TAB_DRAGGED);
		
		this.source = draggedTab;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
	}
	
	/**
	 * Gets the dragged tab.
	 * 
	 * @return the dragged tab.
	 */
	public Tab getDraggedTab() {
		return (Tab) source;
	}
	
	/**
	 * Gets the from index.
	 * 
	 * @return the from index.
	 */
	public int getFromIndex() {
		return fromIndex;
	}
	
	/**
	 * Gets the to index.
	 * 
	 * @return the to index.
	 */
	public int getToIndex() {
		return toIndex;
	}
}
