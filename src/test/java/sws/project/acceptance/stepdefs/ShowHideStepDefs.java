package sws.project.acceptance.stepdefs;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.Assert;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;

public class ShowHideStepDefs extends ApplicationTest {

    @Before
    public void setup() throws Exception {
        String[] args = {};
        launch(sws.project.view.App.class, args);
    }

    @When("^I click the View menu$")
    public void I_click_the_View_menu() throws Throwable {
        moveTo("#viewMenu").clickOn("#viewMenu");
    }

    @Given("^The side panel is hidden$")
    public void The_side_panel_is_hidden() throws Throwable {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GuiTest.find("#vBoxSideDisplay").setVisible(false);
            }
        });
    }

    @And("^I click the Show/Hide Item list button$")
    public void I_click_the_Show_Hide_Item_list_button() throws Throwable {
        moveTo("#viewShowHide").clickOn("#viewShowHide");
    }

    @Then("^The side panel shows$")
    public void The_side_panel_shows() throws Throwable {
        Assert.assertTrue(GuiTest.find("#vBoxSideDisplay").isVisible());
    }

    @Given("^The side panel list is shown$")
    public void The_side_panel_list_is_shown() throws Throwable {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GuiTest.find("#vBoxSideDisplay").setVisible(true);
            }
        });
    }

    @Then("^The the side panel hides$")
    public void The_the_side_panel_hides() throws Throwable {
        Assert.assertFalse(GuiTest.find("#vBoxSideDisplay").isVisible());
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
