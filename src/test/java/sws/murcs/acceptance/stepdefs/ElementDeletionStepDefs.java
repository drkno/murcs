package sws.murcs.acceptance.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sws.murcs.controller.JavaFXHelpers;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.*;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ElementDeletionStepDefs extends ApplicationTest{

    private FxRobot fx;
    private Stage primaryStage;
    private Project project;
    private Release release;
    private Person person;
    private Skill skill;
    private Team team;
    private Story story;
    private Backlog backlog;
    private Organisation model;
    private Application app;


    @Override
    public void start(Stage stage) throws Exception {}

    @Before("@ElementDeletion")
    public void setUp() throws Exception {
        UndoRedoManager.setDisabled(false);
        primaryStage = FxToolkit.registerPrimaryStage();
        app = FxToolkit.setupApplication(App.class);
        fx = new FxRobot();
        launch(App.class);

        interact(() -> {
            try {
                model = new Organisation();
                PersistenceManager.getCurrent().setCurrentModel(model);
                UndoRedoManager.forget(true);
                UndoRedoManager.add(model);

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
                project.addRelease(release);
                release.setReleaseDate(LocalDate.of(2015, 4, 22));

                story = new Story();
                story.setShortName("TestStory");
                story.setCreator(person);

                backlog = new Backlog();
                backlog.setShortName("TestBacklog");
                backlog.setAssignedPO(person);
                backlog.addStory(story, 1);

                model.add(project);
                model.add(person);
                model.add(release);
                model.add(team);
                model.add(skill);
                model.add(story);
                model.add(backlog);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Mac OSX Workaround for testing ONLY!
        interact(() -> {
            final String os = System.getProperty("os.name");
            if (os != null && os.startsWith("Mac")) {
                MenuBar menuBar = (MenuBar) JavaFXHelpers.getByID(primaryStage.getScene().getRoot(), "menuBar");
                menuBar.useSystemMenuBarProperty().set(false);
            }
        });
    }

    @After("@ElementDeletion")
    public void tearDown() throws Exception {
        PersistenceManager.getCurrent().setCurrentModel(null);
        UndoRedoManager.forgetListeners();
        UndoRedoManager.setDisabled(true);
        FxToolkit.cleanupStages();
        FxToolkit.cleanupApplication(app);
    }

    @And("^I press the delete button$")
    public void I_press_the_delete_button() throws Throwable {
        fx.clickOn("#removeButton");
    }

    @Then("^a confirm dialog is displayed$")
    public void a_confirm_dialog_is_displayed() throws Throwable {
        Label messageText = fx.lookup("#messageText").queryFirst();
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

    @Then("^the (\\w+) is deleted$")
    public void the_model_is_deleted(String type) throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        assertEquals("The " + type + " was not deleted.", type.equals("skill") ? 2 : 0, displayList.getItems().size());
    }

    @And("^(\\w+) deletion can be undone$")
    public void the_deletion_of_a_type_can_be_undone(String type) throws Throwable {
        ObservableList items = ((ListView) primaryStage.getScene().lookup("#displayList")).getItems();
        int displayListSizeBefore = items.size();
        fx.clickOn("#editMenu");
        fx.moveBy(0, 20);
        fx.clickOn("#undoMenuItem");
        assertEquals("Deletion could not be undone", displayListSizeBefore + 1, items.size());
    }

    @Given("^I have a (\\w+) selected$")
    public void I_have_selected(String type) throws Throwable {
        int selectIndex = 0;
        switch (type) {
            case "project": fx.clickOn("#displayChoiceBox").clickOn("Person"); break;
            case "person": type = "person"; break;
            case "skill": type = "skill"; selectIndex = 1; break;
        }
        type = type.substring(0,1).toUpperCase() + type.substring(1);

        fx.clickOn("#displayChoiceBox").clickOn(type);
        final int finalSelectIndex = selectIndex;
        interact(() -> ((ListView) primaryStage
                .getScene()
                .lookup("#displayList"))
                .getSelectionModel()
                .select(finalSelectIndex));
    }
}
