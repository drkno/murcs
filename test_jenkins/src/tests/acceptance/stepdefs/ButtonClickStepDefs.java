package tests.acceptance.stepdefs;

import com.sun.deploy.uitoolkit.impl.fx.FXPluginToolkit;
import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import dontclick.MainWindow;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.categories.TestFX;
import org.loadui.testfx.utils.FXTestUtils;

import java.io.IOException;

/**
 * Created by jayha_000 on 3/2/2015.
 */
public class ButtonClickStepDefs extends GuiTest {
    private String originalText;

    @Given("I click the button")
    public void click_the_button() throws Exception{
        originalText = getButtonText();
        System.out.println(originalText);
    }

    @Then("the text changes")
    public void then_the_text_changed(){
        /*String currentText = getButtonText();

        Button buttonNode = (Button)FXTestUtils.getOrFail("#button");*/

        //TODO Assert.assertNotEqual(currentText, originalText);
    }

    private String getButtonText(){
        new JFXPanel();

        Button b = (Button)find("#button", getRootNode());
        return b.getText();
    }

    @Override
    protected Parent getRootNode() {
        try {

            return FXMLLoader.load(MainWindow.class.getResource("testwindow.fxml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
