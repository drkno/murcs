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

    /**
     * The text fields for the names and description of the project.
     */
    @FXML private TextField textFieldShortName, textFieldLongName, textFieldDescription;
    /**
     * The table view for work allocations.
     */
    @FXML private TableView<WorkAllocation> teamsViewer;
    /**
     * The table column for the team.
     */
    @FXML private TableColumn<WorkAllocation, Team> tableColumnTeams;
    /**
     * The table column for the start and end dates.
     */
    @FXML private TableColumn<WorkAllocation, LocalDate> tableColumnStartDates, tableColumnEndDates;
    /**
     * The datepickers for the start and end dates.
     */
    @FXML private DatePicker datePickerStartDate, datePickerEndDate;
    /**
     * The choice box for the team you want to allocate.
     */
    @FXML private ChoiceBox<Team> choiceBoxAddTeam;
    /**
     * The error message label for the window.
     */
    @FXML private Label labelErrorMessage;

    /**
     * The list of teams allocated to the project.
     */
    private ObservableList<WorkAllocation> observableAllocations;

    /**
     * Creates a new or updates the current edit being edited.
     * @throws Exception The exception thrown when invalid input is given.
     */
    public final void update() throws Exception {
        if (edit.getShortName() == null || !textFieldShortName.getText().equals(edit.getShortName())) {
            edit.setShortName(textFieldShortName.getText());
        }
        if (edit.getLongName() == null || !textFieldLongName.getText().equals(edit.getLongName())) {
            edit.setLongName(textFieldLongName.getText());
        }
        if (edit.getDescription() == null || !textFieldDescription.getText().equals(edit.getDescription())) {
            edit.setDescription(textFieldDescription.getText());
        }

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

            // Save this work allocation to the model
            WorkAllocation allocation = new WorkAllocation(edit, selectedTeam, startDate, endDate);
            model.addAllocation(allocation);
            observableAllocations.setAll(model.getProjectsAllocations(edit)); // This way, the list remains ordered
        }
    }

    /**
     * Updates the object in memory and handles any exception.
     */
    public final void updateAndHandle() {
        try {
            labelErrorMessage.setText("");
            update();
        }
        catch (CustomException e) {
            labelErrorMessage.setText(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            //Output any other exception to the console
        }
    }

    /**
     * Loads the edit into the form.
     */
    public final void load() {
        updateFields();
        updateAndHandle();
    }

    /**
     * Sets the fields in the editing pane if and only if they are different to the current values.
     * Done so that Undo/Redo can update the editing pane without losing current selection.
     */
    public final void updateFields() {
        RelationalModel model = PersistenceManager.Current.getCurrentModel();

        String currentShortName = textFieldShortName.getText();
        String currentLongName = textFieldLongName.getText();
        String currentDescription = textFieldDescription.getText();
        if (edit.getShortName() != null && !currentShortName.equals(edit.getShortName())) {
            textFieldShortName.setText(edit.getShortName());
        }
        if (edit.getLongName() != null && !currentLongName.equals(edit.getLongName())) {
            textFieldLongName.setText(edit.getLongName());
        }
        if (edit.getDescription() != null && !currentDescription.equals(edit.getShortName())) {
            textFieldDescription.setText(edit.getDescription());
        }

        choiceBoxAddTeam.getItems().setAll(model.getTeams());
        observableAllocations.setAll(model.getProjectsAllocations(edit));
    }

    /**
     * Initializes the editor for use, sets up listeners etc.
     */
    @FXML
    public final void initialize() {
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

        datePickerStartDate.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        datePickerEndDate.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) updateAndHandle();
        });

        observableAllocations = FXCollections.observableArrayList();
        tableColumnTeams.setCellValueFactory(new PropertyValueFactory<>("team"));
        tableColumnStartDates.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        tableColumnEndDates.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        teamsViewer.setItems(observableAllocations);
    }

    /**
     * Called by the "Unschedule Work" button.
     * Removes a work period from both the project and team.
     */
    @FXML
    private void buttonUnscheduleTeamClick() {
        if (teamsViewer.getSelectionModel().getSelectedIndex() == -1) {
            return;
        }

        int rowNumber = teamsViewer.getSelectionModel().getSelectedIndex();
        WorkAllocation allocation = observableAllocations.get(rowNumber);

        GenericPopup alert = new GenericPopup();
        alert.setTitleText("Unshedule A Team");
        alert.setMessageText("Are you sure you wish to unshedule \"" + allocation.getTeam() +
                "\" from \"" + allocation.getProject() + "\"?");
        alert.addOkCancelButtons(a -> {
            PersistenceManager.Current.getCurrentModel().removeAllocation(allocation);
            observableAllocations.remove(rowNumber);
            alert.close();
        });
        alert.show();
    }
}
