package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import sws.murcs.internationalization.InternationalizationHelper;
import sws.murcs.search.tokens.BangCommand;
import sws.murcs.search.tokens.Token;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Controller for search commands of the search window.
 */
public class SearchCommandsController {

    /**
     * Helper text for search commands.
     */
    @FXML
    private Label helpText;

    /**
     * The Tile pane for each command tile.
     */
    @FXML
    private TilePane commandsTilePane;

    /**
     * The outer container of the search commands.
     */
    @FXML
    private GridPane commandsPane;

    /**
     * The search controller.
     */
    private SearchController searchController;

    /**
     * Empty constructor for JavaFX.
     */
    public SearchCommandsController() {
    }

    /**
     * Empty initialize for JavaFx.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets up the search commands controller.
     * @param pSearchController The search controller
     */
    public final void setup(final SearchController pSearchController) {
        searchController = pSearchController;
        loadCommands();
    }

    /**
     * Loads each command into the title pane.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void loadCommands() {
        Collection<BangCommand> commands = Arrays.asList(Token.getSpecialTokens());
        commands.stream().forEach(c -> {
            String longSyntax = c.getCommands()[0];
            String shortSyntax = c.getCommands()[1];
            Hyperlink commandLink1 = new Hyperlink(longSyntax);
            commandLink1.getStyleClass().add("zero-border");
            commandLink1.setTooltip(new Tooltip(InternationalizationHelper.tryGet("ClickMe")));
            setupAutoFill(commandLink1, longSyntax);
            Label orLabel = new Label(" {Or} ");
            Hyperlink commandLink2 = new Hyperlink(shortSyntax);
            commandLink2.getStyleClass().add("zero-border");
            commandLink2.setTooltip(new Tooltip(InternationalizationHelper.tryGet("ClickMe")));
            setupAutoFill(commandLink2, longSyntax);
            HBox hBox1 = new HBox();
            hBox1.setAlignment(Pos.CENTER);
            hBox1.getChildren().addAll(commandLink1, orLabel, commandLink2);

            Label description = new Label(c.getDescription());
            HBox hbox2 = new HBox();
            hbox2.setAlignment(Pos.CENTER);
            hbox2.getChildren().add(description);
            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER);
            vbox.getChildren().addAll(hBox1, hbox2);
            vbox.setPadding(new Insets(7.2));
            vbox.getStyleClass().add("search-command");
            commandsTilePane.getChildren().add(vbox);
        });
        helpText.setText(InternationalizationHelper.tryGet("SearchAdvancedCommands"));
        helpText.getStyleClass().add("search-help-text");
    }

    /**
     * If the label is clicked on it auto fills the search box with the command.
     * @param commandNode The command node.
     * @param command The command to fill the search box with.
     */
    private void setupAutoFill(final Node commandNode, final String command) {
        TextField searchText = searchController.searchText;
        commandNode.setOnMousePressed(event -> {
            if (searchText.getText() != null && !Objects.equals(searchText.getText(), "")) {
                searchText.setText(command + " " + searchText.getText());
            } else {
                searchText.setText(command + " ");
            }
            int length = searchText.getText().length();
            searchText.requestFocus();
            searchText.positionCaret(length);
        });
    }
}

