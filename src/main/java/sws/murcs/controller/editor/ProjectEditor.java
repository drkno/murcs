package sws.murcs.controller.editor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import sws.murcs.controller.GenericPopup;
import sws.murcs.magic.tracking.UndoRedoManager;
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

    /**
     * The shortName, longName and DescriptionFields for a person.
     */
    @FXML
    private TextField shortNameTextField, longNameTextField, descriptionTextField;
    /**
     * The Work Allocation table, team view.
     */
    @FXML
    private TableView<WorkAllocation> teamsViewer;
    /**
     * The Work Allocation table, team column.
     */
    @FXML
    private TableColumn<WorkAllocation, Team> tableColumnTeams;
    /**
     * The Work Allocation table, start and end date columns.
     */
    @FXML
    private TableColumn<WorkAllocation, LocalDate> tableColumnStartDates, tableColumnEndDates;
    /**
     * The date picker for the start and end dates of a work allocation.
     */
    @FXML
    private DatePicker datePickerStartDate, datePickerEndDate;
    /**
     * The team picker for a work allocation.
     */
    @FXML
    private ChoiceBox<Team> choiceBoxAddTeam;
    /**
     * The label to show error messages.
     */
    @FXML
    private Label labelErrorMessage;
    /**
     * An observable list of work allocations.
     */
    private ObservableList<WorkAllocation> observableAllocations;

    @FXML
    @Override
    public final void initialize() {
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                saveChanges();
            }
        });

        longNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                saveChanges();
            }
        });

        descriptionTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                saveChanges();
            }
        });

        choiceBoxAddTeam.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        });

        datePickerStartDate.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                saveChanges();
            }
        });

        datePickerEndDate.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                saveChanges();
            }
        });

        observableAllocations = FXCollections.observableArrayList();
        tableColumnTeams.setCellValueFactory(new PropertyValueFactory<>("team"));
        tableColumnStartDates.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        tableColumnEndDates.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        teamsViewer.setItems(observableAllocations);

        setErrorCallback(message -> {
            if (message.getClass() == String.class) {
                labelErrorMessage.setText(message);
            }
        });
    }

    @Override
    public final void loadObject() {
        // todo decouple from model
        RelationalModel relationalModel = PersistenceManager.Current.getCurrentModel();

        String modelShortName = this.getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        String modelLongName = this.getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqual(modelLongName, viewLongName)) {
            longNameTextField.setText(modelLongName);
        }

        String modelDescription = this.getModel().getDescription();
        String viewDescription = descriptionTextField.getText();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextField.setText(modelDescription);
        }

        choiceBoxAddTeam.getItems().setAll(relationalModel.getTeams());
        observableAllocations.setAll(relationalModel.getProjectsAllocations(this.getModel()));
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        String modelShortName = this.getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelShortName, viewShortName)) {
            this.getModel().setShortName(viewShortName);
        }

        String modelLongName = this.getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqualOrIsEmpty(modelLongName, viewLongName)) {
            this.getModel().setLongName(viewLongName);
        }

        String modelDescription = this.getModel().getDescription();
        String viewDescription = descriptionTextField.getText();
        if (isNotEqualOrIsEmpty(modelDescription, viewDescription)) {
            this.getModel().setDescription(viewDescription);
        }

        // todo decouple from model
        RelationalModel relationalModel = PersistenceManager.Current.getCurrentModel();

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
            WorkAllocation allocation = new WorkAllocation(this.getModel(), selectedTeam, startDate, endDate);
            relationalModel.addAllocation(allocation);
            // This way, the list remains ordered
            observableAllocations.setAll(relationalModel.getProjectsAllocations(this.getModel()));
        }
    }

    @Override
    public final void dispose() {
        observableAllocations = null;
        UndoRedoManager.removeChangeListener(this);
        this.setModel(null);
        this.setErrorCallback(null);
    }
    /**
     * Called by the "Unschedule Work" button.
     * Removes a work period from both the project and team
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
        alert.setMessageText("Are you sure you wish to unshedule \""
                + allocation.getTeam()
                + "\" from \""
                + allocation.getProject()
                + "\"?");
        alert.addOkCancelButtons(a -> {
            PersistenceManager.Current.getCurrentModel().removeAllocation(allocation);
            observableAllocations.remove(rowNumber);
            alert.close();
        });
        alert.show();
    }
}
