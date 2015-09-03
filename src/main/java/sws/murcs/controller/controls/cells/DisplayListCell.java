package sws.murcs.controller.controls.cells;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import sws.murcs.controller.controls.StoryProgressBar;
import sws.murcs.model.Model;
import sws.murcs.model.Story;

/**
 * A class representing a display list cell.
 */
public class DisplayListCell extends ListCell<Model> {
    /**
     * A container for the model currently being hovered over.
     * It's okay for this to be static, as we can only ever hover over
     * one item.
     */
    protected static Model hoveringOver;

    /**
     * The root node for the list cell.
     */
    protected VBox root = new VBox();

    /**
     * The label for the name of the story
     */
    protected Label nameLabel = new Label();

    /**
     * The progress bar for the story. Only displayed if the current model is a story.
     */
    protected StoryProgressBar progressBar = new StoryProgressBar();

    /**
     * The model item this cell points at.
     */
    protected Model model;

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

        root.getChildren().add(nameLabel);
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

        if (model instanceof Story) {
            progressBar.setStory((Story) model);

            if (!root.getChildren().contains(progressBar)) {
                root.getChildren().add(progressBar);
                progressBar.setPrefHeight(2);
                progressBar.setMaxHeight(2);
                progressBar.getRowConstraints().get(0).setMinHeight(2);
                progressBar.getRowConstraints().get(0).setPrefHeight(2);
                progressBar.getRowConstraints().get(0).setMaxHeight(2);
                root.setMargin(progressBar, new Insets(0, -5, 0, -5));

            }
        } else {
            root.getChildren().remove(progressBar);
        }

        nameLabel.textProperty().bind(model.getShortNameProperty());
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
