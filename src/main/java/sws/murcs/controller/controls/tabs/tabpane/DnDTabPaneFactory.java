package sws.murcs.controller.controls.tabs.tabpane;

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import sws.murcs.controller.controls.tabs.markers.PositionMarker;
import sws.murcs.controller.controls.tabs.markers.TabOutlineMarker;
import sws.murcs.controller.controls.tabs.tabpane.skin.DnDTabPaneSkin;

/**
 * A modified version of the class found in this repository
 * https://github.com/sibvisions/javafx.DndTabPane
 *
 * Factory to create a tab pane who support DnD.
 */
public final class DnDTabPaneFactory {
    /**
     * A static instance of the feedback marker.
     */
	private static MarkerFeedback currentFeedback;

    /**
     * Private constructor, as this is a Factory
     * and should never be directly instantiated.
     */
	private DnDTabPaneFactory() {

	}

	/**
	 * Create a tab pane and set the drag strategy.
	 * @param setup the setup instance for the pane
	 * @return the tab pane
	 */
	public static DnDTabPane createDndTabPane(final Consumer<DragSetup> setup) {
		return new DnDTabPane() {
			@Override
			protected javafx.scene.control.Skin<?> createDefaultSkin() {
				DnDTabPaneSkin skin = new DnDTabPaneSkin(this);
				setup.accept(skin);
				return skin;
			}
		};
	}

	/**
	 * Create a tab pane with a default setup for drag feedback.
	 * @param feedbackType
	 *            the feedback type
	 * @param setup
	 *            consumer to set up the tab pane
	 * @return a pane containing the TabPane
	 */
	public static Pane createDefaultDnDPane(final FeedbackType feedbackType, final Consumer<TabPane> setup) {
		StackPane pane = new StackPane();
		DnDTabPane tabPane = new DnDTabPane() {
			@Override
			protected javafx.scene.control.Skin<?> createDefaultSkin() {
				DnDTabPaneSkin skin = new DnDTabPaneSkin(this);
				setup(feedbackType, pane, skin);

				return skin;
			}
		};

		if (setup != null) {
			setup.accept(tabPane);
		}

		pane.getChildren().add(tabPane);
		return pane;
	}

	/**
	 * Extract the tab content.
	 * @param e
	 *            the event
	 * @return the content
	 */
	public static boolean hasDnDContent(final DragEvent e) {
		return e.getDragboard().hasContent(DnDTabPaneSkin.TAB_MOVE);
	}

	/**
	 * Extract the content.
	 * @param e
	 *            the event
	 * @return the return value
	 */
	public static String getDnDContent(final DragEvent e) {
		return (String) e.getDragboard().getContent(DnDTabPaneSkin.TAB_MOVE);
	}

	/**
	 * Setup insert marker.
	 * @param type
	 *            the feedback type.
	 * @param layoutNode
	 *            the layout node used to position
	 * @param setup
	 *            the setup
	 */
	public static void setup(final FeedbackType type, final Pane layoutNode, final DragSetup setup) {
		setup.setStartFunction((t) ->
                Boolean.valueOf(!t.isDisabled() && ((DnDTabPane) t.getTabPane()).isDraggingEnabled()));
		setup.setFeedbackConsumer((d) -> handleFeedback(type, layoutNode, d));
		setup.setDropConsumer(DnDTabPaneFactory::handleDropped);
		setup.setDragFinishedConsumer(DnDTabPaneFactory::handleFinished);
	}

    /**
     * Fires the tab dragged event.
     * @param tabPane The tab pane
     * @param draggedTab The tab being dragged
     * @param fromIndex The position the tab is being dragged from
     * @param toIndex The index the tab is being dragged to
     */
	private static void fireTabDraggedEvent(final DnDTabPane tabPane, final Tab draggedTab,
                                            final int fromIndex, final int toIndex) {
		tabPane.fireTabDragged(draggedTab, fromIndex, toIndex);
	}

    /**
     * Handles the tab being dropped.
     * @param data The information about a drop
     */
	private static void handleDropped(final DroppedData data) {
		TabPane targetPane = data.targetTab.getTabPane();
		int oldIndex = data.draggedTab.getTabPane().getTabs().indexOf(data.draggedTab);
		data.draggedTab.getTabPane().getTabs().remove(data.draggedTab);
		int idx = targetPane.getTabs().indexOf(data.targetTab);
		if (data.dropType == DropType.AFTER) {
			if (idx + 1 <= targetPane.getTabs().size()) {
				targetPane.getTabs().add(idx + 1, data.draggedTab);
			} else {
				targetPane.getTabs().add(data.draggedTab);
			}
		} else {
			targetPane.getTabs().add(idx, data.draggedTab);
		}

		fireTabDraggedEvent((DnDTabPane) targetPane, data.draggedTab, oldIndex,
                targetPane.getTabs().indexOf(data.draggedTab));

		data.draggedTab.getTabPane().getSelectionModel().select(data.draggedTab);
	}

    /**
     * Handles the feedback marker for the user.
     * @param type The type of feedback
     * @param layoutNode The container of the tabpane
     * @param data The feedback data
     */
	private static void handleFeedback(final FeedbackType type, final Pane layoutNode, final FeedbackData data) {
		if (data.dropType == DropType.NONE) {
			cleanup();
			return;
		}

		MarkerFeedback f = currentFeedback;
		if (f == null || !f.data.equals(data)) {
			cleanup();
			if (type == FeedbackType.MARKER) {
				currentFeedback = handleMarker(layoutNode, data);
			} else {
				currentFeedback = handleOutline(layoutNode, data);
			}
		}
	}

    /**
     * Cleans up when finished dragging the tab.
     * @param tab The tab
     */
	private static void handleFinished(final Tab tab) {
		cleanup();
	}

    /**
     * Cleans up after a drag event.
     */
	static void cleanup() {
		if (currentFeedback != null) {
			currentFeedback.hide();
			currentFeedback = null;
		}
	}

    /**
     * Handles the feedback marker for a drag event.
     * @param layoutNode The container of the tab pane
     * @param data The feedback data
     * @return The marker
     */
	private static MarkerFeedback handleMarker(final Pane layoutNode, final FeedbackData data) {
		PositionMarker marker = null;
		for (Node n : layoutNode.getChildren()) {
			if (n instanceof PositionMarker) {
				marker = (PositionMarker) n;
			}
		}

		if (marker == null) {
			marker = new PositionMarker();
			marker.setManaged(false);
			layoutNode.getChildren().add(marker);
		} else {
			marker.setVisible(true);
		}

		double w = marker.getBoundsInLocal().getWidth();
		double h = marker.getBoundsInLocal().getHeight();

		double ratio = data.bounds.getHeight() / h;

		//CHECKSTYLE: OFF
        ratio += 0.1;
		//CHECKSTYLE: ON
        marker.setScaleX(ratio);
		marker.setScaleY(ratio);

		double wDiff = w / 2;
		double hDiff = (h - h * ratio) / 2;

		if (data.dropType == DropType.AFTER) {
			marker.relocate(data.bounds.getMinX()
                    + data.bounds.getWidth()
                    - wDiff, data.bounds.getMinY()
                    - hDiff);
		} else {
			marker.relocate(data.bounds.getMinX() - wDiff, data.bounds.getMinY() - hDiff);
		}

		final PositionMarker fmarker = marker;

		return new MarkerFeedback(data) {

			@Override
			public void hide() {
				fmarker.setVisible(false);
			}
		};
	}

    /**
     * Handles the outline marker feedback.
     * @param layoutNode The container node
     * @param data The feedback data
     * @return The feedback marker
     */
	private static MarkerFeedback handleOutline(final Pane layoutNode, final FeedbackData data) {
		TabOutlineMarker marker = null;

		for (Node n : layoutNode.getChildren()) {
			if (n instanceof TabOutlineMarker) {
				marker = (TabOutlineMarker) n;
			}
		}

		if (marker == null) {
			marker = new TabOutlineMarker(layoutNode.getBoundsInLocal(),
                    new BoundingBox(data.bounds.getMinX(),
                            data.bounds.getMinY(),
                            data.bounds.getWidth(),
                            data.bounds.getHeight()),
                    data.dropType == DropType.BEFORE);
			marker.setManaged(false);
			marker.setMouseTransparent(true);
			layoutNode.getChildren().add(marker);
		} else {
			marker.updateBounds(layoutNode.getBoundsInLocal(),
                    new BoundingBox(data.bounds.getMinX(),
                            data.bounds.getMinY(),
                            data.bounds.getWidth(),
                            data.bounds.getHeight()),
                    data.dropType == DropType.BEFORE);
			marker.setVisible(true);
		}

		final TabOutlineMarker fmarker = marker;

		return new MarkerFeedback(data) {

			@Override
			public void hide() {
				fmarker.setVisible(false);
			}
		};
	}

    /**
     * A class representing marker feedback.
     */
	@SuppressWarnings("CheckStyle")
	private abstract static class MarkerFeedback {
        /**
         * The feedback data.
         */
		public final FeedbackData data;

        /**
         * Creates a new marker from some feedback data.
         * @param data The data
         */
		public MarkerFeedback(final FeedbackData data) {
			this.data = data;
		}

        /**
         * Hides the feedback marker.
         */
		public abstract void hide();
	}

	/**
	 * The drop type.
	 */
	public enum DropType {
		/**
		 * No dropping.
		 */
		NONE,
		/**
		 * Dropped before a reference tab.
		 */
		BEFORE,
		/**
		 * Dropped after a reference tab.
		 */
		AFTER
	}

	/**
	 * The feedback type to use.
	 */
	public enum FeedbackType {
		/**
		 * Show a marker.
		 */
		MARKER,
		/**
		 * Show an outline.
		 */
		OUTLINE
	}

	/**
	 * Data to create a feedback.
	 */
	public static class FeedbackData {
		/**
		 * The tab dragged.
		 */
		public final Tab draggedTab;

		/**
		 * The reference tab.
		 */
		public final Tab targetTab;

		/**
		 * The bounds of the reference tab.
		 */
		public final Bounds bounds;

		/**
		 * The drop type.
		 */
		public final DropType dropType;

		/**
		 * Create a feedback data.
		 * @param draggedTab
		 *            the dragged tab
		 * @param targetTab
		 *            the reference tab
		 * @param bounds
		 *            the bounds of the reference tab
		 * @param dropType
		 *            the drop type
		 */
		public FeedbackData(final Tab draggedTab,
                            final Tab targetTab,
                            final Bounds bounds,
                            final DropType dropType) {
			this.draggedTab = draggedTab;
			this.targetTab = targetTab;
			this.bounds = bounds;
			this.dropType = dropType;
		}

		@Override
		public final int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.bounds == null) ? 0 : this.bounds.hashCode());
			result = prime * result + this.draggedTab.hashCode();
			result = prime * result + this.dropType.hashCode();
			result = prime * result + ((this.targetTab == null) ? 0 : this.targetTab.hashCode());
			return result;
		}

		@Override
		public final boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FeedbackData other = (FeedbackData) obj;
			if (this.bounds == null) {
				if (other.bounds != null)
					return false;
			} else if (!this.bounds.equals(other.bounds))
				return false;
			if (!this.draggedTab.equals(other.draggedTab))
				return false;
			if (this.dropType != other.dropType)
				return false;
			if (this.targetTab == null) {
				if (other.targetTab != null)
					return false;
			} else if (!this.targetTab.equals(other.targetTab))
				return false;
			return true;
		}

	}

	//CHECKSTYLE: OFF
	/**
	 * The drop data.
	 */
	public static class DroppedData {
		/**
		 * The dragged tab.
		 */
		public final Tab draggedTab;
		/**
		 * The reference tab.
		 */
		public final Tab targetTab;
		/**
		 * The drop type.
		 */
		public final DropType dropType;

		/**
		 * Create drop data.
		 * @param draggedTab
		 *            the dragged tab
		 * @param targetTab
		 *            the target tab
		 * @param dropType
		 *            the drop type
		 */
		public DroppedData(final Tab draggedTab, final Tab targetTab, final DropType dropType) {
			this.draggedTab = draggedTab;
			this.targetTab = targetTab;
			this.dropType = dropType;
		}
	}
	//CHECKSTYLE: ON

	/**
	 * Setup of the drag and drop.
	 */
	public interface DragSetup {
		/**
		 * Function to handle the starting of the the drag.
		 * @param startFunction
		 *            the function
		 */
		void setStartFunction(Function<Tab, Boolean> startFunction);

		/**
		 * Consumer called to handle the finishing of the drag process.
		 * @param dragFinishedConsumer
		 *            the consumer
		 */
		void setDragFinishedConsumer(Consumer<Tab> dragFinishedConsumer);

		/**
		 * Consumer called to present drag feedback.
		 * @param feedbackConsumer
		 *            the consumer to call
		 */
		void setFeedbackConsumer(Consumer<FeedbackData> feedbackConsumer);

		/**
		 * Consumer called when the drop has to be handled.
		 * @param dropConsumer
		 *            the consumer
		 */
		void setDropConsumer(Consumer<DroppedData> dropConsumer);

		/**
		 * Function to translate the tab content into clipboard content.
		 * @param clipboardDataFunction
		 *            the function
		 */
		void setClipboardDataFunction(Function<Tab, String> clipboardDataFunction);
	}
}
