package sws.murcs.controller.editor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.ImperialException;
import sws.murcs.internationalization.AutoLanguageFXMLLoader;
import sws.murcs.model.Backlog;
import sws.murcs.model.EstimateType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The estimation workspace for manipulating stories in a backlog.
 */
public class EstimationWorkspace extends GenericEditor<Backlog> {

    /**
     * The root pane of the estimation workspace.
     */
    private Pane rootPane;

    /**
     * List of estimate panes.
     */
    private Collection<EstimatePane> estimatePanes;

    /**
     * Loader for loading estimate pane fxml.
     */
    private FXMLLoader estimateLoader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/EstimationWorkspacePane.fxml"));

    /**
     * The current backlog.
     */
    private Backlog currentBacklog;

    @Override
    public void loadObject() {
        if (getModel() != null) {
            if (currentBacklog == null
                    || !getModel().equals(currentBacklog)) {
                estimatePanes.clear();
                rootPane.getChildren().clear();
                loadEstimatePanels();
                estimatePanes.stream().forEach(EstimatePane::loadObject);
                currentBacklog = getModel();
            }
        }
    }

    /**
     * Loads the estimate panes.
     */
    private void loadEstimatePanels() {
        List<String> estimates = new ArrayList<>();
        estimates.add(EstimateType.NOT_ESTIMATED);
        estimates.add(EstimateType.ZERO);
        estimates.addAll(getModel().getEstimateType().getEstimates());
        estimates.add(EstimateType.INFINITE);
        for (String estimateType : estimates) {
            try {
                estimateLoader.setRoot(null);
                estimateLoader.setController(null);
                Parent root = estimateLoader.load();
                EstimatePane controller = estimateLoader.getController();
                controller.configure(estimateType, getModel());
                rootPane.getChildren().add(root);
                estimatePanes.add(controller);
            } catch (IOException e) {
                ErrorReporter.get().reportError(e, "Failed to load estimate pane in estimate workspace");
            }
        }
    }

    @Override
    protected void saveChangesAndErrors() {
        throw new ImperialException();
    }

    @Override
    protected void initialize() {
        currentBacklog = getModel();
    }

    /**
     * Sets up the estimation workspace.
     * @param root The root pane.
     */
    protected void setup(final Pane root) {
        rootPane = root;
        estimatePanes = new ArrayList<>();
    }
}
