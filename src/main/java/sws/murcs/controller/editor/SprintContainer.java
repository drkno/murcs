package sws.murcs.controller.editor;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.pipes.Navigable;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;

import java.util.ArrayList;

/**
 * Controller for SprintContainer.
 */
public class SprintContainer extends GenericEditor<Sprint> {

    /**
     * The three tabs used to view a sprint.
     */
    @FXML
    private Tab overviewTab, scrumBoardTab, burnDownChartTab, allTasksTab;

    /**
     * The tab pane that the tabs sit in.
     */
    @FXML
    private TabPane containerTabPane;

    @FXML AnchorPane overviewAnchorPane, burnDownChartAnchorPane, allTasksAnchorPane, scrumBoardAnchorPane;

    /**
     * The editor of the overview.
     */
    private SprintEditor overviewEditor;

    private SprintAllTasksController allTasksController;

    /**
     * Creates a new Sprint Container editor.
     */
    public SprintContainer() {
        super();
    }

    @Override
    protected final void initialize() {
        containerTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                tabSelectionChanged(newValue);
            }
        });
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/SprintEditor.fxml"));
            Parent view = loader.load();
            overviewAnchorPane.getChildren().add(view);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            overviewEditor = loader.getController();
        } catch (Exception e) {
            ErrorReporter.get().reportError(e, "Unable to load sprint overview");
        }
    }

    @Override
    public final void loadObject() {
        int tab = containerTabPane.getSelectionModel().getSelectedIndex();
        if (tab == 0) {
            overviewTabSelected();
        }
        else if (tab == 1) {
            scrumBoardTabSelected();
        }
        else if (tab == 2) {
            burnDownChartTabSelected();
        }
        else if (tab == 3) {
            if (!isLoaded) {
                allTasksTabSelected();
            }
        }
        isLoaded = true;
    }


    private void tabSelectionChanged(Tab tab) {
        if (tab.equals(overviewTab)) {
            overviewTabSelected();
        }
        else if (tab.equals(scrumBoardTab)) {
            scrumBoardTabSelected();
        }
        else if (tab.equals(burnDownChartTab)) {
            burnDownChartTabSelected();
        }
        else if (tab.equals(allTasksTab)) {
            allTasksTabSelected();
        }
    }

    /**
     * Loads this sprints overview into the overview tab.
     */
    private void overviewTabSelected() {
        if (getModel() != null) {
            overviewEditor.setModel(getModel());
            overviewEditor.loadObject();
        }
    }

    /**
     * Loads this sprints scrum board into the scrum board tab.
     */
    private void scrumBoardTabSelected() {
        // Currently doesn't do anything as there is no scrum board chart to load
    }

    /**
     * Loads this sprints burndown chart into the burndown tab.
     */
    private void burnDownChartTabSelected() {
        // Currently doesn't do anything as there is no burndown chart to load
    }

    private void allTasksTabSelected() {
        if (allTasksController == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/AllTasksView.fxml"));
                Parent view = loader.load();
                allTasksAnchorPane.getChildren().add(view);
                AnchorPane.setRightAnchor(view, 0.0);
                AnchorPane.setLeftAnchor(view, 0.0);
                AnchorPane.setTopAnchor(view, 0.0);
                AnchorPane.setBottomAnchor(view, 0.0);
                allTasksController = loader.getController();
                allTasksController.setModel(getModel());
                allTasksController.setNavigationManager(getNavigationManager());
                allTasksController.loadObject();
            } catch (Exception e) {
                ErrorReporter.get().reportError(e, "Failed to load the all tasks tab in sprints.");
            }
        }
        else if (allTasksController.getModel() != getModel()) {
            allTasksController.setModel(getModel());
            allTasksController.loadObject();
        }
        else {
            ArrayList<Story> checkList = new ArrayList<Story>();
            checkList.addAll(getModel().getStories());
            checkList.retainAll(allTasksController.currentStories());
            if (checkList.size() != getModel().getStories().size()) {
                allTasksController.loadObject();
            }
        }
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
        if (allTasksController != null) {
            allTasksController.dispose();
        }
        //Makes sure to add dispose methods for the other tabs.
    }

    @Override
    public void setNavigationManager(Navigable navigationManager) {
        overviewEditor.setNavigationManager(navigationManager);
        if (allTasksController != null) {
            allTasksController.setNavigationManager(navigationManager);
        }
        super.setNavigationManager(navigationManager);
    }

    @Override
    public void undoRedoNotification(final ChangeState param) {
    }
}
