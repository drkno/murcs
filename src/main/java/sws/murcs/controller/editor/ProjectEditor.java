package sws.murcs.controller.editor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sws.murcs.controller.GenericPopup;
import sws.murcs.controller.NavigationManager;
import sws.murcs.exceptions.CustomException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Organisation;
import sws.murcs.model.Project;
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
        setChangeListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        });

        shortNameTextField.focusedProperty().addListener(getChangeListener());
        longNameTextField.focusedProperty().addListener(getChangeListener());
        descriptionTextField.focusedProperty().addListener(getChangeListener());

        observableAllocations = FXCollections.observableArrayList();
        tableColumnTeams.setCellValueFactory(new PropertyValueFactory<>("team"));
        tableColumnTeams.setCellFactory(a -> new HyperlinkTeamCell());
        tableColumnStartDates.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        tableColumnEndDates.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        tableColumnEndDates.setCellFactory(a -> new NullableLocalDateCell());
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
        Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();

        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNotEqual(modelShortName, viewShortName)) {
            shortNameTextField.setText(modelShortName);
        }

        String modelLongName = getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNotEqual(modelLongName, viewLongName)) {
            longNameTextField.setText(modelLongName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextField.getText();
        if (isNotEqual(modelDescription, viewDescription)) {
            descriptionTextField.setText(modelDescription);
        }

        choiceBoxAddTeam.getItems().setAll(organisation.getTeams());
        observableAllocations.setAll(organisation.getProjectsAllocations(getModel()));

        setIsCreationWindow(modelShortName == null);
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        String modelShortName = getModel().getShortName();
        String viewShortName = shortNameTextField.getText();
        if (isNullOrNotEqual(modelShortName, viewShortName)) {
            getModel().setShortName(viewShortName);
        }

        String modelLongName = getModel().getLongName();
        String viewLongName = longNameTextField.getText();
        if (isNullOrNotEqual(modelLongName, viewLongName)) {
            getModel().setLongName(viewLongName);
        }

        String modelDescription = getModel().getDescription();
        String viewDescription = descriptionTextField.getText();
        if (isNullOrNotEqual(modelDescription, viewDescription)) {
            getModel().setDescription(viewDescription);
        }
    }

    @Override
    public final void dispose() {
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        longNameTextField.focusedProperty().removeListener(getChangeListener());
        shortNameTextField.focusedProperty().removeListener(getChangeListener());
        observableAllocations = null;
        setChangeListener(null);
        UndoRedoManager.removeChangeListener(this);
        setModel(null);
        setErrorCallback(null);
    }

    /**
     * Called by the "Add Team" button.
     * Adds a work allocation to the list for the selected project
     */
    @FXML
    private void buttonScheduleTeamClick() {
        Team team = choiceBoxAddTeam.getValue();
        LocalDate startDate = datePickerStartDate.getValue();
        LocalDate endDate = datePickerEndDate.getValue();

        // Must meet minimum requirements for an allocation
        String message = "";
        if (team == null) {
            message += "Team may not be null";
        }
        if (startDate == null) {
            if (message.length() != 0) {
                message += " and ";
            }
            message += "Start Date may not be null";
        }
        if (!message.equals("")) {
            labelErrorMessage.setText(message);
            return;
        }
        else {
            labelErrorMessage.setText("");
        }

        try {
            // Attempt to save the allocation
            Organisation organisation = PersistenceManager.getCurrent().getCurrentModel();
            WorkAllocation allocation = new WorkAllocation(getModel(), team, startDate, endDate);
            organisation.addAllocation(allocation);
            observableAllocations.setAll(organisation.getProjectsAllocations(getModel()));

            // Clear user inputs for work period
            choiceBoxAddTeam.getSelectionModel().clearSelection();
            datePickerStartDate.setValue(null);
            datePickerEndDate.setValue(null);
        }
        catch (CustomException e) {
            labelErrorMessage.setText(e.getMessage());
        }
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
            PersistenceManager.getCurrent().getCurrentModel().removeAllocation(allocation);
            observableAllocations.remove(rowNumber);
            alert.close();
        });
        alert.show();
    }

    /**
     * A TableView cell that contains a link to the team it represents.
     */
    private class HyperlinkTeamCell extends TableCell<WorkAllocation, Team> {
        @Override
        protected void updateItem(final Team team, final boolean empty) {
            super.updateItem(team, empty);
            if (team == null) {
                setText("");
            }
            else if (getIsCreationWindow()) {
                setText(team.toString());
            }
            else {
                Hyperlink text = new Hyperlink(team.toString());
                text.setOnAction(param -> NavigationManager.navigateTo(team));
                setGraphic(text);
            }
        }
    }

    /**
     * Used to represent the end date cell as it could receive a null pointer if no end is specified.
     */
    private class NullableLocalDateCell extends TableCell<WorkAllocation, LocalDate> {
        @Override
        protected void updateItem(final LocalDate date, final boolean empty) {
            super.updateItem(date, empty);
            if (date != null) {
                setText(date.toString());
            }
            else {
                setText("");
            }
        }
    }
}
