package sws.project.magic.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 */
public class BasicStringEditController extends BasicEditController<String> implements Initializable {
    @FXML private Text titleText;
    @FXML private TextField valueText;

    @Override
    public void setTitle(String text) {
        titleText.setText(text);
    }

    @Override
    public void setValue(String value) {
        valueText.setText(value);
    }

    @Override
    public Class[] supportedTypes() {
        return new Class[]{String.class};
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        valueText.textProperty().addListener(onChange());
    }

    @Override
    protected void showValid() {
        //We don't need to do anything here
    }

    @Override
    protected void showInvalid() {
        throw new IllegalArgumentException("Invalid value!");
    }
}
