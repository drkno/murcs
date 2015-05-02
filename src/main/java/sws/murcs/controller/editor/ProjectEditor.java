package sws.murcs.controller.editor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;
import sws.murcs.model.persistence.PersistenceManager;

import java.time.LocalDate;

/**
 * Controller for the model creator popup window.
 * Since there should only be one instance of this PopUp
 */
public class ProjectEditor extends GenericEditor<Project> {

    @FXML
    private TextField shortNameTextField, longNameTextField, descriptionTextField;
    @FXML
    private TableView<WorkAllocation> teamsViewer;
    @FXML
    private TableColumn<WorkAllocation, Team> tableColumnTeams;
    @FXML
    private TableColumn<WorkAllocation, LocalDate> tableColumnStartDates, tableColumnEndDates;
    @FXML
    private DatePicker datePickerStartDate, datePickerEndDate;
    @FXML
    private ChoiceBox<Team> choiceBoxAddTeam;
    @FXML
    private Label labelErrorMessage;

    private ObservableList<WorkAllocation> observableAllocations;

    @FXML
    @Override
    public void initialize() {
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) saveChanges();
        });

        longNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) saveChanges();
        });

        descriptionTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) saveChanges();
        });

        choiceBoxAddTeam.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) saveChanges();
        });

        datePickerStartDate.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) saveChanges();
        });

        datePickerEndDate.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) saveChanges();
        });

        observableAllocations = FXCollections.observableArrayList();
        tableColumnTeams.setCellValueFactory(new PropertyValueFactory<>("team"));
        tableColumnStartDates.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        tableColumnEndDates.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        teamsViewer.setItems(observableAllocations);

        setErrorCallback(message -> {
            if (message.getClass() == String.class)
                labelErrorMessage.setText(message);
        });
    }

    @Override
    public void loadObject() {
        // todo decouple from model
        RelationalModel model = PersistenceManager.Current.getCurrentModel();

        String modelShortName = this.model.getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName))
            shortNameTextField.setText(modelShortName);

        String modelLongName = this.model.getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqual(modelLongName, viewLongName))
            longNameTextField.setText(modelLongName);

        String modelDescription = this.model.getDescription();
        String viewDescription = descriptionTextField.getText();
        if (isNotEqual(modelDescription, viewDescription))
            descriptionTextField.setText(modelDescription);

        choiceBoxAddTeam.getItems().setAll(model.getTeams());
        observableAllocations.setAll(model.getProjectsAllocations(this.model));
    }

    @Override
    protected void saveChangesWithException() throws Exception {
        String modelShortName = model.getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelShortName, viewShortName))
            model.setShortName(viewShortName);

        String modelLongName = model.getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelLongName, viewLongName))
            model.setLongName(viewLongName);

        String modelDescription = model.getDescription();
        String viewDescription = descriptionTextField.getText();
        if (isNotEqualOrIsEmpty(modelDescription, viewDescription))
            model.setDescription(viewDescription);

        // todo decouple from model
        RelationalModel model = PersistenceManager.Current.getCurrentModel();

        // Extract details of a work period
        LocalDate startDate = datePickerStartDate.getValue();
        LocalDate endDate = datePickerEndDate.getValue();
        Team selectedTeam = choiceBoxAddTeam.getValue();

        if (selectedTeam != null && startDate != null && endDate != null) {

            // Clear user inputs for work period
            choiceBoxAddTeam.getSelectionModel().clearSelection();
            datePickerStartDate.setValue(null);
            datePickerEndDate.setValue(null);

            // Save this work allocation to the model
            WorkAllocation allocation = new WorkAllocation(this.model, selectedTeam, startDate, endDate);
            model.addAllocation(allocation);
            observableAllocations.setAll(model.getProjectsAllocations(this.model)); // This way, the list remains ordered
        }
    }

    @Override
    public void dispose() {
        observableAllocations = null;
        super.dispose();
    }
    /**
     * Called by the "Unschedule Work" button
     * Removes a work period from both the project and team
     */
    @FXML
    private void buttonUnscheduleTeamClick() {
        if (teamsViewer.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }

        int rowNumber = teamsViewer.getSelectionModel().getSelectedIndex();
        WorkAllocation allocation = observableAllocations.get(rowNumber);
        PersistenceManager.Current.getCurrentModel().removeAllocation(allocation);
        observableAllocations.remove(rowNumber);
    }
}
