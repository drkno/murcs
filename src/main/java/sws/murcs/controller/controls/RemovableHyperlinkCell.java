package sws.murcs.controller.controls;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import sws.murcs.controller.controls.md.MaterialDesignButton;
import sws.murcs.controller.controls.md.animations.FadeButtonOnHover;
import sws.murcs.controller.editor.GenericEditor;
import sws.murcs.internationalization.InternationalizationHelper;
import sws.murcs.listeners.ChangeCallback;
import sws.murcs.model.Model;
import sws.murcs.model.Story;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.List;

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
    private List<ChangeCallback<Story>> callbacks;

    /**
     * Creates a table view cell for a story with a button for removing it and a hyperlink to its name.
     * @param editor editor that this cell is contained within.
     * @param callback callback for removing the story.
     */
    public RemovableHyperlinkCell(final GenericEditor<? extends Model> editor, final List<ChangeCallback<Story>> callback) {
        genericEditor = editor;
        callbacks = callback;
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
            button.setOnAction(e -> callbacks.get(0).changeItem(story));
            FadeButtonOnHover fadeButtonOnHover = new FadeButtonOnHover(button, getTableRow());
            fadeButtonOnHover.setupEffect();
            AnchorPane.setRightAnchor(button, 0.0);
            container.getChildren().add(button);

            if (callbacks.size() == 2) {
                MaterialDesignButton otherButton = new MaterialDesignButton(null);
                otherButton.setPrefHeight(15);
                otherButton.setPrefWidth(15);
                Image image2;
                Tooltip tooltip;
                // I hate doing this here as it assumes that a story is only in one backlog, (which it should be anyway)
                // but yeah.
                // Please suggest a better solution in code review.
                if (PersistenceManager.getCurrent().getCurrentModel().getBacklogs()
                        .stream()
                        .filter(b -> b.getWorkspaceStories().contains(story))
                        .findFirst()
                        .isPresent()) {
                    image2 = new Image("sws/murcs/icons/minusWhite.png");
                    tooltip = new Tooltip(InternationalizationHelper.tryGet("Removestoryfromestimationworkspace"));
                }
                else {
                    image2 = new Image("sws/murcs/icons/addWhite.png");
                    tooltip = new Tooltip(InternationalizationHelper.tryGet("Addstorytoestimationworkspace"));
                }
                ImageView imageView2 = new ImageView(image2);
                imageView2.setFitHeight(20);
                imageView2.setFitWidth(20);
                imageView2.setPreserveRatio(true);
                imageView2.setPickOnBounds(true);
                otherButton.setTooltip(tooltip);
                otherButton.setGraphic(imageView2);
                otherButton.getStyleClass().add("mdr-button");
                otherButton.getStyleClass().add("mdge-button");
                otherButton.setOnAction(e -> callbacks.get(1).changeItem(story));
                AnchorPane.setRightAnchor(otherButton, 40.0);
                container.getChildren().add(otherButton);
            }

            container.setMaxHeight(FIXED_ROW_HEIGHT_STORY_TABLE);
            setGraphic(container);
            setAlignment(Pos.CENTER);
        }
    }
}
