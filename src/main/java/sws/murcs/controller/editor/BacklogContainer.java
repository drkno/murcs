package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import sws.murcs.controller.pipes.Navigable;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;
import sws.murcs.magic.tracking.listener.ChangeState;
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
     * Container for the estimate panes.
     */
    @FXML
    private VBox estimatesContainerVBox;

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
            FXMLLoader loader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/BacklogEditor.fxml"));
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
            estimationWorkspace = new EstimationWorkspace();
            estimationWorkspace.setup(estimatesContainerVBox);
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

    /**
     * Sets up the estimation workspace.
     */
    private void workspaceTabSelected() {
        estimationWorkspace.setModel(getModel());
        estimationWorkspace.loadObject();
    }

    /**
     * loads the backlog overview.
     */
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
            estimationWorkspace.forceLoadObject();
        }
    }

    @Override
    protected void saveChangesAndErrors() {
        // Not used.
        // Loosened precondition, Moffat ;)
    }

    @Override
    public final void setupSaveChangesButton() {
        // Not used.
        // Loosened precondition, Moffat ;)
    }

    @Override
    public void dispose() {
        if (backlogOverview != null) {
            backlogOverview.dispose();
        }
        if (estimationWorkspace != null) {
            estimationWorkspace.dispose();
        }
    }

    @Override
    public void setNavigationManager(final Navigable navigationManager) {
        backlogOverview.setNavigationManager(navigationManager);
        if (estimationWorkspace != null) {
            estimationWorkspace.setNavigationManager(navigationManager);
        }
        super.setNavigationManager(navigationManager);
    }

    @Override
    public void undoRedoNotification(final ChangeState param) {
        if (backlogOverview != null) {
            backlogOverview.loadObject();
        }
        if (estimationWorkspace != null) {
            estimationWorkspace.loadObject();
        }
    }
}
