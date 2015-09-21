package sws.murcs.controller.editor;

import com.sun.javafx.css.StyleManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.exceptions.ImperialException;
import sws.murcs.magic.tracking.listener.ChangeState;
import sws.murcs.model.Backlog;
import sws.murcs.model.EstimateType;
import sws.murcs.model.Sprint;
import sws.murcs.model.Story;
import sws.murcs.view.App;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The estimation workspace for manipulating stories in a backlog.
 */
public class EstimationWorkspace extends GenericEditor<Backlog> {

    private Pane rootPane;

    private Collection<EstimatePane> estimatePanes;

    private FXMLLoader estimateLoader = new FXMLLoader(getClass().getResource("/sws/murcs/EstimationWorkspacePane.fxml"));

    @Override
    public void undoRedoNotification(ChangeState param) {

    }

    @Override
    public void loadObject() {
        estimatePanes.clear();
        rootPane.getChildren().clear();
        loadEstimatePanels();
        estimatePanes.stream().forEach(EstimatePane::loadObject);
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
    }

    protected void setup(final Pane root) {
        rootPane = root;
        estimatePanes = new ArrayList<>();
    }




}
