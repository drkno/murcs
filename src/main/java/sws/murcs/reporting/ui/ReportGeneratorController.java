package sws.murcs.reporting.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sws.murcs.controller.GenericPopup;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Organisation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.reporting.ReportGenerator;
import sws.murcs.view.App;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controller for the report generator.
 */
public class ReportGeneratorController {
    /**
     * The content relevant to management.
     */
    @FXML
    private VBox managementContent;
    /**
     * The combo box for selecting different management types.
     */
    @FXML
    private ComboBox<ModelType> managementTypeComboBox;
    /**
     * The list which is populated with selected mangagement type.
     */
    @FXML
    private ListView<Model> managementList;
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
     * Container for toolbar buttons.
     */
    @FXML
    private HBox toolBarContainer;

    /**
     * The error message displayed.
     */
    @FXML
    private Label errorMessage;

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
        all.alignmentProperty().setValue(Pos.CENTER);
        management.alignmentProperty().setValue(Pos.CENTER);
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(
                all,
                management
        );
        toggleGroup.selectToggle(all);
        toggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                changeView();
            }
        }));
        toolBarContainer.getChildren().addAll(
                all,
                management
        );
        if (!managementContent.managedProperty().isBound()) {
            managementContent.managedProperty().bind(managementContent.visibleProperty());
        }
        setupInnerContent();
        hideAllContent();
    }

    /**
     * Sets up the inner content of different report types.
     */
    private void setupInnerContent() {
        setupManagementContent();
    }

    /**
     * Sets up the content for the management report type.
     */
    private void setupManagementContent() {
        managementTypeComboBox.getItems().addAll(
                ModelType.Project,
                ModelType.Team,
                ModelType.Person
        );
        managementTypeComboBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> changeManagementSelection()));
        managementList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Repopulates the list of models to generate a report from.
     */
    private void changeManagementSelection() {
        ModelType type = (ModelType) managementTypeComboBox.getSelectionModel().getSelectedItem();
        managementList.getItems().clear();
        List<Model> values = new ArrayList<>();
        Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();

        switch (type) {
            case Project:
                values.addAll(organisation.getProjects());
                break;
            case Team:
                values.addAll(organisation.getTeams());
                break;
            case Person:
                values.addAll(organisation.getPeople());
                break;
            default:
                throw new UnsupportedOperationException("Reporting on this model type has not yet been implemented.");
        }
        managementList.getItems().setAll(values);
    }

    /**
     * Changes the view dependent on the report type.
     */
    private void changeView() {
        Toggle selected = toggleGroup.selectedToggleProperty().getValue();
        if (selected == all) {
            hideAllContent();
        }
        else if (selected == management) {
            managementContent.setVisible(true);
        }
    }

    /**
     * Hides all content.
     */
    private void hideAllContent() {
        managementContent.setVisible(false);
    }

    /**
     * The function called on the cancel button being clicked.
     * @param event The event that calls this function.
     */
    @FXML
    private void cancelButtonClicked(final ActionEvent event) {
        stage.close();
    }

    /**
     * The function called on the okay button being clicked.
     * @param event The event that fires this function.
     */
    @FXML
    private void createButtonClicked(final ActionEvent event) {
        if (validateSelection()) {
            File file = null;
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters()
                        .add(new FileChooser.ExtensionFilter("XML File (*.xml)", "*.xml"));
                fileChooser.getExtensionFilters()
                        .add(new FileChooser.ExtensionFilter("Report File (*.report)", "*.report"));
                fileChooser.setInitialDirectory(new File(PersistenceManager.getCurrent().getCurrentWorkingDirectory()));
                fileChooser.setTitle("Report Save Location");
                file = fileChooser.showSaveDialog(App.getStage());
                if (file != null) {
                    generateReport(file);
                    PersistenceManager.getCurrent().setCurrentWorkingDirectory(file.getParentFile().getAbsolutePath());
                    stage.close();
                }
            } catch (Exception e) {
                if (file != null) {
                    file.delete();
                }
                GenericPopup popup = new GenericPopup(e);
                popup.show();
            }
        }
    }

    /**
     * Clears all errors.
     */
    private void clearErrors() {
        errorMessage.setText("");
        managementTypeComboBox.getStyleClass().removeAll(Collections.singleton("error"));
        managementList.getStyleClass().removeAll(Collections.singleton("error"));
    }

    /**
     * Checks that something is selected to generate a report from.
     * @return if the selection is valid.
     */
    private boolean validateSelection() {
        clearErrors();
        Toggle type = toggleGroup.getSelectedToggle();
        if (type == management) {
            return checkForErrors(managementTypeComboBox, managementList);
        }
        return true;
    }

    /**
     * Generates the report based on the report type and selected model.
     * @param file the file were the report is saved to.
     * @exception JAXBException Exception that may be thrown during the generation of the report
     */
    private void generateReport(final File file) throws JAXBException {
        Toggle type = toggleGroup.getSelectedToggle();

        if (type == all) {
            ReportGenerator.generate(PersistenceManager.getCurrent().getCurrentModel(), file);
        }
        else if (type == management) {
            ReportGenerator.generate(managementList.getSelectionModel().getSelectedItems(), file);
        }
    }

    /**
     * Checks that something is selected to generate a report from.
     * @param type the comboBox type
     * @param list the list of model objects
     * @return if something is selected to create a report from.
     */
    private boolean checkForErrors(final ComboBox<ModelType> type, final ListView<Model> list) {
        if (type.getSelectionModel().getSelectedIndex() == -1) {
            if (!type.getStyleClass().contains("error")) {
                type.getStyleClass().add("error");
            }
            errorMessage.setText("Oh no, you don't have a type of report selected.");
            return false;
        }
        else if (list.getSelectionModel().getSelectedItem() == null) {
            if (!list.getStyleClass().contains("error")) {
                list.getStyleClass().add("error");
            }
            errorMessage.setText("Well now you need to select something to generate a report for.");
            return false;
        }
        else {
            return true;
        }
    }
}
