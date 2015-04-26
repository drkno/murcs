package sws.murcs.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sws.murcs.exceptions.CustomException;
import sws.murcs.model.Project;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.Team;
import sws.murcs.model.WorkAllocation;
import sws.murcs.model.persistence.PersistenceManager;

import java.time.LocalDate;

/**
 * Controller for the edit creator popup window.
 * Since there should only be one instance of this PopUp
 */
public class ProjectEditor extends GenericEditor<Project> {

    @FXML private TextField textFieldShortName, textFieldLongName, textFieldDescription;
    @FXML private TableView<WorkAllocation> teamsViewer;
    @FXML private TableColumn<WorkAllocation, Team> tableColumnTeams;
    @FXML private TableColumn<WorkAllocation, LocalDate> tableColumnStartDates, tableColumnEndDates;
    @FXML private DatePicker datePickerStartDate, datePickerEndDate;
    @FXML private ChoiceBox<Team> choiceBoxAddTeam;
    @FXML private Label labelErrorMessage;

    ObservableList<WorkAllocation> observableAllocations;

    /**
     * Creates a new or updates the current edit being edited.
     */
    public void update() throws Exception {
        edit.setShortName(textFieldShortName.getText());
        edit.setLongName(textFieldLongName.getText());
        edit.setDescription(textFieldDescription.getText());

        // Save the project if it hasn't been yet
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

            // Attempt to update the model
            // If the work is successfully added to the team then it will
            // certainly be able to be added to the project
            WorkAllocation allocation = new WorkAllocation(edit, selectedTeam, startDate, endDate);
            selectedTeam.addAllocation(allocation);
            edit.addAllocation(allocation);
            observableAllocations.setAll(edit.getAllocations()); // This way, the list remains ordered
        }

        if (!model.getProjects().contains(edit))
            model.addProject(edit);

        //If we have a saved callBack, call it
        if (onSaved != null)
            onSaved.eventNotification(edit);
    }

    /**
     * Updates the object in memory and handles any exception
     */
    public void updateAndHandle(){
        try {
            labelErrorMessage.setText("");
            update();
        }
        catch (CustomException e) {
            labelErrorMessage.setText(e.getMessage());
        }
        catch (Exception e) {
            //Don't show the user this.
        }
    }

    /**
     * Loads the edit into the form
     */
    public void load(){
        textFieldShortName.setText(edit.getShortName());
        textFieldLongName.setText(edit.getLongName());
        textFieldDescription.setText(edit.getDescription());

        choiceBoxAddTeam.getItems().setAll(PersistenceManager.Current.getCurrentModel().getTeams());
        observableAllocations.setAll(edit.getAllocations());

        updateAndHandle();
    }

    /**
     * Initializes the editor for use, sets up listeners etc.
     */
    @FXML
    public void initialize() {
        textFieldShortName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        textFieldLongName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        textFieldDescription.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        choiceBoxAddTeam.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) updateAndHandle();
        });

        observableAllocations = FXCollections.observableArrayList();
        tableColumnTeams.setCellValueFactory(new PropertyValueFactory<>("team"));
        tableColumnStartDates.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        tableColumnEndDates.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        teamsViewer.setItems(observableAllocations);
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
        allocation.getTeam().removeAllocation(allocation);
        edit.removeAllocation(allocation);
        observableAllocations.remove(rowNumber);
    }
}
