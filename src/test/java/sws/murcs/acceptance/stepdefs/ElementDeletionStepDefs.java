package sws.murcs.acceptance.stepdefs;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.NodeQuery;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.*;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.framework.junit.ApplicationTest.launch;

public class ElementDeletionStepDefs extends ApplicationTest{

    private FxRobot fx;
    private Stage primaryStage;
    private Project project;
    private Release release;
    private Person person;
    private Skill skill;
    private Team team;
    private RelationalModel model;
    private Application app;


    @Override
    public void start(Stage stage) throws Exception {}

    @Before
    public void setUpStuff() throws Exception {
        UndoRedoManager.setDisabled(true);
        primaryStage = FxToolkit.registerPrimaryStage();
        app = FxToolkit.setupApplication(App.class);
        fx = new FxRobot();
        launch(App.class);

        model = new RelationalModel();
        PersistenceManager.Current.setCurrentModel(model);

        project = new Project();
        project.setShortName("Testing");

        skill = new Skill();
        skill.setShortName("Skill");
        skill.setDescription("A skill");

        person = new Person();
        person.setUserId("foo123");
        person.setShortName("Things");
        person.addSkill(skill);

        team = new Team();
        team.setShortName("team");
        team.setDescription("A team");
        team.addMember(person);

        release = new Release();
        release.setShortName("TestRelease");
        release.setAssociatedProject(project);
        release.setReleaseDate(LocalDate.of(2015, 4, 22));

        model.addProject(project);
        model.addPerson(person);
        model.addRelease(release);
        model.addTeam(team);
        model.addSkill(skill);
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.cleanupStages();
        FxToolkit.cleanupApplication(app);
    }

    @Given("^I have a project selected$")
    public void I_have_a_project_selected() throws Throwable {
        fx.clickOn("#displayChoiceBox").clickOn("Project");
        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
    }

    @And("^I press the delete button$")
    public void I_press_the_delete_button() throws Throwable {
        fx.clickOn("#removeButton");
    }

    @Then("^a confirm dialog is displayed$")
    public void a_confirm_dialog_is_displayed() throws Throwable {
        Text messageText = fx.lookup("#messageText").queryFirst();
        assertTrue(messageText.getText().contains("Are you sure you want to delete this?"));
    }

    @And("^all the places that the object is used are displayed$")
    public void all_the_places_that_the_object_is_used_are_displayed() throws Throwable {
        //Todo if necessary
    }

    @And("^I confirm I want to delete$")
    public void I_confirm_I_want_to_delete() throws Throwable {
        fx.clickOn("Yes");
    }

    @Then("^the model is deleted$")
    public void the_model_is_deleted() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        assertTrue(displayList.getItems().size() == 0);
    }

    @And("^the deletion can be undone$")
    public void the_deletion_can_be_undone() throws Throwable {
        fx.clickOn("#editMenu");
        fx.clickOn("#undoMenuItem");
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        assertTrue(displayList.getItems().size() == 1);
    }

    @Given("^I have a team selected$")
    public void I_have_a_team_selected() throws Throwable {
        fx.clickOn("#displayChoiceBox").clickOn("Team");
        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
    }

    @Given("^I have a person selected$")
    public void I_have_a_person_selected() throws Throwable {
        fx.clickOn("#displayChoiceBox").clickOn("People");
        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
    }

    @Given("^I have a skill selected$")
    public void I_have_a_skill_selected() throws Throwable {
        fx.clickOn("#displayChoiceBox").clickOn("Skill");
        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
    }

    @Given("^I have a release selected$")
    public void I_have_a_release_selected() throws Throwable {
        fx.clickOn("#displayChoiceBox").clickOn("Release");
        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
    }
}
