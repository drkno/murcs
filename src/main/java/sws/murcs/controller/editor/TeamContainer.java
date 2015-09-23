package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import sws.murcs.controller.pipes.Navigable;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;
import sws.murcs.model.Team;

/**
 * Controller for TeamContainer.
 */
public class TeamContainer extends GenericEditor<Team> {

    /**
     * The tab pane that the tabs sit in.
     */
    @FXML
    private TabPane containerTabPane;

    /**
     * The two tabs on the right side of the page for displaying different
     * information relating to teams.
     */
    @FXML
    private Tab overviewTab, velocityTab;

    /**
     * Where to place the content in each tab.
     */
    @FXML
    private AnchorPane overviewAnchorPane, velocityAnchorPane;

    /**
     * The controller for the overview page.
     */
    private TeamEditor overviewEditor;

    /**
     * The controller for the velocity board.
     */
    private VelocityBoard velocityBoard;

    @Override
    protected void initialize() {
        containerTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                tabSelectionChanged(newValue);
            }
        });

        try {
            FXMLLoader loader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/TeamEditor.fxml"));
            Parent view = loader.load();
            overviewAnchorPane.getChildren().add(view);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            overviewEditor = loader.getController();
        } catch (Exception e) {
            ErrorReporter.get().reportError(e, "Unable to load team overview");
        }

        try {
            FXMLLoader loader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/VelocityBoard.fxml"));
            Parent view = loader.load();
            velocityAnchorPane.getChildren().add(view);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            velocityBoard = loader.getController();
        } catch (Exception e) {
            ErrorReporter.get().reportError(e, "Unable to load velocity board");
        }
    }

    @Override
    public void loadObject() {
        int tab = containerTabPane.getSelectionModel().getSelectedIndex();
        switch (tab) {
            case 0:
                overviewTabSelected();
                break;
            case 1:
                velocityTabSelected();
                break;
            default:
                throw new UnsupportedOperationException("You tried switch to a tab that hasn't been linked yet");
        }
        Platform.runLater(() -> isLoaded = true);
    }

    @Override
    public final void clearErrors(final String sectionName) {
    }

    @Override
    protected final void saveChangesAndErrors() {
    }

    @Override
    public final void setupSaveChangesButton() {
    }

    @Override
    public void dispose() {
        if (overviewEditor != null) {
            overviewEditor.dispose();
        }
        if (velocityBoard != null) {
            velocityBoard.dispose();
        }
    }

    @Override
    public void setNavigationManager(final Navigable navigationManager) {
        overviewEditor.setNavigationManager(navigationManager);
        super.setNavigationManager(navigationManager);
    }

    /**
     * Called when the tab selected is changed.
     * @param tab the new tab that has been selected
     */
    private void tabSelectionChanged(final Tab tab) {
        if (tab.equals(overviewTab)) {
            overviewTabSelected();
        }
        else if (tab.equals(velocityTab)) {
            velocityTabSelected();
        }
    }

    /**
     * Loads this team overview into the overview tab.
     */
    private void overviewTabSelected() {
        overviewEditor.setModel(getModel());
        overviewEditor.loadObject();
    }

    /**
     * Loads the velocity board into the velocity tab.
     */
    private void velocityTabSelected() {
        velocityBoard.setTeam(getModel());
        velocityBoard.loadObject();
    }
}
