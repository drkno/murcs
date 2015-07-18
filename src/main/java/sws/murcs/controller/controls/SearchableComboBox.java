package sws.murcs.controller.controls;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;

/**
 * A searchable/autocomplete ComboBox to make finding items more achievable.
 * @param <T> type of the ComboBox.
 */
public class SearchableComboBox<T> {

    /**
     * The ComboBox this affects.
     */
    private ComboBox<T> comboBox;

    /**
     * Data contained within the ComboBox.
     */
    private ObservableList<T> data;

    /**
     * Event called when a keyboard key is pressed.
     */
    private EventHandler<KeyEvent> keyEvent;

    /**
     * Event called when a mouse click occurs.
     */
    private EventHandler<MouseEvent> mouseEvent;

    /**
     * Event filter to ensure the tab key works.
     */
    private EventHandler<KeyEvent> tabConsumerEvent;

    /**
     * Converter to convert strings into generics.
     */
    private StringConverter<T> converter;

    /**
     * Index in the currently filtered list of the current tab selection.
     */
    private int tabIndex = -1;

    /**
     * Listener to listen for changes in focus.
     */
    private ChangeListener<? super Boolean> focusListener;

    /**
     * Instantiates a new searchable ComboBox.
     * @param aComboBox the ComboBox this affects.
     */
    public SearchableComboBox(final ComboBox<T> aComboBox) {
        comboBox = aComboBox;
        List<String> styles = aComboBox.getStylesheets();
        data = comboBox.getItems();
        comboBox.setEditable(true);
        keyEvent = createKeyEventHandler();
        comboBox.setOnKeyReleased(keyEvent);
        mouseEvent = createMouseEventHandler();
        comboBox.getEditor().setOnMouseClicked(mouseEvent);
        converter = createStringConverter();
        comboBox.setConverter(converter);
        comboBox.setTooltip(new Tooltip(comboBox.getPromptText()));
        tabConsumerEvent = createTabKeyFilter();
        comboBox.getEditor().addEventFilter(KeyEvent.KEY_PRESSED, tabConsumerEvent);
        focusListener = createFocusListener();
        comboBox.focusedProperty().addListener(focusListener);
        comboBox.getStyleClass().clear();
        comboBox.getStyleClass().addAll(styles);
        comboBox.getStyleClass().add("combo-box");
    }

    /**
     * Disposes of the current SearchableComboBox.
     */
    public final void dispose() {
        comboBox.setOnKeyPressed(null);
        keyEvent = null;
        comboBox.getEditor().setOnMouseClicked(null);
        mouseEvent = null;
        comboBox.setConverter(null);
        converter = null;
        comboBox.getEditor().removeEventFilter(KeyEvent.KEY_PRESSED, tabConsumerEvent);
        tabConsumerEvent = null;
        comboBox.focusedProperty().removeListener(focusListener);
        focusListener = null;
    }

    /**
     * Creates a listener for handling gain and loss of focus.
     * @return a new focus listener.
     */
    private ChangeListener<? super Boolean> createFocusListener() {
        return (observableValue, oldValue, newValue) -> {
            if (!comboBox.isFocused()) {
                comboBox.getEditor().setText(null);
                comboBox.setItems(data);
                comboBox.setVisibleRowCount(data.size());
            }
        };
    }

    /**
     * Creates a new event filter to ensure the tab key works.
     * @return new tab key event filter.
     */
    private EventHandler<KeyEvent> createTabKeyFilter() {
        return k -> {
            if (k.getCode() == KeyCode.TAB && comboBox.getItems().size() > 0) {
                tabIndex++;
                if (tabIndex >= comboBox.getItems().size()) {
                    tabIndex = 0;
                }
                String text = comboBox.getItems().get(tabIndex).toString();
                comboBox.getEditor().setText(text);
                comboBox.getEditor().positionCaret(text.length());
                k.consume();
            }
            else {
                tabIndex = -1;
            }
        };
    }

    /**
     * Creates a new string converter to convert from a string to a generic.
     * @return string converter.
     */
    private StringConverter<T> createStringConverter() {
        return new StringConverter<T>() {
            @Override
            public String toString(final T object) {
                if (object == null) {
                    return "";
                }
                return object.toString();
            }

            @Override
            public T fromString(final String string) {
                Optional stream = data.stream().filter(i -> i.toString().equalsIgnoreCase(string)).findFirst();
                if (stream.isPresent()) {
                    return (T) stream.get();
                }
                return null;
            }
        };
    }

    /**
     * Creates a new mouse click event handler.
     * @return new mouse click event handler.
     */
    private EventHandler<MouseEvent> createMouseEventHandler() {
        return event -> {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }
        };
    }

    /**
     * Creates a handler for key press events.
     * @return new key event handler.
     */
    private EventHandler<KeyEvent> createKeyEventHandler() {
        return event -> {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }

            String text = comboBox.getEditor().getText();
            KeyCode code = event.getCode();
            if (code == KeyCode.UP || code == KeyCode.DOWN || code == KeyCode.ENTER) {
                positionCursor(text.length());
                return;
            }
            else if ((code == KeyCode.TAB || code == KeyCode.ENTER || code == KeyCode.CONTROL)
                    && comboBox.getItems().size() > 0) {
                event.consume();
                return;
            }

            if (code == KeyCode.RIGHT || code == KeyCode.LEFT
                    || event.isControlDown() || code == KeyCode.HOME
                    || code == KeyCode.END) {
                return;
            }

            ObservableList<T> list = FXCollections.observableArrayList();
            String typedInput;
            if (text != null) {
                typedInput = text.toLowerCase();
            }
            else {
                typedInput = "";
            }
            data.forEach(d -> {
                if (d.toString().toLowerCase().contains(typedInput)) {
                    list.add(d);
                }
            });

            comboBox.setItems(list);
            comboBox.setVisibleRowCount(list.size());

            if (list.size() == 1 && code != KeyCode.DELETE) {
                text = list.get(0).toString();
                if (text.toLowerCase().startsWith(typedInput)) {
                    int pos = comboBox.getEditor().getCaretPosition();
                    if (code == KeyCode.BACK_SPACE) {
                        if (pos == 0) {
                            text = "";
                            comboBox.setItems(data);
                        }
                        else {
                            pos -= 1;
                        }
                    }
                    comboBox.getSelectionModel().select(null);
                    comboBox.getEditor().setText(text);
                    comboBox.getEditor().selectRange(pos, text.length());
                }
            }
        };
    }

    /**
     * Moves the text cursor to a position.
     * @param position position of the cursor.
     */
    private void positionCursor(final int position) {
        comboBox.getEditor().positionCaret(position);
    }

    /**
     * Adds an item to the searchable items.
     * @param item item to add.
     * @return if the item was successfully added.
     */
    public final boolean add(final T item) {
        boolean result = data.add(item);
        comboBox.getEditor().setText("");
        comboBox.setItems(data);
        comboBox.setVisibleRowCount(data.size());
        return result;
    }

    /**
     * Removes an item from the searchable items.
     * @param item item to remove.
     * @return if the item was successfully removed.
     */
    public final boolean remove(final T item) {
        boolean result = data.remove(item);
        comboBox.getEditor().setText("");
        comboBox.setItems(data);
        comboBox.setVisibleRowCount(data.size());
        return result;
    }
}
