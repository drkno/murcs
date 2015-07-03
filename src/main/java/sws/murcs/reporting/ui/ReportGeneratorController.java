package sws.murcs.reporting.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sws.murcs.controller.GenericPopup;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.io.File;

/**
 * Controller for the report generator.
 */
public class ReportGeneratorController {
    /**
     * The buttons in the create window.
     */
    @FXML
    private Button createButton, cancelButton;

    /**
     * The toolbar for containing toggle buttons.
     */
    @FXML
    private ToolBar toolBar;

    /**
     * The stage of the creation window.
     */
    private Stage stage;
    /**
     * toggle buttons for type of report generation.
     */
    private ToggleButton all, management;
    /**
     * Group containing toggle buttons.
     */
    private ToggleGroup toggleGroup;

    /**
     * Empty Constructor for fxml creation.
     */
    public ReportGeneratorController() {
    }

    /**
     * Sets the stage of the creation window.
     * @param pStage The Stage to set
     */
    public final void setStage(final Stage pStage) {
        stage = pStage;
    }
    /**
     * Sets up the report generatorUI.
     */
    @FXML
    public final void initialize() {
        all = new ToggleButton("All");
        management = new ToggleButton("Management");
        all.setAlignment(Pos.CENTER);
        management.setAlignment(Pos.CENTER);
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(
                all,
                management
        );
//        toggleGroup.getSelectedToggle().selectedProperty().addListener(((observable, oldValue, newValue) -> {
//            if (oldValue != newValue) {
//                changeView();
//            }
//        }));
        toolBar.getItems().addAll(
                all,
                management
        );
    }

    private void changeView() {

    }

    /**
     * The function called on the cancel button being clicked.
     * @param actionEvent The event that calls this function.
     */
    @FXML
    private void cancelButtonClicked(final ActionEvent actionEvent) {
        stage.close();
    }

    /**
     * The function called on the okay button being clicked.
     * @param event The event that fires this function.
     */
    @FXML
    private void createButtonClicked(final ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("XML File (*.xml)", "*.xml"));
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("Report File (*.report)", "*.report"));
            fileChooser.setInitialDirectory(new File(PersistenceManager.getCurrent().getCurrentWorkingDirectory()));
            fileChooser.setTitle("Report Save Location");
            File file = fileChooser.showSaveDialog(App.getStage());
            if (file != null) {
                generateReport(file);
                PersistenceManager.getCurrent().setCurrentWorkingDirectory(file.getParentFile().getAbsolutePath());
            }
            stage.close();
        }
        catch (Exception e) {
            GenericPopup popup = new GenericPopup(e);
            popup.show();
        }
    }

    /**
     *
     * @param file
     */
    private void generateReport(File file) {

    }
}
