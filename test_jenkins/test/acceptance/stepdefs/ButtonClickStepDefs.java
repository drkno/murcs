package acceptance.stepdefs;

import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import dontclick.MainWindow;
import javafx.scene.Parent;
import org.loadui.testfx.GuiTest;
import org.testfx.api.FxToolkit;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxToolkit.showStage;

/**
 * Created by jayha_000 on 3/2/2015.
 */
public class ButtonClickStepDefs extends GuiTest {
    private String originalText;

    @org.junit.Before
    public void setup() throws TimeoutException {
        FxToolkit.setupApplication(MainWindow.class);
        showStage();

    }

    @Given("I click the button")
    public void click_the_button() throws Exception{
        originalText = getButtonText();
    }

    @Then("the text changes")
    public void then_the_text_changed(){
        /*String currentText = getButtonText();

        Button buttonNode = (Button)FXTestUtils.getOrFail("#button");*/

        //TODO Assert.assertNotEqual(currentText, originalText);

        assertTrue(true);
    }

    private String getButtonText(){
        //NodeQuery node = nodes("#button");

        //Set<Node> results = byPredicate(NodeQueryUtils.hasText("foo")).apply(node);
        //System.out.println(node.toString());
        return null;
    }

    @Override
    protected Parent getRootNode() {
        //return new Parent(MainWindow);
        return null;
    }
}
