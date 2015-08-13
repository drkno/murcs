package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
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
     * The Tile pane for each command tile.
     */
    @FXML
    private TilePane commandsTitlePane;

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
        loadCommands(Token.getSpecialTokens());
    }

    /**
     * Loads each command into the title pane.
     * @param commandsArray The commands to load
     */
    @SuppressWarnings("checkstyle:magicnumber")
    private void loadCommands(final BangCommand[] commandsArray) {
        Collection<BangCommand> commands = Arrays.asList(commandsArray);
        commands.stream().forEach(c -> {
            Label commandLabel = new Label();
            String longSyntax = c.getCommands()[0];
            String shortSyntax = c.getCommands()[1];
            commandLabel.setText(longSyntax + " or " + shortSyntax + "\n" + c.getDescription());
            commandLabel.setPadding(new Insets(10));
            commandLabel.getStyleClass().add("search-command");
            commandLabel.setTooltip(new Tooltip("Click me :)"));
            setupAutoFill(commandLabel, longSyntax);
            commandsTitlePane.getChildren().add(commandLabel);
        });
    }

    /**
     * If the label is click on it auto fills the search box with the command.
     * @param commandLabel The command label.
     * @param command The command to fill the search box with.
     */
    private void setupAutoFill(final Label commandLabel, final String command) {
        TextField searchText = searchController.searchText;
        commandLabel.setOnMousePressed(event -> {
            if (searchText.getText() != null && !Objects.equals(searchText.getText(), "")) {
                searchText.setText(command + " " + searchText.getText());
            }
            else {
                searchText.setText(command + " ");
            }
            int length = searchText.getText().length();
            searchText.positionCaret(length);
        });
    }
}

