package sws.murcs.controller.controls;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import sws.murcs.controller.controls.md.MaterialDesignButton;
import sws.murcs.controller.controls.md.animations.FadeButtonOnHover;
import sws.murcs.controller.editor.GenericEditor;
import sws.murcs.listeners.RemoveCallback;
import sws.murcs.model.Model;
import sws.murcs.model.Story;

/**
 * A TableView cell that contains a link to the story it represents and a button to remove it.
 */
@SuppressWarnings("checkstyle:magicnumber")
public class RemovableHyperlinkCell extends TableCell<Story, String> {

    /**
     * The fixed height of rows in the table view for stories.
     */
    private static final Double FIXED_ROW_HEIGHT_STORY_TABLE = 30.0;

    /**
     * Editor the cell is contained within.
     */
    private GenericEditor<? extends Model> genericEditor;

    /**
     * Callback for when the story is removed.
     */
    private RemoveCallback<Story> removeCallback;

    /**
     * Creates a table view cell for a story with a button for removing it and a hyperlink to its name.
     * @param editor editor that this cell is contained within.
     * @param callback callback for removing the story.
     */
    public RemovableHyperlinkCell(final GenericEditor<? extends Model> editor, final RemoveCallback<Story> callback) {
        genericEditor = editor;
        removeCallback = callback;
    }

    @Override
    protected void updateItem(final String storyName, final boolean empty) {
        super.updateItem(storyName, empty);
        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
            setText(null);
            setGraphic(null);
        }
        else {
            Story story = (Story) getTableRow().getItem();
            AnchorPane container = new AnchorPane();
            if (genericEditor.getIsCreationWindow()) {
                Label name = new Label(story.getShortName());
                container.getChildren().add(name);
            } else {
                Hyperlink nameLink = new Hyperlink(story.getShortName());
                nameLink.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
                    if (e.isControlDown()) {
                        genericEditor.getNavigationManager().navigateToNewTab(story);
                    } else {
                        genericEditor.getNavigationManager().navigateTo(story);
                    }
                });
                container.getChildren().add(nameLink);
            }

            MaterialDesignButton button = new MaterialDesignButton(null);
            button.setPrefHeight(15);
            button.setPrefWidth(15);
            Image image = new Image("sws/murcs/icons/removeWhite.png");
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            imageView.setPreserveRatio(true);
            imageView.setPickOnBounds(true);
            button.setGraphic(imageView);
            button.getStyleClass().add("mdr-button");
            button.getStyleClass().add("mdrd-button");
            button.setOnAction(e -> removeCallback.removeItem(story));
            FadeButtonOnHover fadeButtonOnHover = new FadeButtonOnHover(button, getTableRow());
            fadeButtonOnHover.setupEffect();
            AnchorPane.setRightAnchor(button, 0.0);
            container.getChildren().add(button);

            container.setMaxHeight(FIXED_ROW_HEIGHT_STORY_TABLE);
            setGraphic(container);
            setAlignment(Pos.CENTER);
        }
    }
}
