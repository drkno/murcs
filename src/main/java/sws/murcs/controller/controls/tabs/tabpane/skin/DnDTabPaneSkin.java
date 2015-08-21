package sws.murcs.controller.controls.tabs.tabpane.skin;

import com.sun.javafx.scene.control.skin.TabPaneSkin;
import java.awt.MouseInfo;
import java.awt.Point;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import sws.murcs.controller.controls.tabs.tabpane.DropListener;

import static sws.murcs.controller.controls.tabs.tabpane.DnDTabPaneFactory.DragSetup;
import static sws.murcs.controller.controls.tabs.tabpane.DnDTabPaneFactory.DropType;
import static sws.murcs.controller.controls.tabs.tabpane.DnDTabPaneFactory.DroppedData;
import static sws.murcs.controller.controls.tabs.tabpane.DnDTabPaneFactory.FeedbackData;

/**
 * A lightly modified version of the class found here:
 * https://github.com/sibvisions/javafx.DndTabPane
 *
 * Skin for TabPane which support DnD.
 */
public class DnDTabPaneSkin extends TabPaneSkin implements DragSetup {
	/**
	 * The Dragged Tab.
	 */
	private static Tab draggedTab;

	/**
	 * Custom data format for move data.
	 */
	public static final DataFormat TAB_MOVE = new DataFormat("DnDTabPane:tabMove"); //$NON-NLS-1$

	/**
	 * A none enum.
	 */
	private Object noneEnum;

	/**
	 * The open animation.
	 */
	private StyleableProperty<Object> openAnimation;

	/**
	 * The close animation.
	 */
	private StyleableProperty<Object> closeAnimation;

	/**
	 * The header area of the tab pane.
	 */
    private Pane tabHeaderArea;

	/**
	 * Create a new skin.
	 * @param tabPane
	 *            the tab pane
	 */
	public DnDTabPaneSkin(final TabPane tabPane) {
		super(tabPane);
		hookTabFolderSkin();
    }

	/**
	 * Adds the relevant listeners to the tab pane to allow drag and drop.
	 */
	@SuppressWarnings("unchecked")
	private void hookTabFolderSkin() {
		try {
			Field fTabHeaderArea = TabPaneSkin.class.getDeclaredField("tabHeaderArea"); //$NON-NLS-1$
			fTabHeaderArea.setAccessible(true);

			tabHeaderArea = (StackPane) fTabHeaderArea.get(this);
			tabHeaderArea.setOnDragOver((e) -> e.consume());

			Field fHeadersRegion = tabHeaderArea.getClass().getDeclaredField("headersRegion"); //$NON-NLS-1$
			fHeadersRegion.setAccessible(true);

			Pane headersRegion = (StackPane) fHeadersRegion.get(tabHeaderArea);
			EventHandler<MouseEvent> handler = this::tabPaneHandleDragStart;
			EventHandler<DragEvent> handlerFinished = this::tabPaneHandleDragDone;

			for (Node tabHeaderSkin : headersRegion.getChildren()) {
				tabHeaderSkin.addEventHandler(MouseEvent.DRAG_DETECTED, handler);
				tabHeaderSkin.addEventHandler(DragEvent.DRAG_DONE, handlerFinished);
			}

			headersRegion.getChildren().addListener((javafx.collections.ListChangeListener.Change<? extends Node> change) -> {
				while (change.next()) {
					if (change.wasRemoved()) {
						for (Node node : change.getRemoved()) {
							node.removeEventHandler(MouseEvent.DRAG_DETECTED, handler);
						}
						for (Node node : change.getRemoved()) {
							node.removeEventHandler(DragEvent.DRAG_DONE, handlerFinished);
						}
					}
					if (change.wasAdded()) {
						for (Node node : change.getAddedSubList()) {
							node.addEventHandler(MouseEvent.DRAG_DETECTED, handler);
						}
						for (Node node : change.getAddedSubList()) {
							node.addEventHandler(DragEvent.DRAG_DONE, handlerFinished);
						}
					}
				}
			});

			tabHeaderArea.addEventHandler(DragEvent.DRAG_OVER, (e) -> tabPaneHandleDragOver(tabHeaderArea, headersRegion, e));
			tabHeaderArea.addEventHandler(DragEvent.DRAG_DROPPED, (e) -> tabPaneHandleDragDropped(tabHeaderArea, headersRegion, e));
			tabHeaderArea.addEventHandler(DragEvent.DRAG_EXITED, this::tabPaneHandleDragDone);

			Field field = TabPaneSkin.class.getDeclaredField("openTabAnimation"); //$NON-NLS-1$
			field.setAccessible(true);
			this.openAnimation = (StyleableProperty<Object>) field.get(this);

			field = TabPaneSkin.class.getDeclaredField("closeTabAnimation"); //$NON-NLS-1$
			field.setAccessible(true);
			this.closeAnimation = (StyleableProperty<Object>) field.get(this);

			for (Class<?> cl : getClass().getDeclaredClasses()) {
				if ("TabAnimation".equals(cl.getSimpleName())) { //$NON-NLS-1$
					for (Enum<?> enumConstant : (Enum<?>[]) cl.getEnumConstants()) {
						if ("NONE".equals(enumConstant.name())) { //$NON-NLS-1$
							this.noneEnum = enumConstant;
							break;
						}
					}
					break;
				}

			}
		} catch (Throwable t) {
			// // TODO Auto-generated catch block
			t.printStackTrace();
		}
	}

	/**
	 * A method that handles a drag starting on the tab header.
	 * @param event The mouse event
	 */
	@SuppressWarnings("all")
	void tabPaneHandleDragStart(final MouseEvent event) {
		try {
			Field fTab = event.getSource().getClass().getDeclaredField("tab"); //$NON-NLS-1$
			fTab.setAccessible(true);
			Tab t = (Tab) fTab.get(event.getSource());
            if (!validateDrag(t)) {
                return;
            }

			if (t != null && efxCanStartDrag(t)) {
				draggedTab = t;
				Node node = (Node) event.getSource();
				Dragboard db = node.startDragAndDrop(TransferMode.MOVE);
				node.setOnDragDone(e -> {
                    fireDropListeners(e, t);
                });

				WritableImage snapShot = node.snapshot(new SnapshotParameters(), null);
				PixelReader reader = snapShot.getPixelReader();
				int padX = 10;
				int padY = 10;
				int width = (int) snapShot.getWidth();
				int height = (int) snapShot.getHeight();
				WritableImage image = new WritableImage(width + padX, height + padY);
				PixelWriter writer = image.getPixelWriter();

				int h = 0;
				int v = 0;
				while (h < width + padX) {
					v = 0;
					while (v < height + padY) {
						if (h >= padX && h <= width + padX && v >= padY && v <= height + padY) {
							writer.setColor(h, v, reader.getColor(h - padX, v - padY));
						} else {
							writer.setColor(h, v, Color.TRANSPARENT);
						}

						v++;
					}
					h++;
				}

				//TODO make a cooler screen shot
				db.setDragView(image, image.getWidth() * 0.5, 0);

				ClipboardContent content = new ClipboardContent();
				String data = efxGetClipboardContent(t);
				if (data != null) {
					content.put(TAB_MOVE, data);
				}
				db.setContent(content);
			}
		} catch (Throwable t) {
			// // TODO Auto-generated catch block
			t.printStackTrace();
		}
	}

	/**
	 * Handles a drag event that goes over the tab header area.
	 * @param tabHeaderArea The tab header area
	 * @param headersRegion The region containing the tab headers
	 * @param event The drag event.
	 */
	@SuppressWarnings("all")
	void tabPaneHandleDragOver(final Pane tabHeaderArea, final Pane headersRegion, final DragEvent event) {
		Tab draggedTab = DnDTabPaneSkin.draggedTab;
		if (draggedTab == null) {
			return;
		}

		// Consume the drag in any case
		event.consume();

		double x = event.getX() - headersRegion.getBoundsInParent().getMinX();

		Node referenceNode = null;
		DropType type = DropType.AFTER;
		for (Node n : headersRegion.getChildren()) {
			Bounds b = n.getBoundsInParent();
			if (b.getMaxX() > x) {
				if (b.getMinX() + b.getWidth() / 2 > x) {
					referenceNode = n;
					type = DropType.BEFORE;
				} else {
					referenceNode = n;
					type = DropType.AFTER;
				}
				break;
			}
		}

		if (referenceNode == null && headersRegion.getChildren().size() > 0) {
			referenceNode = headersRegion.getChildren().get(headersRegion.getChildren().size() - 1);
			type = DropType.AFTER;
		}

		if (referenceNode != null) {
			try {
				Field field = referenceNode.getClass().getDeclaredField("tab"); //$NON-NLS-1$
				field.setAccessible(true);
				Tab tab = (Tab) field.get(referenceNode);

				boolean noMove = false;
				if (tab == draggedTab) {
					noMove = true;
				} else if (type == DropType.BEFORE) {
					int idx = getSkinnable().getTabs().indexOf(tab);
					if (idx > 0) {
						if (getSkinnable().getTabs().get(idx - 1) == draggedTab) {
							noMove = true;
						}
					}
				} else {
					int idx = getSkinnable().getTabs().indexOf(tab);

					if (idx + 1 < getSkinnable().getTabs().size()) {
						if (getSkinnable().getTabs().get(idx + 1) == draggedTab) {
							noMove = true;
						}
					}
				}

				if (noMove) {
					efxDragFeedback(draggedTab, null, null, DropType.NONE);
					return;
				}

				Bounds b = referenceNode.getBoundsInLocal();
				b = referenceNode.localToScene(b);
				b = getSkinnable().sceneToLocal(b);

				efxDragFeedback(draggedTab, tab, b, type);
			} catch (Throwable e) {
				e.printStackTrace();
			}

			event.acceptTransferModes(TransferMode.MOVE);
		} else {
			efxDragFeedback(draggedTab, null, null, DropType.NONE);
		}
	}

	/**
	 * Handles a drop event on the tab header area.
	 * @param tabHeaderArea The header area
	 * @param headersRegion The region containing the tab headers
	 * @param event The drag event
	 */
	@SuppressWarnings("all")
	void tabPaneHandleDragDropped(final Pane tabHeaderArea, final Pane headersRegion, final DragEvent event) {
		Tab draggedTab = DnDTabPaneSkin.draggedTab;
		if (draggedTab == null) {
			return;
		}

		double x = event.getX() - headersRegion.getBoundsInParent().getMinX();

		Node referenceNode = null;
		DropType type = DropType.AFTER;
		for (Node n : headersRegion.getChildren()) {
			Bounds b = n.getBoundsInParent();
			if (b.getMaxX() > x) {
				if (b.getMinX() + b.getWidth() / 2 > x) {
					referenceNode = n;
					type = DropType.BEFORE;
				} else {
					referenceNode = n;
					type = DropType.AFTER;
				}
				break;
			}
		}

		if (referenceNode == null && headersRegion.getChildren().size() > 0) {
			referenceNode = headersRegion.getChildren().get(headersRegion.getChildren().size() - 1);
			type = DropType.AFTER;
		}

		if (referenceNode != null) {
			try {
				Field field = referenceNode.getClass().getDeclaredField("tab"); //$NON-NLS-1$
				field.setAccessible(true);
				Tab tab = (Tab) field.get(referenceNode);

				boolean noMove = false;
				if (tab == null) {
					event.setDropCompleted(false);
					return;
				}
				else if (tab == draggedTab) {
					noMove = true;
				}
				else if (type == DropType.BEFORE) {
					int idx = getSkinnable().getTabs().indexOf(tab);
					if (idx > 0) {
						if (getSkinnable().getTabs().get(idx - 1) == draggedTab) {
							noMove = true;
						}
					}
				}
				else {
					int idx = getSkinnable().getTabs().indexOf(tab);

					if (idx + 1 < getSkinnable().getTabs().size()) {
						if (getSkinnable().getTabs().get(idx + 1) == draggedTab) {
							noMove = true;
						}
					}
				}

				if (!noMove) {
					StyleOrigin openOrigin = this.openAnimation.getStyleOrigin();
					StyleOrigin closeOrigin = this.closeAnimation.getStyleOrigin();
					Object openValue = this.openAnimation.getValue();
					Object closeValue = this.closeAnimation.getValue();
					try {
						this.openAnimation.setValue(this.noneEnum);
						this.closeAnimation.setValue(this.noneEnum);
						efxDropped(draggedTab, tab, type);
						event.setDropCompleted(true);
					} finally {
						this.openAnimation.applyStyle(openOrigin, openValue);
						this.closeAnimation.applyStyle(closeOrigin, closeValue);
					}

				}
				else {
					event.setDropCompleted(false);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}

			event.consume();
		}
	}

	/**
	 * Handles a drag being finished.
	 * @param event The drag event
	 */
	void tabPaneHandleDragDone(final DragEvent event) {
		Tab tab = draggedTab;
		if (tab == null) {
			return;
		}

		efxDragFinished(tab);
	}

	/**
	 * A function that takes a tab and returns a boolean
	 * which indicates if a drag can be started.
	 */
	private Function<Tab, Boolean> startFunction;

	/**
	 * A consumer taking a tab that is fired when
	 * a drag finished and recieves the dragged
	 * tab as a parameter.
	 */
	private Consumer<Tab> dragFinishedConsumer;

	/**
	 * A list of listeners that are fired when a tab is dropped
	 * outside of the header area.
	 */
	private List<DropListener> dropListeners = new ArrayList<>();

	/**
	 * A list of predicates that determine whether a tab can be dragged.
	 * In order to be dragged all predicates must be true.
	 */
	private List<Predicate<Tab>> dragFilters = new ArrayList<>();

	/**
	 * A consumer of feedback that determines what the user feedback
	 * will look like.
	 */
	private Consumer<FeedbackData> feedbackConsumer;

	/**
	 * A dropped data consumer, dired when a tab is dropped.
	 */
	private Consumer<DroppedData> dropConsumer;

	/**
	 * A consumer that is fired when the drag completes.
	 */
	private Consumer<DragEvent> doneConsumer;

	/**
	 * A function that converts a tab into a string
	 * for use in the clip board.
	 */
	private Function<Tab, String> clipboardDataFunction;

	@Override
	public void setClipboardDataFunction(final Function<Tab, String> clipboardDataFunction) {
		this.clipboardDataFunction = clipboardDataFunction;
	}

	@Override
	public void setStartFunction(final Function<Tab, Boolean> startFunction) {
		this.startFunction = startFunction;
	}

	@Override
	public void setDragFinishedConsumer(final Consumer<Tab> dragFinishedConsumer) {
		this.dragFinishedConsumer = dragFinishedConsumer;
	}

	@Override
	public void setFeedbackConsumer(final Consumer<FeedbackData> feedbackConsumer) {
		this.feedbackConsumer = feedbackConsumer;
	}

	@Override
	public void setDropConsumer(final Consumer<DroppedData> dropConsumer) {
		this.dropConsumer = dropConsumer;
	}

    /**
     * Adds a drag filter. If any filter returns falst the tab will not be dragged.
     * @param filter The filter.
     */
    public void addDragFilter(final Predicate<Tab> filter) {
        dragFilters.add(filter);
    }

    /**
     * Removes a drag filter.
     * @param filter The filter
     */
    public void removeDragFilter(final Predicate<Tab> filter) {
        dragFilters.remove(filter);
    }

    /**
     * Ensures that a tab can be dragged.
     * @param tab The tab to drag
     * @return Whether the tab can be dragged.
     */
    private boolean validateDrag(final Tab tab) {
        return dragFilters.stream().allMatch(p -> p.test(tab));
    }

	/**
	 * Adds a drop listener to the list of listeners that will be fired when a tab is dropped.
	 * @param dropListener The drop listener to add.
	 */
	public void addDropListener(final DropListener dropListener) {
		dropListeners.add(dropListener);
	}

	/**
	 * Removes a drop listener.
	 * @param dropListener The drop listener to remove
	 */
	public void removeDropListener(final DropListener dropListener) {
		dropListeners.remove(dropListener);
	}

	/**
	 * Removes all drop listeners.
	 */
	public void clearDropListeners() {
		dropListeners.clear();
	}

	/**
	 * Fires all the drop listeners.
	 * @param event The drag event
	 * @param dropped The dropped tab
	 */
	private void fireDropListeners(final DragEvent event, final Tab dropped) {
        Bounds b = tabHeaderArea.getBoundsInLocal();
        b = tabHeaderArea.localToScreen(b);
        Point mousePoint = MouseInfo.getPointerInfo().getLocation();

        if (b.contains(mousePoint.getX(), mousePoint.getY())) {
            return;
        }

		for (DropListener listener : dropListeners) {
			listener.dropped(event, dropped);
		}
	}

	/**
	 * Determines whether a drag can be started for a specific tab.
	 * @param tab The tab
	 * @return Whether the tab can be dragged.
	 */
	private boolean efxCanStartDrag(final Tab tab) {
		if (this.startFunction != null) {
			return this.startFunction.apply(tab).booleanValue();
		}
		return true;
	}

	/**
	 * Handles feedback for a tab that is being dragged.
	 * @param draggedTab The tab being dragged
	 * @param targetTab The  tab that the dragged tab is being dropped onto
	 * @param bounds The bounds of the target tab
	 * @param dropType The type of drop
	 */
	private void efxDragFeedback(final Tab draggedTab, final Tab targetTab,
								 final Bounds bounds, final DropType dropType) {
		if (this.feedbackConsumer != null) {
			this.feedbackConsumer.accept(new FeedbackData(draggedTab, targetTab, bounds, dropType));
		}
	}

	/**
	 * Handles a tab being dropped onto another tab.
	 * @param draggedTab The tab being dropped
	 * @param targetTab The target
	 * @param dropType The type of drop
	 */
	private void efxDropped(final Tab draggedTab, final Tab targetTab, final DropType dropType) {
		if (this.dropConsumer != null) {
			this.dropConsumer.accept(new DroppedData(draggedTab, targetTab, dropType));
		}
	}

	/**
	 * Handles a drag finishing for a specific tab.
	 * @param tab The tab
	 */
	private void efxDragFinished(final Tab tab) {
		if (this.dragFinishedConsumer != null) {
			this.dragFinishedConsumer.accept(tab);
		}
	}

	/**
	 * Converts a tab into clipboard content.
	 * @param t The tab
	 * @return The clipboard content
	 */
	private String efxGetClipboardContent(final Tab t) {
		if (this.clipboardDataFunction != null) {
			return this.clipboardDataFunction.apply(t);
		}
		return System.identityHashCode(t) + ""; //$NON-NLS-1$
	}
}
