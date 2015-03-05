package sws.dontclick;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Created by jayha_000 on 3/2/2015.
 */
public class AppController {
    @FXML
    protected Button buttonYouShouldntPress;

    private StudentList model = new StudentList();

    @FXML
    protected void becomeUnhinged(ActionEvent event){
        Button sender = (Button)event.getSource();

        sender.setText(model.currentStudentName());
        model.click();
    }
}
