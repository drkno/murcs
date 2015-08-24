package sws.murcs.controller.editor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Sprint;

/**
 * Controller for SprintContainer.
 */
public class SprintContainer extends GenericEditor<Sprint> {

    /**
     * The three tabs used to view a sprint.
     */
    @FXML
    private Tab overviewTab, scrumBoardTab, burnDownChartTab;

    /**
     * The tab pane that the tabs sit in.
     */
    @FXML
    private TabPane containerTabPane;

    /**
     * The editor of the overview.
     */
    private SprintEditor overviewEditor;

    /**
     * Creates a new Sprint Container editor.
     */
    public SprintContainer() {
        super();
    }

    @Override
    protected final void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sws/murcs/SprintEditor.fxml"));
            Parent view = loader.load();
            overviewEditor = loader.getController();
            overviewTab.setContent(view);
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
    }

    /**
     * Loads this sprints overview into the overview tab.
     */
    @FXML
    private void overviewTabSelected() {
        if (getModel() != null) {
            overviewEditor.setModel(getModel());
            overviewEditor.loadObject();
        }
    }

    /**
     * Loads this sprints scrum board into the scrum board tab.
     */
    @FXML
    private void scrumBoardTabSelected() {
        // Currently doesn't do anything as there is no scrum board chart to load
    }

    /**
     * Loads this sprints burndown chart into the burndown tab.
     */
    @FXML
    private void burnDownChartTabSelected() {
        // Currently doesn't do anything as there is no burndown chart to load
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
}
