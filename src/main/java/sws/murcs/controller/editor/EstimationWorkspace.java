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

    private Pane rootPane;

    private Collection<EstimatePane> estimatePanes;

    private FXMLLoader estimateLoader = new AutoLanguageFXMLLoader(getClass().getResource("/sws/murcs/EstimationWorkspacePane.fxml"));
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

    protected void setup(final Pane root) {
        rootPane = root;
        estimatePanes = new ArrayList<>();
    }




}
