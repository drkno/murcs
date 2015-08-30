package sws.murcs.controller.editor;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import sws.murcs.controller.pipes.Navigable;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * The anchor panges where the content for each tab is.
     */
    @FXML
    private AnchorPane overviewAnchorPane, burnDownChartAnchorPane, allTasksAnchorPane, scrumBoardAnchorPane;

    /**
     * The editor of the overview.
     */
    private SprintEditor overviewEditor;

    /**
     * The controller for the all tasks view in the sprint.
     */
    private SprintAllTasksController allTasksController;

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

        switch (tab) {
            case 0:
                overviewTabSelected();
                break;
            case 1:
                scrumBoardTabSelected();
                break;
            case 2:
                burnDownChartTabSelected();
                break;
            case 3:
                if (!isLoaded) {
                    allTasksTabSelected();
                }
                break;
            default:
                throw new UnsupportedOperationException("You tried switch to a tab that hasn't been linked yet");
        }
        isLoaded = true;
    }


    /**
     * Called when the tab selected is changed.
     * @param tab the new tab that has been selected.
     */
    private void tabSelectionChanged(final Tab tab) {
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
        // todo Currently doesn't do anything as there is no scrum board chart to load
    }

    /**
     * Loads this sprints burn down chart into the burn down tab.
     */
    private void burnDownChartTabSelected() {
        // todo Currently doesn't do anything as there is no burndown chart to load
    }

    /**
     * Called when the all tasks tab has been selected.
     */
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
            List<Story> checkList = new ArrayList<Story>();
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
    public void setNavigationManager(final Navigable navigationManager) {
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
