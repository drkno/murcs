package sws.murcs.reporting.ui;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sws.murcs.controller.JavaFXHelpers;
import sws.murcs.controller.controls.md.MaterialDesignButton;
import sws.murcs.controller.controls.md.MaterialDesignToggleButton;
import sws.murcs.debug.errorreporting.ErrorReporter;
import sws.murcs.model.Model;
import sws.murcs.model.ModelType;
import sws.murcs.model.Organisation;
import sws.murcs.model.observable.ModelObservableArrayList;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.reporting.ReportGenerator;
import sws.murcs.view.App;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Supplier;

/**
 * Controller for the report generator.
 */
public class ReportGeneratorController {

    /**
     * Contains all of the buttons in the ReportGenerator Window (Generate/Cancel).
     */
    @FXML
    private HBox buttonContainer;

    /**
     * Grid that contains the options section (selecting items to generate a report on) of the Report Generator Window.
     */
    @FXML
    private GridPane lowerThird;

    /**
     * The content relevant to management or workflow.
     */
    @FXML
    private VBox managementContent, workflowContent;

    /**
     * The combo box for selecting different management or workflow types.
     */
    @FXML
    private ComboBox<ModelType> managementTypeComboBox, workflowTypeComboBox;

    /**
     * The list which is populated with selected management or workflow type.
     */
    @FXML
    private ListView<Model> managementList, workflowList;

    /**
     * The buttons in the create window.
     */
    private MaterialDesignButton createButton, cancelButton;

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
    private MaterialDesignToggleButton all, management, workflow;

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
        if (!managementContent.managedProperty().isBound()) {
            managementContent.managedProperty().bind(managementContent.visibleProperty());
        }
        if (!workflowContent.managedProperty().isBound()) {
            workflowContent.managedProperty().bind(workflowContent.visibleProperty());
        }
        setupReportTypeSelection();
        setupInnerContent();
        setupActionSection();
        hideAllContent();
    }

    /**
     * Sets up the lower third of the Report Generator Window.
     */
    private void setupActionSection() {
        final int minWidth = 80;
        final int five = 5;
        final int ten = 10;
        createButton = new MaterialDesignButton("Generate Report");
        buttonContainer.getChildren().add(createButton);
        createButton.alignmentProperty().set(Pos.CENTER);
        createButton.setDefaultButton(true);
        createButton.setMinHeight(0);
        createButton.setMinWidth(minWidth);
        createButton.setMnemonicParsing(false);
        createButton.setOnAction(this::createButtonClicked);
        GridPane.setRowIndex(createButton, 1);
        GridPane.setMargin(createButton, new Insets(ten, ten, ten, ten));
        HBox.setMargin(createButton, new Insets(five, five, ten, ten));

        createButton.getStyleClass().add("create-save-button");
        createButton.setRippleColour(JavaFXHelpers.hex2RGB("#42A5F5"));

        cancelButton = new MaterialDesignButton("Cancel");
        buttonContainer.getChildren().add(cancelButton);
        cancelButton.alignmentProperty().set(Pos.CENTER);
        cancelButton.setCancelButton(true);
        cancelButton.setMinHeight(0);
        cancelButton.setMinWidth(minWidth);
        cancelButton.setMnemonicParsing(false);
        cancelButton.setOnAction(this::cancelButtonClicked);
        GridPane.setColumnIndex(cancelButton, 1);
        GridPane.setMargin(cancelButton, new Insets(ten, ten, ten, ten));
        HBox.setMargin(cancelButton, new Insets(five, ten, ten, ten));

        cancelButton.setRippleColour(JavaFXHelpers.hex2RGB("#bdbdbd"));
    }

    /**
     * Sets up the content inside the toolbar.
     */
    private void setupReportTypeSelection() {
        all = new MaterialDesignToggleButton("All");
        management = new MaterialDesignToggleButton("Management");
        workflow = new MaterialDesignToggleButton("Workflow");
        all.alignmentProperty().setValue(Pos.CENTER);
        management.alignmentProperty().setValue(Pos.CENTER);
        workflow.alignmentProperty().setValue(Pos.CENTER);

        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(
                all,
                management,
                workflow
        );
        toggleGroup.selectToggle(all);
        toggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                changeView();
            }
        }));
        toolBarContainer.getChildren().addAll(
                all,
                management,
                workflow
        );

        all.getStyleClass().add("md-button");
        management.getStyleClass().add("md-button");
        workflow.getStyleClass().add("md-button");
        all.getStyleClass().add("left-button");
        workflow.getStyleClass().add("right-button");

        all.setRippleColour(JavaFXHelpers.hex2RGB("#42A5F5"));
        workflow.setRippleColour(JavaFXHelpers.hex2RGB("#42A5F5"));
        management.setRippleColour(JavaFXHelpers.hex2RGB("#42A5F5"));
    }

    /**
     * Sets up the inner content of different report types.
     */
    private void setupInnerContent() {
        setupManagementContent();
        setupWorkflowContent();
    }

    /**
     * Sets up the content for the workflow report type.
     */
    private void setupWorkflowContent() {
        workflowTypeComboBox.getItems().addAll(
                ModelType.Backlog,
                ModelType.Story,
                ModelType.Sprint
        );
        workflowTypeComboBox
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> changeWorkflowSelection()));
        workflowList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
        ModelType type = managementTypeComboBox.getSelectionModel().getSelectedItem();
        if (type != null) {
            ObservableList<Model> values = new ModelObservableArrayList<>();
            workflowList.setItems(values);
            Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();

            switch (type) {
                case Project:
                    values = checkListType(organisation::getProjects);
                    managementList.setVisible(true);
                    break;
                case Team:
                    values = checkListType(organisation::getTeams);
                    managementList.setVisible(true);
                    break;
                case Person:
                    values = checkListType(organisation::getPeople);
                    managementList.setVisible(true);
                    break;
                default:
                    managementList.setVisible(false);
                    throw new UnsupportedOperationException("Reporting on this model type "
                            + "has not yet been implemented.");
            }
        }
    }

    /**
     * Repopulates the list of models to generate a report from.
     */
    private void changeWorkflowSelection() {
        ModelType type = workflowTypeComboBox.getSelectionModel().getSelectedItem();
        if (type != null) {
            ObservableList<Model> values = new ModelObservableArrayList<>();
            workflowList.setItems(values);
            Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();

            switch (type) {
                case Backlog:
                    values.addAll(checkListType(organisation::getBacklogs));
                    workflowList.setVisible(true);
                    break;
                case Story:
                    values.addAll(checkListType(organisation::getStories));
                    workflowList.setVisible(true);
                    break;
                case Sprint:
                    values.addAll(checkListType(organisation::getSprints));
                    workflowList.setVisible(true);
                    break;
                default:
                    workflowList.setVisible(false);
                    throw new UnsupportedOperationException("Reporting on this model type "
                            + "has not yet been implemented.");
            }
        }
    }

    /**
     * Checks that a list is of the correct type for the reporter to update.
     * @param values The call to the organisation to get the list of things.
     * @return A sorted list of model objects.
     */
    private SortedList<Model> checkListType(final Supplier values) {
        if (values.get() instanceof ObservableList) {
            ObservableList<? extends Model> arrList = (ObservableList<Model>) values.get();
            return new SortedList<>(arrList, (Comparator<? super Model>) arrList);
        }
        else {
            throw new UnsupportedOperationException("List ordering not specified");
        }
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
            hideAllContent();
            managementContent.setVisible(true);
            changeManagementSelection();

        }
        else if (selected == workflow) {
            hideAllContent();
            workflowContent.setVisible(true);
            changeWorkflowSelection();
        }
        clearErrors();
        stage.sizeToScene();
    }

    /**
     * Hides all content.
     */
    private void hideAllContent() {
        managementContent.setVisible(false);
        workflowContent.setVisible(false);
        managementList.setVisible(false);
        workflowList.setVisible(false);
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
                ErrorReporter.get().reportError(e, "Failed to generate report");
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
        workflowTypeComboBox.getStyleClass().removeAll(Collections.singleton("error"));
        workflowList.getStyleClass().removeAll(Collections.singleton("error"));
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
        else if (type == workflow) {
            return checkForErrors(workflowTypeComboBox, workflowList);
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
        else if (type == workflow) {
            ReportGenerator.generate(workflowList.getSelectionModel().getSelectedItems(), file);
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
