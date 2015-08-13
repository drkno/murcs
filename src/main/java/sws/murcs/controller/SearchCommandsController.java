package sws.murcs.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import sws.murcs.search.tokens.BangCommand;

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
        loadCommands();
    }

    private void loadCommands(final Collection<BangCommand> commands) {
        commands.stream().forEach(c -> {
            Label commandLabel = new Label();
            String longSyntax = c.getCommands()[0];
            String shortSyntax = c.getCommands()[1];
            commandLabel.setText(longSyntax + " or " + shortSyntax + "\n" + c.getDescription());
            setupAutoFill(commandLabel, longSyntax);
            commandsTitlePane.getChildren().add(commandLabel);
        });
    }

    private void setupAutoFill(final Label commandLabel, final String command) {
        TextField searchText = searchController.searchText;
        commandLabel.setOnMousePressed(event -> {
            if (searchText.getText() != null || !Objects.equals(searchText.getText(), "")) {
                searchText.setText(command + " " + searchText.getText());
            }
            searchText.setText(command);
        });
    }
}

