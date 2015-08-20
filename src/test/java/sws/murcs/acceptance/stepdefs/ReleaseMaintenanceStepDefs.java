package sws.murcs.acceptance.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sws.murcs.controller.AppController;
import sws.murcs.controller.JavaFXHelpers;
import sws.murcs.controller.windowManagement.Window;
import sws.murcs.exceptions.CustomException;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Organisation;
import sws.murcs.model.Project;
import sws.murcs.model.Release;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReleaseMaintenanceStepDefs extends ApplicationTest{

    private FxRobot fx;
    private Stage primaryStage;
    private Project project;
    private Organisation model;
    private Application app;
    private Release release;
    private List<Window> registeredStages = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {}

    @Before("@ReleaseMaintenance")
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

                release = new Release();
                release.setShortName("TestRelease");
                project.addRelease(release);
                release.setReleaseDate(LocalDate.of(2015, 4, 22));

                model.add(project);
                registeredStages.add(App.getAppController().getWindow());
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

    @After("@ReleaseMaintenance")
    public void tearDown() throws Exception {
        PersistenceManager.getCurrent().setCurrentModel(null);
        UndoRedoManager.forgetListeners();
        UndoRedoManager.setDisabled(true);
        FxToolkit.cleanupStages();
        FxToolkit.cleanupApplication(app);
        registeredStages = new ArrayList<>();
    }

    @And("^I have selected the release view from the display list type$")
    public void I_have_selected_the_release_view_from_the_display_list_type() throws Throwable {
        fx.clickOn("#displayChoiceBox").clickOn("Release");
        interact(() -> ((ListView) primaryStage.getScene().lookup("#displayList")).getSelectionModel().select(0));
    }

    @And("^a release is selected from the list$")
    public void a_release_is_selected_from_the_list() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        interact(() -> displayList.getSelectionModel().select(0));
    }

    @When("^I click on the remove button$")
    public void I_click_on_the_remove_button() throws Throwable {
        fx.clickOn("#removeButton");
        App.getWindowManager().getAllWindows().stream().filter(w -> !registeredStages.contains(w)).forEach(s -> {
            try {
                FxToolkit.registerStage(s::getStage);
                registeredStages.add(s);
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        });
        fx.clickOn("Yes");
    }

    @Then("^the release is removed from the list$")
    public void the_release_is_removed_from_the_list() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        assertEquals(0, displayList.getItems().size());
    }

    @When("^I select new release from the file menu$")
    public void I_select_new_release_from_the_file_menu() throws Throwable {
        fx.clickOn("#fileMenu").clickOn("#newMenu");
        fx.moveTo("#newModel");
        fx.clickOn("#addRelease");
        App.getWindowManager().getAllWindows().stream().filter(w -> !registeredStages.contains(w)).forEach(s -> {
            try {
                FxToolkit.registerStage(s::getStage);
                registeredStages.add(s);
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        });
    }

    @And("^I fill in valid information in the popup$")
    public void I_fill_in_valid_information_in_the_popup() throws Throwable {
        fx.clickOn("#shortNameTextField").write("Release");
        fx.clickOn("#projectChoiceBox").clickOn("Testing");
    }

    @And("^Click Ok$")
    public void Click_Ok() throws Throwable {
        fx.clickOn("Create");
    }

    @Then("^A release is made with the given information$")
    public void A_release_is_made_with_the_given_information() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        Release release1 = (Release) displayList.getItems().get(0);
        assertEquals("Release",release1.getShortName());
        assertTrue(project.getReleases().contains(release1));
    }

    @And("^I click on the add button$")
    public void I_click_on_the_add_button() throws Throwable {
        fx.clickOn("#addButton");
        App.getWindowManager().getAllWindows().stream().filter(w -> w.getController().getClass() != AppController.class).forEach(s -> {
            try {
                FxToolkit.registerStage(s::getStage);
                registeredStages.add(s);
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        });
    }

    @Given("^there is a release$")
    public void there_is_a_release() throws Throwable {
        Platform.runLater(() -> {
            try {
                PersistenceManager.getCurrent().getCurrentModel().add(release);
            } catch (CustomException e) {
                e.printStackTrace();
            }
        });
    }

    @When("^I edit the values of the release$")
    public void I_edit_the_values_of_the_release() throws Throwable {
        fx.clickOn("#shortNameTextField").write("Foo");
        fx.press(KeyCode.TAB);
        fx.clickOn("#descriptionTextArea");
        fx.write("This is really important");
        fx.clickOn("#shortNameTextField");
    }

    @Then("^the release updates to the values given$")
    public void the_release_updates_to_the_values_given() throws Throwable {
        ListView displayList = (ListView) primaryStage.getScene().lookup("#displayList");
        Release release1 = (Release) displayList.getItems().get(0);
        assertEquals("TestReleaseFoo", release1.getShortName());
        assertEquals("This is really important", release1.getDescription());
        assertTrue(project.getReleases().contains(release1));
    }
}
