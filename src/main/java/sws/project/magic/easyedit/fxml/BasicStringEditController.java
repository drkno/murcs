package sws.project.magic.easyedit.fxml;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A basic String edit controller.
 * Any FXML associated with this class should specify a titleText Text object and a valueText textField
 */
public class BasicStringEditController extends BasicEditController<String> implements Initializable {
    @FXML private Text titleText;
    @FXML private TextField valueText;

    private String oldText;

    @Override
    public void setTitle(String text) {
        titleText.setText(text);
    }

    @Override
    public void setValue(String value) {
        valueText.setText(value);
        oldText = value;
    }

    @Override
    public Class[] supportedTypes() {
        return new Class[]{String.class};
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        valueText.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!(oldValue && !newValue)) return;
            notifyChanged(valueText.textProperty(), oldText, valueText.getText());
            oldText = valueText.getText();
        });
    }
}
