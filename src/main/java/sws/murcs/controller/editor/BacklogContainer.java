package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.ImperialException;
import sws.murcs.model.Backlog;

/**
 * Controller for Backlog container.
 */
public class BacklogContainer extends GenericEditor<Backlog> {

    /**
     * The root of the FXML.
     */
    @FXML
    private AnchorPane rootPane;
    /**
     * The tab pane the tabs reside in.
     */
    @FXML
    private TabPane containerTabPane;

    /**
     * The tabs used to view the backlog.
     */
    @FXML
    private Tab overviewTab, workspaceTab;

    /**
     * The anchor panes where the content for each tab is in.
     */
    @FXML
    private AnchorPane overviewAnchorPane, workspaceAnchorPane;

    /**
     * The overview of the backlog.
     */
    private BacklogEditor backlogOverview;

    /**
     * The story estimation workspace.
     */
    private EstimationWorkspace estimationWorkspace;

    @Override
    protected final void initialize() {
        containerTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                tabSelectionChanged(newValue);
            }
        });

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/BacklogEditor.fxml"));
            Parent view = loader.load();
            overviewAnchorPane.getChildren().add(view);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            backlogOverview = loader.getController();
        } catch (Exception e) {
            ErrorReporter.get().reportError(e, "Unable to load backlog overview");
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/EstimationWorkspace.fxml"));
            Parent view = loader.load();
            workspaceAnchorPane.getChildren().add(view);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            estimationWorkspace = loader.getController();
        } catch (Exception e) {
            ErrorReporter.get().reportError(e, "Unable to load estimation workspace");
        }
    }

    @Override
    @SuppressWarnings("checkstyle:magicnumber")
    public final void loadObject() {
        int tab = containerTabPane.getSelectionModel().getSelectedIndex();
        switch (tab) {
            case 0:
                overviewTabSelected();
                break;
            case 1:
                workspaceTabSelected();
                break;
            default:
                throw new UnsupportedOperationException("You tried switch to a tab that hasn't been linked yet");
        }
        Platform.runLater(() -> { isLoaded = true; });
    }

    private void workspaceTabSelected() {
        estimationWorkspace.setModel(getModel());
        estimationWorkspace.loadObject();
    }

    private void overviewTabSelected() {
        backlogOverview.setModel(getModel());
        backlogOverview.loadObject();
    }

    /**
     * Called when the tab selected is changed.
     * @param tab the new tab that has been selected.
     */
    private void tabSelectionChanged(final Tab tab) {
        if (tab.equals(overviewTab)) {
            overviewTabSelected();
        }
        else if (tab.equals(workspaceTab)) {
            workspaceTabSelected();
        }
    }

    @Override
    protected void saveChangesAndErrors() {

    }
}
