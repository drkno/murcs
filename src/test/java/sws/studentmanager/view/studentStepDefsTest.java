package sws.studentmanager.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.junit.Test;
import org.loadui.testfx.Assertions;
import org.loadui.testfx.GuiTest;
import org.loadui.testfx.controls.ListViews;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * Create student test.Dion Woolley
 *
 * Created by Dion on 3/6/2015.
 */
public class studentStepDefsTest extends GuiTest {

    /**
     * When I click the save button
     * And The name field and dob field is filled out
     * Then The the student is saved to application
     * @throws Exception
     */
    @Test
    public void creatingAStudent() throws Exception{
        // Get the objects interacting with
        Button save = find("#saveButton");
        TextField textField = find("#nameText");
        DatePicker dob = find("#dobCalendar");
        ListView studentList = find("#studentList");

        // Fill in details
        click(textField).type("Dion Woolley");
        click(dob).type("1995/05/02");

        // Save
        click(save);

        // Check that the last item in the list is the one we just created
        assertEquals("Dion Woolley", studentList.getItems().get(0).toString());
    }

    @Test
    public void deleteAStudent() throws Exception {
        // Get the objects interacting with
        Button save = find("#saveButton");
        Button delete = find("#deleteButton");
        TextField textField = find("#nameText");
        DatePicker dob = find("#dobCalendar");
        ListView studentList = find("#studentList");

        // Fill in details
        click(textField).type("Dion Woolley");
        click(dob).type("1995/05/02");

        // Save
        click(save);

        // Check that the last item in the list is the one we just created
        assertEquals("Dion Woolley", studentList.getItems().get(0).toString());
        waitUntil(studentList, ListViews.containsRow("Dion Woolley"));
        Assertions.verifyThat(studentList, ListViews.containsRow("Dion Woolley"));

        studentList.getSelectionModel().select(0);

        click(delete);

        // Check that there are no students
        assertEquals(0, studentList.getItems().size());
    }

    @Override
    protected Parent getRootNode() {
        try {
            return FXMLLoader.load(getClass().getResource("/main.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException();
    }
}
