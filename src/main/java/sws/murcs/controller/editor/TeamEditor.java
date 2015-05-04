package sws.murcs.controller.editor;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sws.murcs.controller.GenericPopup;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Person;
import sws.murcs.model.Skill;
import sws.murcs.model.Team;
import sws.murcs.model.persistence.PersistenceManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The controller for the team editor.
 */
public class TeamEditor extends GenericEditor<Team> {

    /**
     * The member of the team.
     */
    @FXML
    private VBox teamMembersContainer;
    /**
     * The shortName, longName and description fields for a Team.
     */
    @FXML
    private TextField shortNameTextField, longNameTextField, descriptionTextField;
    /**
     * The productOwner, scrumMaster and member pickers.
     */
    @FXML
    private ChoiceBox<Person> productOwnerPicker, scrumMasterPicker, addTeamMemberPicker;
    /**
     * The label for showing error messages.
     */
    @FXML
    private Label labelErrorMessage;
    /**
     * The ChangeListener for the productOwner or scrumMaster pickers.
     */
    private ChangeListener<Person> smpoChangeListener;

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

        // Use a removable listener
        // to work around a selected index flip-flop bug
        smpoChangeListener = (observable, oldValue, newValue) -> {
            if (newValue != null) {
                saveChanges();
            }
        };

        addTeamMemberPicker.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        saveChanges();
                    }
                });

        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener(smpoChangeListener);
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener(smpoChangeListener);

        setErrorCallback(message -> {
            if (message.getClass() == String.class) {
                labelErrorMessage.setText(message);
            }
        });
    }


    @Override
    public final void loadObject() {
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

        updateMemberList();
        updatePOSM();
    }

    @Override
    protected final void saveChangesWithException() throws Exception {
        Person modelProductOwner = this.getModel().getProductOwner();
        Person viewProductOwner = productOwnerPicker.getValue();
        if (isNotEqual(modelProductOwner, viewProductOwner)) {
            this.getModel().setProductOwner(viewProductOwner);
        }

        Person modelScrumMaster = this.getModel().getScrumMaster();
        Person viewScrumMaster = scrumMasterPicker.getValue();
        if (isNotEqual(modelScrumMaster, viewScrumMaster)) {
            this.getModel().setScrumMaster(viewScrumMaster);
        }

        Person person = addTeamMemberPicker.getValue();
        if (person != null) {
            this.getModel().addMember(person);
        }

        updateMemberList();
        updatePOSM();

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
    }

    @Override
    public final void dispose() {
        productOwnerPicker.getSelectionModel().selectedItemProperty().removeListener(smpoChangeListener);
        scrumMasterPicker.getSelectionModel().selectedItemProperty().removeListener(smpoChangeListener);
        smpoChangeListener = null;
        UndoRedoManager.removeChangeListener(this);
        this.setModel(null);
        this.setErrorCallback(null);
    }

    /**
     * Updates the member list and people that can be assigned to the team.
     */
    private void updateMemberList() {
        addTeamMemberPicker.getItems().clear();
        addTeamMemberPicker.getItems().addAll(PersistenceManager.Current.getCurrentModel().getUnassignedPeople()
                .stream()
                .filter(person -> !this.getModel().getMembers().contains(person))
                .collect(Collectors.toList()));

        teamMembersContainer.getChildren().clear();
        for (Person person : this.getModel().getMembers()) {
            Node node = generateMemberNode(person);
            teamMembersContainer.getChildren().add(node);
        }
    }

    /**
     * Updates the PO and SM.
     */
    private void updatePOSM() {
        Person productOwner = this.getModel().getProductOwner();
        Person scrumMaster = this.getModel().getScrumMaster();

        // Add all the people with the PO skill to the list of POs
        List<Person> productOwners = this.getModel().getMembers()
                .stream()
                .filter(p -> p.canBeRole(Skill.PO_NAME))
                .collect(Collectors.toList());

        // The ScrumMaster can not be a valid product owner
        productOwners.remove(scrumMaster);

        // Remove listener while editing the product owner picker
        productOwnerPicker.getSelectionModel().selectedItemProperty().removeListener(smpoChangeListener);
        productOwnerPicker.getItems().clear();
        productOwnerPicker.getItems().addAll(productOwners);
        if (productOwner != null) {
            productOwnerPicker.getSelectionModel().select(productOwner);
        }
        productOwnerPicker.getSelectionModel().selectedItemProperty().addListener(smpoChangeListener);

        //Add all the people with the scrum master skill
        // to the list of scrum masters
        List<Person> scrumMasters = this.getModel().getMembers()
                .stream()
                .filter(p -> p.canBeRole(Skill.SM_NAME))
                .collect(Collectors.toList());

        // The ProductOwner cannot be a valid scrum master
        scrumMasters.remove(productOwner);

        // Remove listener while editing the scrum master picker
        scrumMasterPicker.getSelectionModel().selectedItemProperty().removeListener(smpoChangeListener);
        scrumMasterPicker.getItems().clear();
        scrumMasterPicker.getItems().addAll(scrumMasters);
        scrumMasterPicker.getSelectionModel().clearSelection();
        if (scrumMaster != null) {
            scrumMasterPicker.getSelectionModel().select(scrumMaster);
        }
        scrumMasterPicker.getSelectionModel().selectedItemProperty().addListener(smpoChangeListener);
    }

    /**
     * Generates a node for a team member.
     * @param person The team member
     * @return the node representing the team member
     */
    private Node generateMemberNode(final Person person) {
        Text nameText = new Text(person.toString());
        Button removeButton = new Button("X");
        removeButton.setOnAction(event -> {
            GenericPopup popup = new GenericPopup();
            popup.setTitleText("Remove Team Member");
            String message = "Are you sure you wish to remove " + person.getShortName() + " from this team?";
            if (this.getModel().getScrumMaster() != null && this.getModel().getScrumMaster().equals(person)) {
                message += "\nThey are currently the teams Scrum Master.";
            }
            if (this.getModel().getProductOwner() != null && this.getModel().getProductOwner().equals(person)) {
                message += "\nThey are currently the teams Product Owner.";
            }
            popup.setMessageText(message);
            popup.addOkCancelButtons(p -> {
                this.getModel().removeMember(person);
                saveChanges();
                popup.close();
            });
            popup.show();
        });

        GridPane pane = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        column1.fillWidthProperty().setValue(true);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.SOMETIMES);

        pane.getColumnConstraints().add(column1);
        pane.getColumnConstraints().add(column2);

        pane.add(nameText, 0, 0);
        pane.add(removeButton, 1, 0);

        return pane;
    }
}
