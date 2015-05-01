package sws.murcs.acceptance.stepdefs;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import javafx.application.Application;
import javafx.stage.Stage;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import sws.murcs.magic.tracking.UndoRedoManager;
import sws.murcs.model.RelationalModel;
import sws.murcs.model.persistence.PersistenceManager;
import sws.murcs.view.App;

/**
 * Tests the ability to use CreateProjectPopUp to create a new project
 */
public class ProjectMaintenanceStepDefs extends ApplicationTest {

    private FxRobot fx;
    private Stage primaryStage;
    private Application app;
    private RelationalModel model;

    @Before("@ProjectMaintenance")
    public void setUp() throws Exception {
        UndoRedoManager.setDisabled(false);
        primaryStage = FxToolkit.registerPrimaryStage();
        app = FxToolkit.setupApplication(App.class);
        fx = new FxRobot();
        launch(App.class);

        interact(() -> {
            try {
                PersistenceManager.Current.setCurrentModel(null);
                model = new RelationalModel();
                PersistenceManager.Current.setCurrentModel(model);
                UndoRedoManager.forget(true);
                UndoRedoManager.add(model);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @After("@ProjectMaintenance")
    public void tearDown() throws Exception {
        UndoRedoManager.forgetListeners();
        UndoRedoManager.setDisabled(true);
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
