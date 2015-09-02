package sws.murcs.acceptance.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import javafx.application.Application;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sws.murcs.controller.JavaFXHelpers;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.Organisation;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

/**
 * Tests the ability to use CreateProjectPopUp to create a new project
 */
public class ProjectMaintenanceStepDefs extends ApplicationTest {

    private FxRobot fx;
    private Stage primaryStage;
    private Application app;
    private Organisation model;

    @Before("@ProjectMaintenance")
    public void setUp() throws Exception {
        UndoRedoManager.get().setDisabled(false);
        primaryStage = FxToolkit.registerPrimaryStage();
        app = FxToolkit.setupApplication(App.class);
        fx = new FxRobot();
        launch(App.class);

        interact(() -> {
            try {
                model = new Organisation();
                PersistenceManager.getCurrent().setCurrentModel(model);
                UndoRedoManager.get().forget(true);
                UndoRedoManager.get().add(model);
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

    @After("@ProjectMaintenance")
    public void tearDown() throws Exception {
        PersistenceManager.getCurrent().setCurrentModel(null);
        UndoRedoManager.get().forgetListeners();
        UndoRedoManager.get().setDisabled(true);
        FxToolkit.cleanupStages();
        FxToolkit.cleanupApplication(app);
    }

    @And("^I click the Project selection$")
    public void And_I_click_the_Project_selection() throws Throwable {
        fx.moveTo("#projectOption").clickOn("#projectOption");
    }

    @Then("^the popup shows$")
    public void Then_the_popup_shows() throws Throwable {

    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
