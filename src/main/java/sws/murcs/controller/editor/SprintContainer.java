package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import sws.murcs.controller.controls.ModelProgressBar;
import sws.murcs.controller.pipes.Navigable;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controller for SprintContainer.
 */
public class SprintContainer extends GenericEditor<Sprint> {

    /**
     * The container for the progress bar.
     */
    @FXML
    private VBox progressContainer;

    /**
     * The four tabs used to view a sprint.
     */
    @FXML
    private Tab overviewTab, scrumBoardTab, burnDownChartTab, allTasksTab;

    /**
     * The tab pane that the tabs sit in.
     */
    @FXML
    private TabPane containerTabPane;

    /**
     * The anchor panes where the content for each tab is.
     */
    @FXML
    private AnchorPane overviewAnchorPane, burnDownChartAnchorPane, allTasksAnchorPane, scrumBoardAnchorPane;

    /**
     * The editor of the overview.
     */
    private SprintEditor overviewEditor;

    /**
     * The scrum board editor.
     */
    private ScrumBoard scrumBoard;

    /**
     * The controller for the all tasks view in the sprint.
     */
    private SprintAllTasksController allTasksController;

    /**
     * The burndown controller.
     */
    private BurndownController burndownController;

    /**
     * The progress bar for the sprint.
     */
    private ModelProgressBar progressBar;

    @Override
    protected final void initialize() {
        containerTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                tabSelectionChanged(newValue);
            }
        });

        createOverviewEditor();

        progressBar = new ModelProgressBar(true);
        progressContainer.getChildren().add(progressBar);
    }

    @Override
    @SuppressWarnings("checkstyle:magicnumber")
    public final void loadObject() {
        progressBar.setSprint(getModel());

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
                allTasksTabSelected();
                break;
            default:
                throw new UnsupportedOperationException("You tried switch to a tab that hasn't been linked yet");
        }
        Platform.runLater(() -> {
            isLoaded = true;
        });
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
        if (overviewEditor == null || overviewEditor.disposed) {
            createOverviewEditor();
        }
        overviewEditor.setModel(getModel());
        overviewEditor.loadObject();
    }

    /**
     * Loads this sprints scrum board into the scrum board tab.
     */
    private void scrumBoardTabSelected() {
        if (scrumBoard == null || scrumBoard.disposed) {
            createScrumBoardEditor();
        }
        scrumBoard.setModel(getModel());
        scrumBoard.loadObject();
    }

    /**
     * Creates the scrum board editor.
     */
    private void createScrumBoardEditor() {
        try {
            FXMLLoader loader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/ScrumBoard.fxml"));
            Parent view = loader.load();
            scrumBoardAnchorPane.getChildren().add(view);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            scrumBoard = loader.getController();
            scrumBoard.setSprintContainer(this);
        } catch (Exception e) {
            ErrorReporter.get().reportError(e, "Unable to load the scrumboard");
        }
    }

    /**
     * Loads this sprints burn down chart into the burn down tab.
     */
    private void burnDownChartTabSelected() {
        if (burndownController == null || burndownController.disposed) {
            createBurndownEditor();
        }

        if (!Objects.equals(burndownController.getModel(), getModel())) {
            burndownController.setModel(getModel());
            burndownController.loadObject();
        }
    }

    /**
     * Creates a burndown editor.
     */
    private void createBurndownEditor() {
        try {
            FXMLLoader loader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/Burndown.fxml"));
            Parent view = loader.load();
            burnDownChartTab.setContent(view);

            burndownController = loader.getController();
            burndownController.setNavigationManager(getNavigationManager());
        } catch (Exception e) {
            ErrorReporter.get().reportError(e, "Failed to load the burndown tab in sprints.");
        }
    }

    /**
     * Called when the all tasks tab has been selected.
     */
    private void allTasksTabSelected() {
        if (allTasksController == null || allTasksController.disposed) {
            createAllTasksEditor();
        }
        else if (allTasksController.getModel() != getModel()) {
            allTasksController.setModel(getModel());
            allTasksController.loadObject();
        }
        else {
            List<Story> checkList = new ArrayList<>();
            checkList.addAll(getModel().getStories());
            checkList.retainAll(allTasksController.currentStories());
            if (checkList.size() != getModel().getStories().size()) {
                allTasksController.loadObject();
            }
            else {
                allTasksController.updateEditors();
            }
        }
    }

    /**
     * Creates the all tasks editor.
     */
    private void createAllTasksEditor() {
        try {
            FXMLLoader loader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/AllTasksView.fxml"));
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
        if (scrumBoard != null) {
            scrumBoard.dispose();
        }
        if (burndownController != null) {
            burndownController.dispose();
        }
    }

    @Override
    public void setNavigationManager(final Navigable navigationManager) {
        if (overviewEditor == null) {
            createOverviewEditor();
        }
        overviewEditor.setNavigationManager(navigationManager);
        if (allTasksController == null) {
            createAllTasksEditor();
        }
        allTasksController.setNavigationManager(navigationManager);
        if (scrumBoard == null) {
            createScrumBoardEditor();
        }
        scrumBoard.setNavigationManager(navigationManager);
        super.setNavigationManager(navigationManager);
    }


    /**
     * Creates the overview editor.
     */
    private void createOverviewEditor() {
        try {
            FXMLLoader loader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/SprintEditor.fxml"));
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
    public void undoRedoNotification(final ChangeState param) {
        if (burndownController != null) {
            burndownController.loadObject();
        }
    }
}
