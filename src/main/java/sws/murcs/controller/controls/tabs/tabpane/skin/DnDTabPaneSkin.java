package sws.murcs.controller.controls.tabs.tabpane.skin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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

import com.sun.javafx.scene.control.skin.TabPaneSkin;
import sws.murcs.controller.controls.tabs.tabpane.DropListener;

import static sws.murcs.controller.controls.tabs.tabpane.DnDTabPaneFactory.*;

/**
 * A lightly modified version of the class found here:
 * https://github.com/sibvisions/javafx.DndTabPane
 *
 * Skin for TabPane which support DnD
 */
public class DnDTabPaneSkin extends TabPaneSkin implements DragSetup {
	private static Tab DRAGGED_TAB;
	/**
	 * Custom data format for move data
	 */
	public static final DataFormat TAB_MOVE = new DataFormat("DnDTabPane:tabMove"); //$NON-NLS-1$

	private Object noneEnum;
	private StyleableProperty<Object> openAnimation;
	private StyleableProperty<Object> closeAnimation;

	/**
	 * Create a new skin
	 * 
	 * @param tabPane
	 *            the tab pane
	 */
	public DnDTabPaneSkin(TabPane tabPane) {
		super(tabPane);
		hookTabFolderSkin();
	}

	@SuppressWarnings("unchecked")
	private void hookTabFolderSkin() {
		try {
			Field f_tabHeaderArea = TabPaneSkin.class.getDeclaredField("tabHeaderArea"); //$NON-NLS-1$
			f_tabHeaderArea.setAccessible(true);

			Pane tabHeaderArea = (StackPane) f_tabHeaderArea.get(this);
			tabHeaderArea.setOnDragOver((e) -> e.consume());

			Field f_headersRegion = tabHeaderArea.getClass().getDeclaredField("headersRegion"); //$NON-NLS-1$
			f_headersRegion.setAccessible(true);

			Pane headersRegion = (StackPane) f_headersRegion.get(tabHeaderArea);
			EventHandler<MouseEvent> handler = this::tabPane_handleDragStart;
			EventHandler<DragEvent> handlerFinished = this::tabPane_handleDragDone;

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

			tabHeaderArea.addEventHandler(DragEvent.DRAG_OVER, (e) -> tabPane_handleDragOver(tabHeaderArea, headersRegion, e));
			tabHeaderArea.addEventHandler(DragEvent.DRAG_DROPPED, (e) -> tabPane_handleDragDropped(tabHeaderArea, headersRegion, e));
			tabHeaderArea.addEventHandler(DragEvent.DRAG_EXITED, this::tabPane_handleDragDone);

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

	void tabPane_handleDragStart(MouseEvent event) {
		try {
			Field f_tab = event.getSource().getClass().getDeclaredField("tab"); //$NON-NLS-1$
			f_tab.setAccessible(true);
			Tab t = (Tab) f_tab.get(event.getSource());

			if (t != null && efx_canStartDrag(t)) {
				DRAGGED_TAB = t;
				Node node = (Node) event.getSource();
				Dragboard db = node.startDragAndDrop(TransferMode.MOVE);

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

				db.setDragView(image, image.getWidth(), image.getHeight() * -1);

				ClipboardContent content = new ClipboardContent();
				String data = efx_getClipboardContent(t);
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

	@SuppressWarnings("all")
	void tabPane_handleDragOver(Pane tabHeaderArea, Pane headersRegion, DragEvent event) {
		Tab draggedTab = DRAGGED_TAB;
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
					efx_dragFeedback(draggedTab, null, null, DropType.NONE);
					return;
				}

				Bounds b = referenceNode.getBoundsInLocal();
				b = referenceNode.localToScene(b);
				b = getSkinnable().sceneToLocal(b);

				efx_dragFeedback(draggedTab, tab, b, type);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			event.acceptTransferModes(TransferMode.MOVE);
		} else {
			efx_dragFeedback(draggedTab, null, null, DropType.NONE);
		}
	}

	@SuppressWarnings("all")
	void tabPane_handleDragDropped(Pane tabHeaderArea, Pane headersRegion, DragEvent event) {
		Tab draggedTab = DRAGGED_TAB;
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
				if( tab == null ) {
					event.setDropCompleted(false);
					return;
				} else if (tab == draggedTab) {
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

				if (!noMove) {
					StyleOrigin openOrigin = this.openAnimation.getStyleOrigin();
					StyleOrigin closeOrigin = this.closeAnimation.getStyleOrigin();
					Object openValue = this.openAnimation.getValue();
					Object closeValue = this.closeAnimation.getValue();
					try {
						this.openAnimation.setValue(this.noneEnum);
						this.closeAnimation.setValue(this.noneEnum);
						efx_dropped(draggedTab, tab, type);
						event.setDropCompleted(true);
					} finally {
						this.openAnimation.applyStyle(openOrigin, openValue);
						this.closeAnimation.applyStyle(closeOrigin, closeValue);
					}

				} else {
					event.setDropCompleted(false);
				}
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			event.consume();
		}
	}

	void tabPane_handleDragDone(DragEvent event) {
		Tab tab = DRAGGED_TAB;
		if (tab == null) {
			return;
		}

		for (DropListener listener : dropListeners) {
			listener.dropped(event, tab);
		}

		efx_dragFinished(tab);
	}

	private Function<Tab, Boolean> startFunction;
	private Consumer<Tab> dragFinishedConsumer;
	private List<DropListener> dropListeners = new ArrayList<>();
	private Consumer<FeedbackData> feedbackConsumer;
	private Consumer<DroppedData> dropConsumer;
	private Function<Tab, String> clipboardDataFunction;

	@Override
	public void setClipboardDataFunction(Function<Tab, String> clipboardDataFunction) {
		this.clipboardDataFunction = clipboardDataFunction;
	}

	@Override
	public void setStartFunction(Function<Tab, Boolean> startFunction) {
		this.startFunction = startFunction;
	}

	@Override
	public void setDragFinishedConsumer(Consumer<Tab> dragFinishedConsumer) {
		this.dragFinishedConsumer = dragFinishedConsumer;
	}

	@Override
	public void setFeedbackConsumer(Consumer<FeedbackData> feedbackConsumer) {
		this.feedbackConsumer = feedbackConsumer;
	}

	@Override
	public void setDropConsumer(Consumer<DroppedData> dropConsumer) {
		this.dropConsumer = dropConsumer;
	}

	/**
	 * Adds a drop listener to the list of listeners that will be fired when a tab is dropped.
	 * @param dropListener The drop listener to add.
	 */
	public void addDropListener(DropListener dropListener) {
		dropListeners.add(dropListener);
	}

	/**
	 * Removes a drop listener.
	 * @param dropListener The drop listener to remove
	 */
	public void removeDropListener(DropListener dropListener) {
		dropListeners.remove(dropListener);
	}

	/**
	 * Removes all drop listeners.
	 */
	public void clearDropListeners() {
		dropListeners.clear();
	}

	private boolean efx_canStartDrag(Tab tab) {
		if (this.startFunction != null) {
			return this.startFunction.apply(tab).booleanValue();
		}
		return true;
	}

	private void efx_dragFeedback(Tab draggedTab, Tab targetTab, Bounds bounds, DropType dropType) {
		if (this.feedbackConsumer != null) {
			this.feedbackConsumer.accept(new FeedbackData(draggedTab, targetTab, bounds, dropType));
		}
	}

	private void efx_dropped(Tab draggedTab, Tab targetTab, DropType dropType) {
		if (this.dropConsumer != null) {
			this.dropConsumer.accept(new DroppedData(draggedTab, targetTab, dropType));
		}
	}

	private void efx_dragFinished(Tab tab) {
		if (this.dragFinishedConsumer != null) {
			this.dragFinishedConsumer.accept(tab);
		}
	}

	private String efx_getClipboardContent(Tab t) {
		if (this.clipboardDataFunction != null) {
			return this.clipboardDataFunction.apply(t);
		}
		return System.identityHashCode(t) + ""; //$NON-NLS-1$
	}

}
