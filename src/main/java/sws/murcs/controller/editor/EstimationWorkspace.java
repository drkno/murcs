package sws.murcs.controller.editor;

import javafx.application.Platform;
import javafx.fxml.FXML;
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
import java.util.Objects;

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
                load();
            }
        }
    }

    /**
     * Forces a reload of the estimate panes.
     */
    protected void forceLoadObject() {
        load();
    }

    /**
     * Loads the estimate panes.
     */
    private void load() {
        estimatePanes.clear();
        rootPane.getChildren().clear();
        loadEstimatePanels();
        estimatePanes.stream().forEach(EstimatePane::loadObject);
        currentBacklog = getModel();

        Platform.runLater(() -> {
            double max = 0;
            for (EstimatePane pane : estimatePanes) {
                max = pane.getEstimateLabelWidth() > max ? pane.getEstimateLabelWidth() : max;
            }
            final double realMax = max;
            estimatePanes.forEach(estimatePane -> estimatePane.setEstimateLabelWidth(realMax));
        });
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
                controller.configure(estimateType, getModel(), this);
                rootPane.getChildren().add(root);
                estimatePanes.add(controller);
            } catch (IOException e) {
                ErrorReporter.get().reportError(e, "Failed to load estimate pane in estimate workspace");
            }
        }
    }

    @Override
    protected void saveChangesAndErrors() {
        // Not used.
        // Loosened precondition, Moffat ;)
    }

    @FXML
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

    /**
     * Reloads an estimate pane of a given estimate.
     * @param estimate The estimate to reload
     */
    protected void reloadEstimationPane(final String estimate) {
        EstimatePane estimatePane = estimatePanes.stream().filter(e -> Objects.equals(e.estimate, estimate)).findFirst().get();
        if (estimatePane != null) {
            estimatePane.loadObject();
        }
    }
}
