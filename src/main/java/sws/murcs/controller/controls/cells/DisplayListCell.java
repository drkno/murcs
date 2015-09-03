package sws.murcs.controller.controls.cells;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import sws.murcs.model.Model;

/**
 * A class representing a display list cell.
 */
public class DisplayListCell extends ListCell<Model> {
    /**
     * A container for the model currently being hovered over.
     * It's okay for this to be static, as we can only ever hover over
     * one item.
     */
    private static Model hoveringOver;

    /**
     * The root node for the list cell.
     */
    private Label root;

    /**
     * The model item this cell points at.
     */
    private Model model;

    /**
     * Initializes a new display list cell.
     */
    public DisplayListCell() {
        setOnMouseEntered(e -> {
            hoveringOver = model;
        });
        setOnMouseExited(e -> {
            if (model != null && model.equals(hoveringOver)) {
                hoveringOver = null;
            }
        });
    }

    @Override
    protected void updateItem(final Model model, final boolean empty) {
        this.model = model;
        super.updateItem(model, empty);
        if (model == null) {
            setText(null);
            setGraphic(null);
            if (this.model != null && this.model.equals(hoveringOver)) {
                hoveringOver = null;
            }
            return;
        }

        if (root == null) {
            root = new Label();
        }
        root.textProperty().bind(model.getShortNameProperty());

        setGraphic(root);
    }

    /**
     * The item that the mouse is hovering over.
     * @return The model item the mouse is hovering over
     */
    public static Model getHoveringOver() {
        return hoveringOver;
    }
}
