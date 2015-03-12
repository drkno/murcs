package sws.project.magic.easyedit.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A basic boolean edit controller. Any FXML associated with this controller should specify
 * a 'titleText' Text object, and 'trueButton,' 'falseButton' radioButtons
 */
public class BasicBooleanEditController extends BasicEditController<Boolean> implements Initializable {
    @FXML private Text titleText;

    private ToggleGroup toggleGroup;
    @FXML private RadioButton trueButton, falseButton;

    @Override
    public void setTitle(String text) {
        titleText.setText(text);
    }

    @Override
    public void setValue(Boolean value) {
        trueButton.setSelected(value);
        falseButton.setSelected(!value);
    }

    @Override
    public Class[] supportedTypes() {
        return new Class[] {Boolean.class, boolean.class};
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().add(trueButton);
        toggleGroup.getToggles().add(falseButton);

        toggleGroup.selectedToggleProperty().addListener((p, o, n) -> {
            notifyChanged(null, !n.isSelected(), n.isSelected());
        });
    }
}
