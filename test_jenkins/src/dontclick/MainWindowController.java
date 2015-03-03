package dontclick;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Created by jayha_000 on 3/2/2015.
 */
public class MainWindowController {
    @FXML
    protected Button buttonYouShouldntPress;

    private StudentList model = new StudentList(true);

    @FXML
    protected void becomeUnhinged(ActionEvent event){
        Button sender = (Button)event.getSource();

        sender.setText(model.currentStudentName());
        model.click();
    }
}
