package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import sws.murcs.search.tokens.BangCommand;
import sws.murcs.search.tokens.Token;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Created by Dion on 13/08/2015.
 */
public class SearchCommandsController {

    @FXML
    private TilePane commandsTitlePane;
    @FXML
    private GridPane commandsPane;
    private SearchController searchController;

    public SearchCommandsController() {}

    @FXML
    private void initialize() {}

    public void setup(final SearchController pSearchController) {
        searchController = pSearchController;
        loadCommands(Token.getSpecialTokens());
    }

    private void loadCommands(final BangCommand[] commandsArray) {
        Collection<BangCommand> commands = Arrays.asList(commandsArray);
        commands.stream().forEach(c -> {
            Label commandLabel = new Label();
            String longSyntax = c.getCommands()[0];
            String shortSyntax = c.getCommands()[1];
            commandLabel.setText(longSyntax + " or " + shortSyntax + "\n" + c.getDescription());
            commandLabel.setPadding(new Insets(10));
            commandLabel.getStyleClass().add("search-command");
            setupAutoFill(commandLabel, longSyntax);
            commandsTitlePane.getChildren().add(commandLabel);
        });
    }

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

