package sws.murcs.controller.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import java.util.Optional;

/**
 * A searchable/autocomplete ComboBox to make finding items more achievable.
 * @param <T> type of the ComboBox.
 */
public class SearchableComboBox<T> extends ComboBox<T> {
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
     * Converter to convert string's into generics.
     */
    private StringConverter<T> converter;

    /**
     * Instantiates a new searchable ComboBox.
     */
    public SearchableComboBox() {
        data = getItems();
        setEditable(true);
        keyEvent = createKeyEventHandler();
        setOnKeyReleased(keyEvent);
        mouseEvent = createMouseEventHandler();
        getEditor().setOnMouseClicked(mouseEvent);
        converter = createStringConverter();
        setConverter(converter);
        setTooltip(new Tooltip(getPromptText()));
    }

    /**
     * Disposes of the current SearchableComboBox.
     */
    public final void dispose() {
        setOnKeyPressed(null);
        keyEvent = null;
        getEditor().setOnMouseClicked(null);
        mouseEvent = null;
        setConverter(null);
        converter = null;
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
            if (!isShowing()) {
                show();
            }
        };
    }

    /**
     * Creates a handler for key press events.
     * @return new key event handler.
     */
    private EventHandler<KeyEvent> createKeyEventHandler() {
        return event -> {
            if (!isShowing()) {
                show();
            }

            String text = getEditor().getText();
            KeyCode code = event.getCode();
            if (code == KeyCode.UP || code == KeyCode.DOWN || code == KeyCode.ENTER) {
                positionCursor(text.length());
                return;
            }
            else if ((code == KeyCode.TAB || code == KeyCode.ENTER || code == KeyCode.CONTROL)
                    && getItems().size() > 0) {
                getEditor().setText(getItems().get(0).toString());
                event.consume();
                return;
            }

            if (code == KeyCode.RIGHT || code == KeyCode.LEFT
                    || event.isControlDown() || code == KeyCode.HOME
                    || code == KeyCode.END) {
                return;
            }

            ObservableList list = FXCollections.observableArrayList();
            String typedInput = text.toLowerCase();
            data.forEach(d -> {
                if (d.toString().toLowerCase().contains(typedInput)) {
                    list.add(d);
                }
            });

            setItems(list);

            if (list.size() == 1 && code != KeyCode.DELETE) {
                text = list.get(0).toString();
                if (text.toLowerCase().startsWith(typedInput)) {
                    int pos = getEditor().getCaretPosition();
                    if (code == KeyCode.BACK_SPACE) {
                        pos -= 1;
                    }
                    getEditor().setText(text);
                    getEditor().selectRange(pos, text.length());
                }
            }
        };
    }

    /**
     * Moves the text cursor to a position.
     * @param position position of the cursor.
     */
    private void positionCursor(final int position) {
        getEditor().positionCaret(position);
    }
}
