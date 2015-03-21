package sws.project.acceptance.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sws.murcs.view.App;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShowHideStepDefs extends ApplicationTest {

    private FxRobot fx;
    private Stage primaryStage;

    @Before
    public void setUp() throws Exception {
        primaryStage = FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        fx = new FxRobot();
        launch(App.class);
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.cleanupStages();
    }

    @When("^I click the View menu$")
    public void When_I_click_the_View_menu() throws Throwable {
        fx.moveTo("#viewMenu").clickOn("#viewMenu");
    }

    @And("^I click the Show/Hide Item list button$")
    public void And_I_click_the_Show_Hide_Item_list_button() throws Throwable {
        fx.moveTo("#viewShowHide").clickOn("#viewShowHide");
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
