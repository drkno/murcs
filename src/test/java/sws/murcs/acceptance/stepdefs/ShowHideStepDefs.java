package sws.murcs.acceptance.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sws.murcs.controller.JavaFXHelpers;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Organisation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShowHideStepDefs extends ApplicationTest {

    private FxRobot fx;
    private Stage primaryStage;
    private Application app;
    private Organisation model;

    @Before("@ShowHide")
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
            }
            catch (Exception e) {
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

    @After("@ShowHide")
    public void tearDown() throws Exception {
        PersistenceManager.getCurrent().setCurrentModel(null);
        UndoRedoManager.forgetListeners();
        UndoRedoManager.setDisabled(true);
        FxToolkit.cleanupStages();
        FxToolkit.cleanupApplication(app);
    }

    @When("^I click the View menu$")
    public void When_I_click_the_View_menu() throws Throwable {
        fx.clickOn("View");
    }

    @And("^I click the Show/Hide Item list button$")
    public void And_I_click_the_Show_Hide_Item_list_button() throws Throwable {
        fx.clickOn("Show/Hide Item List");
    }

    @Given("^the side panel is hidden$")
    public void Given_the_side_panel_is_hidden() throws Throwable {
        VBox vBoxSideDisplay = (VBox) primaryStage.getScene().lookup("#vBoxSideDisplay");
        Platform.runLater(() -> {
            vBoxSideDisplay.managedProperty().bind(vBoxSideDisplay.visibleProperty());
            vBoxSideDisplay.setVisible(false);
            assertFalse(primaryStage.getScene().lookup("#vBoxSideDisplay").isVisible());
        });
    }

    @Then("^the side panel shows$")
    public void Then_the_side_panel_shows() throws Throwable {
        assertTrue(primaryStage.getScene().lookup("#vBoxSideDisplay").isVisible());
    }

    @Given("^the side panel list is shown$")
    public void Given_the_side_panel_list_is_shown() throws Throwable {
        VBox vBoxSideDisplay = (VBox) primaryStage.getScene().lookup("#vBoxSideDisplay");
        Platform.runLater(() -> {
            vBoxSideDisplay.managedProperty().bind(vBoxSideDisplay.visibleProperty());
            vBoxSideDisplay.setVisible(true);
            assertTrue(primaryStage.getScene().lookup("#vBoxSideDisplay").isVisible());
        });
    }

    @Then("^the side panel hides$")
    public void Then_the_side_panel_hides() throws Throwable {
        assertFalse(primaryStage.getScene().lookup("#vBoxSideDisplay").isVisible());
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
