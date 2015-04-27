//This does not work properly and may never work properly but just in case it is left here so it can be
//changed in the future to work
//
package sws.murcs.acceptance.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.*;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;

public class ElementDeletionStepDefs extends ApplicationTest{

    private FxRobot fx;
    private Stage primaryStage;
    private Project project;
    private Project project2;
    private Release release;
    private Person person;
    private Skill skill;
    private Team team;
    private RelationalModel model;
    private Application app;


    @Override
    public void start(Stage stage) throws Exception {}

//    @Before
//    public void setUpStuff() throws Exception {
//        UndoRedoManager.forget(true);
//        //UndoRedoManager.setDisabled(false);
//        primaryStage = FxToolkit.registerPrimaryStage();
//        app = FxToolkit.setupApplication(App.class);
//        fx = new FxRobot();
//        launch(App.class);
//
//        fx.interact(() -> {
//            model = new RelationalModel();
//            PersistenceManager.Current.setCurrentModel(model);
//
//            project = new Project();
//            project2 = new Project();
//            try {
//                project.setShortName("Testing");
//                project2.setShortName("Testing2");
//
//                skill = new Skill();
//                skill.setShortName("Skill");
//                skill.setDescription("A skill");
//
//                person = new Person();
//                person.setUserId("foo123");
//                person.setShortName("Things");
//                person.addSkill(skill);
//
//                team = new Team();
//                team.setShortName("team");
//                team.setDescription("A team");
//                team.addMember(person);
//
//                release = new Release();
//                release.setShortName("TestRelease");
//                release.setAssociatedProject(project);
//                release.setReleaseDate(LocalDate.of(2015, 4, 22));
//
//                model.add(project);
//                model.addProject(project2);
//                model.add(person);
//                model.add(release);
//                model.add(team);
//                model.add(skill);
//            }
//            catch (Exception e) {
//                //Do nothing
//            }
//        });
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        FxToolkit.cleanupStages();
//        FxToolkit.cleanupApplication(app);
//    }
//
//    @Given("^I have a project selected$")
//    public void I_have_a_project_selected() throws Throwable {
//        fx.clickOn("#displayChoiceBox").clickOn("Project");
//        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
//    }
//
//    @And("^I press the delete button$")
//    public void I_press_the_delete_button() throws Throwable {
//        fx.clickOn("#removeButton");
//    }
//
//    @Then("^a confirm dialog is displayed$")
//    public void a_confirm_dialog_is_displayed() throws Throwable {
//        fx.interact(() -> {
//            Text messageText = fx.lookup("#messageText").queryFirst();
//            assertTrue(messageText.getText().contains("Are you sure you want to delete this?"));
//        });
//    }
//
//    @And("^all the places that the object is used are displayed$")
//    public void all_the_places_that_the_object_is_used_are_displayed() throws Throwable {
//        //Todo if necessary
//    }
//
//    @And("^I confirm I want to delete$")
//    public void I_confirm_I_want_to_delete() throws Throwable {
//        fx.clickOn("Yes");
//    }
//
//    @Then("^the model is deleted$")
//    public void the_model_is_deleted() throws Throwable {
//        interact(() -> {
//            ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
//            assertTrue(displayList.getItems().size() == 1);
//        });
//    }
//
//    @And("^the deletion can be undone$")
//    public void the_deletion_can_be_undone() throws Throwable {
//        fx.clickOn("#editMenu");
//        fx.moveBy(-10, 0);
//        fx.clickOn("#undoMenuItem");
//        fx.clickOn("#displayChoiceBox").clickOn("People");
//        fx.clickOn("#displayChoiceBox").clickOn("Project");
//        interact(() -> {
//            int displayListSize = ((ListView) primaryStage.getScene().lookup("#displayList")).getItems().size();
//            assertTrue(displayListSize == 2);
//        });
//
//    }
//
//    @Given("^I have a team selected$")
//    public void I_have_a_team_selected() throws Throwable {
//        fx.clickOn("#displayChoiceBox").clickOn("Team");
//        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
//    }
//
//    @Given("^I have a person selected$")
//    public void I_have_a_person_selected() throws Throwable {
//        fx.clickOn("#displayChoiceBox").clickOn("People");
//        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
//    }
//
//    @Given("^I have a skill selected$")
//    public void I_have_a_skill_selected() throws Throwable {
//        fx.clickOn("#displayChoiceBox").clickOn("Skill");
//        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
//    }
//
//    @Given("^I have a release selected$")
//    public void I_have_a_release_selected() throws Throwable {
//        fx.clickOn("#displayChoiceBox").clickOn("Release");
//        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
//    }
}
